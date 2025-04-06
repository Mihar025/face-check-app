import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SettingsService {
  static const String LANGUAGE_KEY = 'app_language';
  final _storage = const FlutterSecureStorage();

  Future<void> setLanguage(String languageCode) async {
    await _storage.write(key: LANGUAGE_KEY, value: languageCode);
  }

  Future<String> getLanguage() async {
    return await _storage.read(key: LANGUAGE_KEY) ?? 'en';
  }
}