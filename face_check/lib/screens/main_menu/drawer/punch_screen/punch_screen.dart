import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_dialog.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_selector.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_service.dart';
import 'package:face_check/screens/main_menu/main_menu_punch_screen/punch_success_dialo.dart';
import 'package:face_check/services/location_tracking_service.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';
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
import '../../main_menu_punch_screen/punch_manager.dart';
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

  // State
  late final ValueNotifier<bool> _isLoading;
  late final ValueNotifier<Position?> _currentPosition;
  late final ValueNotifier<WorkSiteResponse?> _selectedWorkSite;
  late final ValueNotifier<List<WorkSiteResponse>> _workSites;
  late final ValueNotifier<bool> _isTrackingActive;
  late final ValueNotifier<int?> _currentUserId;
  late final PunchManager _punchManager;

  // ✅ NEW: Notes controller
  final TextEditingController _notesController = TextEditingController();
  static const int _maxNotesLength = 3000;

  // Map
  GoogleMapController? mapController;

  // UI cache
  late Size _screenSize;
  late bool _isSmallScreen;
  late ThemeData _theme;

  // Const
  static const double _smallScreenThreshold = 360.0;
  static final tz.Location _ny = tz.getLocation('America/New_York');
  static const Duration _snackBarDuration = Duration(seconds: 3);
  static const EdgeInsets _standardPadding = EdgeInsets.symmetric(horizontal: 20);
  static const EdgeInsets _smallPadding = EdgeInsets.symmetric(horizontal: 16);

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);

    _isLoading = ValueNotifier<bool>(false);
    _punchManager = PunchManager();
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
    _punchManager.dispose();
    _currentPosition.dispose();
    _selectedWorkSite.dispose();
    _workSites.dispose();
    _isTrackingActive.dispose();
    _currentUserId.dispose();
    _notesController.dispose(); // ✅ NEW
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      timeService.sync().then((_) => _punchManager.forceRefresh());
    }
  }

  // ✅ NEW: Get notes text (trimmed, or null if empty)
  String? _getNotesText() {
    final text = _notesController.text.trim();
    return text.isEmpty ? null : text;
  }

  // ✅ NEW: Clear notes after successful punch
  void _clearNotes() {
    _notesController.clear();
  }

  // ---------- Init ----------
  void _initializeDependencies() async {
    _isLoading.value = true;

    _initializeDio();
    timeService = TimeService(dio);
    locationService = LocationService();
    workSiteService = WorkSiteService(dio);
    attendanceApi = WorkerAttendanceControllerApi(dio, serializers);
    _locationTrackingService = LocationTrackingService(dio);

    try {
      await Future.wait([
        _fetchAndSaveUserId(),
        timeService.sync(),
        _getCurrentLocation(),
        _loadWorkSites(),
      ]);

      await _checkTodayPunchStatus();
      await _checkTrackingStatus();
    } catch (e) {
      debugPrint('❌ Error during initialization: $e');
    } finally {
      _isLoading.value = false;
    }
  }

  void _initializeDio() {
    dio = Dio(BaseOptions(
      baseUrl: 'https://face-check-prod-drgsy.ondigitalocean.app/api/v1/',
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 45),
      sendTimeout: const Duration(seconds: 15),
      headers: const {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      validateStatus: (status) => status != null && status < 500,
    ));

    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        try {
          final token = await ApiService.instance.getAuthToken();
          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
          }
        } catch (_) {}
        return handler.next(options);
      },
      onError: (DioException e, handler) {
        debugPrint('Dio Error: ${e.message}');
        return handler.next(e);
      },
    ));
  }


  Future<bool> _ensureCameraPermission() async {
    final status = await Permission.camera.status;

    if (status.isGranted) {
      return true;
    }

    // Запрашиваем
    final newStatus = await Permission.camera.request();

    if (newStatus.isGranted) {
      return true;
    }

    // Пользователь отказал
    await _showCameraPermissionDialog(newStatus);
    return false;
  }

  Future<void> _showCameraPermissionDialog(PermissionStatus status) async {
    if (!mounted) return;

    String message;
    List<Widget> actions = [];

    if (status.isPermanentlyDenied || status.isRestricted) {
      message =
      'Camera access is disabled. To use photo verification when punching in or out, please enable camera access in Settings.';
      actions = [
        TextButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: const Text('Cancel'),
        ),
        TextButton(
          onPressed: () async {
            Navigator.of(context).pop();
            await openAppSettings();
          },
          child: const Text('Open Settings'),
        ),
      ];
    } else {
      // обычный отказ "Don\'t Allow"
      message =
      'Camera access is required to take your photo for punch in/out. You can continue using the app, but photo-based punch will not work until you allow camera access.';
      actions = [
        TextButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: const Text('OK'),
        ),
      ];
    }

    await showDialog(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Camera permission'),
          content: Text(message),
          actions: actions,
        );
      },
    );
  }


  // ---------- Helpers ----------
  Future<void> _fetchAndSaveUserId() async {
    try {
      final response = await dio.get('user/find-user-id');
      if (response.statusCode == 200 && response.data != null) {
        final userId = response.data['userId'];
        if (userId != null) {
          final prefs = await SharedPreferences.getInstance();
          await prefs.setInt('user_id', userId);
          _currentUserId.value = userId;
        }
      }
    } catch (e) {
      final prefs = await SharedPreferences.getInstance();
      final savedUserId = prefs.getInt('user_id');
      if (savedUserId != null && savedUserId != 0) {
        _currentUserId.value = savedUserId;
      }
    }
  }

  Future<void> _checkTrackingStatus() async {
    final isActive = await _locationTrackingService.isTrackingActive();
    _isTrackingActive.value = isActive;
  }

  bool _isSameDayNY(DateTime aUtc, DateTime bUtc) {
    final aNy = tz.TZDateTime.from(aUtc.toUtc(), _ny);
    final bNy = tz.TZDateTime.from(bUtc.toUtc(), _ny);
    return aNy.year == bNy.year && aNy.month == bNy.month && aNy.day == bNy.day;
  }

  Future<void> _checkTodayPunchStatus() async {
    if (_currentUserId.value != null) {
      _punchManager.setUserId(_currentUserId.value!);
      await _punchManager.checkPunchStatus();
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
      _showErrorSnackBar('Failed to load worksites! Please try again!');
    } finally {
      _isLoading.value = false;
    }
  }

  Future<void> _getCurrentLocation() async {
    try {
      final position = await locationService.getCurrentLocation();
      if (!mounted) return;
      if (position != null) {
        _currentPosition.value = position;
      }
    } catch (e) {
      debugPrint('❌ _getCurrentLocation error: $e');
      if (!mounted) return;
      _showErrorSnackBar('Cannot get location. Please enable GPS and try again.');
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
              _showErrorSnackBar('Failed to select work site. Try again.');
            }
          },
        ),
      ),
    );
  }

  Future<String?> _captureImage() async {
    try {
      // 1) Проверяем и запрашиваем разрешение
      final hasPermission = await _ensureCameraPermission();
      if (!hasPermission) {
        // тут уже показали диалог, просто выходим
        return null;
      }

      // 2) Открываем камеру
      final XFile? image = await _imagePicker.pickImage(
        source: ImageSource.camera,
        imageQuality: 70,
        maxWidth: 1024,
        maxHeight: 1024,
      );

      // Пользователь мог нажать "Cancel" в камере
      if (image == null) {
        // Никаких ошибок, просто отмена
        return null;
      }

      final bytes = await File(image.path).readAsBytes();
      return base64Encode(bytes);
    } catch (e) {
      debugPrint('Error capturing image: $e');
      // Можно показать более дружелюбный текст
      _showErrorSnackBar('Could not open camera. Please try again.');
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

  String _cleanErrorMessage(String message) {
    final regex = RegExp(r'(\d{2}:\d{2}:\d{2})\.\d+');
    return message.replaceAllMapped(regex, (m) => m.group(1)!);
  }

  bool _isDebugMode() {
    bool inDebugMode = false;
    assert(inDebugMode = true);
    return inDebugMode;
  }

  // ---------- Punch handlers ----------
  Future<void> _handlePunchIn() async {
    if (_selectedWorkSite.value == null || _currentPosition.value == null) {
      _showErrorSnackBar('Please select work site and enable location');
      return;
    }

    final photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      return;
    }

    _isLoading.value = true;
    try {
      final requestData = {
        'workSiteId': _selectedWorkSite.value?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': _currentPosition.value?.latitude,
        'longitude': _currentPosition.value?.longitude,
        'notesForPunchIn': _getNotesText(), // ✅ NEW: Send notes
      };

      final response = await dio.post('attendance/punch-in', data: requestData);

      if (response.statusCode != null &&
          response.statusCode! >= 200 &&
          response.statusCode! < 300) {
        final prefs = await SharedPreferences.getInstance();
        final userId = prefs.getInt('user_id') ?? _currentUserId.value ?? 0;

        await _locationTrackingService.startTracking(userId);

        final nowUtcIso = timeService.nowUtc().toIso8601String();
        await prefs.setString('lastPunchInDate', nowUtcIso);
        await prefs.setBool('isPunchedInToday', true);

        // сброс потенциально неконсистентного punchOut
        final outStr = prefs.getString('lastPunchOutDate');
        if (outStr != null && outStr.isNotEmpty) {
          final outUtc = DateTime.tryParse(outStr)?.toUtc();
          final inUtc = DateTime.tryParse(nowUtcIso)!.toUtc();
          if (outUtc != null) {
            if (!_isSameDayNY(outUtc, inUtc) || !outUtc.isAfter(inUtc)) {
              await prefs.remove('lastPunchOutDate');
            }
          }
        }

        await _punchManager.onPunchInSuccess();
        _isTrackingActive.value = true;

        _clearNotes(); // ✅ NEW: Clear notes only on success

        _showSuccessDialog(true, _getCurrentFormattedTime());
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Punched in! Tracking started for user: $userId'),
              backgroundColor: Colors.green,
            ),
          );
        }
      } else {
        // ❌ Error — notes NOT cleared
        String errorMessage = 'Punch in failed';
        if (response.data is Map) {
          final raw = response.data['message'] ??
              response.data['error'] ??
              'Server returned error: ${response.statusCode}';
          errorMessage = _cleanErrorMessage(raw.toString());
        }
        _showErrorSnackBar(errorMessage);
      }
    } catch (e) {
      // ❌ Error — notes NOT cleared
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

    final photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      return;
    }

    _isLoading.value = true;
    try {
      final requestData = {
        'workSiteId': _selectedWorkSite.value?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': _currentPosition.value?.latitude,
        'longitude': _currentPosition.value?.longitude,
        'notesForPunchOut': _getNotesText(), // ✅ NEW: Send notes
      };

      final response = await dio.post('attendance/punch-out', data: requestData);

      if (response.statusCode != null &&
          response.statusCode! >= 200 &&
          response.statusCode! < 300) {
        await _locationTrackingService.stopTracking();

        final prefs = await SharedPreferences.getInstance();
        final nowUtcIso = timeService.nowUtc().toIso8601String();
        await prefs.setString('lastPunchOutDate', nowUtcIso);
        await prefs.setBool('isPunchedInToday', false);

        await _punchManager.onPunchOutSuccess();
        _isTrackingActive.value = false;

        _clearNotes(); // ✅ NEW: Clear notes only on success

        _showSuccessDialog(false, _getCurrentFormattedTime());
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(
                  'Punched out! Location tracking stopped for user: ${_currentUserId.value}'),
              backgroundColor: Colors.blue,
            ),
          );
        }
      } else {
        // ❌ Error — notes NOT cleared
        String errorMessage = 'Punch out failed';
        if (response.data is Map) {
          final raw = response.data['message'] ??
              response.data['error'] ??
              'Server returned error: ${response.statusCode}';
          errorMessage = _cleanErrorMessage(raw.toString());
        }
        _showErrorSnackBar(errorMessage);
      }
    } catch (e) {
      // ❌ Error — notes NOT cleared
      _showErrorSnackBar('Failed to punch out: $e');
    } finally {
      _isLoading.value = false;
    }
  }

  // ✅ NEW: Build the Notes section widget — dynamic based on punch state
  Widget _buildNotesSection() {
    final isDark = _theme.brightness == Brightness.dark;

    return ValueListenableBuilder<bool>(
      valueListenable: _punchManager.hasPunchIn,
      builder: (context, hasPunchIn, _) {
        final bool isPunchOut = hasPunchIn;
        final String label = isPunchOut ? 'Notes for Punch Out' : 'Notes for Punch In';
        final String hintText = isPunchOut
            ? 'What was done today...\n\nExample:\n- Installed wiring on 3rd floor\n- Fixed outlet in room 204'
            : 'Notes before starting work...\n\nExample:\n- Starting electrical work\n- Materials received';
        final Color accentColor = isPunchOut ? Colors.blue : Colors.green;

        return Padding(
          padding: _isSmallScreen ? _smallPadding : _standardPadding,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header row
              Row(
                children: [
                  Icon(
                    isPunchOut ? Icons.edit_note_rounded : Icons.notes_rounded,
                    size: _isSmallScreen ? 18 : 20,
                    color: accentColor.withOpacity(0.7),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    label,
                    style: TextStyle(
                      fontSize: _isSmallScreen ? 14 : 16,
                      fontWeight: FontWeight.w600,
                      color: _theme.textTheme.bodyLarge?.color,
                    ),
                  ),
                  const SizedBox(width: 6),
                  Text(
                    '(optional)',
                    style: TextStyle(
                      fontSize: _isSmallScreen ? 11 : 12,
                      color: _theme.textTheme.bodySmall?.color?.withOpacity(0.5),
                      fontStyle: FontStyle.italic,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),

              // TextField container
              Container(
                decoration: BoxDecoration(
                  color: isDark ? Colors.grey[900] : Colors.grey[100],
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(
                    color: accentColor.withOpacity(0.3),
                    width: 1,
                  ),
                ),
                child: Column(
                  children: [
                    TextField(
                      controller: _notesController,
                      maxLength: _maxNotesLength,
                      maxLines: 6,
                      minLines: 4,
                      keyboardType: TextInputType.multiline,
                      textInputAction: TextInputAction.newline,
                      style: TextStyle(
                        fontSize: _isSmallScreen ? 14 : 15,
                        color: _theme.textTheme.bodyLarge?.color,
                        height: 1.4,
                      ),
                      decoration: InputDecoration(
                        hintText: hintText,
                        hintMaxLines: 5,
                        hintStyle: TextStyle(
                          fontSize: _isSmallScreen ? 13 : 14,
                          color: _theme.textTheme.bodySmall?.color?.withOpacity(0.35),
                          height: 1.4,
                        ),
                        contentPadding: EdgeInsets.symmetric(
                          horizontal: _isSmallScreen ? 12 : 16,
                          vertical: _isSmallScreen ? 12 : 14,
                        ),
                        border: InputBorder.none,
                        counterText: '',
                      ),
                    ),

                    // Custom character counter
                    Padding(
                      padding: const EdgeInsets.only(right: 12, bottom: 8),
                      child: Align(
                        alignment: Alignment.centerRight,
                        child: ValueListenableBuilder<TextEditingValue>(
                          valueListenable: _notesController,
                          builder: (context, value, _) {
                            final length = value.text.length;
                            final isNearLimit = length > (_maxNotesLength * 0.9);

                            return Text(
                              '$length / $_maxNotesLength',
                              style: TextStyle(
                                fontSize: 11,
                                color: isNearLimit
                                    ? Colors.orange
                                    : _theme.textTheme.bodySmall?.color?.withOpacity(0.4),
                                fontWeight: isNearLimit ? FontWeight.w600 : FontWeight.normal,
                              ),
                            );
                          },
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  // ---------- UI ----------
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
      // ✅ NEW: GestureDetector to dismiss keyboard on tap outside
      body: GestureDetector(
        onTap: () => FocusScope.of(context).unfocus(),
        child: Stack(
          children: [
            SingleChildScrollView(
              physics: const BouncingScrollPhysics(),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Clock
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

                  // Map
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

                  // ✅ NEW: Notes section
                  _buildNotesSection(),

                  SizedBox(height: _isSmallScreen ? 8 : 12),

                  // Status Badge
                  ValueListenableBuilder<bool>(
                    valueListenable: _punchManager.hasPunchIn,
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

                  SizedBox(height: _isSmallScreen ? 88 : 104),
                ],
              ),
            ),

            // Loading overlay
            ValueListenableBuilder<bool>(
              valueListenable: _isLoading,
              builder: (context, isLoading, _) {
                if (!isLoading) return const SizedBox.shrink();

                return Positioned.fill(
                  child: Stack(
                    children: const [
                      ModalBarrier(dismissible: false, color: Colors.transparent),
                      Center(child: CircularProgressIndicator()),
                    ],
                  ),
                );
              },
            ),

          ],
        ),
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
        padding: EdgeInsets.fromLTRB(
          16,
          10,
          16,
          _isSmallScreen ? 16 : 20,
        ),
        child: ValueListenableBuilder<bool>(
          valueListenable: _isLoading,
          builder: (context, isLoading, _) {
            return ValueListenableBuilder<bool>(
              valueListenable: _punchManager.hasPunchIn,
              builder: (context, hasPunchIn, __) {
                final inEnabled = !isLoading && !hasPunchIn;
                final outEnabled = !isLoading && hasPunchIn;

                return SafeArea(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      // Punch In
                      _ActionCircleButton(
                        label: l10n.get('punchIn'),
                        icon: Icons.login,
                        color: Colors.green,
                        enabled: inEnabled,
                        onTap: inEnabled ? _handlePunchIn : null,
                        small: _isSmallScreen,
                      ),
                      // Punch Out
                      _ActionCircleButton(
                        label: l10n.get('punchOut'),
                        icon: Icons.logout,
                        color: Colors.blue,
                        enabled: outEnabled,
                        onTap: outEnabled ? _handlePunchOut : null,
                        small: _isSmallScreen,
                      ),
                    ],
                  ),
                );
              },
            );
          },
        ),
      ),
    );
  }
}
class _ActionCircleButton extends StatelessWidget {
  final String label;
  final IconData icon;
  final Color color;
  final bool enabled;
  final VoidCallback? onTap;
  final bool small;

  const _ActionCircleButton({
    required this.label,
    required this.icon,
    required this.color,
    required this.enabled,
    required this.onTap,
    this.small = false,
  });

  @override
  Widget build(BuildContext context) {
    final double size = small ? 65 : 75;
    final double iconSize = small ? 20 : 24;
    final double fontSize = small ? 9 : 11;

    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: enabled ? onTap : null,
        borderRadius: BorderRadius.circular(small ? 22 : 25),
        child: Opacity(
          opacity: enabled ? 1.0 : 0.4,
          child: Container(
            width: size,
            height: size,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: color.withOpacity(0.12),
              border: Border.all(
                color: color,
                width: small ? 1.5 : 2,
              ),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(icon, color: color, size: iconSize),
                SizedBox(height: small ? 2 : 4),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 4),
                  child: Text(
                    label,
                    textAlign: TextAlign.center,
                    maxLines: 1,
                    overflow: TextOverflow.clip,
                    style: TextStyle(
                      color: color,
                      fontSize: fontSize,
                      fontWeight: FontWeight.bold,
                      letterSpacing: -0.5,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}