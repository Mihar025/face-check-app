import 'package:dio/dio.dart';
import 'package:face_check/api_client/api/authentication_api.dart';
import 'package:face_check/api_client/model/settings_email_request.dart';
import 'package:face_check/api_client/model/settings_password_request.dart';
import 'package:face_check/api_client/model/settings_phone_number_request.dart';

class SettingsController {
  final AuthenticationApi _authApi;

  SettingsController(this._authApi);

  Future<void> updateEmail({
    required String currentEmail,
    required String newEmail,
    required String confirmEmail,
  }) async {
    try {
      final request = SettingsEmailRequest((b) => b
        ..currentEmail = currentEmail
        ..newEmail = newEmail
        ..confirmNewEmail = confirmEmail);

      await _authApi.updateEmail(settingsEmailRequest: request);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  Future<void> updatePassword({
    required String currentPassword,
    required String newPassword,
    required String confirmPassword,
  }) async {
    try {
      final request = SettingsPasswordRequest((b) => b
        ..currentPassword = currentPassword
        ..newPassword = newPassword
        ..confirmPassword = confirmPassword);

      await _authApi.updatePassword(settingsPasswordRequest: request);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  Future<void> updatePhoneNumber({
    required String currentPhoneNumber,
    required String newPhoneNumber,
    required String confirmPhoneNumber,
  }) async {
    try {
      final request = SettingsPhoneNumberRequest((b) => b
        ..currentPhoneNumber = currentPhoneNumber
        ..newPhoneNumber = newPhoneNumber
        ..confirmNewPhoneNumber = confirmPhoneNumber);

      await _authApi.updatePhoneNumber(settingsPhoneNumberRequest: request);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  String _handleDioError(DioException error) {
    if (error.response?.data != null && error.response?.data['message'] != null) {
      return error.response?.data['message'];
    }
    return 'An error occurred. Please try again.';
  }
}