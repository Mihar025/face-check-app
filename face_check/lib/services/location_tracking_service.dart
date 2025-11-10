import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:battery_plus/battery_plus.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart'; // kDebugMode
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

  // ================== INIT ==================
  static Future<void> initializeBackgroundService() async {
    try {
      // небольшая задержка, чтобы Flutter успел подняться
      await Future.delayed(const Duration(milliseconds: 500));

      await Workmanager().initialize(
        callbackDispatcher,
        // в релизе — тихо, в дебаге — с логами
        isInDebugMode: kDebugMode,
      );
      if (kDebugMode) {
        debugPrint('✅ Workmanager initialized successfully');
      }
    } catch (e, st) {
      if (kDebugMode) {
        debugPrint('❌ Workmanager init error: $e');
        debugPrint('$st');
      }
    }
  }

  // ================== PERMISSIONS (foreground) ==================
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
      if (permission == LocationPermission.denied) {
        return false;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      // в реальном UI ты покажешь экран "включите в настройках"
      await openAppSettings();
      return false;
    }

    if (Platform.isAndroid) {
      final backgroundStatus = await Permission.locationAlways.status;
      if (!backgroundStatus.isGranted) {
        final res = await Permission.locationAlways.request();
        if (!res.isGranted) {
          if (kDebugMode) {
            debugPrint('⚠️ Background location not granted, will track only in foreground');
          }
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
  Future<void> startTracking(int userId) async {
    final prefs = await SharedPreferences.getInstance();

    await prefs.setBool(_trackingStatusKey, true);
    await prefs.setString(_lastPunchTypeKey, 'IN');
    await prefs.setInt(_userIdKey, userId);

    final hasPermission = await checkAndRequestPermissions();
    if (!hasPermission) {
      if (kDebugMode) debugPrint('⚠️ startTracking: permissions not granted');
      return;
    }

    // отправим сразу
    await _sendCurrentLocationSafe(userId);

    // запустим таймер, пока приложение в фореграунде
    _startForegroundTracking(userId);

    // в проде регистрируем задачу только когда реально начали трекинг
    await _registerBackgroundTask();

    if (kDebugMode) {
      debugPrint('✅ Location tracking started for user: $userId');
    }
  }

  Future<void> stopTracking() async {
    final prefs = await SharedPreferences.getInstance();

    final userId = prefs.getInt(_userIdKey);
    if (userId != null) {
      await _sendCurrentLocationSafe(userId);
    }

    try {
      await Workmanager().cancelAll();
    } catch (e) {
      if (kDebugMode) debugPrint('⚠️ cancelAll error: $e');
    }

    await prefs.setBool(_trackingStatusKey, false);
    await prefs.setString(_lastPunchTypeKey, 'OUT');
    await prefs.remove(_userIdKey);

    _locationTimer?.cancel();
    _positionStream?.cancel();

    if (kDebugMode) {
      debugPrint('🛑 Location tracking stopped');
    }
  }

  Future<bool> isTrackingActive() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_trackingStatusKey) ?? false;
  }

  // ================== FOREGROUND LOOP ==================
  void _startForegroundTracking(int userId) {
    _locationTimer?.cancel();
    _positionStream?.cancel();

    void scheduleNext() {
      _locationTimer = Timer(_updateInterval, () async {
        final prefs = await SharedPreferences.getInstance();
        final isActive = prefs.getBool(_trackingStatusKey) ?? false;
        if (isActive) {
          await _sendCurrentLocationSafe(userId);
          if (kDebugMode) {
            debugPrint('📍 Foreground location sent at ${DateTime.now()}');
          }
          scheduleNext();
        } else {
          if (kDebugMode) {
            debugPrint('⏹️ Foreground timer stopped (tracking= false)');
          }
        }
      });
    }

    scheduleNext();
  }

  // ================== BACKGROUND TASK ==================
  Future<void> _registerBackgroundTask() async {
    try {
      await Workmanager().registerPeriodicTask(
        _backgroundTaskName,
        _backgroundTaskName,
        frequency: _updateInterval,
        constraints:  Constraints(
          networkType: NetworkType.connected,
        ),
        backoffPolicy: BackoffPolicy.exponential,
        backoffPolicyDelay: const Duration(seconds: 10),
      );
    } catch (e) {
      if (kDebugMode) debugPrint('❌ registerPeriodicTask error: $e');
    }
  }

  // ================== LOCATION SENDING ==================
  Future<Position?> _getSafePosition({Duration timeout = const Duration(seconds: 10)}) async {
    try {
      final serviceEnabled = await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        if (kDebugMode) debugPrint('⚠️ Location service disabled');
        return null;
      }

      final permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.deniedForever) {
        if (kDebugMode) debugPrint('⚠️ Location permission not granted (background safe)');
        return null;
      }

      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: timeout,
      );
      return position;
    } catch (e) {
      if (kDebugMode) debugPrint('❌ _getSafePosition error: $e');
      return null;
    }
  }

  Future<void> _sendCurrentLocationSafe(int userId) async {
    final position = await _getSafePosition();
    if (position == null) {
      if (kDebugMode) debugPrint('⚠️ No position available to send');
      return;
    }
    await _sendLocationToServer(position, userId);
  }

  Future<void> _sendLocationToServer(Position position, int userId) async {
    try {
      final batteryLevel = await _battery.batteryLevel;

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
          debugPrint('✅ Location sent: ${position.latitude}, ${position.longitude}');
        }
        await _saveLocationLocally(locationDto, userId);
      } else {
        if (kDebugMode) {
          debugPrint('⚠️ Server responded with ${response.statusCode}');
        }
      }
    } catch (e) {
      if (kDebugMode) debugPrint('❌ Error sending location to server: $e');
    }
  }

  // ================== OFFLINE SAVE ==================
  Future<void> _saveLocationLocally(LocationUpdateDto location, int userId) async {
    final prefs = await SharedPreferences.getInstance();

    final locationsJson = prefs.getString('offline_locations') ?? '[]';
    final locations = jsonDecode(locationsJson) as List;

    locations.add({
      'userId': userId,
      'location': location.toJson(),
      'synced': false,
    });

    if (locations.length > 100) {
      locations.removeRange(0, locations.length - 100);
    }

    await prefs.setString('offline_locations', jsonEncode(locations));
  }

  Future<void> syncOfflineLocations() async {
    final prefs = await SharedPreferences.getInstance();
    final locationsJson = prefs.getString('offline_locations') ?? '[]';
    final locations = jsonDecode(locationsJson) as List;

    final unsynced = locations.where((l) => l['synced'] == false).toList();

    for (final l in unsynced) {
      try {
        await _dio.post(
          'location/update/${l['userId']}',
          data: l['location'],
        );
        l['synced'] = true;
      } catch (e) {
        if (kDebugMode) debugPrint('⚠️ Failed to sync offline location: $e');
      }
    }

    await prefs.setString('offline_locations', jsonEncode(locations));
  }
}

// ================== WORKMANAGER CALLBACK ==================

@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    if (task == LocationTrackingService._backgroundTaskName) {
      final prefs = await SharedPreferences.getInstance();
      final isTracking = prefs.getBool(LocationTrackingService._trackingStatusKey) ?? false;
      final lastPunchType = prefs.getString(LocationTrackingService._lastPunchTypeKey);

      if (!isTracking || lastPunchType != 'IN') {
        if (kDebugMode) debugPrint('🛑 Background: tracking is off');
        return true;
      }

      final userId = prefs.getInt(LocationTrackingService._userIdKey);
      if (userId == null) {
        if (kDebugMode) debugPrint('❌ Background: no userId');
        return true;
      }

      try {
        final dio = Dio(
          BaseOptions(
            baseUrl: 'https://face-check-prod-drgsy.ondigitalocean.app/api/v1/',
            connectTimeout: const Duration(seconds: 5),
            receiveTimeout: const Duration(seconds: 30),
          ),
        );

        final token = prefs.getString('auth_token');
        if (token != null) {
          dio.options.headers['Authorization'] = 'Bearer $token';
        }

        final service = LocationTrackingService(dio);
        await service._sendCurrentLocationSafe(userId);

        if (kDebugMode) {
          debugPrint('✅ Background location sent at ${DateTime.now()}');
        }
      } catch (e) {
        if (kDebugMode) debugPrint('❌ Background location error: $e');
      }
    }

    return true;
  });
}
