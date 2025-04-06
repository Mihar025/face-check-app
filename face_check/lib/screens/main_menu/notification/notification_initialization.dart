import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'notification_service.dart';

class InitializationWidget extends StatefulWidget {
  final Widget child;

  const InitializationWidget({
    Key? key,
    required this.child,
  }) : super(key: key);

  @override
  State<InitializationWidget> createState() => _InitializationWidgetState();
}

class _InitializationWidgetState extends State<InitializationWidget> {
  bool _initialized = false;

  @override
  void initState() {
    super.initState();
    _initializeApp();
  }

  Future<void> _initializeApp() async {
    try {
      // Ждем один кадр, чтобы context был доступен
      WidgetsBinding.instance.addPostFrameCallback((_) async {
        // Инициализируем уведомления
        await NotificationService.initialize(context);
        await NotificationService.instance.scheduleWeeklyNotifications();

        if (mounted) {
          setState(() {
            _initialized = true;
          });
        }
      });
    } catch (e) {
      print('Ошибка инициализации: $e');
      // В случае ошибки все равно показываем приложение
      if (mounted) {
        setState(() {
          _initialized = true;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    // Получаем размеры экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    if (!_initialized) {
      return MaterialApp(
        home: Scaffold(
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                SizedBox(
                  width: isSmallScreen ? 30 : 40,
                  height: isSmallScreen ? 30 : 40,
                  child: const CircularProgressIndicator(),
                ),
                SizedBox(height: isSmallScreen ? 12 : 16),
                Text(
                  'Инициализация...',
                  style: TextStyle(
                    fontSize: isSmallScreen ? 14 : 16,
                    fontWeight: FontWeight.w400,
                  ),
                ),
              ],
            ),
          ),
        ),
      );
    }
    return widget.child;
  }
}