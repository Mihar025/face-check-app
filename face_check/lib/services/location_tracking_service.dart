

import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:battery_plus/battery_plus.dart';
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:workmanager/workmanager.dart';

import '../models/location_update_dto.dart';


class LocationTrackingService {
  static const String _trackingStatusKey = 'location_tracking_active';
  static const String _lastPunchTypeKey = 'last_punch_type';
  static const String _userIdKey = 'user_id';

  static const String _backgroundTaskName = "location_tracking_task";
  static const Duration _updateInterval = Duration(minutes: 12);

  final Dio _dio;
  final Battery _battery = Battery();

  Timer? _locationTimer;
  StreamSubscription<Position>? _positionStream;

  LocationTrackingService(this._dio);

  // Инициализация фонового воркера
  static void initializeBackgroundService() {
    Workmanager().initialize(
      callbackDispatcher,
      isInDebugMode: true,
    );
  }

  // Проверка и запрос разрешений
  Future<bool> checkAndRequestPermissions() async {
    // Проверяем базовое разрешение на геолокацию
    LocationPermission permission = await Geolocator.checkPermission();

    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return false;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      // Открываем настройки приложения
      await openAppSettings();
      return false;
    }

    // Для Android 10+ нужно дополнительное разрешение для фоновой геолокации
    if (Platform.isAndroid) {
      final backgroundStatus = await Permission.locationAlways.status;
      if (!backgroundStatus.isGranted) {
        final result = await Permission.locationAlways.request();
        if (!result.isGranted) {
          debugPrint('Background location permission denied');
          // Можем работать только когда приложение активно
        }
      }
    }

    // Проверяем, включена ли геолокация на устройстве
    final serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      // Предлагаем включить геолокацию
      await Geolocator.openLocationSettings();
      return false;
    }

    return true;
  }

  // Начать трекинг после Punch In
  Future<void> startTracking(int userId) async {
    final prefs = await SharedPreferences.getInstance();

    // Сохраняем статус трекинга
    await prefs.setBool(_trackingStatusKey, true);
    await prefs.setString(_lastPunchTypeKey, 'IN');
    await prefs.setInt(_userIdKey, userId);

    // Проверяем разрешения
    final hasPermission = await checkAndRequestPermissions();
    if (!hasPermission) {
      debugPrint('Location permissions not granted');
      return;
    }

    // Отправляем первую локацию сразу
    await _sendCurrentLocation(userId);

    // Запускаем периодическое обновление для активного приложения
    _startForegroundTracking(userId);

    // Регистрируем фоновую задачу для работы когда приложение свернуто
    await _registerBackgroundTask();

    debugPrint('Location tracking started for user: $userId');
  }

  // Остановить трекинг после Punch Out
  Future<void> stopTracking() async {
    final prefs = await SharedPreferences.getInstance();

    // Отправляем последнюю локацию перед остановкой
    final userId = prefs.getInt(_userIdKey);
    if (userId != null) {
      await _sendCurrentLocation(userId);
    }

    // Останавливаем трекинг
    await prefs.setBool(_trackingStatusKey, false);
    await prefs.setString(_lastPunchTypeKey, 'OUT');

    // Отменяем таймеры и подписки
    _locationTimer?.cancel();
    _positionStream?.cancel();

    // Отменяем фоновую задачу
    await Workmanager().cancelByUniqueName(_backgroundTaskName);

    debugPrint('Location tracking stopped');
  }

  // Проверить, активен ли трекинг
  Future<bool> isTrackingActive() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_trackingStatusKey) ?? false;
  }

  // Трекинг для активного приложения
  void _startForegroundTracking(int userId) {
    // Отменяем предыдущий таймер если есть
    _locationTimer?.cancel();

    // Создаем новый таймер для отправки каждые 5 минут
    _locationTimer = Timer.periodic(_updateInterval, (timer) async {
      final isActive = await isTrackingActive();
      if (isActive) {
        await _sendCurrentLocation(userId);
      } else {
        timer.cancel();
      }
    });

    // Альтернативный вариант - стрим обновлений с фильтрацией
    _setupLocationStream(userId);
  }

  // Настройка стрима для более точного трекинга
  void _setupLocationStream(int userId) {
    const locationSettings = LocationSettings(
      accuracy: LocationAccuracy.high,
      distanceFilter: 100, // Обновление каждые 100 метров
    );

    _positionStream = Geolocator.getPositionStream(
      locationSettings: locationSettings,
    ).listen((Position position) async {
      // Здесь можно добавить логику для фильтрации
      // Например, отправлять только если прошло 5 минут с последней отправки
      await _handlePositionUpdate(position, userId);
    });
  }

  // Обработка обновления позиции
  Future<void> _handlePositionUpdate(Position position, int userId) async {
    final prefs = await SharedPreferences.getInstance();
    final lastUpdateTime = prefs.getInt('last_location_update') ?? 0;
    final now = DateTime.now().millisecondsSinceEpoch;

    // Отправляем только если прошло 5 минут
    if (now - lastUpdateTime >= _updateInterval.inMilliseconds) {
      await _sendLocationToServer(position, userId);
      await prefs.setInt('last_location_update', now);
    }
  }

  // Регистрация фоновой задачи
  Future<void> _registerBackgroundTask() async {
    await Workmanager().registerPeriodicTask(
      _backgroundTaskName,
      _backgroundTaskName,
      frequency: _updateInterval,
      constraints: Constraints(
        networkType: NetworkType.connected,
      ),
      backoffPolicy: BackoffPolicy.exponential,
      backoffPolicyDelay: const Duration(seconds: 10),
    );
  }

  // Отправка текущей локации
  Future<void> _sendCurrentLocation(int userId) async {
    try {
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: const Duration(seconds: 10),
      );

      await _sendLocationToServer(position, userId);
    } catch (e) {
      debugPrint('Error getting current location: $e');
    }
  }

  // Отправка на сервер
  Future<void> _sendLocationToServer(Position position, int userId) async {
    try {
      // Получаем уровень батареи
      final batteryLevel = await _battery.batteryLevel;

      final locationDto = LocationUpdateDto(
        latitude: position.latitude,
        longitude: position.longitude,
        timestamp: position.timestamp ?? DateTime.now(),
        accuracy: position.accuracy,
        speed: position.speed,
        bearing: position.heading,
        altitude: position.altitude,
        batteryLevel: batteryLevel,
      );

      // Отправляем на бэкенд
      final response = await _dio.post(
        'location/update/$userId',
        data: locationDto.toJson(),
      );

      if (response.statusCode == 200) {
        debugPrint('Location sent successfully: ${position.latitude}, ${position.longitude}');

        // Сохраняем локально для офлайн режима (опционально)
        await _saveLocationLocally(locationDto, userId);
      }
    } catch (e) {
      debugPrint('Error sending location to server: $e');

      // Сохраняем локально для последующей отправки
      // await _saveLocationForLaterSync(position, userId);
    }
  }

  // Сохранение локации локально (для офлайн режима)
  Future<void> _saveLocationLocally(LocationUpdateDto location, int userId) async {
    final prefs = await SharedPreferences.getInstance();

    // Получаем существующие локации
    final locationsJson = prefs.getString('offline_locations') ?? '[]';
    final locations = jsonDecode(locationsJson) as List;

    // Добавляем новую
    locations.add({
      'userId': userId,
      'location': location.toJson(),
      'synced': false,
    });

    // Сохраняем обратно (ограничиваем количество)
    if (locations.length > 100) {
      locations.removeRange(0, locations.length - 100);
    }

    await prefs.setString('offline_locations', jsonEncode(locations));
  }

  // Синхронизация офлайн локаций
  Future<void> syncOfflineLocations() async {
    final prefs = await SharedPreferences.getInstance();
    final locationsJson = prefs.getString('offline_locations') ?? '[]';
    final locations = jsonDecode(locationsJson) as List;

    final unsyncedLocations = locations.where((l) => l['synced'] == false).toList();

    for (final locationData in unsyncedLocations) {
      try {
        await _dio.post(
          '/location/update/${locationData['userId']}',
          data: locationData['location'],
        );

        locationData['synced'] = true;
      } catch (e) {
        debugPrint('Failed to sync offline location: $e');
      }
    }

    // Сохраняем обновленный список
    await prefs.setString('offline_locations', jsonEncode(locations));
  }
}

@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    if (task == LocationTrackingService._backgroundTaskName) {
      // Проверяем, активен ли трекинг
      final prefs = await SharedPreferences.getInstance();
      final isTracking = prefs.getBool(LocationTrackingService._trackingStatusKey) ?? false;

      if (isTracking) {
        final userId = prefs.getInt('user_id');
        if (userId != null) {
          // Создаем Dio для фоновой задачи
          final dio = Dio(BaseOptions(
           // baseUrl: 'http://192.168.1.194:8088/api/v1/',
            baseUrl:'https://face-check-prod-drgsy.ondigitalocean.app/api/v1',
            connectTimeout: const Duration(seconds: 5),
            receiveTimeout: const Duration(seconds: 30),
          ));

          // Добавляем токен
          final token = prefs.getString('auth_token');
          if (token != null) {
            dio.options.headers['Authorization'] = 'Bearer $token';
          }

          final service = LocationTrackingService(dio);
          await service._sendCurrentLocation(userId);
        }
      }
    }

    return Future.value(true);
  });
}
