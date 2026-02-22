import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:battery_plus/battery_plus.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_background_service/flutter_background_service.dart';
import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../models/location_update_dto.dart';
import 'ApiService.dart';

// ═══════════════════════════════════════════════════════════
//  LocationTrackingService v2 — максимально надёжный трекинг
//
//  Android: foreground service (99%)
//  iOS:     continuous location stream (95-98%)
//
//  Вместо Timer.periodic используем Geolocator.getPositionStream()
//  iOS видит активное использование GPS → не убивает процесс
//  На сервер отправляем каждые 12 минут (не каждую точку)
//
//  API:
//    - startTracking(userId)   ← Punch In
//    - stopTracking()          ← Punch Out
// ═══════════════════════════════════════════════════════════

class LocationTrackingService {
  // SharedPreferences keys
  static const String _trackingStatusKey = 'location_tracking_active';
  static const String _lastPunchTypeKey = 'last_punch_type';
  static const String _userIdKey = 'tracking_user_id';
  static const String _baseUrlKey = 'tracking_base_url';
  static const String _authTokenKey = 'auth_token';
  static const String _lastSendTimeKey = 'tracking_last_send_time';

  // Интервал отправки на сервер (12 минут)
  static const int sendIntervalMinutes = 12;

  // Базовый URL API
  static const String _defaultBaseUrl =
      'https://face-check-prod-drgsy.ondigitalocean.app/api/v1/';

  final Dio _dio;

  LocationTrackingService(this._dio);

  // ================== INITIALIZATION ==================

  /// Вызывай в main.dart ОДИН раз при старте приложения
  static Future<void> initializeBackgroundService() async {
    final service = FlutterBackgroundService();

    await service.configure(
      androidConfiguration: AndroidConfiguration(
        onStart: _onServiceStart,
        autoStart: false,
        isForegroundMode: true,
        autoStartOnBoot: false,
        notificationChannelId: 'facecheck_location',
        initialNotificationTitle: 'FaceCheck',
        initialNotificationContent: 'Tracking your work shift',
        foregroundServiceNotificationId: 888,
        foregroundServiceTypes: [AndroidForegroundType.location],
      ),
      iosConfiguration: IosConfiguration(
        autoStart: false,
        onForeground: _onServiceStart,
        onBackground: _onIosBackground,
      ),
    );

    if (kDebugMode) {
      debugPrint('✅ Background service configured (v2 - stream mode)');
    }
  }

  // ================== PERMISSIONS ==================

  Future<bool> checkAndRequestPermissions() async {
    LocationPermission permission;
    try {
      permission = await Geolocator.checkPermission();
    } catch (e) {
      if (kDebugMode) debugPrint('❌ checkPermission error: $e');
      return false;
    }

    if (permission == LocationPermission.denied) {
      try {
        permission = await Geolocator.requestPermission();
      } catch (e) {
        if (kDebugMode) debugPrint('❌ requestPermission error: $e');
        return false;
      }
      if (permission == LocationPermission.denied) return false;
    }

    if (permission == LocationPermission.deniedForever) {
      await openAppSettings();
      return false;
    }

    // Android: запрашиваем "Always" для фона
    if (Platform.isAndroid) {
      final bgStatus = await Permission.locationAlways.status;
      if (!bgStatus.isGranted) {
        final result = await Permission.locationAlways.request();
        if (!result.isGranted) {
          if (kDebugMode) debugPrint('⚠️ Background location not granted');
        }
      }
    }

    // iOS: запрашиваем "Always" для фона
    if (Platform.isIOS) {
      final bgStatus = await Permission.locationAlways.status;
      if (!bgStatus.isGranted) {
        final result = await Permission.locationAlways.request();
        if (!result.isGranted) {
          if (kDebugMode) debugPrint('⚠️ iOS Always location not granted');
        }
      }
    }

    bool serviceEnabled;
    try {
      serviceEnabled = await Geolocator.isLocationServiceEnabled();
    } catch (e) {
      if (kDebugMode) debugPrint('❌ isLocationServiceEnabled error: $e');
      return false;
    }

    if (!serviceEnabled) {
      await Geolocator.openLocationSettings();
      return false;
    }

    return true;
  }

  // ================== PUBLIC API ==================

  /// Начать трекинг — вызывай при Punch In
  Future<void> startTracking(int userId) async {
    final prefs = await SharedPreferences.getInstance();

    // Сохраняем данные для фонового сервиса
    await prefs.setBool(_trackingStatusKey, true);
    await prefs.setString(_lastPunchTypeKey, 'IN');
    await prefs.setInt(_userIdKey, userId);
    await prefs.setString(_baseUrlKey, _defaultBaseUrl);
    await prefs.setInt(_lastSendTimeKey, DateTime.now().millisecondsSinceEpoch);

    // Сохраняем токен для фонового сервиса
    String? token;
    try {
      token = await ApiService.instance.getAuthToken();
    } catch (_) {
      token = _dio.options.headers['Authorization']?.toString().replaceFirst('Bearer ', '');
    }
    if (token != null && token.isNotEmpty) {
      await prefs.setString(_authTokenKey, token);
    }

    // Проверяем пермишены
    final hasPermission = await checkAndRequestPermissions();
    if (!hasPermission) {
      if (kDebugMode) debugPrint('⚠️ Permissions not granted');
    }

    // Отправляем текущую локацию сразу
    await _sendCurrentLocationSafe(userId);

    // Запускаем фоновый сервис
    final service = FlutterBackgroundService();
    final isRunning = await service.isRunning();

    if (!isRunning) {
      await service.startService();
      if (kDebugMode) debugPrint('✅ Background service started');
    } else {
      service.invoke('startTracking', {'userId': userId});
      if (kDebugMode) debugPrint('✅ Tracking restarted (service was running)');
    }

    if (kDebugMode) {
      debugPrint('✅ Location tracking started for user: $userId');
    }
  }

  /// Остановить трекинг — вызывай при Punch Out
  Future<void> stopTracking() async {
    final prefs = await SharedPreferences.getInstance();

    // Отправляем последнюю локацию
    final userId = prefs.getInt(_userIdKey);
    if (userId != null) {
      await _sendCurrentLocationSafe(userId);
    }

    // Обновляем статус
    await prefs.setBool(_trackingStatusKey, false);
    await prefs.setString(_lastPunchTypeKey, 'OUT');

    // Останавливаем сервис
    final service = FlutterBackgroundService();
    final isRunning = await service.isRunning();
    if(isRunning){
      service.invoke('stopService');
    }

    if (kDebugMode) {
      debugPrint('🛑 Location tracking stopped');
    }
  }

  /// Проверить активен ли трекинг
  Future<bool> isTrackingActive() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_trackingStatusKey) ?? false;
  }

  // ================== LOCATION SENDING (foreground context) ==================

  Future<void> _sendCurrentLocationSafe(int userId) async {
    final position = await _getSafePosition();
    if (position == null) return;
    await _sendLocationToServer(position, userId);
  }

  Future<Position?> _getSafePosition({
    Duration timeout = const Duration(seconds: 15),
  }) async {
    try {
      final serviceEnabled = await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) return null;

      final permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.deniedForever) {
        return null;
      }

      return await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: timeout,
      );
    } catch (e) {
      if (kDebugMode) debugPrint('❌ _getSafePosition error: $e');
      return null;
    }
  }

  Future<void> _sendLocationToServer(Position position, int userId) async {
    try {
      final batteryLevel = await Battery().batteryLevel;

      final locationDto = LocationUpdateDto(
        latitude: position.latitude,
        longitude: position.longitude,
        timestamp: position.timestamp?.toUtc() ?? DateTime.now().toUtc(),
        accuracy: position.accuracy,
        speed: position.speed,
        bearing: position.heading,
        altitude: position.altitude,
        batteryLevel: batteryLevel,
      );

      final response = await _dio.post(
        'location/update/$userId',
        data: locationDto.toJson(),
      );

      if (response.statusCode == 200) {
        if (kDebugMode) {
          debugPrint(
              '✅ Location sent: ${position.latitude}, ${position.longitude}');
        }
      }
    } catch (e) {
      if (kDebugMode) debugPrint('❌ Error sending location: $e');
    }
  }
}

// ═══════════════════════════════════════════════════════════
//  BACKGROUND SERVICE — ОТДЕЛЬНЫЙ ИЗОЛЯТ
//
//  Ключевое отличие v2:
//  Вместо Timer.periodic используем getPositionStream()
//  iOS видит активный GPS listener → не убивает процесс
//  На сервер шлём только раз в 12 минут
// ═══════════════════════════════════════════════════════════

@pragma('vm:entry-point')
Future<void> _onServiceStart(ServiceInstance service) async {
  DartPluginRegistrant.ensureInitialized();

  StreamSubscription<Position>? positionSubscription;
  Position? lastPosition;
  DateTime lastSendTime = DateTime.now();

  // --- Команда СТОП ---
  service.on('stopService').listen((event) {
    positionSubscription?.cancel();
    service.stopSelf();
    if (kDebugMode) debugPrint('🛑 Background service stopped');
  });

  // --- Команда рестарт ---
  service.on('startTracking').listen((event) {
    lastSendTime = DateTime.now();
    if (kDebugMode) debugPrint('🔄 Tracking restarted from foreground');
  });

  // --- Android нотификация ---
  if (service is AndroidServiceInstance) {
    service.setForegroundNotificationInfo(
      title: 'FaceCheck — Shift Active',
      content: 'Tracking your location during work shift',
    );
  }

  // --- Отправляем первую локацию сразу ---
  await _backgroundSendLocation(service);
  lastSendTime = DateTime.now();

  // ═══════════════════════════════════════════
  //  CONTINUOUS LOCATION STREAM
  //
  //  iOS: держит приложение живым потому что
  //  мы активно слушаем GPS обновления
  //
  //  distanceFilter: 10 — обновление каждые 10 метров
  //  Если юзер сидит дома неподвижно — iOS всё равно
  //  шлёт обновления раз в несколько минут
  //
  //  accuracy: low — минимальный расход батареи
  //  Для трекинга "где работник" этого достаточно
  // ═══════════════════════════════════════════

  late LocationSettings locationSettings;

  if (Platform.isIOS) {
    locationSettings = AppleSettings(
      accuracy: LocationAccuracy.low,
      activityType: ActivityType.other,
      distanceFilter: 10,
      pauseLocationUpdatesAutomatically: false, // ← КРИТИЧНО: не ставить на паузу
      showBackgroundLocationIndicator: true, // ← синяя полоска в статус баре
    );
  } else {
    // Android — можно medium, foreground service и так не убьётся
    locationSettings = AndroidSettings(
      accuracy: LocationAccuracy.low,
      distanceFilter: 10,
      intervalDuration: const Duration(minutes: 2), // проверять каждые 2 мин
      foregroundNotificationConfig: const ForegroundNotificationConfig(
        notificationTitle: 'FaceCheck — Shift Active',
        notificationText: 'Tracking your location during work shift',
        enableWakeLock: true,
      ),
    );
  }

  positionSubscription = Geolocator.getPositionStream(
    locationSettings: locationSettings,
  ).listen(
        (Position position) async {
      lastPosition = position;

      if (kDebugMode) {
        debugPrint(
            '📍 Stream position: ${position.latitude}, ${position.longitude}');
      }

      // Проверяем: прошло ли 12 минут с последней отправки?
      final now = DateTime.now();
      final minutesSinceLastSend =
          now.difference(lastSendTime).inMinutes;

      if (minutesSinceLastSend >= LocationTrackingService.sendIntervalMinutes) {
        // Проверяем что трекинг ещё активен
        final prefs = await SharedPreferences.getInstance();
        final isTracking =
            prefs.getBool(LocationTrackingService._trackingStatusKey) ?? false;

        if (!isTracking) {
          positionSubscription?.cancel();
          service.stopSelf();
          if (kDebugMode) debugPrint('🛑 Tracking off, stopping service');
          return;
        }

        // Отправляем на сервер
        await _backgroundSendLocationWithPosition(service, position);
        lastSendTime = now;

        if (kDebugMode) {
          debugPrint('📤 Location sent to server (${minutesSinceLastSend}min since last)');
        }
      }
    },
    onError: (error) {
      if (kDebugMode) debugPrint('❌ Position stream error: $error');
    },
  );

  // ═══════════════════════════════════════════
  //  SAFETY NET TIMER
  //
  //  Если stream молчит слишком долго (юзер совсем
  //  не двигается), принудительно отправляем локацию
  //  через getCurrentPosition каждые 15 минут
  // ═══════════════════════════════════════════

  Timer.periodic(
    const Duration(minutes: 15),
        (timer) async {
      final prefs = await SharedPreferences.getInstance();
      final isTracking =
          prefs.getBool(LocationTrackingService._trackingStatusKey) ?? false;

      if (!isTracking) {
        timer.cancel();
        positionSubscription?.cancel();
        service.stopSelf();
        return;
      }

      final now = DateTime.now();
      final minutesSinceLastSend =
          now.difference(lastSendTime).inMinutes;

      // Если stream не отправлял больше 14 минут — шлём принудительно
      if (minutesSinceLastSend >= 14) {
        if (kDebugMode) {
          debugPrint('⏰ Safety net: forcing location send (stream silent for ${minutesSinceLastSend}min)');
        }
        await _backgroundSendLocation(service);
        lastSendTime = now;
      }
    },
  );
}

/// Отправка с конкретной позицией (из stream)
Future<void> _backgroundSendLocationWithPosition(
    ServiceInstance service, Position position) async {
  try {
    final prefs = await SharedPreferences.getInstance();

    final isTracking =
        prefs.getBool(LocationTrackingService._trackingStatusKey) ?? false;
    final lastPunchType =
    prefs.getString(LocationTrackingService._lastPunchTypeKey);

    if (!isTracking || lastPunchType != 'IN') return;

    final userId = prefs.getInt(LocationTrackingService._userIdKey);
    if (userId == null) return;

    // Батарея
    int batteryLevel = 0;
    try {
      batteryLevel = await Battery().batteryLevel;
    } catch (_) {}

    // Dio
    final baseUrl = prefs.getString(LocationTrackingService._baseUrlKey) ??
        LocationTrackingService._defaultBaseUrl;
    final token = prefs.getString(LocationTrackingService._authTokenKey);

    final dio = Dio(BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 30),
    ));

    if (token != null && token.isNotEmpty) {
      dio.options.headers['Authorization'] = 'Bearer $token';
    }

    final locationData = {
      'latitude': position.latitude,
      'longitude': position.longitude,
      'timestamp':
      (position.timestamp?.toUtc() ?? DateTime.now().toUtc()).toIso8601String(),
      'accuracy': position.accuracy,
      'speed': position.speed,
      'bearing': position.heading,
      'altitude': position.altitude,
      'batteryLevel': batteryLevel,
    };

    final response = await dio.post(
      'location/update/$userId',
      data: locationData,
    );

    if (response.statusCode == 200) {
      if (service is AndroidServiceInstance) {
        final now = DateTime.now();
        final timeStr =
            '${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}';
        service.setForegroundNotificationInfo(
          title: 'FaceCheck — Shift Active',
          content: 'Last update: $timeStr • Battery: $batteryLevel%',
        );
      }

      if (kDebugMode) {
        debugPrint(
            '✅ BG location sent: ${position.latitude}, ${position.longitude}');
      }
    }
  } catch (e) {
    if (kDebugMode) debugPrint('❌ Background send error: $e');
  }
}

/// Отправка через getCurrentPosition (safety net / первая отправка)
Future<void> _backgroundSendLocation(ServiceInstance service) async {
  try {
    final prefs = await SharedPreferences.getInstance();

    final isTracking =
        prefs.getBool(LocationTrackingService._trackingStatusKey) ?? false;
    final lastPunchType =
    prefs.getString(LocationTrackingService._lastPunchTypeKey);

    if (!isTracking || lastPunchType != 'IN') return;

    final userId = prefs.getInt(LocationTrackingService._userIdKey);
    if (userId == null) return;

    final serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) return;

    final permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied ||
        permission == LocationPermission.deniedForever) return;

    final position = await Geolocator.getCurrentPosition(
      desiredAccuracy: LocationAccuracy.medium,
      timeLimit: const Duration(seconds: 15),
    );

    await _backgroundSendLocationWithPosition(service, position);
  } catch (e) {
    if (kDebugMode) debugPrint('❌ Background location error: $e');
  }
}

/// iOS background handler
@pragma('vm:entry-point')
Future<bool> _onIosBackground(ServiceInstance service) async {
  DartPluginRegistrant.ensureInitialized();
  await _backgroundSendLocation(service);
  return true;
}