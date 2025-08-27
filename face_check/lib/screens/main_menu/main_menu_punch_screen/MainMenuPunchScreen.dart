import 'package:face_check/screens/main_menu/main_menu_punch_screen/punch_success_dialo.dart';
import 'package:face_check/services/time_service.dart';
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

import '../../../providers/localization_provider.dart';
import '../../../services/ApiService.dart';
import '../../../../../api_client/model/work_site_response.dart';
import '../../../../../api_client/api/worker_attendance_controller_api.dart';
import '../../../../../api_client/serializers.dart';
import '../../../services/location_tracking_service.dart';
import '../../../utils/error_handler.dart';
import '../../../widgets/error_dialog.dart';
import '../drawer/punch_screen/clock_display.dart';
import '../drawer/punch_screen/location_service.dart';
import '../drawer/punch_screen/map_container.dart';
import '../drawer/punch_screen/work_site_dialog.dart';
import '../drawer/punch_screen/work_site_selector.dart';
import '../drawer/punch_screen/work_site_service.dart';

class Mainmenupunchscreen extends StatefulWidget {
  const Mainmenupunchscreen({super.key});

  @override
  State<Mainmenupunchscreen> createState() => _FaceCheckScreenState();
}

class _FaceCheckScreenState extends State<Mainmenupunchscreen>
    with WidgetsBindingObserver {
  // Services
  late final Dio dio;
  late final LocationService locationService;
  late final WorkSiteService workSiteService;
  late final WorkerAttendanceControllerApi attendanceApi;
  late final TimeService timeService;
  late final LocationTrackingService _locationTrackingService;
  final ImagePicker _imagePicker = ImagePicker();

  // ValueNotifiers для оптимизации перерисовок
  late final ValueNotifier<bool> _isLoading;
  late final ValueNotifier<bool> _hasPunchIn;
  late final ValueNotifier<Position?> _currentPosition;
  late final ValueNotifier<WorkSiteResponse?> _selectedWorkSite;
  late final ValueNotifier<List<WorkSiteResponse>> _workSites;
  late final ValueNotifier<bool> _isTrackingActive;
  late final ValueNotifier<int?> _currentUserId;

  // Google Maps Controller
  GoogleMapController? mapController;

  // Кэшированные значения MediaQuery
  late Size _screenSize;
  late bool _isSmallScreen;
  late ThemeData _theme;

  // Константы
  static const double _smallScreenThreshold = 360.0;
  static final tz.Location _ny = tz.getLocation('America/New_York');

  // Предопределенные константы для UI
  static const EdgeInsets _standardPadding = EdgeInsets.symmetric(horizontal: 16);
  static const EdgeInsets _smallPadding = EdgeInsets.symmetric(horizontal: 12);
  static const Duration _snackBarDuration = Duration(seconds: 3);

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);

    // Инициализация ValueNotifiers
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
    _initializeDio();
    timeService = TimeService(dio);
    locationService = LocationService();
    workSiteService = WorkSiteService(dio);
    attendanceApi = WorkerAttendanceControllerApi(dio, serializers);
    _locationTrackingService = LocationTrackingService(dio);

    // Получаем userId при инициализации
    await _fetchAndSaveUserId();

    // Последовательно загружаем данные
    await timeService.sync();
    await _getCurrentLocation();
    await _loadWorkSites();
    await _checkTodayPunchStatus();
    await _checkTrackingStatus();
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
        } else {
          print('⚠️ User ID is null in response');
        }
      }
    } catch (e) {
      print('❌ Error fetching user ID: $e');

      // Пробуем получить из кэша
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

    if (isActive && _currentUserId.value != null) {
      print('🌍 Location tracking is active for user ID: ${_currentUserId.value}');
    }
  }

  void _initializeDio() {
    dio = Dio(BaseOptions(
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

  String _nyYmd(DateTime utc) {
    final d = tz.TZDateTime.from(utc.toUtc(), _ny);
    return '${d.year}-${d.month.toString().padLeft(2,'0')}-${d.day.toString().padLeft(2,'0')}';
  }

  Future<void> _checkTodayPunchStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      // Быстрый флаг
      final bool? flag = prefs.getBool('isPunchedInToday');
      if (flag != null) {
        _hasPunchIn.value = flag;
      }

      final String? inStr  = prefs.getString('lastPunchInDate');
      final String? outStr = prefs.getString('lastPunchOutDate');

      final DateTime nowUtc = timeService.nowUtc();

      final DateTime? inUtc  = (inStr  != null && inStr.isNotEmpty)  ? DateTime.tryParse(inStr )?.toUtc() : null;
      final DateTime? outUtc = (outStr != null && outStr.isNotEmpty) ? DateTime.tryParse(outStr)?.toUtc() : null;

      DateTime? effectiveOutUtc = outUtc;
      if (effectiveOutUtc != null && !_isSameDayNY(effectiveOutUtc, nowUtc)) {
        effectiveOutUtc = null;
      }

      bool? derived;

      if (inUtc != null && effectiveOutUtc == null && _isSameDayNY(inUtc, nowUtc)) {
        derived = true;
      } else if (inUtc != null && effectiveOutUtc != null) {
        final bool inToday  = _isSameDayNY(inUtc, nowUtc);
        final bool outToday = _isSameDayNY(effectiveOutUtc, nowUtc);

        if (inToday || outToday) {
          derived = effectiveOutUtc.isAfter(inUtc) ? false : true;
        }
      } else if (inUtc == null && effectiveOutUtc != null) {
        derived = false;
      }

      if (derived != null) {
        _hasPunchIn.value = derived;
        await prefs.setBool('isPunchedInToday', _hasPunchIn.value);
      }
    } catch (e) {
      print('Error checking punch status: $e');
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
      _isLoading.value = false;
    } catch (e) {
      _isLoading.value = false;
      _showErrorSnackBar('Failed to load work sites: $e');
    }
  }

  void _showErrorSnackBar(String message) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
        duration: _snackBarDuration,
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
    final nyNow = timeService.nowNY();
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

  Future<void> _handlePunchInOut() async {
    if (!_hasPunchIn.value) {
      await _handlePunchInWithCamera();
    } else {
      await _handlePunchOutWithCamera();
    }
  }

  Future<void> _handlePunchInWithCamera() async {
    // Валидация
    if (_selectedWorkSite.value == null) {
      _showErrorDialog(
        title: 'Work Site Required',
        message: 'Please select a work site before punching in.',
      );
      return;
    }

    if (_currentPosition.value == null) {
      _showErrorDialog(
        title: 'Location Required',
        message: 'Please enable location services to punch in.',
        onRetry: _getCurrentLocation,
      );
      return;
    }

    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      _showErrorDialog(
        title: 'Photo Required',
        message: 'A photo is required to punch in. Please take a photo.',
      );
      return;
    }

    try {
      _isLoading.value = true;

      final Map<String, dynamic> requestData = {
        'workSiteId': _selectedWorkSite.value?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': _currentPosition.value?.latitude,
        'longitude': _currentPosition.value?.longitude
      };

      final response = await dio.post(
        'attendance/punch-in',
        data: requestData,
      );

      if (!mounted) return;

      if (response.statusCode! >= 200 && response.statusCode! < 300) {
        final prefs = await SharedPreferences.getInstance();
        final nowUtcIso = timeService.nowUtc().toIso8601String();

        final userId = prefs.getInt('user_id') ?? _currentUserId.value ?? 0;

        print('🚀 Starting location tracking for user ID: $userId');
        await _locationTrackingService.startTracking(userId);

        await prefs.setString('lastPunchInDate', nowUtcIso);
        await prefs.setBool('isPunchedInToday', true);

        // Очистка старого punch out
        final String? outStr = prefs.getString('lastPunchOutDate');
        if (outStr != null && outStr.isNotEmpty) {
          final DateTime? outUtc = DateTime.tryParse(outStr)?.toUtc();
          if (outUtc != null) {
            final inUtc = DateTime.tryParse(nowUtcIso)!.toUtc();
            if (!_isSameDayNY(outUtc, inUtc) || !outUtc.isAfter(inUtc)) {
              await prefs.remove('lastPunchOutDate');
            }
          }
        }

        _isLoading.value = false;
        _hasPunchIn.value = true;
        _isTrackingActive.value = true;

        final currentTime = _getCurrentFormattedTime();
        _showSuccessDialog(true, currentTime);

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Location tracking started for user: $userId'),
            backgroundColor: Colors.green,
          ),
        );
      }
    } catch (e) {
      if (!mounted) return;
      _isLoading.value = false;

      final errorMessage = ErrorHandler.getErrorMessage(e);

      _showErrorDialog(
        title: 'Punch In Failed',
        message: errorMessage,
        details: _isDebugMode() ? e.toString() : null,
        onRetry: () => _handlePunchInWithCamera(),
      );
    }
  }

  Future<void> _handlePunchOutWithCamera() async {
    // Валидация
    if (_selectedWorkSite.value == null) {
      _showErrorDialog(
        title: 'Work Site Required',
        message: 'Please select a work site before punching out.',
      );
      return;
    }

    if (_currentPosition.value == null) {
      _showErrorDialog(
        title: 'Location Required',
        message: 'Please enable location services to punch out.',
        onRetry: _getCurrentLocation,
      );
      return;
    }

    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      _showErrorDialog(
        title: 'Photo Required',
        message: 'A photo is required to punch out. Please take a photo.',
      );
      return;
    }

    try {
      _isLoading.value = true;

      final Map<String, dynamic> requestData = {
        'workSiteId': _selectedWorkSite.value?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': _currentPosition.value?.latitude,
        'longitude': _currentPosition.value?.longitude
      };

      final response = await dio.post(
        'attendance/punch-out',
        data: requestData,
      );

      if (!mounted) return;

      if (response.statusCode! >= 200 && response.statusCode! < 300) {
        print('🛑 Stopping location tracking for user ID: ${_currentUserId.value}');
        await _locationTrackingService.stopTracking();

        final prefs = await SharedPreferences.getInstance();
        final nowUtcIso = timeService.nowUtc().toIso8601String();

        await prefs.setString('lastPunchOutDate', nowUtcIso);
        await prefs.setBool('isPunchedInToday', false);

        _isLoading.value = false;
        _hasPunchIn.value = false;
        _isTrackingActive.value = false;

        final currentTime = _getCurrentFormattedTime();
        _showSuccessDialog(false, currentTime);

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Location tracking stopped for user: ${_currentUserId.value}'),
            backgroundColor: Colors.blue,
          ),
        );
      }
    } catch (e) {
      if (!mounted) return;
      _isLoading.value = false;

      final errorMessage = ErrorHandler.getErrorMessage(e);

      _showErrorDialog(
        title: 'Punch Out Failed',
        message: errorMessage,
        details: _isDebugMode() ? e.toString() : null,
        onRetry: () => _handlePunchOutWithCamera(),
      );
    }
  }

  void _showErrorDialog({
    required String title,
    required String message,
    String? details,
    VoidCallback? onRetry,
  }) {
    if (!mounted) return;

    showDialog(
      context: context,
      builder: (context) => ErrorDialog(
        title: title,
        message: message,
        details: details,
        onRetry: onRetry,
      ),
    );
  }

  bool _isDebugMode() {
    bool inDebugMode = false;
    assert(inDebugMode = true);
    return inDebugMode;
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
            fontSize: _isSmallScreen ? 18 : 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: _theme.iconTheme.color,
            size: _isSmallScreen ? 22 : 24,
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
        actions: _isDebugMode()
            ? [
          ValueListenableBuilder<int?>(
            valueListenable: _currentUserId,
            builder: (context, userId, _) {
              if (userId == null) return const SizedBox.shrink();
              return Padding(
                padding: const EdgeInsets.only(right: 16),
                child: Center(
                  child: Text(
                    'ID: $userId',
                    style: TextStyle(
                      color: _theme.textTheme.bodySmall?.color,
                      fontSize: 12,
                    ),
                  ),
                ),
              );
            },
          ),
        ]
            : null,
      ),
      body: Stack(
        children: [
          Container(
            color: _theme.scaffoldBackgroundColor,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(height: _isSmallScreen ? 16 : 20),

                // Clock Display
                ClockDisplay(
                  textColor: _theme.textTheme.bodyLarge?.color ?? Colors.white,
                  isSmallScreen: _isSmallScreen,
                  timeStream: timeService.nyTicker(),
                ),

                SizedBox(height: _isSmallScreen ? 16 : 20),

                // Map Container with ValueListenableBuilder
                ValueListenableBuilder<Position?>(
                  valueListenable: _currentPosition,
                  builder: (context, position, _) {
                    return MapContainer(
                      currentPosition: position,
                      onMapCreated: (controller) => mapController = controller,
                    );
                  },
                ),

                SizedBox(height: _isSmallScreen ? 16 : 20),

                // Work Site Selector
                Padding(
                  padding: _isSmallScreen ? _smallPadding : _standardPadding,
                  child: ValueListenableBuilder<WorkSiteResponse?>(
                    valueListenable: _selectedWorkSite,
                    builder: (context, workSite, _) {
                      return WorkSiteSelectorButton(
                        selectedWorkSite: workSite,
                        onTap: _showWorkSiteDialog,
                        backgroundColor: _theme.brightness == Brightness.dark
                            ? Colors.grey[900]
                            : Colors.grey[100],
                        textColor: _theme.textTheme.bodyLarge?.color,
                        isSmallScreen: _isSmallScreen,
                      );
                    },
                  ),
                ),

                SizedBox(height: _isSmallScreen ? 16 : 20),

                // Debug tracking status
                if (_isDebugMode())
                  ValueListenableBuilder<bool>(
                    valueListenable: _isTrackingActive,
                    builder: (context, isActive, _) {
                      if (!isActive) return const SizedBox.shrink();

                      return ValueListenableBuilder<int?>(
                        valueListenable: _currentUserId,
                        builder: (context, userId, _) {
                          if (userId == null) return const SizedBox.shrink();

                          return Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 16),
                            child: Container(
                              padding: const EdgeInsets.all(8),
                              decoration: BoxDecoration(
                                color: Colors.green.withOpacity(0.1),
                                borderRadius: BorderRadius.circular(8),
                                border: Border.all(
                                  color: Colors.green.withOpacity(0.3),
                                ),
                              ),
                              child: Row(
                                children: [
                                  const Icon(
                                    Icons.location_on,
                                    color: Colors.green,
                                    size: 16,
                                  ),
                                  const SizedBox(width: 8),
                                  Text(
                                    'Tracking active for user: $userId',
                                    style: const TextStyle(
                                      color: Colors.green,
                                      fontSize: 12,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          );
                        },
                      );
                    },
                  ),
              ],
            ),
          ),

          // Loading overlay
          ValueListenableBuilder<bool>(
            valueListenable: _isLoading,
            builder: (context, isLoading, _) {
              if (!isLoading) return const SizedBox.shrink();

              return Container(
                color: Colors.black54,
                child: Center(
                  child: CircularProgressIndicator(
                    color: _theme.brightness == Brightness.dark
                        ? Colors.white
                        : Colors.blue,
                  ),
                ),
              );
            },
          ),
        ],
      ),
      bottomNavigationBar: Container(
        color: _theme.scaffoldBackgroundColor,
        padding: EdgeInsets.only(bottom: _isSmallScreen ? 16 : 20),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ValueListenableBuilder<bool>(
              valueListenable: _isLoading,
              builder: (context, isLoading, _) {
                return ValueListenableBuilder<bool>(
                  valueListenable: _hasPunchIn,
                  builder: (context, hasPunchIn, _) {
                    return Material(
                      color: Colors.transparent,
                      child: InkWell(
                        onTap: isLoading ? null : _handlePunchInOut,
                        borderRadius: BorderRadius.circular(
                          _isSmallScreen ? 22 : 25,
                        ),
                        child: Container(
                          width: _isSmallScreen ? 65 : 75,
                          height: _isSmallScreen ? 65 : 75,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: (hasPunchIn ? Colors.blue : Colors.green)
                                .withOpacity(0.2),
                            border: Border.all(
                              color: hasPunchIn ? Colors.blue : Colors.green,
                              width: _isSmallScreen ? 1.5 : 2,
                            ),
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(
                                hasPunchIn ? Icons.logout : Icons.login,
                                color: hasPunchIn ? Colors.blue : Colors.green,
                                size: _isSmallScreen ? 20 : 24,
                              ),
                              SizedBox(height: _isSmallScreen ? 2 : 4),
                              Text(
                                hasPunchIn
                                    ? l10n.get('punchOut')
                                    : l10n.get('punchIn'),
                                style: TextStyle(
                                  color: hasPunchIn ? Colors.blue : Colors.green,
                                  fontSize: _isSmallScreen ? 10 : 12,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  },
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}