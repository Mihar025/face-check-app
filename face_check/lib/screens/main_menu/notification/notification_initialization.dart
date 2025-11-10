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
    // ждём пока построится первый фрейм, чтобы Localizations уже были
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      try {
        final locale = Localizations.localeOf(context).languageCode;

        final service = await NotificationService.initialize(
          context: context,
          languageCode: locale,
        );

        await service.scheduleWeeklyNotifications();
      } catch (e, st) {
        debugPrint('Ошибка инициализации: $e');
        debugPrint('$st');
      } finally {
        if (mounted) {
          setState(() {
            _initialized = true;
          });
        }
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    if (!_initialized) {
      final screenSize = MediaQuery.of(context).size;
      final isSmallScreen = screenSize.width < 360;

      return Scaffold(
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
      );
    }

    return widget.child;
  }
}
