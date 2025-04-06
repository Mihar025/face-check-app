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

import '../../../providers/localization_provider.dart';
import '../../../services/ApiService.dart';
import '../../../../../api_client/model/work_site_response.dart';
import '../../../../../api_client/api/worker_attendance_controller_api.dart';
import '../../../../../api_client/serializers.dart';
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

class _FaceCheckScreenState extends State<Mainmenupunchscreen> {
  late final Dio dio;
  late final LocationService locationService;
  late final WorkSiteService workSiteService;
  late final WorkerAttendanceControllerApi attendanceApi;
  final ImagePicker _imagePicker = ImagePicker();

  GoogleMapController? mapController;
  Position? currentPosition;
  WorkSiteResponse? selectedWorkSite;
  bool isLoading = false;
  bool hasPunchIn = false; // Статус, был ли совершен Punch In сегодня
  List<WorkSiteResponse> workSites = [];

  @override
  void initState() {
    super.initState();
    _initializeDependencies();
  }

  // Оптимизация: объединение инициализации зависимостей
  void _initializeDependencies() {
    _initializeDio();
    locationService = LocationService();
    workSiteService = WorkSiteService(dio);
    attendanceApi = WorkerAttendanceControllerApi(dio, serializers);

    // Параллельная загрузка данных при старте
    Future.wait([
      _getCurrentLocation(),
      _loadWorkSites(),
      _checkTodayPunchStatus(), // Используем новый метод вместо _checkPunchInStatus
    ]).then((_) {
      // Все данные загружены
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

// Обновленный метод проверки статуса
  Future<void> _checkTodayPunchStatus() async {
    try {
      // Получаем текущую дату в формате YYYY-MM-DD
      final DateTime now = DateTime.now();
      final String today = DateFormat('yyyy-MM-dd').format(now);

      final prefs = await SharedPreferences.getInstance();
      final String lastPunchInDate = prefs.getString('lastPunchInDate') ?? '';
      final String lastPunchOutDate = prefs.getString('lastPunchOutDate') ?? '';

      print('Today: $today');
      print('Last Punch In: $lastPunchInDate');
      print('Last Punch Out: $lastPunchOutDate');

      // Получаем даты (без времени) для сравнения
      String lastPunchInDateOnly = lastPunchInDate.isNotEmpty ? lastPunchInDate.split(' ')[0] : '';
      String lastPunchOutDateOnly = lastPunchOutDate.isNotEmpty ? lastPunchOutDate.split(' ')[0] : '';

      bool lastPunchInWasToday = lastPunchInDateOnly == today;
      bool lastPunchOutWasToday = lastPunchOutDateOnly == today;

      // Получаем полные datetime объекты для сравнения времени
      DateTime? lastPunchInDateTime = lastPunchInDate.isNotEmpty ? DateTime.tryParse(lastPunchInDate) : null;
      DateTime? lastPunchOutDateTime = lastPunchOutDate.isNotEmpty ? DateTime.tryParse(lastPunchOutDate) : null;

      // Определяем статус кнопки на основе последовательности действий
      if (lastPunchInWasToday && lastPunchOutWasToday) {
        // Если оба события сегодня, смотрим что было последним
        if (lastPunchOutDateTime != null &&
            lastPunchInDateTime != null &&
            lastPunchOutDateTime.isAfter(lastPunchInDateTime)) {
          // Последним был punch out, значит показываем punch in
          setState(() {
            hasPunchIn = false;
          });
        } else {
          // Последним был punch in, показываем punch out
          setState(() {
            hasPunchIn = true;
          });
        }
      } else if (lastPunchInWasToday) {
        // Был только punch in сегодня
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

  // Оставляем старый метод как запасной вариант
  Future<void> _checkPunchInStatus() async {
    try {
      // Проверяем последний punch-in статус
      final lastPunch = await ApiService.instance.getLastPunchTime();
      if (mounted) {
        setState(() {
          hasPunchIn = lastPunch.time != '--:--'; // Если есть время, значит Punch In был сделан
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
        // Выбираем первую площадку по умолчанию, если список не пустой
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

  // Вспомогательный метод для отображения ошибок
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

  // Функция для открытия камеры и получения фото
  Future<String?> _captureImage() async {
    try {
      final XFile? image = await _imagePicker.pickImage(
        source: ImageSource.camera,
        imageQuality: 80, // Уменьшаем качество для экономии трафика
      );

      if (image != null) {
        // Конвертируем изображение в base64
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

  // Получение текущего времени в формате HH:MM:SS
  String _getCurrentFormattedTime() {
    return DateFormat('HH:mm:ss').format(DateTime.now());
  }

  // Показать диалог успешного Punch In/Out
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

  // Главная функция для обработки нажатия на кнопку Punch In/Out
  Future<void> _handlePunchInOut() async {
    if (!hasPunchIn) {
      await _handlePunchInWithCamera();
    } else {
      await _handlePunchOutWithCamera();
    }
  }

  // Обработка Punch In с открытием камеры
  Future<void> _handlePunchInWithCamera() async {
    if (selectedWorkSite == null || currentPosition == null) {
      _showErrorSnackBar('Please select work site and enable location');
      return;
    }

    // Открываем камеру для фото
    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      // Пользователь отменил фото или произошла ошибка
      return;
    }

    try {
      setState(() => isLoading = true);

      // Создаем запрос в формате, который ожидает сервер
      final Map<String, dynamic> requestData = {
        'workSiteId': selectedWorkSite?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': currentPosition?.latitude,
        'longitude': currentPosition?.longitude
      };

      // Прямой вызов API через dio
      final response = await dio.post(
        '/attendance/punch-in',
        data: requestData,
      );

      if (!mounted) return;

      // Сохраняем дату и время punch-in в локальное хранилище
      final prefs = await SharedPreferences.getInstance();
      final now = DateTime.now().toString();
      prefs.setString('lastPunchInDate', now);
      print('Saved Punch In: $now');

      setState(() {
        isLoading = false;
        if (response.statusCode == 200) {
          hasPunchIn = true;

          // Получаем текущее время для отображения
          final currentTime = _getCurrentFormattedTime();

          // Показываем диалог успешного Punch In
          _showSuccessDialog(true, currentTime);

          // Также показываем снекбар для дополнительного уведомления
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

  // Обработка Punch Out с открытием камеры
  Future<void> _handlePunchOutWithCamera() async {
    if (selectedWorkSite == null || currentPosition == null) {
      _showErrorSnackBar('Please select work site and enable location');
      return;
    }

    // Открываем камеру для фото
    final String? photoBase64 = await _captureImage();
    if (photoBase64 == null) {
      // Пользователь отменил фото или произошла ошибка
      return;
    }

    try {
      setState(() => isLoading = true);

      // Создаем запрос в формате, который ожидает сервер
      final Map<String, dynamic> requestData = {
        'workSiteId': selectedWorkSite?.workSiteId,
        'photoBase64': photoBase64,
        'latitude': currentPosition?.latitude,
        'longitude': currentPosition?.longitude
      };

      // Прямой вызов API через dio
      final response = await dio.post(
        '/attendance/punch-out',
        data: requestData,
      );

      if (!mounted) return;

      // Сохраняем дату и время punch-out в локальное хранилище
      final prefs = await SharedPreferences.getInstance();
      final now = DateTime.now().toString();
      prefs.setString('lastPunchOutDate', now);
      print('Saved Punch Out: $now');

      setState(() {
        isLoading = false;
        if (response.statusCode == 200) {
          hasPunchIn = false; // Сбрасываем статус

          // Получаем текущее время для отображения
          final currentTime = _getCurrentFormattedTime();

          // Показываем диалог успешного Punch Out
          _showSuccessDialog(false, currentTime);

          // Также показываем снекбар для дополнительного уведомления
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

    // Получаем размеры экрана для адаптивности
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
          // Добавляем индикатор загрузки поверх экрана
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
      bottomNavigationBar: Container(
        color: theme.scaffoldBackgroundColor,
        padding: EdgeInsets.only(bottom: isSmallScreen ? 16 : 20),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Material(
              color: Colors.transparent,
              child: InkWell(
                onTap: isLoading ? null : _handlePunchInOut,
                borderRadius: BorderRadius.circular(isSmallScreen ? 22 : 25),
                child: Container(
                  width: isSmallScreen ? 65 : 75,
                  height: isSmallScreen ? 65 : 75,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: (hasPunchIn ? Colors.blue : Colors.green).withOpacity(0.2),
                    border: Border.all(
                      color: hasPunchIn ? Colors.blue : Colors.green,
                      width: isSmallScreen ? 1.5 : 2,
                    ),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        hasPunchIn ? Icons.logout : Icons.login,
                        color: hasPunchIn ? Colors.blue : Colors.green,
                        size: isSmallScreen ? 20 : 24,
                      ),
                      SizedBox(height: isSmallScreen ? 2 : 4),
                      Text(
                        hasPunchIn ? l10n.get('punchOut') : l10n.get('punchIn'),
                        style: TextStyle(
                          color: hasPunchIn ? Colors.blue : Colors.green,
                          fontSize: isSmallScreen ? 10 : 12,
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
    );
  }
}