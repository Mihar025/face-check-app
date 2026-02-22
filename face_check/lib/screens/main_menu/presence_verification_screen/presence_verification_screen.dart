import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:geolocator/geolocator.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter/foundation.dart';

import '../../../services/ApiService.dart';
import '../../../api_client/model/work_site_response.dart';
import '../../../widgets/error_dialog.dart';
import '../../../utils/error_handler.dart';
import '../main_menu_punch_screen/retry-interceptor.dart';
import '../drawer/punch_screen/location_service.dart';
import '../drawer/punch_screen/work_site_service.dart';
import '../drawer/punch_screen/work_site_dialog.dart';

class PresenceVerificationScreen extends StatefulWidget {
  final int verificationId;

  const PresenceVerificationScreen({
    super.key,
    required this.verificationId,
  });

  @override
  State<PresenceVerificationScreen> createState() => _PresenceVerificationScreenState();
}

class _PresenceVerificationScreenState extends State<PresenceVerificationScreen> {
  // Services
  late final Dio _dio;
  late final LocationService _locationService;
  late final WorkSiteService _workSiteService;
  final ImagePicker _imagePicker = ImagePicker();

  // State
  final ValueNotifier<bool> _isLoading = ValueNotifier(false);
  final ValueNotifier<Position?> _currentPosition = ValueNotifier(null);
  final ValueNotifier<WorkSiteResponse?> _selectedWorkSite = ValueNotifier(null);
  final ValueNotifier<List<WorkSiteResponse>> _workSites = ValueNotifier([]);
  final ValueNotifier<int> _secondsRemaining = ValueNotifier(300); // 5 min

  Timer? _countdownTimer;
  bool _isSubmitting = false;
  bool _isCompleted = false;

  @override
  void initState() {
    super.initState();
    _initDio();
    _locationService = LocationService();
    _workSiteService = WorkSiteService(_dio);
    _initialize();
    _startCountdown();
  }

  @override
  void dispose() {
    _countdownTimer?.cancel();
    _isLoading.dispose();
    _currentPosition.dispose();
    _selectedWorkSite.dispose();
    _workSites.dispose();
    _secondsRemaining.dispose();
    super.dispose();
  }

  void _initDio() {
    _dio = Dio(BaseOptions(
      baseUrl: 'https://face-check-prod-drgsy.ondigitalocean.app/api/v1/',
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 45),
      sendTimeout: const Duration(seconds: 15),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      validateStatus: (status) => status != null && status < 500,
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        try {
          final token = await ApiService.instance.getAuthToken();
          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
          }
        } catch (_) {}
        return handler.next(options);
      },
    ));

    _dio.interceptors.add(RetryInterceptor(dio: _dio));
  }

  Future<void> _initialize() async {
    _isLoading.value = true;
    try {
      await Future.wait([
        _loadWorkSites(),
        _getCurrentLocation(),
      ]);
    } catch (e) {
      debugPrint('Init error: $e');
    } finally {
      _isLoading.value = false;
    }
  }

  void _startCountdown() {
    _countdownTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_secondsRemaining.value <= 0) {
        timer.cancel();
        if (!_isCompleted && mounted) {
          _showTimeoutDialog();
        }
        return;
      }
      _secondsRemaining.value--;
    });
  }

  Future<void> _loadWorkSites() async {
    try {
      final sites = await _workSiteService.loadWorkSites();
      _workSites.value = sites;
      if (sites.isNotEmpty && _selectedWorkSite.value == null) {
        _selectedWorkSite.value = sites.first;
      }
    } catch (e) {
      debugPrint('Failed to load work sites: $e');
    }
  }

  Future<void> _getCurrentLocation() async {
    try {
      final position = await _locationService.getCurrentLocation();
      if (position != null) {
        _currentPosition.value = position;
      }
    } catch (e) {
      debugPrint('Location error: $e');
    }
  }

  Future<bool> _ensureCameraPermission() async {
    final status = await Permission.camera.status;
    if (status.isGranted) return true;

    final newStatus = await Permission.camera.request();
    if (newStatus.isGranted) return true;

    if (mounted) {
      await showDialog(
        context: context,
        builder: (_) => AlertDialog(
          title: const Text('Camera Permission'),
          content: Text(newStatus.isPermanentlyDenied
              ? 'Camera access is disabled. Please enable it in Settings.'
              : 'Camera access is required for verification.'),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            if (newStatus.isPermanentlyDenied)
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                  openAppSettings();
                },
                child: const Text('Open Settings'),
              ),
          ],
        ),
      );
    }
    return false;
  }

  Future<String?> _captureImage() async {
    final hasPermission = await _ensureCameraPermission();
    if (!hasPermission) return null;

    try {
      final XFile? image = await _imagePicker.pickImage(
        source: ImageSource.camera,
        imageQuality: 50,
        maxWidth: 1024,
        maxHeight: 1024,
      );
      if (image == null) return null;

      final bytes = await File(image.path).readAsBytes();
      return base64Encode(bytes);
    } catch (e) {
      debugPrint('Camera error: $e');
      return null;
    }
  }

  Future<void> _handleVerify() async {
    if (_isSubmitting) return;

    // Validations
    if (_selectedWorkSite.value == null) {
      _showErrorDialog(
        title: 'Work Site Required',
        message: 'Please select a work site before verifying.',
      );
      return;
    }

    if (_currentPosition.value == null) {
      _showErrorDialog(
        title: 'Location Required',
        message: 'Please enable location services.',
        onRetry: _getCurrentLocation,
      );
      return;
    }

    // Camera
    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) return;

    _isSubmitting = true;
    _isLoading.value = true;

    try {
      final requestData = {
        'verificationId': widget.verificationId,
        'workSiteId': _selectedWorkSite.value?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': _currentPosition.value?.latitude,
        'longitude': _currentPosition.value?.longitude,
      };

      final response = await _dio.post(
        'remote-worker/random-attendance-verification',
        data: requestData,
      );

      if (!mounted) return;

      if (response.statusCode! >= 200 && response.statusCode! < 300) {
        final data = response.data;

        if (data['isSuccessful'] == true) {
          _isCompleted = true;
          _countdownTimer?.cancel();
          _isLoading.value = false;
          _showSuccessDialog();
        } else {
          _isLoading.value = false;
          _showErrorDialog(
            title: 'Verification Failed',
            message: data['message'] ?? 'Verification was not successful.',
          );
        }
      } else {
        _isLoading.value = false;
        String errorMessage = 'Verification failed';
        if (response.data is Map) {
          errorMessage = response.data['message'] ??
              response.data['error'] ??
              'Server error: ${response.statusCode}';
        }
        _showErrorDialog(
          title: 'Verification Failed',
          message: errorMessage,
          onRetry: _handleVerify,
        );
      }
    } catch (e) {
      _isLoading.value = false;
      if (!mounted) return;
      _showErrorDialog(
        title: 'Verification Failed',
        message: ErrorHandler.getErrorMessage(e),
        onRetry: _handleVerify,
      );
    } finally {
      _isSubmitting = false;
    }
  }

  void _showSuccessDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: Row(
          children: [
            Icon(Icons.check_circle, color: Colors.green, size: 28),
            const SizedBox(width: 10),
            const Text('Verified!', style: TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
        content: const Text('Your attendance has been successfully verified.'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context); // dialog
              Navigator.pop(context); // screen
            },
            style: TextButton.styleFrom(
              backgroundColor: Colors.green.withOpacity(0.1),
            ),
            child: const Text('OK', style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  void _showTimeoutDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: Row(
          children: [
            Icon(Icons.timer_off, color: Colors.red, size: 28),
            const SizedBox(width: 10),
            const Text('Time Expired', style: TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
        content: const Text(
          'The 5-minute verification window has expired. '
              'This will be marked as missed.',
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context); // dialog
              Navigator.pop(context); // screen
            },
            style: TextButton.styleFrom(
              backgroundColor: Colors.red.withOpacity(0.1),
            ),
            child: const Text('OK', style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  void _showErrorDialog({
    required String title,
    required String message,
    VoidCallback? onRetry,
  }) {
    if (!mounted) return;
    showDialog(
      context: context,
      builder: (_) => ErrorDialog(
        title: title,
        message: message,
        onRetry: onRetry,
      ),
    );
  }

  Future<void> _showWorkSiteDialog() async {
    final theme = Theme.of(context);
    await showDialog(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => Theme(
        data: theme,
        child: WorkSiteDialog(
          workSites: _workSites.value,
          isLoading: _isLoading.value,
          onRefresh: _loadWorkSites,
          onSelect: (site) async {
            try {
              await _workSiteService.selectWorkSite(site.workSiteId ?? 0);
              _selectedWorkSite.value = site;
              Navigator.of(dialogContext).pop();
            } catch (e) {
              Navigator.of(dialogContext).pop();
              _showErrorDialog(title: 'Error', message: 'Failed to select work site');
            }
          },
        ),
      ),
    );
  }

  String _formatCountdown(int totalSeconds) {
    final m = totalSeconds ~/ 60;
    final s = totalSeconds % 60;
    return '${m.toString().padLeft(2, '0')}:${s.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final size = MediaQuery.of(context).size;
    final isSmall = size.width < 360;

    return PopScope(
      canPop: false, // не дать уйти назад случайно
      child: Scaffold(
        backgroundColor: theme.scaffoldBackgroundColor,
        appBar: AppBar(
          backgroundColor: Colors.orange,
          foregroundColor: Colors.white,
          title: Text(
            'Attendance Verification',
            style: TextStyle(fontSize: isSmall ? 16 : 18, fontWeight: FontWeight.bold),
          ),
          automaticallyImplyLeading: false, // убрать кнопку назад
        ),
        body: Stack(
          children: [
            SingleChildScrollView(
              padding: EdgeInsets.all(isSmall ? 12 : 16),
              child: Column(
                children: [
                  // TIMER CARD
                  ValueListenableBuilder<int>(
                    valueListenable: _secondsRemaining,
                    builder: (_, seconds, __) {
                      final isUrgent = seconds <= 60;
                      final color = isUrgent ? Colors.red : Colors.orange;

                      return Container(
                        width: double.infinity,
                        padding: EdgeInsets.all(isSmall ? 16 : 24),
                        decoration: BoxDecoration(
                          color: color.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(16),
                          border: Border.all(color: color.withOpacity(0.3)),
                        ),
                        child: Column(
                          children: [
                            Icon(
                              isUrgent ? Icons.warning_amber_rounded : Icons.timer_outlined,
                              color: color,
                              size: isSmall ? 36 : 48,
                            ),
                            const SizedBox(height: 8),
                            Text(
                              _formatCountdown(seconds),
                              style: TextStyle(
                                fontSize: isSmall ? 40 : 52,
                                fontWeight: FontWeight.bold,
                                color: color,
                                fontFeatures: const [FontFeature.tabularFigures()],
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              isUrgent ? 'Hurry up!' : 'Time remaining to verify',
                              style: TextStyle(
                                fontSize: isSmall ? 13 : 15,
                                color: color.withOpacity(0.8),
                                fontWeight: isUrgent ? FontWeight.w600 : FontWeight.normal,
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  ),

                  SizedBox(height: isSmall ? 16 : 20),

                  // INFO CARD
                  Container(
                    width: double.infinity,
                    padding: EdgeInsets.all(isSmall ? 12 : 16),
                    decoration: BoxDecoration(
                      color: isDark ? Colors.grey[900] : Colors.white,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                        color: isDark ? Colors.grey[700]! : Colors.grey[300]!,
                      ),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            Icon(Icons.info_outline, size: 18, color: Colors.blue),
                            const SizedBox(width: 8),
                            Text(
                              'Verification #${widget.verificationId}',
                              style: TextStyle(
                                fontSize: isSmall ? 14 : 16,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Take a photo to confirm your presence. '
                              'Select your work site and tap "Verify Now".',
                          style: TextStyle(
                            fontSize: isSmall ? 13 : 14,
                            color: isDark ? Colors.grey[400] : Colors.grey[600],
                          ),
                        ),
                      ],
                    ),
                  ),

                  SizedBox(height: isSmall ? 16 : 20),

                  // WORK SITE SELECTOR
                  ValueListenableBuilder<WorkSiteResponse?>(
                    valueListenable: _selectedWorkSite,
                    builder: (_, workSite, __) {
                      return GestureDetector(
                        onTap: _showWorkSiteDialog,
                        child: Container(
                          width: double.infinity,
                          padding: EdgeInsets.all(isSmall ? 12 : 16),
                          decoration: BoxDecoration(
                            color: isDark ? Colors.grey[900] : Colors.grey[100],
                            borderRadius: BorderRadius.circular(12),
                            border: Border.all(
                              color: workSite != null
                                  ? Colors.green.withOpacity(0.5)
                                  : Colors.red.withOpacity(0.5),
                            ),
                          ),
                          child: Row(
                            children: [
                              Icon(
                                Icons.location_on,
                                color: workSite != null ? Colors.green : Colors.red,
                                size: isSmall ? 20 : 24,
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      'Work Site',
                                      style: TextStyle(
                                        fontSize: isSmall ? 12 : 13,
                                        color: isDark ? Colors.grey[400] : Colors.grey[600],
                                      ),
                                    ),
                                    const SizedBox(height: 2),
                                    Text(
                                      workSite?.workSiteName ?? 'Tap to select',
                                      style: TextStyle(
                                        fontSize: isSmall ? 14 : 16,
                                        fontWeight: FontWeight.w600,
                                        color: workSite != null
                                            ? theme.textTheme.bodyLarge?.color
                                            : Colors.red,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              Icon(Icons.chevron_right,
                                  color: isDark ? Colors.grey[500] : Colors.grey[400]),
                            ],
                          ),
                        ),
                      );
                    },
                  ),

                  SizedBox(height: isSmall ? 16 : 20),

                  // LOCATION STATUS
                  ValueListenableBuilder<Position?>(
                    valueListenable: _currentPosition,
                    builder: (_, position, __) {
                      final hasLocation = position != null;
                      return Container(
                        width: double.infinity,
                        padding: EdgeInsets.all(isSmall ? 10 : 12),
                        decoration: BoxDecoration(
                          color: (hasLocation ? Colors.green : Colors.red).withOpacity(0.05),
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(
                            color: (hasLocation ? Colors.green : Colors.red).withOpacity(0.3),
                          ),
                        ),
                        child: Row(
                          children: [
                            Icon(
                              hasLocation ? Icons.gps_fixed : Icons.gps_off,
                              color: hasLocation ? Colors.green : Colors.red,
                              size: 20,
                            ),
                            const SizedBox(width: 10),
                            Text(
                              hasLocation ? 'Location ready' : 'Getting location...',
                              style: TextStyle(
                                fontSize: isSmall ? 13 : 14,
                                color: hasLocation ? Colors.green : Colors.red,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                            if (!hasLocation) ...[
                              const Spacer(),
                              SizedBox(
                                width: 16,
                                height: 16,
                                child: CircularProgressIndicator(
                                  strokeWidth: 2,
                                  color: Colors.red.withOpacity(0.5),
                                ),
                              ),
                            ],
                          ],
                        ),
                      );
                    },
                  ),

                  SizedBox(height: isSmall ? 24 : 32),

                  // VERIFY BUTTON
                  SizedBox(
                    width: double.infinity,
                    height: isSmall ? 52 : 60,
                    child: ElevatedButton.icon(
                      onPressed: _isSubmitting ? null : _handleVerify,
                      icon: Icon(Icons.camera_alt, size: isSmall ? 22 : 26),
                      label: Text(
                        'Verify Now',
                        style: TextStyle(
                          fontSize: isSmall ? 16 : 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.orange,
                        foregroundColor: Colors.white,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(14),
                        ),
                        elevation: 0,
                      ),
                    ),
                  ),

                  const SizedBox(height: 20),
                ],
              ),
            ),

            // LOADING OVERLAY
            ValueListenableBuilder<bool>(
              valueListenable: _isLoading,
              builder: (_, loading, __) {
                if (!loading) return const SizedBox.shrink();
                return const Positioned.fill(
                  child: Stack(
                    children: [
                      ModalBarrier(dismissible: false, color: Colors.black26),
                      Center(child: CircularProgressIndicator(color: Colors.orange)),
                    ],
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}