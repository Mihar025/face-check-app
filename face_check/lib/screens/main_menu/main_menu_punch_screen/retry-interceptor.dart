import 'package:dio/dio.dart';
import 'dart:async';

class RetryInterceptor extends Interceptor {
  final Dio dio;
  final int maxRetries;
  final Duration retryDelay;

  RetryInterceptor({
    required this.dio,
    this.maxRetries = 3,
    this.retryDelay = const Duration(seconds: 2),
  });

  @override
  Future<void> onError(DioException err, ErrorInterceptorHandler handler) async {
    // Проверяем, нужно ли делать retry
    if (_shouldRetry(err)) {
      final retryCount = err.requestOptions.extra['retryCount'] ?? 0;

      if (retryCount < maxRetries) {
        print('🔄 Retrying request (attempt ${retryCount + 1}/$maxRetries)...');

        // Увеличиваем счетчик попыток
        err.requestOptions.extra['retryCount'] = retryCount + 1;


        try {
          // Повторяем запрос
          final response = await dio.fetch(err.requestOptions);
          return handler.resolve(response);
        } catch (e) {
          // Если снова ошибка, передаем дальше
          if (e is DioException) {
            return super.onError(e, handler);
          }
        }
      }
    }

    return super.onError(err, handler);
  }

  bool _shouldRetry(DioException err) {
    // Retry только для определенных ошибок
    return err.type == DioExceptionType.connectionTimeout ||
        err.type == DioExceptionType.sendTimeout ||
        err.type == DioExceptionType.receiveTimeout ||
        err.type == DioExceptionType.unknown ||
        (err.response?.statusCode != null &&
            (err.response!.statusCode! >= 500 ||
                err.response!.statusCode == 429)); // 429 = Too Many Requests
  }
}