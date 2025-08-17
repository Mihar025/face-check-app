import 'package:dio/dio.dart';
import 'dart:convert';

class ErrorHandler {
  static String getErrorMessage(dynamic error) {
    if (error is DioException) {
      return _handleDioError(error);
    }
    return 'Unexpected error occurred: ${error.toString()}';
  }

  static String _handleDioError(DioException error) {
    switch (error.type) {
      case DioExceptionType.connectionTimeout:
        return 'Connection timeout. Please check your internet connection.';

      case DioExceptionType.sendTimeout:
        return 'Request timeout. Please try again.';

      case DioExceptionType.receiveTimeout:
        return 'Server response timeout. Please try again.';

      case DioExceptionType.badResponse:
        return _handleServerError(error.response);

      case DioExceptionType.cancel:
        return 'Request was cancelled.';

      case DioExceptionType.unknown:
        if (error.message?.contains('SocketException') ?? false) {
          return 'No internet connection. Please check your network.';
        }
        return 'Connection failed. Please check your internet.';

      default:
        return 'Something went wrong. Please try again.';
    }
  }

  static String _handleServerError(Response? response) {
    if (response == null) {
      return 'No response from server. Please try again.';
    }

    // Попробуем распарсить ответ от сервера
    try {
      final data = response.data;

      // Если сервер возвращает структурированную ошибку
      if (data is Map<String, dynamic>) {
        // Проверяем разные форматы ошибок от сервера
        final message = data['message'] ??
            data['error'] ??
            data['detail'] ??
            data['errors']?.toString();

        if (message != null && message.toString().isNotEmpty) {
          return _translateServerMessage(message.toString(), response.statusCode);
        }
      }

      // Обработка по коду статуса
      return _getMessageByStatusCode(response.statusCode ?? 0);

    } catch (e) {
      // Если не удалось распарсить, возвращаем общее сообщение по коду
      return _getMessageByStatusCode(response.statusCode ?? 0);
    }
  }

  static String _translateServerMessage(String serverMessage, int? statusCode) {
    // Здесь можно добавить переводы типовых серверных сообщений
    final lowerMessage = serverMessage.toLowerCase();

    // Punch In/Out специфичные ошибки
    if (lowerMessage.contains('already punched in')) {
      return 'You are already punched in. Please punch out first.';
    }

    if (lowerMessage.contains('not punched in')) {
      return 'You are not punched in. Please punch in first.';
    }

    if (lowerMessage.contains('outside work hours')) {
      return 'Cannot punch outside of work hours.';
    }

    if (lowerMessage.contains('location') || lowerMessage.contains('geofence')) {
      return 'You are too far from the work site. Please move closer.';
    }

    if (lowerMessage.contains('work site not found')) {
      return 'Selected work site not found. Please select another one.';
    }

    if (lowerMessage.contains('photo') || lowerMessage.contains('image')) {
      return 'Photo verification failed. Please retake the photo.';
    }

    if (lowerMessage.contains('unauthorized') || statusCode == 401) {
      return 'Session expired. Please login again.';
    }

    if (lowerMessage.contains('forbidden') || statusCode == 403) {
      return 'You don\'t have permission for this action.';
    }

    return serverMessage;
  }

  static String _getMessageByStatusCode(int statusCode) {
    switch (statusCode) {
      case 400:
        return 'Invalid request. Please check your input.';
      case 401:
        return 'Authentication failed. Please login again.';
      case 403:
        return 'You don\'t have permission for this action.';
      case 404:
        return 'Resource not found. Please refresh and try again.';
      case 409:
        return 'Conflict with current state. Please refresh and try again.';
      case 422:
        return 'Invalid data provided. Please check and try again.';
      case 429:
        return 'Too many requests. Please wait a moment.';
      case 500:
        return 'Server error. Please try again later.';
      case 502:
        return 'Server is temporarily unavailable.';
      case 503:
        return 'Service unavailable. Please try again later.';
      default:
        return 'Error $statusCode occurred. Please try again.';
    }
  }
}