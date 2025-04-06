import 'package:face_check/screens/main_menu/drawer/punch_screen/punch_screen.dart';
import 'package:face_check/screens/main_menu/drawer/settings_screen/settings_screen.dart';
import 'package:face_check/services/ApiService.dart';
import 'package:face_check/screens/loginScreen/forgot_password_screen.dart';
import 'package:face_check/screens/loginScreen/login_screen.dart';
import 'package:face_check/screens/main_menu/drawer/employee/employee_screen.dart';
import 'package:face_check/screens/main_menu/drawer/finance_controller/finance_screen.dart';
import 'package:face_check/screens/main_menu/drawer/profile_screen/widgets/profile_screen.dart';
import 'package:face_check/screens/main_menu/main_menu_punch_screen/MainMenuPunchScreen.dart';
import 'package:face_check/screens/main_menu/main_menu_screen/main_menu_screen.dart';
import 'package:face_check/screens/main_menu/notification/notification_screen.dart';
import 'package:face_check/screens/main_menu/notification/notification_service.dart';
import 'package:face_check/screens/theme/theme_provider.dart';
import 'package:face_check/providers/localization_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final prefs = await SharedPreferences.getInstance();

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_) => ThemeProvider(prefs),
        ),
        ChangeNotifierProvider(
          create: (_) => LocalizationProvider(),
        ),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool _notificationsInitialized = false;

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
          home: Builder(
            builder: (context) {
              if (!_notificationsInitialized) {
                _notificationsInitialized = true;
                // Инициализируем уведомления после того как локализация стала доступна
                WidgetsBinding.instance.addPostFrameCallback((_) async {
                  try {
                    await NotificationService.initialize(context);
                    await NotificationService.instance.scheduleWeeklyNotifications();
                  } catch (e) {
                    print('Ошибка инициализации уведомлений: $e');
                  }
                });
              }
              return LoginScreen(
                authApi: ApiService.instance.authenticationApi,
              );
            },
          ),
          routes: {
            '/main': (context) => MainMenuScreen(
              authenticationApi: ApiService.instance.authenticationApi,
            ),
            '/notifications': (context) => const NotificationScreen(),
            '/profile': (context) => const ProfileScreen(),
            '/settings': (context) => const SettingsScreen(),
            '/drawer/punch': (context) => const PunchScreen(),
            '/punch': (context) => const Mainmenupunchscreen(),
            '/finance': (context) => const FinanceScreen(),
            'forgot-password':(context) => ForgotPasswordScreen(),
            '/employee': (context) => EmployeeScreen(
              dio: ApiService.instance.dio,
            ),
          },
        );
      },
    );
  }
}