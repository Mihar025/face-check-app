import 'package:geolocator/geolocator.dart';
import 'package:flutter/foundation.dart';

class LocationService {
  Position? _lastKnownPosition;
  DateTime? _lastLocationTime;

  // сколько секунд считаем позицию свежей
  static const int _maxLocationAgeSeconds = 30;

  Future<Position?> getCurrentLocation() async {
    // 1. если есть свежий кэш — отдаём его
    if (_lastKnownPosition != null && _lastLocationTime != null) {
      final diff = DateTime.now().difference(_lastLocationTime!).inSeconds;
      if (diff < _maxLocationAgeSeconds) {
        return _lastKnownPosition;
      }
    }

    // 2. проверяем, включены ли сервисы
    bool serviceEnabled;
    try {
      serviceEnabled = await Geolocator.isLocationServiceEnabled();
    } catch (e) {
      debugPrint('⚠️ isLocationServiceEnabled error: $e');
      return _lastKnownPosition;
    }

    if (!serviceEnabled) {
      // сервисы выключены — возвращаем то, что было
      return _lastKnownPosition;
    }

    // 3. проверяем / запрашиваем разрешение
    LocationPermission permission;
    try {
      permission = await Geolocator.checkPermission();
    } catch (e) {
      debugPrint('⚠️ checkPermission error: $e');
      return _lastKnownPosition;
    }

    if (permission == LocationPermission.denied) {
      try {
        permission = await Geolocator.requestPermission();
      } catch (e) {
        debugPrint('⚠️ requestPermission error: $e');
        return _lastKnownPosition;
      }
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.deniedForever) {
        // пользователь так и не дал доступ
        return _lastKnownPosition;
      }
    } else if (permission == LocationPermission.deniedForever) {
      // навсегда запрещено — просто отдаём что есть
      return _lastKnownPosition;
    }

    // 4. пробуем сначала last known (он быстрый)
    try {
      final last = await Geolocator.getLastKnownPosition();
      if (last != null) {
        _lastKnownPosition = last;
        _lastLocationTime = DateTime.now();
      }
    } catch (e) {
      debugPrint('⚠️ getLastKnownPosition error: $e');
    }

    // 5. пробуем получить точную позицию (может занять время)
    try {
      final current = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: const Duration(seconds: 5),
      );
      _lastKnownPosition = current;
      _lastLocationTime = DateTime.now();
    } catch (e) {
      // тут не падаем — просто пишем в лог
      debugPrint('❌ Error getting precise location: $e');
    }

    return _lastKnownPosition;
  }

  /// Форсируем обновление — просто очищаем кэш и берём заново
  Future<Position?> forceLocationUpdate() async {
    _lastKnownPosition = null;
    _lastLocationTime = null;
    return getCurrentLocation();
  }
}
