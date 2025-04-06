import 'dart:async';
import 'package:face_check/screens/main_menu/view-details/view_details_screen.dart';
import 'package:face_check/widgets/weather_widget.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../../../api_client/api/authentication_api.dart';
import '../../../providers/localization_provider.dart';
import '../../../utils/date_time_formatter.dart';
import '../../../services/ApiService.dart';
import '../components/custom_drawer.dart';
import '../components/face_check_button.dart';
import '../components/time_circle.dart';

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
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);

  @override
  State<CustomAppBar> createState() => _CustomAppBarState();
}

class _CustomAppBarState extends State<CustomAppBar> {
  int _notificationCount = 0;

  @override
  void initState() {
    super.initState();
    _loadNotifications();
  }

  Future<void> _loadNotifications() async {
    final notifications = await FlutterLocalNotificationsPlugin()
        .pendingNotificationRequests();
    setState(() {
      _notificationCount = notifications.length;
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.watch<LocalizationProvider>().localizations;

    // Получаем размеры экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return AppBar(
      backgroundColor: theme.scaffoldBackgroundColor,
      elevation: 0,
      toolbarHeight: isSmallScreen ? 120 : 140,
      leading: Padding(
        padding: EdgeInsets.only(top: isSmallScreen ? 12 : 16),
        child: IconButton(
          icon: Icon(
            Icons.menu,
            color: theme.iconTheme.color,
            size: isSmallScreen ? 22 : 24,
          ),
          onPressed: widget.onMenuPressed,
        ),
      ),
      centerTitle: true,
      title: SizedBox(
        height: isSmallScreen ? 50 : 60,
        child: Image.asset(
          'assets/images/logo.jpg',
          fit: BoxFit.contain,
        ),
      ),
      actions: [
        Padding(
          padding: EdgeInsets.only(top: isSmallScreen ? 12 : 16),
          child: Stack(
            children: [
              IconButton(
                icon: Icon(
                  Icons.notifications_none,
                  color: theme.iconTheme.color,
                  size: isSmallScreen ? 22 : 24,
                ),
                onPressed: () => Navigator.pushNamed(context, '/notifications')
                    .then((_) => _loadNotifications()),
              ),
              if (_notificationCount > 0)
                Positioned(
                  right: 8,
                  top: 8,
                  child: Container(
                    padding: const EdgeInsets.all(2),
                    decoration: BoxDecoration(
                      color: Colors.red,
                      borderRadius: BorderRadius.circular(10),
                    ),
                    constraints: BoxConstraints(
                      minWidth: isSmallScreen ? 16 : 20,
                      minHeight: isSmallScreen ? 16 : 20,
                    ),
                    child: Text(
                      _notificationCount.toString(),
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: isSmallScreen ? 10 : 12,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
            ],
          ),
        ),
      ],
    );
  }
}

class _MainMenuScreenState extends State<MainMenuScreen> {
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  String _currentDate = '';
  String _currentTime = '';
  String _lastPunchDate = 'DD/MM/YYYY';
  String _lastPunchTime = '--:--';
  late Timer _timer;
  double _workedHours = 0.0;

  void _updateTime() {
    final now = DateTime.now();
    setState(() {
      _currentDate = DateTimeFormatter.formateDate(now);
      _currentTime = DateTimeFormatter.formatTime(now);
    });
  }

  String _getCurrentPeriod() {
    final now = DateTime.now();

    // Find the most recent Sunday (start of the week)
    final startOfWeek = now.subtract(Duration(days: now.weekday % 7));

    // Find the next Saturday (end of the week)
    final endOfWeek = startOfWeek.add(const Duration(days: 6));

    // Format dates as DD/MM/YYYY
    final startFormatted = '${startOfWeek.day.toString().padLeft(2, '0')}/${startOfWeek.month.toString().padLeft(2, '0')}/${startOfWeek.year}';
    final endFormatted = '${endOfWeek.day.toString().padLeft(2, '0')}/${endOfWeek.month.toString().padLeft(2, '0')}/${endOfWeek.year}';

    return '$startFormatted - $endFormatted';
  }

  Future<void> _loadLastPunchTime() async {
    try {
      final punchInfo = await ApiService.instance.getLastPunchTime();
      print("Received punch info - date: ${punchInfo.date}, time: ${punchInfo.time}");
      setState(() {
        _lastPunchDate = punchInfo.date;
        _lastPunchTime = punchInfo.time;
      });
    } catch (e) {
      print('Error loading last punch time: $e');
    }
  }

  Future<void> _loadWorkedHours() async {
    try {
      final hours = await ApiService.instance.getTotalWorkedHoursPerWeek();
      setState(() {
        _workedHours = hours;
      });
    } catch (e) {
      print('Error loading worked hours: $e');
    }
  }

  void _navigateToPunch() {
    Navigator.pushNamed(context, '/punch');
  }

  @override
  void initState() {
    super.initState();
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);

    _updateTime();
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) => _updateTime());
    _loadLastPunchTime();
    _loadWorkedHours();
  }

  @override
  void dispose() {
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.manual,
        overlays: SystemUiOverlay.values);
    _timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.watch<LocalizationProvider>().localizations;

    // Получаем размеры экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        systemNavigationBarColor: theme.scaffoldBackgroundColor,
        statusBarIconBrightness:
        theme.brightness == Brightness.dark ? Brightness.light : Brightness.dark,
        systemNavigationBarIconBrightness:
        theme.brightness == Brightness.dark ? Brightness.light : Brightness.dark,
      ),
      child: Scaffold(
        key: _scaffoldKey,
        backgroundColor: theme.scaffoldBackgroundColor,
        appBar: CustomAppBar(
          currentDate: _currentDate,
          currentTime: _currentTime,
          onMenuPressed: () => _scaffoldKey.currentState?.openDrawer(),
        ),
        drawer: const CustomDrawer(),
        body: Column(
          children: [
            // Верхняя линия
            Container(
              height: 1,
              width: double.infinity,
              color: Colors.grey[600],
            ),
            // Верхний информационный блок
            Container(
              height: isSmallScreen ? 50 : 60,
              child: Stack(
                children: [
                  Row(
                    children: [
                      Expanded(
                        flex: 1,
                        child: Center(
                          child: WeatherWidget(),
                        ),
                      ),
                      Container(
                        width: 1,
                        height: isSmallScreen ? 30 : 40,
                        color: Colors.grey[600],
                      ),
                      Expanded(
                        flex: 1,
                        child: Center(
                          child: Text(
                            DateFormat('MMM dd').format(DateTime.now()),
                            style: TextStyle(
                              fontSize: isSmallScreen ? 16 : 18,
                              color: theme.textTheme.bodyLarge?.color,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ),
                      Container(
                        width: 1,
                        height: isSmallScreen ? 30 : 40,
                        color: Colors.grey[600],
                      ),
                      Expanded(
                        flex: 1,
                        child: Center(
                          child: Padding(
                            padding: EdgeInsets.only(left: isSmallScreen ? 15 : 20),
                            child: Text(
                              _currentTime,
                              style: TextStyle(
                                fontSize: isSmallScreen ? 16 : 18,
                                color: theme.textTheme.bodyLarge?.color,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                  Positioned(
                    bottom: 0,
                    left: 0,
                    right: 0,
                    child: Container(
                      height: 1,
                      color: Colors.grey[600],
                    ),
                  ),
                ],
              ),
            ),
            // Основное содержимое с возможностью прокрутки
            Expanded(
              child: SingleChildScrollView(
                physics: const BouncingScrollPhysics(),
                child: ConstrainedBox(
                  constraints: BoxConstraints(
                    minHeight: MediaQuery.of(context).size.height -
                        (isSmallScreen ? 75 : 90) - // Высота нижнего блока
                        (isSmallScreen ? 50 : 60) - // Высота верхнего инфо-блока
                        (kToolbarHeight + (isSmallScreen ? 12 : 16) * 2), // Высота AppBar с отступами
                  ),
                  child: Center(
                    child: Padding(
                      padding: EdgeInsets.only(
                        top: 20,
                        bottom: 30,
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            '',
                            style: TextStyle(
                              fontSize: isSmallScreen ? 20 : 24,
                              color: theme.textTheme.bodyLarge?.color,
                            ),
                          ),
                          SizedBox(height: isSmallScreen ? 15 : 20),
                          Transform.scale(
                            scale: isSmallScreen ? 0.85 : 1.0,
                            child: TimeCircle(
                              time: _formatHoursToTimeString(_workedHours),
                              workedHours: _workedHours,
                            ),
                          ),
                          SizedBox(height: isSmallScreen ? 30 : 40),
                          Text(
                            _getCurrentPeriod(),
                            style: TextStyle(
                              fontSize: isSmallScreen ? 14 : 16,
                              color: theme.textTheme.bodyLarge?.color,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          SizedBox(height: isSmallScreen ? 20 : 30),
                          Container(
                            height: isSmallScreen ? 28 : 30,
                            width: isSmallScreen ? 110 : 120,
                            child: ElevatedButton(
                              onPressed: () {
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                    builder: (context) => ViewDetailsScreen(workedHours: _workedHours),
                                  ),
                                );
                              },
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.blue[600],
                                foregroundColor: Colors.white,
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(50),
                                ),
                                padding: EdgeInsets.zero,
                              ),
                              child: Text(
                                l10n.get('viewDetails'),
                                style: TextStyle(
                                  color: Colors.white,
                                  fontSize: isSmallScreen ? 12 : 14,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
            // Нижний блок с серой линией и элементами
            Container(
              height: 1,
              width: double.infinity,
              color: Colors.grey[600],
            ),
            // Нижний блок с Last Punch и кнопкой FaceCheck
            SizedBox(
              height: isSmallScreen ? 75 : 90,
              child: Row(
                children: [
                  // Last Punch блок
                  Expanded(
                    child: Padding(
                      padding: EdgeInsets.only(left: isSmallScreen ? 20 : 40),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            l10n.get('lastPunch'),
                            style: TextStyle(
                              color: theme.textTheme.bodyLarge?.color,
                              fontSize: isSmallScreen ? 16 : 18,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          Text(
                            _lastPunchDate,
                            style: TextStyle(
                              color: theme.textTheme.bodyLarge?.color,
                              fontSize: isSmallScreen ? 16 : 18,
                              fontWeight: FontWeight.w400,
                            ),
                          ),
                          Text(
                            _lastPunchTime,
                            style: TextStyle(
                              color: theme.textTheme.bodyLarge?.color,
                              fontSize: isSmallScreen ? 16 : 18,
                              fontWeight: FontWeight.w400,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  // FaceCheck кнопка
                  Padding(
                    padding: EdgeInsets.only(right: isSmallScreen ? 15 : 20),
                    child: Transform.scale(
                      scale: isSmallScreen ? 1.0 : 1.2,
                      child: FaceCheckButton(
                        onPressed: _navigateToPunch,
                      ),
                    ),
                  ),
                ],
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