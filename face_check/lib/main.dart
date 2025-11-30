import 'dart:io';

import 'package:face_check/services/safety_service.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:timezone/data/latest.dart' as tz;

import 'package:face_check/screens/loginScreen/login_screen.dart';
import 'package:face_check/screens/loginScreen/forgot_password_screen.dart';
import 'package:face_check/screens/main_menu/main_menu_screen/main_menu_screen.dart';
import 'package:face_check/screens/main_menu/notification/notification_screen.dart';
import 'package:face_check/screens/main_menu/drawer/profile_screen/widgets/profile_screen.dart';
import 'package:face_check/screens/main_menu/drawer/settings_screen/settings_screen.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/punch_screen.dart';
import 'package:face_check/screens/main_menu/main_menu_punch_screen/MainMenuPunchScreen.dart';
import 'package:face_check/screens/main_menu/drawer/finance_controller/finance_screen.dart';
import 'package:face_check/screens/main_menu/drawer/employee/employee_screen.dart';

import 'package:face_check/screens/theme/theme_provider.dart';
import 'package:face_check/providers/localization_provider.dart';

import 'package:face_check/services/ApiService.dart';
import 'package:face_check/screens/main_menu/notification/notification_service.dart';
import 'package:face_check/services/location_tracking_service.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const BootstrapApp());
}

/// Лёгкий «бутстрап» — сразу отдаём UI (Login),
/// а тяжёлое делаем после первого кадра.
class BootstrapApp extends StatefulWidget {
  const BootstrapApp({super.key});

  @override
  State<BootstrapApp> createState() => _BootstrapAppState();
}

class _BootstrapAppState extends State<BootstrapApp> {
  // Создаём ThemeProvider заранее, без prefs — подхватим позже.
  late final ThemeProvider _themeProvider = ThemeProvider(
    // стартовый выбор — по системной теме
    initialDark:
    WidgetsBinding.instance.platformDispatcher.platformBrightness == Brightness.dark,
  );

  @override
  void initState() {
    super.initState();
    const bool enableIOSBackgroundTracking = false;
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      // 1) таймзоны
      await Future(() => tz.initializeTimeZones());

      // 2) prefs
      final prefs = await SharedPreferences.getInstance();
      _themeProvider.attachPrefs(prefs);

      // 3) уведомления (язык берём из системного, т.к. здесь контекст выше MaterialApp)
      try {
        final lang = WidgetsBinding.instance.platformDispatcher.locale.languageCode;
        final service = await NotificationService.initialize(
          context: context,
          languageCode: lang,
        );
        await service.scheduleWeeklyNotifications();
      } catch (e) {
        debugPrint('Notification init error: $e');
      }

      // 4) фоновые службы (локация и т.д.)
      try {
        await SafetyService.init();

        if (Platform.isAndroid || enableIOSBackgroundTracking) {
          await LocationTrackingService.initializeBackgroundService();
        }
      } catch (e) {
        debugPrint('Location background init error: $e');
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        // ВАЖНО: .value, чтобы не пересоздавать инстанс после attachPrefs
        ChangeNotifierProvider.value(value: _themeProvider),
        ChangeNotifierProvider(create: (_) => LocalizationProvider()),
      ],
      // 👉 Здесь стоит твой MyApp
      child: const MyApp(),
    );
  }
}

/// Твой главный виджет приложения (MaterialApp)
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer2<ThemeProvider, LocalizationProvider>(
      builder: (context, themeProvider, localizationProvider, _) {
        return MaterialApp(
          debugShowCheckedModeBanner: false,
          theme: themeProvider.currentTheme,
          locale: Locale(localizationProvider.currentLanguage),
          supportedLocales: const [
            Locale('en'),
            Locale('es'),
            Locale('ru'),
          ],
          localizationsDelegates: const [
            GlobalMaterialLocalizations.delegate,
            GlobalWidgetsLocalizations.delegate,
            GlobalCupertinoLocalizations.delegate,
          ],
          // сразу показываем логин
          home: LoginScreen(
            authApi: ApiService.instance.authenticationApi,
          ),
          routes: {
            '/main': (context) =>
                MainMenuScreen(authenticationApi: ApiService.instance.authenticationApi),
            '/notifications': (context) => const NotificationScreen(),
            '/profile': (context) => const ProfileScreen(),
            '/settings': (context) => const SettingsScreen(),
            '/drawer/punch': (context) => const PunchScreen(),
            '/punch': (context) => const Mainmenupunchscreen(),
            '/finance': (context) => const FinanceScreen(),
            'forgot-password': (context) => ForgotPasswordScreen(),
            '/employee': (context) => EmployeeScreen(dio: ApiService.instance.dio),
          },
        );
      },
    );
  }
}
