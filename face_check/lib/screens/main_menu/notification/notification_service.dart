import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;
import '../../../localization/app_localizations.dart';

class NotificationService {
  AppLocalizations l10n;
  static NotificationService? _instance;
  static NotificationService get instance => _instance!;

  NotificationService._(this.l10n) {
    _instance = this;
  }

  static Future<NotificationService> initialize(BuildContext context) async {
    final languageCode = Localizations.localeOf(context).languageCode;
    print('Initializing notifications for language: $languageCode'); // Для отладки

    final l10n = AppLocalizations(languageCode);
    final instance = NotificationService._(l10n);
    await instance.init();
    return instance;
  }

  final _notifications = FlutterLocalNotificationsPlugin();

  Future<void> init() async {
    tz.initializeTimeZones();

    const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');
    const darwinSettings = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );

    await _notifications.initialize(
        InitializationSettings(android: androidSettings, iOS: darwinSettings)
    );
  }

  // Метод для обновления языка уведомлений
  Future<void> updateLanguage(String languageCode) async {
    print('Updating notifications language to: $languageCode'); // Для отладки
    l10n = AppLocalizations(languageCode);
    await _notifications.cancelAll(); // Отменяем старые уведомления
    await scheduleWeeklyNotifications(); // Создаем новые с новым языком
  }

  Future<void> scheduleWeeklyNotifications() async {
    print('Scheduling notifications in language: ${l10n.languageCode}'); // Для отладки
    print('PunchIn title will be: ${l10n.get('dailyPunchIn.title')}'); // Для отладки

    // Уведомление о начале рабочего дня
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
      uiLocalNotificationDateInterpretation: UILocalNotificationDateInterpretation.absoluteTime,
      androidAllowWhileIdle: true,
      matchDateTimeComponents: DateTimeComponents.time,
    );

    // Уведомление об окончании рабочего дня
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
      uiLocalNotificationDateInterpretation: UILocalNotificationDateInterpretation.absoluteTime,
      androidAllowWhileIdle: true,
      matchDateTimeComponents: DateTimeComponents.time,
    );

    // Еженедельное уведомление о проверке часов
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
      uiLocalNotificationDateInterpretation: UILocalNotificationDateInterpretation.absoluteTime,
      androidAllowWhileIdle: true,
      matchDateTimeComponents: DateTimeComponents.dayOfWeekAndTime,
    );
  }

  // Эти методы не требуют адаптации, так как не связаны с UI
  tz.TZDateTime _nextInstanceOfWeekday(int hour, int minute) {
    final now = tz.TZDateTime.now(tz.local);
    var scheduledDate = tz.TZDateTime(
      tz.local,
      now.year,
      now.month,
      now.day,
      hour,
      minute,
    );

    if (scheduledDate.weekday == DateTime.saturday) {
      scheduledDate = scheduledDate.add(const Duration(days: 2));
    } else if (scheduledDate.weekday == DateTime.sunday) {
      scheduledDate = scheduledDate.add(const Duration(days: 1));
    }

    if (scheduledDate.isBefore(now)) {
      scheduledDate = scheduledDate.add(const Duration(days: 1));
      if (scheduledDate.weekday == DateTime.saturday) {
        scheduledDate = scheduledDate.add(const Duration(days: 2));
      } else if (scheduledDate.weekday == DateTime.sunday) {
        scheduledDate = scheduledDate.add(const Duration(days: 1));
      }
    }

    return scheduledDate;
  }

  tz.TZDateTime _nextInstanceOfFriday(int hour, int minute) {
    final now = tz.TZDateTime.now(tz.local);
    var scheduledDate = tz.TZDateTime(
      tz.local,
      now.year,
      now.month,
      now.day,
      hour,
      minute,
    );

    while (scheduledDate.weekday != DateTime.friday) {
      scheduledDate = scheduledDate.add(const Duration(days: 1));
    }

    if (scheduledDate.isBefore(now)) {
      scheduledDate = scheduledDate.add(const Duration(days: 7));
    }

    return scheduledDate;
  }
}