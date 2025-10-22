import 'package:shared_preferences/shared_preferences.dart';
import '../../../services/ApiService.dart';

class PunchStatusChecker {
  static const String _cacheKey = 'punch_in_status';
  static const String _cacheTimeKey = 'punch_in_status_time';
  static const Duration _cacheDuration = Duration(minutes: 10);

  static Future<bool> checkPunchInStatus({
    required int workerId,
    bool forceRefresh = false,
  }) async {
    print('🔍 Checking punch in status for worker: $workerId');

    if (!forceRefresh) {
      final cachedStatus = await _getCachedStatus();
      if (cachedStatus != null) {
        print('✅ Using cached punch in status: $cachedStatus');
        return cachedStatus;
      }
    }

    print('🌐 Fetching punch in status from server...');
    try {
      final hasPunchIn = await ApiService.instance.hasPunchIn(workerId: workerId);

      await _saveToCache(hasPunchIn);

      print('✅ Punch in status from server: $hasPunchIn');
      return hasPunchIn;

    } catch (e) {
      print('❌ Error checking punch in status: $e');

      final prefs = await SharedPreferences.getInstance();
      final cachedValue = prefs.getBool(_cacheKey);
      if (cachedValue != null) {
        print('⚠️ Returning stale cached value due to error: $cachedValue');
        return cachedValue;
      }

      return false;
    }
  }

  static Future<bool?> _getCachedStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      final cachedValue = prefs.getBool(_cacheKey);
      final cachedTimeMs = prefs.getInt(_cacheTimeKey);

      if (cachedValue == null || cachedTimeMs == null) {
        print('📦 No cached status found');
        return null;
      }

      final cachedTime = DateTime.fromMillisecondsSinceEpoch(cachedTimeMs);
      final now = DateTime.now();
      final difference = now.difference(cachedTime);

      if (difference < _cacheDuration) {
        final remaining = _cacheDuration - difference;
        print('📦 Cache is valid. Expires in: ${remaining.inMinutes}m ${remaining.inSeconds % 60}s');
        return cachedValue;
      } else {
        print('⏰ Cache expired (age: ${difference.inMinutes}m)');
        return null;
      }

    } catch (e) {
      print('❌ Error reading cache: $e');
      return null;
    }
  }

  static Future<void> _saveToCache(bool status) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final now = DateTime.now().millisecondsSinceEpoch;

      await prefs.setBool(_cacheKey, status);
      await prefs.setInt(_cacheTimeKey, now);

      print('💾 Saved to cache: $status (expires in 10 minutes)');
    } catch (e) {
      print('❌ Error saving to cache: $e');
    }
  }

  static Future<void> clearCache() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(_cacheKey);
      await prefs.remove(_cacheTimeKey);
      print('🗑️ Cache cleared');
    } catch (e) {
      print('❌ Error clearing cache: $e');
    }
  }

  /// Обновить кеш после Punch In
  static Future<void> updateCacheAfterPunchIn() async {
    await _saveToCache(true);
    print('✅ Cache updated: Punch In = true');
  }

  /// Обновить кеш после Punch Out
  static Future<void> updateCacheAfterPunchOut() async {
    await _saveToCache(false);
    print('✅ Cache updated: Punch Out = false');
  }
}