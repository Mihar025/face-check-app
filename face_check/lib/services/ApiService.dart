import 'dart:async';
import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:face_check/api_client/api/file_controller_api.dart';
import 'package:face_check/api_client/api/user_service_controller_api.dart';
import 'package:face_check/screens/theme/page_response.dart';
import 'package:face_check/models/update_punch_in_response.dart';
import 'package:face_check/models/worksite_worker_response.dart';
import 'package:face_check/services/jwt_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../api_client/api/authentication_api.dart';
import '../api_client/api/worker_attendance_controller_api.dart';
import '../api_client/serializers.dart';
import '../models/daily_earnings.dart';
import '../models/finance_info_response.dart';
import '../models/last_punch_info.dart';


class ApiService {
  static ApiService? _instance;
  late final Dio _dio;
  final _storage = const FlutterSecureStorage();
  late WorkerAttendanceControllerApi workerAttendanceApi;
  late final AuthenticationApi authenticationApi;
  late final UserServiceControllerApi userApi;
  late final FileControllerApi fileApi;

  ApiService._() {
    _dio = Dio(
      BaseOptions(
        //baseUrl: 'http://localhost:8088/api/v1/',
        baseUrl: 'http://192.168.1.194:8088/api/v1/',
        connectTimeout: Duration(seconds: 5),
        receiveTimeout: Duration(seconds: 3),
        contentType: 'application/json',
      ),
    );


    _dio.interceptors.add(LogInterceptor(
        requestBody: true,
        responseBody: true,
        requestHeader: true,
        responseHeader: true,
        request: true,
        error: true,
        logPrint: (object) {
          print('DIO LOG: $object');
        }
    ));

    _dio.interceptors.clear();
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          if (options.path.contains('/auth/authenticate')) {
            return handler.next(options);
          }

          final token = await _storage.read(key: 'auth_token');
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          return handler.next(options);
        },
        onError: (error, handler) async {
          if (error.response?.statusCode == 500 &&
              error.requestOptions.path.contains('/auth/authenticate')) {
            await logout();
          }

          return handler.next(error);
        },
      ),
    );

    authenticationApi = AuthenticationApi(_dio, standardSerializers);
    userApi = UserServiceControllerApi(_dio, standardSerializers);
    fileApi = FileControllerApi(_dio, standardSerializers);
    workerAttendanceApi =
        WorkerAttendanceControllerApi(_dio, standardSerializers);
  }

  static ApiService get instance {
    _instance ??= ApiService._();
    return _instance!;
  }

  Future<LastPunchInfo> getLastPunchTime() async {
    try {
      final response = await _dio.get('attendance/last-punch');
      print("API Response: ${response.data}");
      if (response.data != null) {
        return LastPunchInfo.fromJson(response.data);
      }
      return LastPunchInfo(date: 'DD/MM/YYYY', time: '--:--');
    } catch (e) {
      print('Error getting last punch time: $e');
      return LastPunchInfo(date: 'DD/MM/YYYY', time: '--:--');
    }
  }

  Future<List<DailyEarning>> getWeeklyEarnings() async {
    try {
      final response = await _dio.get('/attendance/week');
      print("Weekly Earnings Response: ${response.data}");

      if (response.data != null) {
        return (response.data as List)
            .map((item) => DailyEarning.fromJson(item))
            .toList();
      }
      return [];
    } catch (e) {
      print('Error getting weekly earnings: $e');
      return [];
    }
  }

  Future<void> logout() async {
    await _storage.delete(key: 'auth_token');
    await _storage.delete(key: 'refresh_token');
    await JwtService.clearRole();
    _dio.options.headers.remove('Authorization');
  }

  Future<num> findWorkerBaseHourRate() async {
    try {
      final response = await userApi.findWorkerBaseHourRate();
      return response.data ?? 0;
    } catch (e) {
      print('Error in findWorkerBaseHourRate: $e');
      throw e;
    }
  }

  Future<void> setAuthToken(String token, String? refreshToken) async {
    await _storage.write(key: 'auth_token', value: token);
    if (refreshToken != null) {
      await _storage.write(key: 'refresh_token', value: refreshToken);
    }
    _dio.options.headers['Authorization'] = 'Bearer $token';
  }

  Future<bool> isAuthenticated() async {
    final token = await _storage.read(key: 'auth_token');
    return token != null;
  }

  Future<String?> getCurrentUserEmail() async {
    try {
      final token = await _storage.read(key: 'auth_token');
      if (token != null) {
        final parts = token.split('.');
        if (parts.length > 1) {
          final payload = parts[1];
          final normalized = base64Url.normalize(payload);
          final resp = utf8.decode(base64Url.decode(normalized));
          final Map<String, dynamic> payloadMap = json.decode(resp);
          return payloadMap['sub'] as String?;
        }
      }
    } catch (e) {
      print('Error decoding token: $e');
    }
    return null;
  }

  Future<String?> getAuthToken() async {
    return await _storage.read(key: 'auth_token');
  }

  Future<double> getTotalWorkedHoursPerWeek() async {
    try {
      final response = await _dio.get('/user/total-hours-perWeek');
      return response.data.toDouble();
    } catch (e) {
      print('Error getting total worked hours per week: $e');
      return 0.0;
    }
  }

  Future<FinanceInfoResponse> getFinanceInfo(DateTime weekStart) async {
    try {
      final response = await _dio.get(
        '/attendance/finance-info',
        queryParameters: {
          'weekStart': weekStart.toIso8601String().split('T')[0],
        },
      );

      print("Finance Info Response: ${response.data}");

      if (response.data != null) {
        return FinanceInfoResponse.fromJson(response.data);
      }


      return FinanceInfoResponse(
        totalHoursWorked: 0.0,
        totalGrossPay: 0.0,
        totalNetPay: 0.0,
        periodStart: weekStart,
        periodEnd: weekStart.add(Duration(days: 6)),
        dailyInfo: [],
      );
    } catch (e) {
      print('Error getting finance info: $e');
      throw e;
    }
  }


  Future<void> sendEmail(String email) async {
    try {
      print(
          'Attempting to send email reset request for: $email');
      final Map<String, dynamic> emailRequest = {
        'email': email,
      };

      print('Request payload: ${jsonEncode(emailRequest)}');

      final response = await _dio.post(
        '/auth/forgot-password/email',
        data: emailRequest,
        options: Options(
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
        ),
      );

      print('Response status: ${response.statusCode}');
      print('Response data: ${response.data}');

    } on DioError catch (e) {
      print('DioError occurred:');
      print('  Status code: ${e.response?.statusCode}');
      print('  Response data: ${e.response?.data}');
      print('  Error message: ${e.message}');

      if (e.response?.statusCode == 404) {
        throw 'User wasnt found';
      }
      throw 'User wasnt found: ${e.message}';
    } catch (e) {
      print('Unexpected error: $e');
      throw 'User wasnt found';
    }
  }

  Future<void> verifyCode(String email, String code) async {
    try {
      await _dio.post('/auth/forgot-password/verify',
          data: {
            'email': email,
            'code': code
          }
      );
    } on DioError catch (e) {
      if (e.response?.statusCode == 400) {
        throw 'Not correct code';
      }
      throw 'Error';
    }
  }

  Future<void> resetPassword(String email, String newPassword,
      String confirmPassword, String verificationCode) async {
    try {
      await _dio.post('/auth/forgot-password',
          data: {
            'email': email,
            'newPassword': newPassword,
            'confirmNewPassword': confirmPassword,
            'code': verificationCode
          }
      );
    } on DioError catch (e) {
      if (e.response?.statusCode == 400) {
        throw 'Passwords not match';
      } else if (e.response?.statusCode == 404) {
        throw 'User wasnt found';
      }
      throw 'Error during uupdating password';
    }
  }

  Dio get dio => _dio;



  Future<PageResponse<WorksiteWorkerResponse>> getWorkersInWorksite({
    required int worksiteId,
    required int page,
  }) async {
    try {
      final response = await _dio.get(
        '/admin/employee',
        queryParameters: {
          'page': page,
          'size': 20,
          'worksiteId': worksiteId,
        },
      );

      debugPrint('Raw response: ${response.data}');

      if (response.data['content'] == null) {
        return PageResponse(
            content: [],
            pageNumber: response.data['number'] ?? 0,
            pageSize: response.data['size'] ?? 20,
            totalElements: response.data['totalElement'] ?? 0,
            totalPages: response.data['totalPages'] ?? 0,
            first: response.data['first'] ?? true,
            last: response.data['last'] ?? true
        );
      }

      return PageResponse.fromJson(
        response.data,
            (json) => WorksiteWorkerResponse.fromJson(json as Map<String, dynamic>),
      );
    } catch (e) {
      debugPrint('Error getting workers in worksite: $e');
      rethrow;
    }
  }
  Future<void> deleteWorkerPunchIn(int workerId) async {
    try {
      await _dio.delete('/admin/workers/$workerId/punch-in');
    } catch (e) {
      print('Error deleting worker punch in: $e');
      throw e;
    }
  }

  Future<UpdatePunchInForWorkerResponse?> updatePunchInTime(
      int workerId,
      DateTime newPunchInTime,
      {int? punchInId}
      ) async {
    try {

      final String formattedDateTime =
          "${newPunchInTime.year.toString().padLeft(4, '0')}-"
          "${newPunchInTime.month.toString().padLeft(2, '0')}-"
          "${newPunchInTime.day.toString().padLeft(2, '0')}T"
          "${newPunchInTime.hour.toString().padLeft(2, '0')}:"
          "${newPunchInTime.minute.toString().padLeft(2, '0')}:"
          "${newPunchInTime.second.toString().padLeft(2, '0')}";

      final Map<String, dynamic> requestData = {
        'newCheckInTIme': formattedDateTime
      };
      if (punchInId != null) {
        requestData['punchInId'] = punchInId;
      }
      print('Sending punch-in update request: $requestData');

      final response = await _dio.put(
        '/admin/worker/$workerId/punch-in',
        data: requestData,
      );

      if (response.statusCode == 200 && response.data != null) {
        print('Punch-in update successful: ${response.data}');
        return UpdatePunchInForWorkerResponse.fromJson(response.data);
      } else {
        print('Punch-in update response empty or invalid');
        return null;
      }
    } on DioError catch (e) {
      print('Punch-in update error: ${e.message}');
      print('Response data: ${e.response?.data}');
      print('Response status: ${e.response?.statusCode}');


      if (e.response?.statusCode == 400 &&
          e.response!.data.toString().contains('LocalDateTime')) {
        print('Date parsing error occurred, but operation may have succeeded');
        return null;
      }

      if (e.response?.statusCode == 400) {
        throw 'Invalid date format';
      }
      throw 'Error updating punch-in time: ${e.message}';
    } catch (e) {
      print('Unexpected error in updatePunchInTime: $e');
      throw 'Unexpected error';
    }
  }



}



