import 'package:face_check/screens/main_menu/drawer/punch_screen/punch_buttons.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_dialog.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_selector.dart';
import 'package:face_check/screens/main_menu/drawer/punch_screen/work_site_service.dart';
import 'package:face_check/screens/main_menu/main_menu_punch_screen/punch_success_dialo.dart';
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

class _PunchScreenState extends State<PunchScreen> {
  late final Dio dio;
  late final LocationService locationService;
  late final WorkSiteService workSiteService;
  late final WorkerAttendanceControllerApi attendanceApi;
  final ImagePicker _imagePicker = ImagePicker();

  GoogleMapController? mapController;
  Position? currentPosition;
  WorkSiteResponse? selectedWorkSite;
  bool isLoading = false;
  bool hasPunchIn = false;
  List<WorkSiteResponse> workSites = [];

  @override
  void initState() {
    super.initState();
    _initializeDependencies();
  }

  void _initializeDependencies() {
    _initializeDio();
    locationService = LocationService();
    workSiteService = WorkSiteService(dio);
    attendanceApi = WorkerAttendanceControllerApi(dio, serializers);

    Future.wait([
      _getCurrentLocation(),
      _loadWorkSites(),
      _checkTodayPunchStatus(),
    ]).then((_) {
      if (mounted) setState(() {});
    });
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

  Future<void> _checkTodayPunchStatus() async {
    try {
      final DateTime now = DateTime.now();
      final String today = DateFormat('yyyy-MM-dd').format(now);

      final prefs = await SharedPreferences.getInstance();
      final String lastPunchInDate = prefs.getString('lastPunchInDate') ?? '';
      final String lastPunchOutDate = prefs.getString('lastPunchOutDate') ?? '';

      print('Today: $today');
      print('Last Punch In: $lastPunchInDate');
      print('Last Punch Out: $lastPunchOutDate');

      String lastPunchInDateOnly = lastPunchInDate.isNotEmpty ? lastPunchInDate.split(' ')[0] : '';
      String lastPunchOutDateOnly = lastPunchOutDate.isNotEmpty ? lastPunchOutDate.split(' ')[0] : '';

      bool lastPunchInWasToday = lastPunchInDateOnly == today;
      bool lastPunchOutWasToday = lastPunchOutDateOnly == today;

      DateTime? lastPunchInDateTime = lastPunchInDate.isNotEmpty ? DateTime.tryParse(lastPunchInDate) : null;
      DateTime? lastPunchOutDateTime = lastPunchOutDate.isNotEmpty ? DateTime.tryParse(lastPunchOutDate) : null;

      if (lastPunchInWasToday && lastPunchOutWasToday) {
        if (lastPunchOutDateTime != null &&
            lastPunchInDateTime != null &&
            lastPunchOutDateTime.isAfter(lastPunchInDateTime)) {
          setState(() {
            hasPunchIn = false;
          });
        } else {
          setState(() {
            hasPunchIn = true;
          });
        }
      } else if (lastPunchInWasToday) {
        setState(() {
          hasPunchIn = true;
        });
      } else {
        setState(() {
          hasPunchIn = false;
        });
      }

      if (lastPunchInDate.isEmpty && lastPunchOutDate.isEmpty) {
        await _checkPunchInStatus();
      }
    } catch (e) {
      print('Error checking punch status: $e');
      await _checkPunchInStatus();
    }
  }

  Future<void> _checkPunchInStatus() async {
    try {
      final lastPunch = await ApiService.instance.getLastPunchTime();
      if (mounted) {
        setState(() {
          hasPunchIn = lastPunch.time != '--:--';
        });
      }
    } catch (e) {
      print('Error checking punch in status: $e');
    }
  }

  Future<void> _loadWorkSites() async {
    if (!mounted) return;
    setState(() => isLoading = true);

    try {
      final sites = await workSiteService.loadWorkSites();
      if (!mounted) return;

      setState(() {
        workSites = sites;
        if (sites.isNotEmpty && selectedWorkSite == null) {
          selectedWorkSite = sites.first;
        }
        isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() => isLoading = false);
      _showErrorSnackBar('Failed to load work sites: $e');
    }
  }

  void _showErrorSnackBar(String message) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
        duration: const Duration(seconds: 3),
      ),
    );
  }

  Future<void> _getCurrentLocation() async {
    final position = await locationService.getCurrentLocation();
    if (mounted && position != null) {
      setState(() => currentPosition = position);
    }
  }

  Future<void> _showWorkSiteDialog() async {
    final theme = Theme.of(context);

    await showDialog(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => Theme(
        data: theme,
        child: WorkSiteDialog(
          workSites: workSites,
          isLoading: isLoading,
          onRefresh: _loadWorkSites,
          onSelect: (site) async {
            try {
              await workSiteService.selectWorkSite(site.workSiteId ?? 0);

              if (!mounted) return;

              setState(() {
                selectedWorkSite = site;
              });

              Navigator.of(dialogContext).pop();
            } catch (e) {
              if (!mounted) return;

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
    return DateFormat('HH:mm:ss').format(DateTime.now());
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
    if (selectedWorkSite == null || currentPosition == null) {
      _showErrorSnackBar('Please select work site and enable location');
      return;
    }

    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      return;
    }

    try {
      setState(() => isLoading = true);

      final Map<String, dynamic> requestData = {
        'workSiteId': selectedWorkSite?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': currentPosition?.latitude,
        'longitude': currentPosition?.longitude
      };

      final response = await dio.post(
        '/attendance/punch-in',
        data: requestData,
      );

      if (!mounted) return;

      final prefs = await SharedPreferences.getInstance();
      final now = DateTime.now().toString();
      prefs.setString('lastPunchInDate', now);
      print('Saved Punch In: $now');

      setState(() {
        isLoading = false;
        if (response.statusCode == 200) {
          hasPunchIn = true;

          final currentTime = _getCurrentFormattedTime();

          _showSuccessDialog(true, currentTime);

          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Successfully punched in!'),
              backgroundColor: Colors.green,
            ),
          );
        } else {
          _showErrorSnackBar('Failed to punch in');
        }
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => isLoading = false);
      _showErrorSnackBar('Failed to punch in: $e');
    }
  }

  Future<void> _handlePunchOut() async {
    if (selectedWorkSite == null || currentPosition == null) {
      _showErrorSnackBar('Please select work site and enable location');
      return;
    }

    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      return;
    }

    try {
      setState(() => isLoading = true);

      final Map<String, dynamic> requestData = {
        'workSiteId': selectedWorkSite?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': currentPosition?.latitude,
        'longitude': currentPosition?.longitude
      };

      final response = await dio.post(
        '/attendance/punch-out',
        data: requestData,
      );

      if (!mounted) return;

      final prefs = await SharedPreferences.getInstance();
      final now = DateTime.now().toString();
      prefs.setString('lastPunchOutDate', now);
      print('Saved Punch Out: $now');

      setState(() {
        isLoading = false;
        if (response.statusCode == 200) {
          hasPunchIn = false;

          final currentTime = _getCurrentFormattedTime();

          _showSuccessDialog(false, currentTime);

          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Successfully punched out!'),
              backgroundColor: Colors.green,
            ),
          );
        } else {
          _showErrorSnackBar('Failed to punch out');
        }
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => isLoading = false);
      _showErrorSnackBar('Failed to punch out: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: theme.scaffoldBackgroundColor,
        elevation: 0,
        title: Text(
          l10n.get('punch'),
          style: TextStyle(
            color: theme.textTheme.titleLarge?.color,
            fontSize: isSmallScreen ? 18 : 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: theme.iconTheme.color,
            size: isSmallScreen ? 22 : 24,
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: Stack(
        children: [
          Container(
            color: theme.scaffoldBackgroundColor,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(height: isSmallScreen ? 16 : 20),
                ClockDisplay(
                  textColor: theme.textTheme.bodyLarge?.color ?? Colors.white,
                  isSmallScreen: isSmallScreen,
                ),
                SizedBox(height: isSmallScreen ? 16 : 20),
                MapContainer(
                  currentPosition: currentPosition,
                  onMapCreated: (controller) => mapController = controller,
                ),
                SizedBox(height: isSmallScreen ? 16 : 20),
                Padding(
                  padding: EdgeInsets.symmetric(horizontal: isSmallScreen ? 12 : 16),
                  child: WorkSiteSelectorButton(
                    selectedWorkSite: selectedWorkSite,
                    onTap: _showWorkSiteDialog,
                    backgroundColor: theme.brightness == Brightness.dark
                        ? Colors.grey[900]
                        : Colors.grey[100],
                    textColor: theme.textTheme.bodyLarge?.color,
                    isSmallScreen: isSmallScreen,
                  ),
                ),
                SizedBox(height: isSmallScreen ? 16 : 20),
              ],
            ),
          ),
          if (isLoading)
            Container(
              color: Colors.black54,
              child: Center(
                child: CircularProgressIndicator(
                  color: theme.brightness == Brightness.dark ? Colors.white : Colors.blue,
                ),
              ),
            ),
        ],
      ),
      bottomNavigationBar: PunchButtons(
        onPunchIn: isLoading ? null : _handlePunchIn,
        onPunchOut: isLoading ? null : _handlePunchOut,
        backgroundColor: theme.scaffoldBackgroundColor,
        buttonColor: theme.brightness == Brightness.dark
            ? Colors.grey[900]
            : Colors.grey[100],
        textColor: theme.textTheme.bodyLarge?.color,
      ),
    );
  }
}