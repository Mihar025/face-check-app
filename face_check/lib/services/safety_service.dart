import 'dart:async';

import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:geolocator/geolocator.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SafetyService {
  static Timer? _timer;
  static bool _running = false;

  static const double jobZoneRadius = 150; // метров

  static final FlutterLocalNotificationsPlugin _notifications =
  FlutterLocalNotificationsPlugin();

  /// Инициализация уведомлений
  static Future<void> init() async {
    const android = AndroidInitializationSettings('@mipmap/ic_launcher');
    const ios = DarwinInitializationSettings();

    await _notifications.initialize(
      const InitializationSettings(
        android: android,
        iOS: ios,
      ),
    );
  }

  /// Запуск мониторинга
  static Future<void> start() async {
    if (_running) return;
    _running = true;

    final allowed = await _ensureLocationPermission();
    if (!allowed) {
      _running = false;
      return;
    }

    _timer = Timer.periodic(const Duration(minutes: 2), (_) async {
      await _checkPosition();
    });
  }

  /// Остановка мониторинга
  static void stop() {
    _running = false;
    _timer?.cancel();
    _timer = null;
  }

  /// Проверяем координаты
  static Future<void> _checkPosition() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      final storedLat = prefs.getDouble('job_site_lat');
      final storedLng = prefs.getDouble('job_site_lng');

      if (storedLat == null || storedLng == null) return;

      final pos = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );

      final distance = Geolocator.distanceBetween(
        storedLat,
        storedLng,
        pos.latitude,
        pos.longitude,
      );

      if (distance > jobZoneRadius) {
        await _notify();
      }
    } catch (_) {}
  }

  /// Уведомление пользователю
  static Future<void> _notify() async {
    const android = AndroidNotificationDetails(
      'safety_zone',
      'Safety Zone Alerts',
      importance: Importance.max,
      priority: Priority.high,
    );

    const ios = DarwinNotificationDetails();

    const details = NotificationDetails(
      android: android,
      iOS: ios,
    );

    await _notifications.show(
      1002,
      'Safety Zone Alert',
      'You are leaving the job site area.',
      details,
    );
  }

  /// Проверяем разрешения на локацию
  static Future<bool> _ensureLocationPermission() async {
    final enabled = await Geolocator.isLocationServiceEnabled();
    if (!enabled) return false;

    var permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
    }

    return permission == LocationPermission.always ||
        permission == LocationPermission.whileInUse;
  }
}
