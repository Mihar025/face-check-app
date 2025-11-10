import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;
import '../../../localization/app_localizations.dart';

class NotificationService {
  late AppLocalizations l10n;
  static NotificationService? _instance;
  static NotificationService get instance => _instance!;

  final FlutterLocalNotificationsPlugin _notifications =
  FlutterLocalNotificationsPlugin();

  NotificationService._(this.l10n);

  /// Безопасная инициализация — можно вызвать из любого контекста под MaterialApp
  static Future<NotificationService> initialize({
    required BuildContext context,
    required String languageCode,
  }) async {
    try {
      print('🔔 Initializing NotificationService (lang: $languageCode)');
      final l10n = AppLocalizations(languageCode);
      final instance = NotificationService._(l10n);
      _instance = instance;
      await instance._init();
      return instance;
    } catch (e, st) {
      debugPrint('❌ NotificationService init error: $e');
      debugPrint('$st');
      rethrow;
    }
  }

  Future<void> _init() async {
    tz.initializeTimeZones();

    const androidSettings =
    AndroidInitializationSettings('@mipmap/ic_launcher');
    const darwinSettings = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );

    await _notifications.initialize(
      const InitializationSettings(
        android: androidSettings,
        iOS: darwinSettings,
      ),
    );
    print('✅ Notification plugin initialized');
  }

  /// При смене языка
  Future<void> updateLanguage(String languageCode) async {
    print('🌍 Updating notification language to: $languageCode');
    l10n = AppLocalizations(languageCode);
    await _notifications.cancelAll();
    await scheduleWeeklyNotifications();
  }

  /// Планируем три уведомления (2 ежедневных и 1 еженедельное)
  Future<void> scheduleWeeklyNotifications() async {
    try {
      print('🕐 Scheduling notifications for ${l10n.languageCode}');
      print('→ PunchIn: ${l10n.get('dailyPunchIn.title')}');

      // Punch In
      await _notifications.zonedSchedule(
        1,
        l10n.get('dailyPunchIn.title'),
        l10n.get('dailyPunchIn.body'),
        _nextInstanceOfWeekday(7, 10),
        NotificationDetails(
          android: AndroidNotificationDetails(
            'daily_notifications',
            'Daily Notifications',
            channelDescription: 'Daily notification channel',
            importance: Importance.high,
            priority: Priority.high,
          ),
          iOS: const DarwinNotificationDetails(
            presentAlert: true,
            presentBadge: true,
            presentSound: true,
          ),
        ),
        uiLocalNotificationDateInterpretation:
        UILocalNotificationDateInterpretation.absoluteTime,
        androidAllowWhileIdle: true,
        matchDateTimeComponents: DateTimeComponents.time,
      );

      // Punch Out
      await _notifications.zonedSchedule(
        2,
        l10n.get('dailyPunchOut.title'),
        l10n.get('dailyPunchOut.body'),
        _nextInstanceOfWeekday(12, 0),
        NotificationDetails(
          android: AndroidNotificationDetails(
            'daily_notifications',
            'Daily Notifications',
            channelDescription: 'Daily notification channel',
            importance: Importance.high,
            priority: Priority.high,
          ),
          iOS: const DarwinNotificationDetails(
            presentAlert: true,
            presentBadge: true,
            presentSound: true,
          ),
        ),
        uiLocalNotificationDateInterpretation:
        UILocalNotificationDateInterpretation.absoluteTime,
        androidAllowWhileIdle: true,
        matchDateTimeComponents: DateTimeComponents.time,
      );

      // Weekly Hours Check
      await _notifications.zonedSchedule(
        3,
        l10n.get('weeklyHoursCheck.title'),
        l10n.get('weeklyHoursCheck.body'),
        _nextInstanceOfFriday(15, 0),
        NotificationDetails(
          android: AndroidNotificationDetails(
            'weekly_notifications',
            'Weekly Notifications',
            channelDescription: 'Weekly notification channel',
            importance: Importance.high,
            priority: Priority.high,
          ),
          iOS: const DarwinNotificationDetails(
            presentAlert: true,
            presentBadge: true,
            presentSound: true,
          ),
        ),
        uiLocalNotificationDateInterpretation:
        UILocalNotificationDateInterpretation.absoluteTime,
        androidAllowWhileIdle: true,
        matchDateTimeComponents: DateTimeComponents.dayOfWeekAndTime,
      );

      print('✅ Notifications scheduled successfully');
    } catch (e, st) {
      debugPrint('❌ Failed to schedule notifications: $e');
      debugPrint('$st');
    }
  }

  /// Следующий будний день в нужное время
  tz.TZDateTime _nextInstanceOfWeekday(int hour, int minute) {
    final now = tz.TZDateTime.now(tz.local);
    var date = tz.TZDateTime(tz.local, now.year, now.month, now.day, hour, minute);

    if (date.weekday >= DateTime.saturday) {
      date = date.add(Duration(days: 8 - date.weekday));
    } else if (date.isBefore(now)) {
      date = date.add(const Duration(days: 1));
    }

    return date;
  }

  /// Следующая пятница
  tz.TZDateTime _nextInstanceOfFriday(int hour, int minute) {
    final now = tz.TZDateTime.now(tz.local);
    var date = tz.TZDateTime(tz.local, now.year, now.month, now.day, hour, minute);

    while (date.weekday != DateTime.friday) {
      date = date.add(const Duration(days: 1));
    }

    if (date.isBefore(now)) {
      date = date.add(const Duration(days: 7));
    }

    return date;
  }
}
