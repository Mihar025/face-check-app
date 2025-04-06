

import 'package:dio/dio.dart';
import 'package:built_value/serializer.dart';


import 'api/admin_controller_api.dart';
import 'api/authentication_api.dart';
import 'api/company_controller_api.dart';
import 'api/file_controller_api.dart';
import 'api/user_service_controller_api.dart';
import 'api/work_schedule_controller_api.dart';
import 'api/work_site_controller_api.dart';
import 'api/worker_attendance_controller_api.dart';
import 'auth/api_key_auth.dart';
import 'auth/basic_auth.dart';
import 'auth/bearer_auth.dart';
import 'auth/oauth.dart';
import 'serializers.dart';

class ApiClient {
 // static const String basePath = r'http://localhost:8088/api/v1';
  static const String basePath = r'http://192.168.1.194:8088/api/v1';
  final Dio dio;
  final Serializers serializers;

  ApiClient({
    Dio? dio,
    Serializers? serializers,
    String? basePathOverride,
    List<Interceptor>? interceptors,
  })  : this.serializers = serializers ?? standardSerializers,
        this.dio = dio ??
            Dio(BaseOptions(
              baseUrl: basePathOverride ?? basePath,
              connectTimeout: const Duration(milliseconds: 5000),
              receiveTimeout: const Duration(milliseconds: 3000),
            )) {
    if (interceptors == null) {
      this.dio.interceptors.addAll([
        OAuthInterceptor(),
        BasicAuthInterceptor(),
        BearerAuthInterceptor(),
        ApiKeyAuthInterceptor(),
      ]);
    } else {
      this.dio.interceptors.addAll(interceptors);
    }
  }

  void setOAuthToken(String name, String token) {
    if (this.dio.interceptors.any((i) => i is OAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is OAuthInterceptor) as OAuthInterceptor).tokens[name] = token;
    }
  }

  void setBearerAuth(String name, String token) {
    if (this.dio.interceptors.any((i) => i is BearerAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BearerAuthInterceptor) as BearerAuthInterceptor).tokens[name] = token;
    }
  }

  void setBasicAuth(String name, String username, String password) {
    if (this.dio.interceptors.any((i) => i is BasicAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BasicAuthInterceptor) as BasicAuthInterceptor).authInfo[name] = BasicAuthInfo(username, password);
    }
  }

  void setApiKey(String name, String apiKey) {
    if (this.dio.interceptors.any((i) => i is ApiKeyAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((element) => element is ApiKeyAuthInterceptor) as ApiKeyAuthInterceptor).apiKeys[name] = apiKey;
    }
  }

  /// Get AdminControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  AdminControllerApi getAdminControllerApi() {
    return AdminControllerApi(dio, serializers);
  }

  /// Get AuthenticationApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  AuthenticationApi getAuthenticationApi() {
    return AuthenticationApi(dio, serializers);
  }

  /// Get CompanyControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  CompanyControllerApi getCompanyControllerApi() {
    return CompanyControllerApi(dio, serializers);
  }

  /// Get FileControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  FileControllerApi getFileControllerApi() {
    return FileControllerApi(dio, serializers);
  }

  /// Get UserServiceControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  UserServiceControllerApi getUserServiceControllerApi() {
    return UserServiceControllerApi(dio, serializers);
  }

  /// Get WorkScheduleControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  WorkScheduleControllerApi getWorkScheduleControllerApi() {
    return WorkScheduleControllerApi(dio, serializers);
  }

  /// Get WorkSiteControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  WorkSiteControllerApi getWorkSiteControllerApi() {
    return WorkSiteControllerApi(dio, serializers);
  }

  /// Get WorkerAttendanceControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  WorkerAttendanceControllerApi getWorkerAttendanceControllerApi() {
    return WorkerAttendanceControllerApi(dio, serializers);
  }
}
