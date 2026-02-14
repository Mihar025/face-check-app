import 'dart:async';
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:dio/dio.dart';
import 'package:timezone/data/latest.dart' as tzdata;
import 'package:timezone/timezone.dart' as tz;

import '../../../api_client/api/authentication_api.dart';
import '../../../providers/localization_provider.dart';
import '../../../services/pivacy_policy_service.dart';
import '../../../services/ApiService.dart';
import '../../../services/time_service.dart';
import '../../loginScreen/privacy_policy_screen.dart';
import '../components/custom_drawer.dart';
import '../view-details/view_details_screen.dart';

// Notification для перезагрузки данных (из drawer и из punch screen)
class MainScreenReloadNotification extends Notification {}

// Cache Manager для хранения данных
class CacheManager {
  static const String _workedHoursKey = 'cached_worked_hours';
  static const String _lastPunchDateKey = 'cached_last_punch_date';
  static const String _lastPunchTimeKey = 'cached_last_punch_time';
  static const String _cacheTimestampKey = 'cache_timestamp';
  static const int _cacheValidityMinutes = 5;

  static Future<Map<String, dynamic>?> getCachedData() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final timestamp = prefs.getInt(_cacheTimestampKey) ?? 0;
      final now = DateTime.now().millisecondsSinceEpoch;

      if (now - timestamp > _cacheValidityMinutes * 60 * 1000) {
        return null;
      }

      return {
        'workedHours': prefs.getDouble(_workedHoursKey) ?? 0.0,
        'lastPunchDate': prefs.getString(_lastPunchDateKey) ?? '--/--/----',
        'lastPunchTime': prefs.getString(_lastPunchTimeKey) ?? '--:--',
      };
    } catch (e) {
      return null;
    }
  }

  static Future<void> saveCachedData({
    required double workedHours,
    required String lastPunchDate,
    required String lastPunchTime,
  }) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await Future.wait([
        prefs.setDouble(_workedHoursKey, workedHours),
        prefs.setString(_lastPunchDateKey, lastPunchDate),
        prefs.setString(_lastPunchTimeKey, lastPunchTime),
        prefs.setInt(_cacheTimestampKey, DateTime.now().millisecondsSinceEpoch),
      ]);
    } catch (e) {
      // Silent fail
    }
  }

  static Future<void> clearCache() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await Future.wait([
        prefs.remove(_workedHoursKey),
        prefs.remove(_lastPunchDateKey),
        prefs.remove(_lastPunchTimeKey),
        prefs.remove(_cacheTimestampKey),
      ]);
    } catch (e) {
      // Silent fail
    }
  }
}

// Красивый виджет для часов
class WeeklyHoursCircle extends StatefulWidget {
  final double hours;
  final String period;
  final String thisWeekText;

  const WeeklyHoursCircle({
    Key? key,
    required this.hours,
    required this.period,
    required this.thisWeekText,
  }) : super(key: key);

  @override
  State<WeeklyHoursCircle> createState() => _WeeklyHoursCircleState();
}

class _WeeklyHoursCircleState extends State<WeeklyHoursCircle>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 1200),
      vsync: this,
    );
    _animation = Tween<double>(
      begin: 0,
      end: widget.hours,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeOutCubic,
    ));
    _animationController.forward();
  }

  @override
  void didUpdateWidget(WeeklyHoursCircle oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.hours != widget.hours) {
      _animation = Tween<double>(
        begin: oldWidget.hours,
        end: widget.hours,
      ).animate(CurvedAnimation(
        parent: _animationController,
        curve: Curves.easeOutCubic,
      ));
      _animationController.forward(from: 0);
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final size = MediaQuery.of(context).size.width;
    final isSmall = size < 360;

    final circleSize = isSmall ? 180.0 : 220.0;

    return Container(
      width: circleSize,
      height: circleSize,
      child: AnimatedBuilder(
        animation: _animation,
        builder: (context, child) {
          final hours = _animation.value.floor();
          final minutes = ((_animation.value - hours) * 60).round();

          return Stack(
            alignment: Alignment.center,
            children: [
              CustomPaint(
                size: Size(circleSize, circleSize),
                painter: CircularProgressPainter(
                  progress: _animation.value / 40,
                  backgroundColor: isDark ? Colors.grey[800]! : Colors.grey[200]!,
                  progressColor: _getProgressColor(_animation.value),
                  strokeWidth: isSmall ? 12 : 14,
                ),
              ),
              Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  RichText(
                    text: TextSpan(
                      children: [
                        TextSpan(
                          text: hours.toString(),
                          style: TextStyle(
                            fontSize: isSmall ? 42 : 48,
                            fontWeight: FontWeight.bold,
                            color: isDark ? Colors.white : Colors.black87,
                          ),
                        ),
                        TextSpan(
                          text: 'h ',
                          style: TextStyle(
                            fontSize: isSmall ? 18 : 20,
                            fontWeight: FontWeight.normal,
                            color: (isDark ? Colors.white : Colors.black87).withOpacity(0.6),
                          ),
                        ),
                        TextSpan(
                          text: minutes.toString().padLeft(2, '0'),
                          style: TextStyle(
                            fontSize: isSmall ? 28 : 32,
                            fontWeight: FontWeight.w500,
                            color: (isDark ? Colors.white : Colors.black87).withOpacity(0.8),
                          ),
                        ),
                        TextSpan(
                          text: 'm',
                          style: TextStyle(
                            fontSize: isSmall ? 16 : 18,
                            fontWeight: FontWeight.normal,
                            color: (isDark ? Colors.white : Colors.black87).withOpacity(0.6),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                    decoration: BoxDecoration(
                      color: _getProgressColor(_animation.value).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      widget.thisWeekText,
                      style: TextStyle(
                        fontSize: 10,
                        letterSpacing: 1.2,
                        fontWeight: FontWeight.w600,
                        color: _getProgressColor(_animation.value),
                      ),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    widget.period,
                    style: TextStyle(
                      fontSize: isSmall ? 11 : 12,
                      color: (isDark ? Colors.white : Colors.black87).withOpacity(0.5),
                    ),
                  ),
                ],
              ),
            ],
          );
        },
      ),
    );
  }

  Color _getProgressColor(double hours) {
    if (hours < 10) return Colors.red;
    if (hours < 20) return Colors.orange;
    if (hours < 30) return Colors.amber;
    if (hours < 40) return Colors.blue;
    return Colors.green;
  }
}

class CircularProgressPainter extends CustomPainter {
  final double progress;
  final Color backgroundColor;
  final Color progressColor;
  final double strokeWidth;

  CircularProgressPainter({
    required this.progress,
    required this.backgroundColor,
    required this.progressColor,
    required this.strokeWidth,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = (size.width - strokeWidth) / 2;

    final backgroundPaint = Paint()
      ..color = backgroundColor
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth;

    canvas.drawCircle(center, radius, backgroundPaint);

    final progressPaint = Paint()
      ..color = progressColor
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;

    final progressAngle = 2 * math.pi * progress.clamp(0.0, 1.0);

    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -math.pi / 2,
      progressAngle,
      false,
      progressPaint,
    );
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}

// Оптимизированный MainMenuScreen
class MainMenuScreen extends StatefulWidget {
  final AuthenticationApi authenticationApi;

  const MainMenuScreen({
    super.key,
    required this.authenticationApi
  });

  @override
  State<MainMenuScreen> createState() => _MainMenuScreenState();
}

class _MainMenuScreenState extends State<MainMenuScreen> {
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

  // ValueNotifiers
  late final ValueNotifier<String> _currentTime;
  late final ValueNotifier<String> _currentDate;
  late final ValueNotifier<double> _workedHours;
  late final ValueNotifier<String> _lastPunchDate;
  late final ValueNotifier<String> _lastPunchTime;
  late final ValueNotifier<bool> _isPunchedIn;
  late final ValueNotifier<String> _weekPeriod;
  late final ValueNotifier<int> _unreadCount;
  Timer? _pollingTimer;


  // Services
  late final TimeService _timeService;
  StreamSubscription<tz.TZDateTime>? _timeSub;

  // Dio instance
  late final Dio _dio;

  // Flags
  bool _isInitialized = false;
  int? _currentUserId;
  bool _privacyPolicyChecked = false;
  bool _canLoadData = false;

  @override
  void initState() {
    super.initState();

    // ✅ ИСПРАВЛЕНИЕ: Устанавливаем светлые иконки для тёмной темы
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light, // ✅ СВЕТЛЫЕ иконки (для тёмного фона)
        statusBarBrightness: Brightness.dark,      // ✅ Для iOS
      ),
    );

    tzdata.initializeTimeZones();

    _currentTime = ValueNotifier<String>(DateFormat('HH:mm').format(DateTime.now()));
    _currentDate = ValueNotifier<String>(DateFormat('EEEE, MMMM d').format(DateTime.now()));
    _workedHours = ValueNotifier<double>(0.0);
    _lastPunchDate = ValueNotifier<String>('--/--/----');
    _lastPunchTime = ValueNotifier<String>('--:--');
    _isPunchedIn = ValueNotifier<bool>(false);

    final now = DateTime.now();
    final startOfWeek = now.subtract(Duration(days: now.weekday % 7));
    final endOfWeek = startOfWeek.add(const Duration(days: 6));
    _weekPeriod = ValueNotifier<String>(
        '${DateFormat('MMM d').format(startOfWeek)} - ${DateFormat('MMM d').format(endOfWeek)}'
    );
    _unreadCount = ValueNotifier<int>(0);

    _dio = Dio(BaseOptions(
      baseUrl: 'https://face-check-prod-drgsy.ondigitalocean.app/api/v1/',
      connectTimeout: const Duration(seconds: 5),
      receiveTimeout: const Duration(seconds: 30),
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await ApiService.instance.getAuthToken();
        if (token != null) options.headers['Authorization'] = 'Bearer $token';
        return handler.next(options);
      },
    ));

    _timeService = TimeService(_dio);

    _fastInitialize();
  }

  @override
  void dispose() {
    _timeSub?.cancel();
    _currentTime.dispose();
    _currentDate.dispose();
    _workedHours.dispose();
    _lastPunchDate.dispose();
    _lastPunchTime.dispose();
    _isPunchedIn.dispose();
    _weekPeriod.dispose();
    _pollingTimer?.cancel();
    _unreadCount.dispose();
    super.dispose();
  }


  void _startNotificationPolling(){

    _fetchUnreadCount();

    _pollingTimer = Timer.periodic(
      const Duration(seconds: 30),
        (_) => _fetchUnreadCount(),
    );
  }

  Future<void> _fetchUnreadCount() async {
    try{
      final companyId = await ApiService.instance.getCompanyId();
      if(companyId != null){
        final count = await ApiService.instance.getUnreadNotificationCount(companyId: companyId);
        _unreadCount.value = count;
      }
    } catch(e){
      print('Error polling unread count $e');
    }
  }








  Future<void> _fastInitialize() async {
    await _loadCachedData();
    setState(() => _isInitialized = true);
    _timeService.sync().then((_) => _startTimeTicker());
    _checkPunchStatus();
    await _initializeUserAndPrivacy();
    if (_canLoadData) {
      await _loadFreshData();
    }
  }

  Future<void> _loadCachedData() async {
    try {
      final cached = await CacheManager.getCachedData();
      if (cached != null) {
        _workedHours.value = cached['workedHours'];
        _lastPunchDate.value = cached['lastPunchDate'];
        _lastPunchTime.value = cached['lastPunchTime'];
      }
    } catch (e) {
      print('Error loading cached data: $e');
    }
  }

  Future<void> _loadFreshData() async {
    if (_currentUserId == null || !_canLoadData) {
      print('Cannot load data: userId=$_currentUserId, canLoadData=$_canLoadData');
      return;
    }

    try {
      print('Loading fresh data for userId: $_currentUserId');

      final token = await ApiService.instance.getAuthToken();
      if (token == null) {
        print('No auth token available, skipping data load');
        return;
      }

      double hours = _workedHours.value;
      dynamic punchInfo;

      try {
        hours = await ApiService.instance.getTotalWorkedHoursPerWeek();
        print('Successfully loaded worked hours: $hours');
      } catch (e) {
        print('Error loading worked hours (likely server issue): $e');
      }

      try {
        punchInfo = await ApiService.instance.getLastPunchTime();
        print('Successfully loaded last punch info');
      } catch (e) {
        print('Error loading last punch time: $e');
        punchInfo = {'date': _lastPunchDate.value, 'time': _lastPunchTime.value};
      }

      _workedHours.value = hours;
      _lastPunchDate.value = punchInfo.date;
      _lastPunchTime.value = punchInfo.time;

      await CacheManager.saveCachedData(
        workedHours: hours,
        lastPunchDate: punchInfo.date,
        lastPunchTime: punchInfo.time,
      );

      print('Data loaded and cached successfully');
    } catch (e) {
      print('Unexpected error loading fresh data: $e');
    }
  }

  void _startTimeTicker() {
    _timeSub = _timeService.nyTicker().listen((nyTime) {
      _currentTime.value = DateFormat('HH:mm').format(nyTime);
      _currentDate.value = DateFormat('EEEE, MMMM d').format(nyTime);

      final startOfWeek = nyTime.subtract(Duration(days: nyTime.weekday % 7));
      final endOfWeek = startOfWeek.add(const Duration(days: 6));
      _weekPeriod.value =
      '${DateFormat('MMM d').format(startOfWeek)} - ${DateFormat('MMM d').format(endOfWeek)}';
    });
  }

  Future<void> _initializeUserAndPrivacy() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      _currentUserId = prefs.getInt('user_id');

      if (_currentUserId == null) {
        print('No cached userId, fetching from API...');
        final response = await _dio.get('user/find-user-id');
        if (response.statusCode == 200 && response.data != null) {
          _currentUserId = response.data['userId'];
          if (_currentUserId != null) {
            await prefs.setInt('user_id', _currentUserId!);
            print('User ID obtained and saved: $_currentUserId');
          }
        }
      } else {
        print('User ID loaded from cache: $_currentUserId');
      }

      if (_currentUserId != null) {
        await _checkPrivacyPolicy(_currentUserId!);
      } else {
        print('Warning: No userId available for privacy policy check');
        _canLoadData = false;
      }
    } catch (e) {
      print('Error initializing user: $e');
      _canLoadData = false;
    }
  }

  Future<void> _checkPrivacyPolicy(int userId) async {
    try {
      print('Checking privacy policy for userId: $userId');

      final hasAccepted = await PrivacyPolicyService.instance
          .hasAcceptedPrivacyPolicy(userId);

      _privacyPolicyChecked = true;

      if (!hasAccepted) {
        print('Privacy policy not accepted, showing screen');
        _canLoadData = false;

        if (mounted) {
          WidgetsBinding.instance.addPostFrameCallback((_) {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => PrivacyPolicyScreen(
                  userId: userId,
                  onAccepted: () async {
                    print('Privacy policy accepted');
                    Navigator.pop(context);
                    _canLoadData = true;
                    await _loadFreshData();
                  },
                ),
                fullscreenDialog: true,
              ),
            );
          });
        }
      } else {
        print('Privacy policy already accepted');
        _canLoadData = true;
      }
    } catch (e) {
      print('Error checking privacy policy: $e');
      _canLoadData = true;
    }
  }

  Future<void> _checkPunchStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      _isPunchedIn.value = prefs.getBool('isPunchedInToday') ?? false;
    } catch (e) {
      print('Error checking punch status: $e');
    }
  }
// Полная перезагрузка данных
  Future<void> _fullReloadData() async {
    print('Starting full reload...');

    // Очищаем кеш
    await CacheManager.clearCache();

    // Сбрасываем значения на дефолтные
    _workedHours.value = 0.0;
    _lastPunchDate.value = '--/--/----';
    _lastPunchTime.value = '--:--';

    // Перезагружаем userId и privacy policy
    await _initializeUserAndPrivacy();

    // ✅ ВАЖНО: Обновляем статус punch in/out
    await _checkPunchStatus();

    // Загружаем свежие данные
    if (_canLoadData && _currentUserId != null) {
      await _loadFreshData();
    }

    print('Full reload completed');
  }

  // Pull to refresh
  Future<void> _onRefresh() async {
    await _fullReloadData();
  }

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final l10n = context.watch<LocalizationProvider>().localizations;

    final isSmall = size.width < 360;
    final basePadding = isSmall ? 12.0 : 16.0;
    final cardRadius = isSmall ? 12.0 : 16.0;
    final mainFontSize = isSmall ? 14.0 : 16.0;
    final titleFontSize = isSmall ? 18.0 : 20.0;

    return NotificationListener<MainScreenReloadNotification>(
      onNotification: (notification) {
        _fullReloadData();
        return true;
      },
      child: Scaffold(
        key: _scaffoldKey,
        backgroundColor: isDark ? Colors.black : const Color(0xFFF5F5F5),
        drawer: const CustomDrawer(),
        body: SafeArea(
          child: Column(
            children: [
              // HEADER
              Container(
                height: 60,
                padding: EdgeInsets.symmetric(horizontal: basePadding),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    IconButton(
                      icon: Icon(Icons.menu, size: 28),
                      onPressed: () => _scaffoldKey.currentState?.openDrawer(),
                    ),
                    Container(
                      height: 40,
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                      child: Image.asset(
                        'assets/images/logo.jpg',
                        fit: BoxFit.contain,
                      ),
                    ),
                    ValueListenableBuilder<int>(
                      valueListenable: _unreadCount,
                      builder: (_, count, __) => IconButton(
                        icon: Stack(
                          clipBehavior: Clip.none,
                          children: [
                            const Icon(Icons.notifications_none_rounded, size: 28),
                            if (count > 0)
                              Positioned(
                                right: -6,
                                top: -4,
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: const BoxDecoration(
                                    color: Colors.red,
                                    shape: BoxShape.circle,
                                  ),
                                  constraints: const BoxConstraints(
                                    minWidth: 18,
                                    minHeight: 18,
                                  ),
                                  child: Text(
                                    count > 99 ? '99+' : count.toString(),
                                    style: const TextStyle(
                                      color: Colors.white,
                                      fontSize: 10,
                                      fontWeight: FontWeight.bold,
                                    ),
                                    textAlign: TextAlign.center,
                                  ),
                                ),
                              ),
                          ],
                        ),
                        onPressed: () async {
                          await Navigator.pushNamed(context, '/notifications');
                          // Когда вернулся из экрана уведомлений — обнуляем
                          final companyId = await ApiService.instance.getCompanyId();
                          if (companyId != null) {
                            await ApiService.instance.markNotificationAsRead(companyId: companyId);
                            _unreadCount.value = 0;
                          }
                        },
                      ),
                    ),
                  ],
                ),
              ),

              // MAIN CONTENT с Pull to Refresh
              Expanded(
                child: RefreshIndicator(
                  onRefresh: _onRefresh,
                  color: Colors.blue,
                  child: ListView(
                    padding: EdgeInsets.all(basePadding),
                    children: [
                      // DATE & TIME CARD
                      Container(
                        padding: EdgeInsets.all(basePadding),
                        decoration: BoxDecoration(
                          color: isDark ? Colors.grey[900] : Colors.white,
                          borderRadius: BorderRadius.circular(cardRadius),
                        ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                ValueListenableBuilder<String>(
                                  valueListenable: _currentDate,
                                  builder: (_, date, __) => Text(
                                    date,
                                    style: TextStyle(
                                      fontSize: mainFontSize - 2,
                                      color: isDark ? Colors.grey[400] : Colors.grey[600],
                                    ),
                                  ),
                                ),
                                ValueListenableBuilder<String>(
                                  valueListenable: _currentTime,
                                  builder: (_, time, __) => Text(
                                    time,
                                    style: TextStyle(
                                      fontSize: titleFontSize + 4,
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            ValueListenableBuilder<bool>(
                              valueListenable: _isPunchedIn,
                              builder: (_, punched, __) => Container(
                                padding: EdgeInsets.symmetric(
                                  horizontal: basePadding,
                                  vertical: basePadding / 2,
                                ),
                                decoration: BoxDecoration(
                                  color: punched
                                      ? Colors.green.withOpacity(0.1)
                                      : Colors.red.withOpacity(0.1),
                                  borderRadius: BorderRadius.circular(20),
                                  border: Border.all(
                                    color: punched ? Colors.green : Colors.red,
                                    width: 1,
                                  ),
                                ),
                                child: Row(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    Container(
                                      width: 8,
                                      height: 8,
                                      decoration: BoxDecoration(
                                        color: punched ? Colors.green : Colors.red,
                                        shape: BoxShape.circle,
                                      ),
                                    ),
                                    SizedBox(width: 6),
                                    Text(
                                      punched ? l10n.get('active') : l10n.get('inactive'),
                                      style: TextStyle(
                                        fontSize: mainFontSize - 2,
                                        fontWeight: FontWeight.w600,
                                        color: punched ? Colors.green : Colors.red,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),

                      SizedBox(height: basePadding),

                      // HOURS CIRCLE
                      Container(
                        padding: EdgeInsets.all(basePadding * 1.5),
                        decoration: BoxDecoration(
                          color: isDark ? Colors.grey[900] : Colors.white,
                          borderRadius: BorderRadius.circular(cardRadius),
                        ),
                        child: Center(
                          child: ValueListenableBuilder<double>(
                            valueListenable: _workedHours,
                            builder: (_, hours, __) => ValueListenableBuilder<String>(
                              valueListenable: _weekPeriod,
                              builder: (_, period, __) => WeeklyHoursCircle(
                                hours: hours,
                                period: period,
                                thisWeekText: l10n.get('thisWeek'),
                              ),
                            ),
                          ),
                        ),
                      ),

                      SizedBox(height: basePadding),

                      // LAST PUNCH CARD
                      Container(
                        padding: EdgeInsets.all(basePadding * 1.2),
                        decoration: BoxDecoration(
                          color: isDark ? Colors.grey[900] : Colors.white,
                          borderRadius: BorderRadius.circular(cardRadius),
                        ),
                        child: Row(
                          children: [
                            Container(
                              padding: EdgeInsets.all(basePadding * 0.8),
                              decoration: BoxDecoration(
                                color: Colors.blue.withOpacity(0.1),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: Icon(
                                Icons.history,
                                color: Colors.blue,
                                size: isSmall ? 24 : 28,
                              ),
                            ),
                            SizedBox(width: basePadding),
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    l10n.get('lastPunch'),
                                    style: TextStyle(
                                      fontSize: mainFontSize - 2,
                                      color: isDark ? Colors.grey[400] : Colors.grey[600],
                                    ),
                                  ),
                                  SizedBox(height: 4),
                                  Row(
                                    children: [
                                      ValueListenableBuilder<String>(
                                        valueListenable: _lastPunchDate,
                                        builder: (_, date, __) => Text(
                                          date,
                                          style: TextStyle(
                                            fontSize: mainFontSize,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      ),
                                      SizedBox(width: 8),
                                      ValueListenableBuilder<String>(
                                        valueListenable: _lastPunchTime,
                                        builder: (_, time, __) => Text(
                                          time,
                                          style: TextStyle(
                                            fontSize: mainFontSize,
                                            fontWeight: FontWeight.bold,
                                            color: Colors.blue,
                                          ),
                                        ),
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),

                      SizedBox(height: basePadding),

                      // VIEW DETAILS BUTTON
                      Row(
                        children: [
                          Expanded(
                            child: SizedBox(
                              height: isSmall ? 48 : 56,
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
                                  backgroundColor: Colors.blue.withOpacity(0.1),
                                  foregroundColor: Colors.blue,
                                  elevation: 0,
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(cardRadius - 4),
                                  ),
                                ),
                                child: Text(
                                  l10n.get('viewDetails'),
                                  style: TextStyle(
                                    fontSize: mainFontSize,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),

                      SizedBox(height: basePadding * 2),
                    ],
                  ),
                ),
              ),

              // BOTTOM PUNCH BUTTON
              Container(
                padding: EdgeInsets.all(basePadding),
                decoration: BoxDecoration(
                  color: isDark ? Colors.grey[900] : Colors.white,
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.1),
                      blurRadius: 10,
                      offset: const Offset(0, -2),
                    ),
                  ],
                ),
                child: SizedBox(
                  width: double.infinity,
                  height: isSmall ? 54 : 60,
                  child: ElevatedButton(
                    onPressed: () => Navigator.pushNamed(context, '/punch'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.green,
                      foregroundColor: Colors.white,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(cardRadius),
                      ),
                      elevation: 0,
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.fingerprint, size: isSmall ? 26 : 30),
                        SizedBox(width: basePadding / 2),
                        Text(
                          l10n.get('punch'),
                          style: TextStyle(
                            fontSize: titleFontSize - 2,
                            fontWeight: FontWeight.bold,
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
      ),
    );
  }

}