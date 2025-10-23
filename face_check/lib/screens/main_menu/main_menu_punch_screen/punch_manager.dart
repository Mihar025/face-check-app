import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../services/ApiService.dart';

/// Единый менеджер для управления Punch In/Out на ВСЕХ экранах
/// Использовать можно и в MainMenuPunchScreen и в drawer PunchScreen
class PunchManager {
  // Кеширование
  static const String _cacheKey = 'punch_in_status';
  static const String _cacheTimeKey = 'punch_in_status_time';
  static const Duration _cacheDuration = Duration(minutes: 1);

  // ValueNotifiers для реактивности
  final ValueNotifier<bool> hasPunchIn = ValueNotifier<bool>(false);
  final ValueNotifier<bool> isLoading = ValueNotifier<bool>(false);

  static final PunchManager _instance = PunchManager._internal();
  factory PunchManager() => _instance;
  PunchManager._internal();

  int? _currentUserId;

  /// Установить текущий User ID
  void setUserId(int userId) {
    _currentUserId = userId;
    print('👤 PunchManager: User ID set to $userId');
  }

  /// Проверить статус Punch In (с кешированием на 10 минут)
  Future<void> checkPunchStatus({bool forceRefresh = false}) async {
    if (_currentUserId == null) {
      print('⚠️ PunchManager: Cannot check - userId is null');
      hasPunchIn.value = false;
      return;
    }

    print('🔍 PunchManager: Checking punch status for user $_currentUserId');

    // Пробуем получить из кеша (если не forceRefresh)
    if (!forceRefresh) {
      final cachedStatus = await _getCachedStatus();
      if (cachedStatus != null) {
        hasPunchIn.value = cachedStatus;
        print('✅ PunchManager: Using cached status = $cachedStatus');
        return;
      }
    }

    // Запрашиваем с сервера
    print('🌐 PunchManager: Fetching from server...');
    try {
      final status = await ApiService.instance.hasPunchIn(workerId: _currentUserId!);

      hasPunchIn.value = status;
      await _saveToCache(status);

      print('✅ PunchManager: Status from server = $status');
    } catch (e) {
      print('❌ PunchManager: Error checking status: $e');

      // При ошибке пробуем взять старый кеш
      final prefs = await SharedPreferences.getInstance();
      final cachedValue = prefs.getBool(_cacheKey);
      if (cachedValue != null) {
        hasPunchIn.value = cachedValue;
        print('⚠️ PunchManager: Using stale cache due to error');
      } else {
        hasPunchIn.value = false;
      }
    }
  }

  /// Обновить кеш после успешного Punch In
  Future<void> onPunchInSuccess() async {
    hasPunchIn.value = true;
    await _saveToCache(true);
    print('✅ PunchManager: Punch In success, cache updated');
  }

  /// Обновить кеш после успешного Punch Out
  Future<void> onPunchOutSuccess() async {
    hasPunchIn.value = false;
    await _saveToCache(false);
    print('✅ PunchManager: Punch Out success, cache updated');
  }

  /// Принудительное обновление (игнорируя кеш)
  Future<void> forceRefresh() async {
    print('🔄 PunchManager: Force refresh');
    await checkPunchStatus(forceRefresh: true);
  }

  /// Очистить весь кеш
  Future<void> clearCache() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_cacheKey);
    await prefs.remove(_cacheTimeKey);
    print('🗑️ PunchManager: Cache cleared');
  }

  // ========== PRIVATE МЕТОДЫ ==========

  /// Получить статус из кеша (если не устарел)
  Future<bool?> _getCachedStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      final cachedValue = prefs.getBool(_cacheKey);
      final cachedTimeMs = prefs.getInt(_cacheTimeKey);

      if (cachedValue == null || cachedTimeMs == null) {
        print('📦 PunchManager: No cache found');
        return null;
      }

      final cachedTime = DateTime.fromMillisecondsSinceEpoch(cachedTimeMs);
      final now = DateTime.now();
      final difference = now.difference(cachedTime);

      if (difference < _cacheDuration) {
        final remaining = _cacheDuration - difference;
        print('📦 PunchManager: Cache valid, expires in ${remaining.inMinutes}m ${remaining.inSeconds % 60}s');
        return cachedValue;
      } else {
        print('⏰ PunchManager: Cache expired (${difference.inMinutes}m old)');
        return null;
      }
    } catch (e) {
      print('❌ PunchManager: Error reading cache: $e');
      return null;
    }
  }

  /// Сохранить статус в кеш
  Future<void> _saveToCache(bool status) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final now = DateTime.now().millisecondsSinceEpoch;

      await prefs.setBool(_cacheKey, status);
      await prefs.setInt(_cacheTimeKey, now);

      print('💾 PunchManager: Saved to cache = $status (10min)');
    } catch (e) {
      print('❌ PunchManager: Error saving cache: $e');
    }
  }

  /// Очистка ресурсов
  void dispose() {
    hasPunchIn.dispose();
    isLoading.dispose();
  }
}