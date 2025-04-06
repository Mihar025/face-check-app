import 'package:flutter/foundation.dart';
import '../localization/app_localizations.dart';
import '../services/settings_service.dart';
import '../screens/main_menu/notification/notification_service.dart';

class LocalizationProvider extends ChangeNotifier {
  String _currentLanguage = 'en';
  final SettingsService _settings = SettingsService();
  late AppLocalizations _localizations;

  LocalizationProvider() {
    _localizations = AppLocalizations(_currentLanguage);
    _loadSavedLanguage();
  }

  Future<void> _loadSavedLanguage() async {
    _currentLanguage = await _settings.getLanguage();
    _localizations = AppLocalizations(_currentLanguage);
    // Обновляем язык уведомлений при загрузке сохраненного языка
    try {
      if (NotificationService.instance != null) {
        await NotificationService.instance.updateLanguage(_currentLanguage);
      }
    } catch (e) {
      print('Notifications not yet initialized: $e');
    }
    notifyListeners();
  }

  Future<void> changeLanguage(String languageCode) async {
    _currentLanguage = languageCode;
    _localizations = AppLocalizations(languageCode);
    await _settings.setLanguage(languageCode);

    // Обновляем язык уведомлений при смене языка
    try {
      if (NotificationService.instance != null) {
        await NotificationService.instance.updateLanguage(languageCode);
      }
    } catch (e) {
      print('Error updating notifications language: $e');
    }

    notifyListeners();
  }

  AppLocalizations get localizations => _localizations;
  String get currentLanguage => _currentLanguage;
}