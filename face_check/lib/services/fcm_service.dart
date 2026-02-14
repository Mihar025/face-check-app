import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'ApiService.dart';

class FcmService {
  static FcmService? _instance;
  static FcmService get instance => _instance!;

  final FirebaseMessaging _fcm = FirebaseMessaging.instance;
  final FlutterLocalNotificationsPlugin _localNotifications =
  FlutterLocalNotificationsPlugin();

  FcmService._();

  static Future<void> initialize() async {
    _instance = FcmService._();
    await _instance!._init();
  }

  Future<void> _init() async {
    // 1) permission
    final settings = await _fcm.requestPermission(alert: true, badge: true, sound: true);
    print('FCM permission: ${settings.authorizationStatus}');

    // 2) listeners (без отправки на сервер тут!)
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      _showLocalNotification(message);
    });

    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      print('Notification tap: ${message.data}');
    });

    // 3) token refresh listener — ВАЖНО: отправляем только если есть auth
    _fcm.onTokenRefresh.listen((newToken) async {
      print('FCM Token refreshed: $newToken');
      await syncTokenToServer(); // внутри проверишь auth
    });

    // 4) create local channel
    const androidChannel = AndroidNotificationChannel(
      'facecheck_push',
      'FaceCheck Notifications',
      description: 'Push notifications from FaceCheck',
      importance: Importance.high,
    );

    await _localNotifications
        .resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(androidChannel);

    await _localNotifications.initialize(
      const InitializationSettings(
        android: AndroidInitializationSettings('@mipmap/ic_launcher'),
        iOS: DarwinInitializationSettings(),
      ),
    );
  }


  Future<void> syncTokenToServer() async {
    final jwt = await ApiService.instance.getAuthToken();
    if (jwt == null || jwt.isEmpty) {
      print('FCM sync skipped: no auth token yet');
      return;
    }

    final token = await _fcm.getToken();
    if (token == null || token.isEmpty) return;

    try {
      await ApiService.instance.saveFcmToken(token: token);
      print('✅ FCM token synced to server');
    } catch (e) {
      print('❌ FCM sync error: $e');
    }
  }

  void _showLocalNotification(RemoteMessage message) {
    final notification = message.notification;
    if (notification == null) return;

    _localNotifications.show(
      notification.hashCode,
      notification.title,
      notification.body,
      const NotificationDetails(
        android: AndroidNotificationDetails(
          'facecheck_push',
          'FaceCheck Notifications',
          importance: Importance.high,
          priority: Priority.high,
          icon: '@mipmap/ic_launcher',
        ),
        iOS: DarwinNotificationDetails(
          presentAlert: true,
          presentBadge: true,
          presentSound: true,
        ),
      ),
    );
  }
}