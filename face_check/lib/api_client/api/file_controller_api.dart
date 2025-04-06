//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

import 'dart:async';
import 'dart:io';

import 'package:built_value/serializer.dart';
import 'package:dio/dio.dart';

import 'package:built_collection/built_collection.dart';

import '../api_util.dart';

class FileControllerApi {

  final Dio _dio;

  final Serializers _serializers;

  const FileControllerApi(this._dio, this._serializers);

  /// deleteFile
  /// 
  ///
  /// Parameters:
  /// * [fileName] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future]
  /// Throws [DioException] if API call or serialization fails
  Future<Response<void>> deleteFile({
    required String fileName,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/files/{fileName}'.replaceAll('{' r'fileName' '}',
        encodeQueryParameter(_serializers, fileName, const FullType(String))
            .toString());
    final _options = Options(
      method: r'DELETE',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    return _response;
  }

  /// getFile
  /// 
  ///
  /// Parameters:
  /// * [fileName] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [BuiltList<String>] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<BuiltList<String>>> getFile({
    required String fileName,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/files/{fileName}'.replaceAll('{' r'fileName' '}',
        encodeQueryParameter(_serializers, fileName, const FullType(String))
            .toString());
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

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    BuiltList<String>? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(BuiltList, [FullType(String)]),
      ) as BuiltList<String>;
    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<BuiltList<String>>(
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

  /// uploadFile
  /// 
  ///
  /// Parameters:
  /// * [photo] 
  /// * [userId] 
  /// * [prefix] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [String] as data
  /// Throws [DioException] if API call or serialization fails
// В FileControllerApi:
  Future<Response<String>> uploadFile({
    required File file,
    required String token,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/files/upload';

    // Создаем FormData
    final formData = FormData.fromMap({
      'file': await MultipartFile.fromFile(
        file.path,
        filename: file.path
            .split('/')
            .last,
      ),
    });

    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        'Authorization': 'Bearer $token',
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      validateStatus: validateStatus ??
              (status) => status! >= 200 && status < 300,
    );

    try {
      final _response = await _dio.request<Object>(
        _path,
        data: formData,
        options: _options,
        cancelToken: cancelToken,
        onSendProgress: onSendProgress,
        onReceiveProgress: onReceiveProgress,
      );

      print('Server response: ${_response.data}');

      String? _responseData;

      try {
        final rawResponse = _response.data;
        _responseData = rawResponse == null ? null : rawResponse as String;
      } catch (error, stackTrace) {
        throw DioException(
          requestOptions: _response.requestOptions,
          response: _response,
          type: DioExceptionType.unknown,
          error: error,
          stackTrace: stackTrace,
        );
      }

      return Response<String>(
        data: _responseData,
        headers: _response.headers,
        isRedirect: _response.isRedirect,
        requestOptions: _response.requestOptions,
        redirects: _response.redirects,
        statusCode: _response.statusCode,
        statusMessage: _response.statusMessage,
        extra: _response.extra,
      );
    } catch (e) {
      print('Upload error details: $e');
      if (e is DioException) {
        print('Response status: ${e.response?.statusCode}');
        print('Response data: ${e.response?.data}');
        print('Headers: ${e.response?.headers}');
      }
      rethrow;
    }
  }


  Future<Response<String>> uploadPhoto({
    required File photo,
    required String email,
    required String prefix,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/files/upload/photo';

    final formData = FormData.fromMap({
      'photo': await MultipartFile.fromFile(
        photo.path,
        filename: photo.path
            .split('/')
            .last,
      ),
      'email': email,
      'prefix': prefix,
    });

    final _options = Options(
      method: r'POST',
      headers: headers,
      extra: extra ?? <String, dynamic>{
        'secure': <Map<String, String>>[],
      },
      validateStatus: validateStatus ??
              (status) => status! >= 200 && status < 300,
    );

    try {
      final _response = await _dio.request<Object>(
        _path,
        data: formData,
        options: _options,
        cancelToken: cancelToken,
        onSendProgress: onSendProgress,
        onReceiveProgress: onReceiveProgress,
      );

      print('Server response: ${_response.data}');

      String? _responseData;

      try {
        final rawResponse = _response.data;
        _responseData = rawResponse == null ? null : rawResponse as String;
      } catch (error, stackTrace) {
        throw DioException(
          requestOptions: _response.requestOptions,
          response: _response,
          type: DioExceptionType.unknown,
          error: error,
          stackTrace: stackTrace,
        );
      }

      return Response<String>(
        data: _responseData,
        headers: _response.headers,
        isRedirect: _response.isRedirect,
        requestOptions: _response.requestOptions,
        redirects: _response.redirects,
        statusCode: _response.statusCode,
        statusMessage: _response.statusMessage,
        extra: _response.extra,
      );
    } catch (e) {
      print('Upload error details: $e');
      if (e is DioException) {
        print('Response status: ${e.response?.statusCode}');
        print('Response data: ${e.response?.data}');
        print('Headers: ${e.response?.headers}');
      }
      rethrow;
    }
  }
}