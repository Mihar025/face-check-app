



import 'dart:io';

import 'package:device_info_plus/device_info_plus.dart';
import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../models/privacy_policy_models.dart';
import 'ApiService.dart';

class PrivacyPolicyService {
  static PrivacyPolicyService? _instance;
  static PrivacyPolicyService get instance => _instance ??= PrivacyPolicyService._();

  late final Dio _dio;
  final DeviceInfoPlugin _deviceInfo = DeviceInfoPlugin();

  static const String _PRIVACY_ACCEPTED_KEY = 'privacy_policy_accepted';
  static const String _USER_ID_KEY = 'user_id';
  static const String _PRIVACY_CHECK_KEY = 'privacy_check_done_for_user_';

  static const String TERMS_VERSION = '1.0.0';
  static const String PRIVACY_VERSION = '1.0.0';

  PrivacyPolicyService._() {
    _dio = Dio(BaseOptions(
      baseUrl: 'http://192.168.1.194:8088/api/v1/',
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 30),
    ));

  _dio.interceptors.add(InterceptorsWrapper(
  onRequest: (options, handler) async {
  final token = await ApiService.instance.getAuthToken();
  if (token != null && token.isNotEmpty) {
  options.headers['Authorization'] = 'Bearer $token';
  print('🔐 Adding token to Privacy Policy request');
  } else {
  print('⚠️ No token available for Privacy Policy request');
  }
  return handler.next(options);
  },
  onError: (e, handler) {
  print('❌ Privacy Policy API Error: ${e.response?.statusCode} - ${e.message}');
  return handler.next(e);
  },
  ));
}


  void _setupInterceptors() {
    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final prefs = await SharedPreferences.getInstance();
        final token = prefs.getString('auth_token');
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
    ));
  }

  // Проверяем, принял ли пользователь Privacy Policy
  Future<bool> hasAcceptedPrivacyPolicy(int userId) async {
    try {
      final prefs = await SharedPreferences.getInstance();

      // Проверяем локальный кэш для этого пользователя
      final cacheKey = '$_PRIVACY_CHECK_KEY$userId';
      if (prefs.getBool(cacheKey) == true) {
        return true;
      }

      // Если нет в кэше, проверяем на сервере
      final response = await _dio.get('/privacy-and-terms',
          queryParameters: {
            'page': 0,
            'size': 100,
          }
      );

      if (response.statusCode == 200) {
        final data = response.data;
        final List<dynamic> content = data['content'] ?? [];

        // Ищем запись для текущего пользователя
        final hasAccepted = content.any((item) =>
        item['userId'] == userId &&
            item['termsVersion'] == TERMS_VERSION &&
            item['privacyVersion'] == PRIVACY_VERSION
        );

        // Сохраняем результат в кэш
        if (hasAccepted) {
          await prefs.setBool(cacheKey, true);
        }

        return hasAccepted;
      }

      return false;
    } catch (e) {
      print('Error checking privacy policy: $e');
      return false;
    }
  }

  // Принимаем Privacy Policy
  Future<bool> acceptPrivacyPolicy(int userId) async {
    try {
      // Получаем информацию об устройстве
      final deviceInfo = await _getDeviceInfo();

      final request = TermsOfUseRequest(
        event: 'ACCEPTED',
        userId: userId,
        termsVersion: TERMS_VERSION,
        privacyVersion: PRIVACY_VERSION,
        ip: await _getIpAddress(),
        device: deviceInfo['device'] ?? 'Unknown',
        osVersion: deviceInfo['osVersion'] ?? 'Unknown',
      );

      final response = await _dio.post(
        '/privacy-and-terms',
        data: request.toJson(),
      );

      if (response.statusCode == 201) {
        // Сохраняем в локальный кэш
        final prefs = await SharedPreferences.getInstance();
        await prefs.setBool('$_PRIVACY_CHECK_KEY$userId', true);
        await prefs.setBool(_PRIVACY_ACCEPTED_KEY, true);

        return true;
      }

      return false;
    } catch (e) {
      print('Error accepting privacy policy: $e');
      return false;
    }
  }

  // Получаем информацию об устройстве
  Future<Map<String, String>> _getDeviceInfo() async {
    try {
      if (Platform.isAndroid) {
        final androidInfo = await _deviceInfo.androidInfo;
        return {
          'device': '${androidInfo.manufacturer} ${androidInfo.model}',
          'osVersion': 'Android ${androidInfo.version.release}',
        };
      } else if (Platform.isIOS) {
        final iosInfo = await _deviceInfo.iosInfo;
        return {
          'device': iosInfo.model ?? 'iPhone',
          'osVersion': 'iOS ${iosInfo.systemVersion}',
        };
      }
    } catch (e) {
      print('Error getting device info: $e');
    }

    return {
      'device': 'Unknown',
      'osVersion': 'Unknown',
    };
  }

  // Получаем IP адрес (можно использовать внешний сервис)
  Future<String> _getIpAddress() async {
    try {
      final response = await Dio().get('https://api.ipify.org?format=json');
      return response.data['ip'] ?? 'Unknown';
    } catch (e) {
      return 'Unknown';
    }
  }

  // Очищаем кэш при выходе пользователя
  Future<void> clearPrivacyCache(int userId) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('$_PRIVACY_CHECK_KEY$userId');
    await prefs.remove(_PRIVACY_ACCEPTED_KEY);
  }
}




