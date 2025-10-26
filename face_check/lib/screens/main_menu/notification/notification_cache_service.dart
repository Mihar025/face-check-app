import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../models/server_notification.dart';

class NotificationCacheService {
  static const String _cacheKey = 'cached_notifications';
  static const String _lastUpdateKey = 'notifications_last_update';
  static const Duration _cacheValidDuration = Duration(minutes: 5);

  // Сохраняем уведомления в кеш
  static Future<void> saveNotifications(List<ServerNotification> notifications) async {
    try {
      final prefs = await SharedPreferences.getInstance();

      // Конвертируем в JSON
      final List<Map<String, dynamic>> jsonList = notifications
          .map((n) => {
        'message': n.message,
        'createdAt': n.createdAt.toIso8601String(),
      })
          .toList();

      // Сохраняем
      await prefs.setString(_cacheKey, jsonEncode(jsonList));
      await prefs.setInt(_lastUpdateKey, DateTime.now().millisecondsSinceEpoch);

      print('Cached ${notifications.length} notifications');
    } catch (e) {
      print('Error caching notifications: $e');
    }
  }

  // Загружаем уведомления из кеша
  static Future<List<ServerNotification>?> loadCachedNotifications() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      // Проверяем, не устарел ли кеш
      final lastUpdate = prefs.getInt(_lastUpdateKey);
      if (lastUpdate != null) {
        final lastUpdateTime = DateTime.fromMillisecondsSinceEpoch(lastUpdate);
        if (DateTime.now().difference(lastUpdateTime) > _cacheValidDuration) {
          print('Cache is outdated');
          return null;
        }
      }

      // Загружаем из кеша
      final String? jsonString = prefs.getString(_cacheKey);
      if (jsonString == null) return null;

      final List<dynamic> jsonList = jsonDecode(jsonString);
      final notifications = jsonList
          .map((json) => ServerNotification(
        message: json['message'],
        createdAt: DateTime.parse(json['createdAt']),
      ))
          .toList();

      print('Loaded ${notifications.length} notifications from cache');
      return notifications;
    } catch (e) {
      print('Error loading cached notifications: $e');
      return null;
    }
  }

  // Очищаем кеш
  static Future<void> clearCache() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(_cacheKey);
      await prefs.remove(_lastUpdateKey);
      print('Cache cleared');
    } catch (e) {
      print('Error clearing cache: $e');
    }
  }

  // Проверяем, есть ли кеш
  static Future<bool> hasCachedData() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      return prefs.containsKey(_cacheKey);
    } catch (e) {
      return false;
    }
  }
}