import 'dart:async';
import 'package:face_check/screens/main_menu/view-details/view_details_screen.dart';
import 'package:face_check/widgets/weather_widget.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';

import '../../../api_client/api/authentication_api.dart';
import '../../../providers/localization_provider.dart';
import '../../../utils/date_time_formatter.dart';
import '../../../services/ApiService.dart';
import '../components/custom_drawer.dart';
import '../components/face_check_button.dart';
import '../components/time_circle.dart';

import 'package:timezone/data/latest.dart' as tzdata;
import 'package:timezone/timezone.dart' as tz;

import 'package:dio/dio.dart';
import 'package:face_check/services/time_service.dart';

class MainMenuScreen extends StatefulWidget {
  final AuthenticationApi authenticationApi;

  const MainMenuScreen({
    super.key,
    required this.authenticationApi
  });

  @override
  State<MainMenuScreen> createState() => _MainMenuScreenState();
}

class CustomAppBar extends StatefulWidget implements PreferredSizeWidget {
  final String currentDate;
  final String currentTime;
  final VoidCallback onMenuPressed;

  const CustomAppBar({
    super.key,
    required this.currentDate,
    required this.currentTime,
    required this.onMenuPressed,
  });

  @override
  Size get preferredSize => const Size.fromHeight(80);

  @override
  State<CustomAppBar> createState() => _CustomAppBarState();
}

class _CustomAppBarState extends State<CustomAppBar> {
  // ValueNotifier для оптимизации
  late final ValueNotifier<int> _notificationCount;

  @override
  void initState() {
    super.initState();
    _notificationCount = ValueNotifier<int>(0);
    _loadNotifications();
  }

  @override
  void dispose() {
    _notificationCount.dispose();
    super.dispose();
  }

  Future<void> _loadNotifications() async {
    final notifications = await FlutterLocalNotificationsPlugin()
        .pendingNotificationRequests();
    _notificationCount.value = notifications.length;
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      decoration: BoxDecoration(
        color: theme.scaffoldBackgroundColor,
        boxShadow: [
          BoxShadow(
            color: isDark
                ? Colors.black.withOpacity(0.3)
                : Colors.grey.withOpacity(0.1),
            blurRadius: 10,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: SafeArea(
        child: Container(
          height: 80,
          padding: const EdgeInsets.symmetric(horizontal: 8),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Menu Button
              Container(
                margin: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: isDark
                      ? Colors.white.withOpacity(0.05)
                      : Colors.grey.withOpacity(0.08),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: IconButton(
                  icon: Icon(
                    Icons.menu_rounded,
                    color: theme.iconTheme.color,
                    size: 26,
                  ),
                  onPressed: widget.onMenuPressed,
                ),
              ),

              // Logo
              Container(
                height: 55,
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                decoration: BoxDecoration(
                  color: isDark
                      ? Colors.white.withOpacity(0.05)
                      : Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(
                    color: isDark
                        ? Colors.white.withOpacity(0.1)
                        : Colors.grey.withOpacity(0.2),
                    width: 1,
                  ),
                ),
                child: Image.asset(
                  'assets/images/logo.jpg',
                  fit: BoxFit.contain,
                ),
              ),

              // Notification Button с ValueListenableBuilder
              Container(
                margin: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: isDark
                      ? Colors.white.withOpacity(0.05)
                      : Colors.grey.withOpacity(0.08),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Stack(
                  children: [
                    IconButton(
                      icon: Icon(
                        Icons.notifications_outlined,
                        color: theme.iconTheme.color,
                        size: 26,
                      ),
                      onPressed: () => Navigator.pushNamed(context, '/notifications')
                          .then((_) => _loadNotifications()),
                    ),
                    ValueListenableBuilder<int>(
                      valueListenable: _notificationCount,
                      builder: (context, count, _) {
                        if (count == 0) return const SizedBox.shrink();

                        return Positioned(
                          right: 6,
                          top: 6,
                          child: Container(
                            padding: const EdgeInsets.all(4),
                            decoration: BoxDecoration(
                              color: Colors.red.shade500,
                              shape: BoxShape.circle,
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.red.withOpacity(0.3),
                                  blurRadius: 4,
                                  spreadRadius: 1,
                                ),
                              ],
                            ),
                            constraints: const BoxConstraints(
                              minWidth: 18,
                              minHeight: 18,
                            ),
                            child: Text(
                              count > 9 ? '9+' : count.toString(),
                              style: const TextStyle(
                                color: Colors.white,
                                fontSize: 10,
                                fontWeight: FontWeight.bold,
                              ),
                              textAlign: TextAlign.center,
                            ),
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _MainMenuScreenState extends State<MainMenuScreen> {
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

  // ValueNotifiers для оптимизации перерисовок
  late final ValueNotifier<String> _currentDate;
  late final ValueNotifier<String> _currentTime;
  late final ValueNotifier<String> _lastPunchDate;
  late final ValueNotifier<String> _lastPunchTime;
  late final ValueNotifier<double> _workedHours;
  late final ValueNotifier<String> _currentPeriod;

  late final TimeService _timeService;
  StreamSubscription<tz.TZDateTime>? _nySub;

  // Кэшированные значения
  late ThemeData _theme;
  late bool _isDark;

  // Константы для производительности
  static const EdgeInsets _standardPadding = EdgeInsets.symmetric(horizontal: 16);
  static const EdgeInsets _bottomPadding = EdgeInsets.symmetric(horizontal: 20, vertical: 16);

  @override
  void initState() {
    super.initState();
    tzdata.initializeTimeZones();

    SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);

    // Инициализация ValueNotifiers
    _currentDate = ValueNotifier<String>('');
    _currentTime = ValueNotifier<String>('');
    _lastPunchDate = ValueNotifier<String>('DD/MM/YYYY');
    _lastPunchTime = ValueNotifier<String>('--:--');
    _workedHours = ValueNotifier<double>(0.0);
    _currentPeriod = ValueNotifier<String>('');

    final dio = Dio(BaseOptions(
      baseUrl: 'http://192.168.1.194:8088/api/v1/',
      connectTimeout: const Duration(seconds: 5),
      receiveTimeout: const Duration(seconds: 30),
    ));

    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await ApiService.instance.getAuthToken();
        if (token != null && token.isNotEmpty) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
      onError: (e, handler) => handler.next(e),
    ));

    _timeService = TimeService(dio);
    _bootstrapTimeAndData();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateCachedValues();
  }

  void _updateCachedValues() {
    _theme = Theme.of(context);
    _isDark = _theme.brightness == Brightness.dark;
  }

  Future<void> _bootstrapTimeAndData() async {
    await _timeService.sync();

    _nySub = _timeService.nyTicker().listen((nyNow) {
      _currentDate.value = DateFormat('MMM dd').format(nyNow);
      _currentTime.value = DateFormat('HH:mm').format(nyNow);
      _updateCurrentPeriod(nyNow);
    });

    _loadLastPunchTime();
    _loadWorkedHours();
  }

  void _updateCurrentPeriod(tz.TZDateTime nyNow) {
    final startOfWeek = nyNow.subtract(Duration(days: nyNow.weekday % 7));
    final endOfWeek = startOfWeek.add(const Duration(days: 6));

    String fmt(tz.TZDateTime d) =>
        '${d.day.toString().padLeft(2, '0')}/${d.month.toString().padLeft(2, '0')}/${d.year}';

    _currentPeriod.value = '${fmt(startOfWeek)} - ${fmt(endOfWeek)}';
  }

  @override
  void dispose() {
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.manual,
        overlays: SystemUiOverlay.values);
    _nySub?.cancel();

    // Dispose ValueNotifiers
    _currentDate.dispose();
    _currentTime.dispose();
    _lastPunchDate.dispose();
    _lastPunchTime.dispose();
    _workedHours.dispose();
    _currentPeriod.dispose();

    super.dispose();
  }

  Future<void> _loadLastPunchTime() async {
    try {
      final punchInfo = await ApiService.instance.getLastPunchTime();
      _lastPunchDate.value = punchInfo.date;
      _lastPunchTime.value = punchInfo.time;
    } catch (e) {
      print('Error loading last punch time: $e');
    }
  }

  Future<void> _loadWorkedHours() async {
    try {
      final hours = await ApiService.instance.getTotalWorkedHoursPerWeek();
      _workedHours.value = hours;
    } catch (e) {
      print('Error loading worked hours: $e');
    }
  }

  void _navigateToPunch() {
    Navigator.pushNamed(context, '/punch');
  }

  @override
  Widget build(BuildContext context) {
    _updateCachedValues();
    final l10n = context.watch<LocalizationProvider>().localizations;

    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        systemNavigationBarColor: _theme.scaffoldBackgroundColor,
        statusBarIconBrightness: _isDark ? Brightness.light : Brightness.dark,
        systemNavigationBarIconBrightness: _isDark ? Brightness.light : Brightness.dark,
      ),
      child: Scaffold(
        key: _scaffoldKey,
        backgroundColor: _theme.scaffoldBackgroundColor,
        appBar: PreferredSize(
          preferredSize: const Size.fromHeight(80),
          child: ValueListenableBuilder<String>(
            valueListenable: _currentDate,
            builder: (context, date, _) {
              return ValueListenableBuilder<String>(
                valueListenable: _currentTime,
                builder: (context, time, _) {
                  return CustomAppBar(
                    currentDate: date,
                    currentTime: time,
                    onMenuPressed: () => _scaffoldKey.currentState?.openDrawer(),
                  );
                },
              );
            },
          ),
        ),
        drawer: const CustomDrawer(),
        body: Column(
          children: [
            // Info Bar
            Container(
              margin: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: _isDark
                    ? Colors.white.withOpacity(0.05)
                    : Colors.white,
                borderRadius: BorderRadius.circular(20),
                boxShadow: [
                  BoxShadow(
                    color: _isDark
                        ? Colors.black.withOpacity(0.2)
                        : Colors.grey.withOpacity(0.1),
                    blurRadius: 10,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(20),
                child: Container(
                  height: 70,
                  child: Row(
                    children: [
                      // Weather Section
                      Expanded(
                        child: Container(
                          padding: const EdgeInsets.symmetric(vertical: 8),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(
                                Icons.location_on_outlined,
                                size: 16,
                                color: _theme.textTheme.bodySmall?.color?.withOpacity(0.6),
                              ),
                              const SizedBox(height: 4),
                               WeatherWidget(),
                            ],
                          ),
                        ),
                      ),

                      // Vertical Divider
                      Container(
                        width: 1,
                        height: 40,
                        color: _isDark
                            ? Colors.white.withOpacity(0.1)
                            : Colors.grey.withOpacity(0.2),
                      ),

                      // Date Section с ValueListenableBuilder
                      Expanded(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.calendar_today_outlined,
                              size: 20,
                              color: _theme.textTheme.bodySmall?.color?.withOpacity(0.6),
                            ),
                            const SizedBox(height: 6),
                            ValueListenableBuilder<String>(
                              valueListenable: _currentDate,
                              builder: (context, date, _) {
                                return Text(
                                  date,
                                  style: GoogleFonts.poppins(
                                    fontSize: 16,
                                    fontWeight: FontWeight.w600,
                                    color: _theme.textTheme.bodyLarge?.color,
                                  ),
                                );
                              },
                            ),
                          ],
                        ),
                      ),

                      // Vertical Divider
                      Container(
                        width: 1,
                        height: 40,
                        color: _isDark
                            ? Colors.white.withOpacity(0.1)
                            : Colors.grey.withOpacity(0.2),
                      ),

                      // Time Section с ValueListenableBuilder
                      Expanded(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.access_time_rounded,
                              size: 20,
                              color: _theme.textTheme.bodySmall?.color?.withOpacity(0.6),
                            ),
                            const SizedBox(height: 6),
                            ValueListenableBuilder<String>(
                              valueListenable: _currentTime,
                              builder: (context, time, _) {
                                return Text(
                                  time,
                                  style: GoogleFonts.poppins(
                                    fontSize: 16,
                                    fontWeight: FontWeight.w600,
                                    color: _theme.textTheme.bodyLarge?.color,
                                  ),
                                );
                              },
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),

            // Main Content Area
            Expanded(
              child: SingleChildScrollView(
                physics: const BouncingScrollPhysics(),
                child: Padding(
                  padding: _standardPadding,
                  child: Column(
                    children: [
                      const SizedBox(height: 20),

                      // Title
                      Text(
                        l10n.get('weeklyProgress'),
                        style: GoogleFonts.poppins(
                          fontSize: 24,
                          fontWeight: FontWeight.w600,
                          color: _theme.textTheme.bodyLarge?.color,
                        ),
                      ),

                      const SizedBox(height: 30),

                      // Time Circle с ValueListenableBuilder
                      Container(
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          boxShadow: [
                            BoxShadow(
                              color: Colors.blue.withOpacity(0.1),
                              blurRadius: 30,
                              spreadRadius: 10,
                            ),
                          ],
                        ),
                        child: ValueListenableBuilder<double>(
                          valueListenable: _workedHours,
                          builder: (context, hours, _) {
                            return TimeCircle(
                              time: _formatHoursToTimeString(hours),
                              workedHours: hours,
                            );
                          },
                        ),
                      ),

                      const SizedBox(height: 40),

                      // Period Card с ValueListenableBuilder
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                        decoration: BoxDecoration(
                          color: _isDark
                              ? Colors.white.withOpacity(0.05)
                              : Colors.blue.withOpacity(0.05),
                          borderRadius: BorderRadius.circular(15),
                          border: Border.all(
                            color: _isDark
                                ? Colors.white.withOpacity(0.1)
                                : Colors.blue.withOpacity(0.2),
                            width: 1,
                          ),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(
                              Icons.date_range_rounded,
                              size: 18,
                              color: Colors.blue.shade400,
                            ),
                            const SizedBox(width: 8),
                            ValueListenableBuilder<String>(
                              valueListenable: _currentPeriod,
                              builder: (context, period, _) {
                                return Text(
                                  period,
                                  style: GoogleFonts.poppins(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w500,
                                    color: _theme.textTheme.bodyLarge?.color,
                                  ),
                                );
                              },
                            ),
                          ],
                        ),
                      ),

                      const SizedBox(height: 30),

                      // View Details Button
                      Container(
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(15),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.blue.withOpacity(0.3),
                              blurRadius: 15,
                              offset: const Offset(0, 5),
                            ),
                          ],
                        ),
                        child: ElevatedButton(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => ViewDetailsScreen(
                                  workedHours: _workedHours.value,
                                ),
                              ),
                            );
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.blue.shade600,
                            foregroundColor: Colors.white,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(15),
                            ),
                            padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 14),
                            elevation: 0,
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(
                                l10n.get('viewDetails'),
                                style: GoogleFonts.poppins(
                                  fontSize: 15,
                                  fontWeight: FontWeight.w600,
                                  color: Colors.white,
                                ),
                              ),
                              const SizedBox(width: 8),
                              const Icon(
                                Icons.arrow_forward_rounded,
                                size: 18,
                                color: Colors.white,
                              ),
                            ],
                          ),
                        ),
                      ),

                      const SizedBox(height: 40),
                    ],
                  ),
                ),
              ),
            ),

            // Bottom Section с ValueListenableBuilders
            Container(
              decoration: BoxDecoration(
                color: _isDark
                    ? Colors.white.withOpacity(0.03)
                    : Colors.grey.withOpacity(0.05),
                boxShadow: [
                  BoxShadow(
                    color: _isDark
                        ? Colors.black.withOpacity(0.2)
                        : Colors.grey.withOpacity(0.1),
                    blurRadius: 10,
                    offset: const Offset(0, -2),
                  ),
                ],
              ),
              child: SafeArea(
                top: false,
                child: Container(
                  padding: _bottomPadding,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      // Last Punch Info Card
                      Expanded(
                        child: Container(
                          padding: const EdgeInsets.all(16),
                          decoration: BoxDecoration(
                            color: _isDark
                                ? Colors.white.withOpacity(0.05)
                                : Colors.white,
                            borderRadius: BorderRadius.circular(16),
                            border: Border.all(
                              color: _isDark
                                  ? Colors.white.withOpacity(0.1)
                                  : Colors.grey.withOpacity(0.2),
                              width: 1,
                            ),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  Icon(
                                    Icons.history_rounded,
                                    size: 18,
                                    color: _theme.textTheme.bodySmall?.color?.withOpacity(0.6),
                                  ),
                                  const SizedBox(width: 8),
                                  Text(
                                    l10n.get('lastPunch'),
                                    style: GoogleFonts.poppins(
                                      fontSize: 12,
                                      fontWeight: FontWeight.w500,
                                      color: _theme.textTheme.bodySmall?.color?.withOpacity(0.6),
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 8),
                              ValueListenableBuilder<String>(
                                valueListenable: _lastPunchDate,
                                builder: (context, date, _) {
                                  return Text(
                                    date,
                                    style: GoogleFonts.poppins(
                                      fontSize: 14,
                                      fontWeight: FontWeight.w600,
                                      color: _theme.textTheme.bodyLarge?.color,
                                    ),
                                  );
                                },
                              ),
                              ValueListenableBuilder<String>(
                                valueListenable: _lastPunchTime,
                                builder: (context, time, _) {
                                  return Text(
                                    time,
                                    style: GoogleFonts.poppins(
                                      fontSize: 16,
                                      fontWeight: FontWeight.bold,
                                      color: Colors.blue.shade600,
                                    ),
                                  );
                                },
                              ),
                            ],
                          ),
                        ),
                      ),

                      const SizedBox(width: 16),

                      // Face Check Button
                      Container(
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          boxShadow: [
                            BoxShadow(
                              color: Colors.green.withOpacity(0.3),
                              blurRadius: 20,
                              spreadRadius: 5,
                            ),
                          ],
                        ),
                        child: Transform.scale(
                          scale: 1.1,
                          child: FaceCheckButton(
                            onPressed: _navigateToPunch,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _formatHoursToTimeString(double hours) {
    int totalMinutes = (hours * 60).round();
    int displayHours = totalMinutes ~/ 60;
    int displayMinutes = totalMinutes % 60;

    return '${displayHours.toString().padLeft(2, '0')}:${displayMinutes.toString().padLeft(2, '0')}';
  }
}