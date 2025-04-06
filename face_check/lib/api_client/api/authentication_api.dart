//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

import 'dart:async';

import 'package:built_value/json_object.dart';
import 'package:built_value/serializer.dart';
import 'package:dio/dio.dart';


import 'package:built_value/json_object.dart';

import '../api_util.dart';
import '../model/authentication_request.dart';
import '../model/authentication_response.dart';
import '../model/company_registration_request.dart';
import '../model/payment_request.dart';
import '../model/registration_admin_request.dart';
import '../model/registration_request.dart';
import '../model/settings_email_request.dart';
import '../model/settings_email_response.dart';
import '../model/settings_password_request.dart';
import '../model/settings_phone_number_request.dart';
import '../model/settings_phone_number_response.dart';

class AuthenticationApi {

  final Dio _dio;

  final Serializers _serializers;

  const AuthenticationApi(this._dio, this._serializers);

  /// authenticate
  /// 
  ///
  /// Parameters:
  /// * [authenticationRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [AuthenticationResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<AuthenticationResponse>> authenticate({
    required AuthenticationRequest authenticationRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/authenticate';
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(AuthenticationRequest);
      _bodyData =
          _serializers.serialize(authenticationRequest, specifiedType: _type);
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _options.compose(
          _dio.options,
          _path,
        ),
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    final _response = await _dio.request<Object>(
      _path,
      data: _bodyData,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    AuthenticationResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(AuthenticationResponse),
      ) as AuthenticationResponse;
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<AuthenticationResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// confirm
  /// 
  ///
  /// Parameters:
  /// * [token] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future]
  /// Throws [DioException] if API call or serialization fails
  Future<Response<void>> confirm({
    required String token,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/activate-account';
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _queryParameters = <String, dynamic>{
      r'token': encodeQueryParameter(
          _serializers, token, const FullType(String)),
    };

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      queryParameters: _queryParameters,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    return _response;
  }

  /// register
  /// 
  ///
  /// Parameters:
  /// * [registrationRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [JsonObject] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<JsonObject>> register({
    required RegistrationRequest registrationRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/register';
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RegistrationRequest);
      _bodyData =
          _serializers.serialize(registrationRequest, specifiedType: _type);
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _options.compose(
          _dio.options,
          _path,
        ),
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    final _response = await _dio.request<Object>(
      _path,
      data: _bodyData,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    JsonObject? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(JsonObject),
      ) as JsonObject;
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<JsonObject>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// registerAdmin
  /// 
  ///
  /// Parameters:
  /// * [registrationAdminRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [JsonObject] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<JsonObject>> registerAdmin({
    required RegistrationAdminRequest registrationAdminRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/register/admin';
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RegistrationAdminRequest);
      _bodyData = _serializers.serialize(
          registrationAdminRequest, specifiedType: _type);
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _options.compose(
          _dio.options,
          _path,
        ),
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    final _response = await _dio.request<Object>(
      _path,
      data: _bodyData,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    JsonObject? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(JsonObject),
      ) as JsonObject;
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<JsonObject>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// registerCompany
  /// 
  ///
  /// Parameters:
  /// * [companyRegistrationRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future]
  /// Throws [DioException] if API call or serialization fails
  Future<Response<void>> registerCompany({
    required CompanyRegistrationRequest companyRegistrationRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/register/company';
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(CompanyRegistrationRequest);
      _bodyData = _serializers.serialize(
          companyRegistrationRequest, specifiedType: _type);
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _options.compose(
          _dio.options,
          _path,
        ),
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    final _response = await _dio.request<Object>(
      _path,
      data: _bodyData,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    return _response;
  }

  /// registerForeman
  /// 
  ///
  /// Parameters:
  /// * [registrationRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [JsonObject] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<JsonObject>> registerForeman({
    required RegistrationRequest registrationRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/register/foreman';
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RegistrationRequest);
      _bodyData =
          _serializers.serialize(registrationRequest, specifiedType: _type);
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _options.compose(
          _dio.options,
          _path,
        ),
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    final _response = await _dio.request<Object>(
      _path,
      data: _bodyData,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    JsonObject? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(JsonObject),
      ) as JsonObject;
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<JsonObject>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// setPaymentData
  /// 
  ///
  /// Parameters:
  /// * [employeeId] 
  /// * [paymentRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future]
  /// Throws [DioException] if API call or serialization fails
  Future<Response<void>> setPaymentData({
    required int employeeId,
    required PaymentRequest paymentRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/employees/{employeeId}/payment'.replaceAll(
        '{' r'employeeId' '}',
        encodeQueryParameter(_serializers, employeeId, const FullType(int))
            .toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(PaymentRequest);
      _bodyData = _serializers.serialize(paymentRequest, specifiedType: _type);
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _options.compose(
          _dio.options,
          _path,
        ),
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    final _response = await _dio.request<Object>(
      _path,
      data: _bodyData,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    return _response;
  }


  /// Update email settings
  Future<Response<SettingsEmailResponse>> updateEmail({
    required SettingsEmailRequest settingsEmailRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/email';
    final _options = Options(
      method: r'PUT',
      headers: <String, dynamic>{
        'Content-Type': 'application/json',
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {'type': 'http', 'scheme': 'bearer', 'name': 'JWT'}
        ],
        ...?extra,
      },
      validateStatus: validateStatus ?? (status) {
        return status! >= 200 && status < 300;
      },
      followRedirects: false,
    );

    try {
      // Сериализация
      const _type = FullType(SettingsEmailRequest);
      final _bodyData = _serializers.serialize(
          settingsEmailRequest, specifiedType: _type);

      final _response = await _dio.request<Object>(
        _path,
        data: _bodyData,
        options: _options,
        cancelToken: cancelToken,
        onSendProgress: onSendProgress,
        onReceiveProgress: onReceiveProgress,
      );

      SettingsEmailResponse? _responseData;

      try {
        final rawResponse = _response.data;
        _responseData = rawResponse == null ? null : _serializers.deserialize(
          rawResponse,
          specifiedType: const FullType(SettingsEmailResponse),
        ) as SettingsEmailResponse;
      } catch (error, stackTrace) {
        throw DioException(
          requestOptions: _response.requestOptions,
          response: _response,
          type: DioExceptionType.unknown,
          error: error,
          stackTrace: stackTrace,
        );
      }

      return Response<SettingsEmailResponse>(
        data: _responseData,
        headers: _response.headers,
        isRedirect: _response.isRedirect,
        requestOptions: _response.requestOptions,
        redirects: _response.redirects,
        statusCode: _response.statusCode,
        statusMessage: _response.statusMessage,
        extra: _response.extra,
      );
    } catch (error) {
      if (error is DioException) {
        if (error.response?.statusCode == 401) {
          throw UnauthorizedException();
        } else if (error.response?.statusCode == 400) {
          final errorMsg = error.response?.data['message'] ?? 'Bad request';
          throw ValidationException(errorMsg);
        }
      }
      rethrow;
    }
  }

  /// Update password settings
  Future<Response<void>> updatePassword({
    required SettingsPasswordRequest settingsPasswordRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/password';
    final _options = Options(
      method: r'PUT',
      headers: <String, dynamic>{
        'Content-Type': 'application/json',
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {'type': 'http', 'scheme': 'bearer', 'name': 'JWT'}
        ],
        ...?extra,
      },
      validateStatus: validateStatus ?? (status) {
        return status! >= 200 && status < 300;
      },
      followRedirects: false,
    );

    try {
      const _type = FullType(SettingsPasswordRequest);
      final _bodyData = _serializers.serialize(
          settingsPasswordRequest, specifiedType: _type);

      final _response = await _dio.request<Object>(
        _path,
        data: _bodyData,
        options: _options,
        cancelToken: cancelToken,
        onSendProgress: onSendProgress,
        onReceiveProgress: onReceiveProgress,
      );

      return Response<void>(
        data: _response.data,
        headers: _response.headers,
        isRedirect: _response.isRedirect,
        requestOptions: _response.requestOptions,
        redirects: _response.redirects,
        statusCode: _response.statusCode,
        statusMessage: _response.statusMessage,
        extra: _response.extra,
      );
    } catch (error) {
      if (error is DioException) {
        if (error.response?.statusCode == 401) {
          throw UnauthorizedException();
        } else if (error.response?.statusCode == 400) {
          final errorMsg = error.response?.data['message'] ??
              'Invalid password data';
          throw ValidationException(errorMsg);
        }
      }
      rethrow;
    }
  }

  /// Update phone number settings
  Future<Response<SettingsPhoneNumberResponse>> updatePhoneNumber({
    required SettingsPhoneNumberRequest settingsPhoneNumberRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/auth/phone';
    final _options = Options(
      method: r'PUT',
      headers: <String, dynamic>{
        'Content-Type': 'application/json',
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {'type': 'http', 'scheme': 'bearer', 'name': 'JWT'}
        ],
        ...?extra,
      },
      validateStatus: validateStatus ?? (status) {
        return status! >= 200 && status < 300;
      },
      followRedirects: false,
    );

    try {
      const _type = FullType(SettingsPhoneNumberRequest);
      final _bodyData = _serializers.serialize(
          settingsPhoneNumberRequest, specifiedType: _type);

      final _response = await _dio.request<Object>(
        _path,
        data: _bodyData,
        options: _options,
        cancelToken: cancelToken,
        onSendProgress: onSendProgress,
        onReceiveProgress: onReceiveProgress,
      );

      SettingsPhoneNumberResponse? _responseData;

      try {
        final rawResponse = _response.data;
        _responseData = rawResponse == null ? null : _serializers.deserialize(
          rawResponse,
          specifiedType: const FullType(SettingsPhoneNumberResponse),
        ) as SettingsPhoneNumberResponse;
      } catch (error, stackTrace) {
        throw DioException(
          requestOptions: _response.requestOptions,
          response: _response,
          type: DioExceptionType.unknown,
          error: error,
          stackTrace: stackTrace,
        );
      }

      return Response<SettingsPhoneNumberResponse>(
        data: _responseData,
        headers: _response.headers,
        isRedirect: _response.isRedirect,
        requestOptions: _response.requestOptions,
        redirects: _response.redirects,
        statusCode: _response.statusCode,
        statusMessage: _response.statusMessage,
        extra: _response.extra,
      );
    } catch (error) {
      if (error is DioException) {
        if (error.response?.statusCode == 401) {
          throw UnauthorizedException();
        } else if (error.response?.statusCode == 400) {
          final errorMsg = error.response?.data['message'] ??
              'Invalid phone number';
          throw ValidationException(errorMsg);
        }
      }
      rethrow;
    }
  }
}

// Исключения
  class UnauthorizedException implements Exception {
  final String message;
  UnauthorizedException([this.message = 'Unauthorized. Please login again.']);

  @override
  String toString() => message;
  }

  class ValidationException implements Exception {
  final String message;
  ValidationException(this.message);

  @override
  String toString() => message;
  }