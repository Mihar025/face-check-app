import 'package:face_check/screens/main_menu/drawer/punch_screen/punch_buttons.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_dialog.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_selector.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_service.dart';
import 'package:face_check/screens/main_menu/main_menu_punch_screen/punch_success_dialo.dart';
import 'package:face_check/services/location_tracking_service.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';
import 'package:provider/provider.dart';
import 'package:image_picker/image_picker.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:io';
import 'dart:convert';
import 'package:intl/intl.dart';
import 'package:timezone/timezone.dart' as tz;
import 'package:face_check/services/time_service.dart';

import '../../../../../api_client/model/work_site_response.dart';
import '../../../../../api_client/api/worker_attendance_controller_api.dart';
import '../../../../../api_client/serializers.dart';
import '../../../../../services/ApiService.dart';
import '../../../../../providers/localization_provider.dart';
import 'clock_display.dart';
import 'location_service.dart';
import 'map_container.dart';

class PunchScreen extends StatefulWidget {
  const PunchScreen({super.key});

  @override
  State<PunchScreen> createState() => _PunchScreenState();
}

class _PunchScreenState extends State<PunchScreen> with WidgetsBindingObserver {
  // Services
  late final Dio dio;
  late final LocationService locationService;
  late final WorkSiteService workSiteService;
  late final WorkerAttendanceControllerApi attendanceApi;
  late final TimeService timeService;
  late final LocationTrackingService _locationTrackingService;
  final ImagePicker _imagePicker = ImagePicker();

  late final ValueNotifier<bool> _isLoading;
  late final ValueNotifier<bool> _hasPunchIn;
  late final ValueNotifier<Position?> _currentPosition;
  late final ValueNotifier<WorkSiteResponse?> _selectedWorkSite;
  late final ValueNotifier<List<WorkSiteResponse>> _workSites;
  late final ValueNotifier<bool> _isTrackingActive;
  late final ValueNotifier<int?> _currentUserId;

  GoogleMapController? mapController;

  late Size _screenSize;
  late bool _isSmallScreen;
  late ThemeData _theme;

  static final tz.Location _ny = tz.getLocation('America/New_York');
  static const double _smallScreenThreshold = 360.0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);

    _isLoading = ValueNotifier<bool>(false);
    _hasPunchIn = ValueNotifier<bool>(false);
    _currentPosition = ValueNotifier<Position?>(null);
    _selectedWorkSite = ValueNotifier<WorkSiteResponse?>(null);
    _workSites = ValueNotifier<List<WorkSiteResponse>>([]);
    _isTrackingActive = ValueNotifier<bool>(false);
    _currentUserId = ValueNotifier<int?>(null);

    _initializeDependencies();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateCachedValues();
  }

  void _updateCachedValues() {
    _screenSize = MediaQuery.of(context).size;
    _isSmallScreen = _screenSize.width < _smallScreenThreshold;
    _theme = Theme.of(context);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _isLoading.dispose();
    _hasPunchIn.dispose();
    _currentPosition.dispose();
    _selectedWorkSite.dispose();
    _workSites.dispose();
    _isTrackingActive.dispose();
    _currentUserId.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      timeService.sync().then((_) => _checkTodayPunchStatus());
    }
  }

  void _initializeDependencies() async {
    _isLoading.value = true; // ← ✅ Включаем загрузку СРАЗУ!

    _initializeDio();
    timeService = TimeService(dio);
    locationService = LocationService();
    workSiteService = WorkSiteService(dio);
    attendanceApi = WorkerAttendanceControllerApi(dio, serializers);
    _locationTrackingService = LocationTrackingService(dio);

    try {
      // ✅ ПАРАЛЛЕЛЬНАЯ загрузка данных!
      await Future.wait([
        _fetchAndSaveUserId(),
        timeService.sync(),
        _getCurrentLocation(),
        _loadWorkSites(),
      ]);

      // Эти зависят от предыдущих, делаем последовательно
      await _checkTodayPunchStatus();
      await _checkTrackingStatus();

    } catch (e) {
      print('❌ Error during initialization: $e');
    } finally {
      _isLoading.value = false; // ← ✅ Отключаем загрузку
    }
  }

  Future<void> _fetchAndSaveUserId() async {
    try {
      print('📱 Fetching user ID from server...');

      final response = await dio.get('user/find-user-id');

      if (response.statusCode == 200 && response.data != null) {
        final userId = response.data['userId'];

        if (userId != null) {
          final prefs = await SharedPreferences.getInstance();
          await prefs.setInt('user_id', userId);
          _currentUserId.value = userId;
          print('✅ User ID successfully fetched and saved: $userId');
        }
      }
    } catch (e) {
      print('❌ Error fetching user ID: $e');

      final prefs = await SharedPreferences.getInstance();
      final savedUserId = prefs.getInt('user_id');

      if (savedUserId != null && savedUserId != 0) {
        _currentUserId.value = savedUserId;
        print('📦 Using cached user ID: $savedUserId');
      }
    }
  }

  Future<void> _checkTrackingStatus() async {
    final isActive = await _locationTrackingService.isTrackingActive();
    _isTrackingActive.value = isActive;
  }

  void _initializeDio() {
    dio = Dio(BaseOptions(
     // baseUrl: 'http://192.168.1.194:8088/api/v1/',
      baseUrl: 'https://face-check-prod-drgsy.ondigitalocean.app/api/v1/',
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
      onError: (DioException e, handler) {
        print('Dio Error: ${e.message}');
        return handler.next(e);
      },
    ));
  }

  bool _isSameDayNY(DateTime aUtc, DateTime bUtc) {
    final aNy = tz.TZDateTime.from(aUtc.toUtc(), _ny);
    final bNy = tz.TZDateTime.from(bUtc.toUtc(), _ny);
    return aNy.year == bNy.year && aNy.month == bNy.month && aNy.day == bNy.day;
  }

  Future<void> _checkTodayPunchStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      // Сначала проверяем флаг быстрой проверки
      final bool? quickFlag = prefs.getBool('isPunchedInToday');

      final String? inStr = prefs.getString('lastPunchInDate');
      final String? outStr = prefs.getString('lastPunchOutDate');

      final DateTime nowUtc = timeService.nowUtc();

      // Если нет сохраненных данных, считаем что не было punch in
      if ((inStr == null || inStr.isEmpty) && (outStr == null || outStr.isEmpty)) {
        _hasPunchIn.value = false;
        await prefs.setBool('isPunchedInToday', false);
        return;
      }

      final DateTime? inUtc = (inStr != null && inStr.isNotEmpty)
          ? DateTime.tryParse(inStr)?.toUtc()
          : null;
      final DateTime? outUtc = (outStr != null && outStr.isNotEmpty)
          ? DateTime.tryParse(outStr)?.toUtc()
          : null;

      // Проверяем только события сегодняшнего дня
      final bool inToday = (inUtc != null) && _isSameDayNY(inUtc, nowUtc);
      final bool outToday = (outUtc != null) && _isSameDayNY(outUtc, nowUtc);

      bool isPunchedIn = false;

      if (inToday && outToday) {
        // Оба события сегодня - проверяем последовательность
        isPunchedIn = inUtc.isAfter(outUtc);
      } else if (inToday && !outToday) {
        // Только punch in сегодня
        isPunchedIn = true;
      } else {
        // Нет punch in сегодня
        isPunchedIn = false;
      }

      _hasPunchIn.value = isPunchedIn;
      await prefs.setBool('isPunchedInToday', isPunchedIn);

    } catch (e) {
      print('Error checking punch status: $e');
      _hasPunchIn.value = false;
    }
  }

  Future<void> _loadWorkSites() async {
    _isLoading.value = true;

    try {
      final sites = await workSiteService.loadWorkSites();
      _workSites.value = sites;

      if (sites.isNotEmpty && _selectedWorkSite.value == null) {
        _selectedWorkSite.value = sites.first;
      }
    } catch (e) {
      _showErrorSnackBarForLoadWorksite('Failed to load worksites!');
    } finally {
      _isLoading.value = false;
    }
  }

  void _showErrorSnackBarForLoadWorksite(String message){
    String newMessage = "Failed to load worksites! Please try again!";
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(newMessage),
        backgroundColor: Colors.red,
        duration: const Duration(seconds:  3),
      ),
    );
  }


  void _showErrorSnackBar(String message) {
    if (!mounted) return;


    String cleanMessage = "Operation failed! Please try again!";

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(cleanMessage),
        backgroundColor: Colors.red,
        duration: const Duration(seconds: 3),
      ),
    );
  }

  Future<void> _getCurrentLocation() async {
    final position = await locationService.getCurrentLocation();
    if (position != null) {
      _currentPosition.value = position;
    }
  }

  Future<void> _showWorkSiteDialog() async {
    await showDialog(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => Theme(
        data: _theme,
        child: WorkSiteDialog(
          workSites: _workSites.value,
          isLoading: _isLoading.value,
          onRefresh: _loadWorkSites,
          onSelect: (site) async {
            try {
              await workSiteService.selectWorkSite(site.workSiteId ?? 0);
              _selectedWorkSite.value = site;
              Navigator.of(dialogContext).pop();
            } catch (e) {
              Navigator.of(dialogContext).pop();
              _showErrorSnackBar('Failed to select work site: $e');
            }
          },
        ),
      ),
    );
  }

  Future<String?> _captureImage() async {
    try {
      final XFile? image = await _imagePicker.pickImage(
        source: ImageSource.camera,
        imageQuality: 80,
      );

      if (image != null) {
        final File imageFile = File(image.path);
        final bytes = await imageFile.readAsBytes();
        return base64Encode(bytes);
      }
      return null;
    } catch (e) {
      print('Error capturing image: $e');
      return null;
    }
  }

  String _getCurrentFormattedTime() {
    final nyNow = tz.TZDateTime.from(timeService.nowUtc(), _ny);
    return DateFormat('HH:mm:ss').format(nyNow);
  }

  void _showSuccessDialog(bool isPunchIn, String time) {
    if (!mounted) return;

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => PunchSuccessDialog(
        isPunchIn: isPunchIn,
        time: time,
        onOk: () => Navigator.of(dialogContext).pop(),
      ),
    );
  }

  Future<void> _handlePunchIn() async {
    if (_selectedWorkSite.value == null || _currentPosition.value == null) {
      _showErrorSnackBar('Please select work site and enable location');
      return;
    }

    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) return;

    try {
      _isLoading.value = true;

      final Map<String, dynamic> requestData = {
        'workSiteId': _selectedWorkSite.value?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': _currentPosition.value?.latitude,
        'longitude': _currentPosition.value?.longitude
      };

      final response = await dio.post('attendance/punch-in', data: requestData);

      if (!mounted) return;

      if (response.statusCode == 200) {
        final prefs = await SharedPreferences.getInstance();
        final userId = prefs.getInt('user_id') ?? _currentUserId.value ?? 0;

        print('🚀 Starting location tracking for user ID: $userId');
        await _locationTrackingService.startTracking(userId);

        final nowUtcIso = timeService.nowUtc().toIso8601String();
        await prefs.setString('lastPunchInDate', nowUtcIso);
        await prefs.setBool('isPunchedInToday', true);

        _hasPunchIn.value = true;
        _isTrackingActive.value = true;

        final currentTime = _getCurrentFormattedTime();
        _showSuccessDialog(true, currentTime);

        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Row(
              children: [
                Icon(Icons.check_circle, color: Colors.white),
                SizedBox(width: 8),
                Text('Punched in! Location tracking started'),
              ],
            ),
            backgroundColor: Colors.green,
          ),
        );
      }
    } catch (e) {
      if (!mounted) return;
      _showErrorSnackBar('Failed to punch in: $e');
    } finally {
      _isLoading.value = false;
    }
  }

  Future<void> _handlePunchOut() async {
    if (_selectedWorkSite.value == null || _currentPosition.value == null) {
      _showErrorSnackBar('Please select work site and enable location');
      return;
    }

    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) return;

    try {
      _isLoading.value = true;

      final Map<String, dynamic> requestData = {
        'workSiteId': _selectedWorkSite.value?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': _currentPosition.value?.latitude,
        'longitude': _currentPosition.value?.longitude
      };

      final response = await dio.post('attendance/punch-out', data: requestData);

      if (!mounted) return;

      if (response.statusCode == 200) {
        print('🛑 Stopping location tracking for user ID: ${_currentUserId.value}');
        await _locationTrackingService.stopTracking();

        final prefs = await SharedPreferences.getInstance();
        final nowUtcIso = timeService.nowUtc().toIso8601String();
        await prefs.setString('lastPunchOutDate', nowUtcIso);
        await prefs.setBool('isPunchedInToday', false);

        _hasPunchIn.value = false;
        _isTrackingActive.value = false;

        final currentTime = _getCurrentFormattedTime();
        _showSuccessDialog(false, currentTime);

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Row(
              children: [
                const Icon(Icons.stop_circle, color: Colors.white),
                const SizedBox(width: 8),
                Text('Punched out! Stopped tracking for user: ${_currentUserId.value}'),
              ],
            ),
            backgroundColor: Colors.blue,
          ),
        );
      }
    } catch (e) {
      if (!mounted) return;
      _showErrorSnackBar('Failed to punch out: $e');
    } finally {
      _isLoading.value = false;
    }
  }

  @override
  Widget build(BuildContext context) {
    _updateCachedValues();
    final l10n = context.read<LocalizationProvider>().localizations;

    return Scaffold(
      backgroundColor: _theme.scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: _theme.scaffoldBackgroundColor,
        elevation: 0,
        title: Text(
          l10n.get('punch'),
          style: TextStyle(
            color: _theme.textTheme.titleLarge?.color,
            fontSize: _isSmallScreen ? 20 : 22,
            fontWeight: FontWeight.w600,
          ),
        ),
        leading: IconButton(
          icon: Container(
            padding: EdgeInsets.all(_isSmallScreen ? 6 : 8),
            decoration: BoxDecoration(
              color: _theme.brightness == Brightness.dark
                  ? Colors.white.withOpacity(0.05)
                  : Colors.black.withOpacity(0.05),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(
              Icons.arrow_back_ios_new_rounded,
              color: _theme.iconTheme.color,
              size: _isSmallScreen ? 18 : 20,
            ),
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: Stack(
        children: [
          SingleChildScrollView(
            physics: const BouncingScrollPhysics(),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Clock Display - ДОБАВЛЕНО
                Padding(
                  padding: EdgeInsets.symmetric(
                    horizontal: _isSmallScreen ? 16 : 20,
                    vertical: _isSmallScreen ? 12 : 16,
                  ),
                  child: ClockDisplay(
                    textColor: _theme.textTheme.bodyLarge?.color,
                    isSmallScreen: _isSmallScreen,
                    timeStream: timeService.nyTicker(),
                  ),
                ),

                // Map Container - ДОБАВЛЕНО
                ValueListenableBuilder<Position?>(
                  valueListenable: _currentPosition,
                  builder: (context, position, _) {
                    return MapContainer(
                      currentPosition: position,
                      onMapCreated: (controller) => mapController = controller,
                    );
                  },
                ),

                SizedBox(height: _isSmallScreen ? 12 : 16),

                // Work Site Selector
                Container(
                  margin: EdgeInsets.symmetric(
                    horizontal: _isSmallScreen ? 16 : 20,
                    vertical: _isSmallScreen ? 8 : 12,
                  ),
                  child: ValueListenableBuilder<WorkSiteResponse?>(
                    valueListenable: _selectedWorkSite,
                    builder: (context, workSite, _) {
                      return WorkSiteSelectorButton(
                        selectedWorkSite: workSite,
                        onTap: _showWorkSiteDialog,
                        backgroundColor: _theme.brightness == Brightness.dark
                            ? Colors.white.withOpacity(0.05)
                            : Colors.white,
                        textColor: _theme.textTheme.bodyLarge?.color,
                        isSmallScreen: _isSmallScreen,
                      );
                    },
                  ),
                ),

                // Status Indicator
                ValueListenableBuilder<bool>(
                  valueListenable: _hasPunchIn,
                  builder: (context, hasPunchIn, _) {
                    if (!hasPunchIn) return const SizedBox.shrink();

                    return Container(
                      margin: EdgeInsets.symmetric(
                        horizontal: _isSmallScreen ? 16 : 20,
                        vertical: _isSmallScreen ? 8 : 12,
                      ),
                      padding: EdgeInsets.all(_isSmallScreen ? 12 : 16),
                      decoration: BoxDecoration(
                        color: Colors.green.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(14),
                        border: Border.all(
                          color: Colors.green.withOpacity(0.3),
                          width: 1,
                        ),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            Icons.check_circle_rounded,
                            color: Colors.green,
                            size: _isSmallScreen ? 20 : 24,
                          ),
                          SizedBox(width: _isSmallScreen ? 8 : 12),
                          Text(
                            'You are currently Punched In',
                            style: TextStyle(
                              color: Colors.green,
                              fontSize: _isSmallScreen ? 14 : 16,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ],
                      ),
                    );
                  },
                ),

                SizedBox(height: _isSmallScreen ? 80 : 100),
              ],
            ),
          ),

          // Loading Overlay
          ValueListenableBuilder<bool>(
            valueListenable: _isLoading,
            builder: (context, isLoading, _) {
              if (!isLoading) return const SizedBox.shrink();

              return Container(
                color: Colors.black54,
                child: Center(
                  child: Container(
                    padding: EdgeInsets.all(_isSmallScreen ? 20 : 24),
                    decoration: BoxDecoration(
                      color: _theme.scaffoldBackgroundColor,
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const CircularProgressIndicator(
                          color: Colors.blue,
                          strokeWidth: 3,
                        ),
                        SizedBox(height: _isSmallScreen ? 12 : 16),
                        Text(
                          'Processing...',
                          style: TextStyle(
                            color: _theme.textTheme.bodyMedium?.color,
                            fontSize: _isSmallScreen ? 14 : 16,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          ),
        ],
      ),
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: _theme.scaffoldBackgroundColor,
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 10,
              offset: const Offset(0, -4),
            ),
          ],
        ),
        child: ValueListenableBuilder<bool>(
          valueListenable: _isLoading,
          builder: (context, isLoading, _) {
            return PunchButtons(
              onPunchIn: isLoading ? null : _handlePunchIn,
              onPunchOut: isLoading ? null : _handlePunchOut,
              backgroundColor: _theme.scaffoldBackgroundColor,
              buttonColor: _theme.brightness == Brightness.dark
                  ? Colors.white.withOpacity(0.05)
                  : Colors.white,
              textColor: _theme.textTheme.bodyLarge?.color,
            );
          },
        ),
      ),
    );
  }
}