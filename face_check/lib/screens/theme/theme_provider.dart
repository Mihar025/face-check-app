import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ThemeProvider extends ChangeNotifier {
  SharedPreferences? _prefs;
  bool _isDarkTheme;

  /// Можно создать провайдер без prefs, а потом «прикрепить» их.
  ThemeProvider({SharedPreferences? prefs, bool initialDark = true})
      : _prefs = prefs,
        _isDarkTheme = prefs?.getBool('isDarkTheme') ?? initialDark;

  bool get isDarkTheme => _isDarkTheme;
  ThemeData get currentTheme => _isDarkTheme ? darkTheme : lightTheme;

  /// Вызываем, когда prefs стали доступны (после старта)
  void attachPrefs(SharedPreferences prefs) {
    _prefs = prefs;
    final saved = prefs.getBool('isDarkTheme');
    if (saved != null && saved != _isDarkTheme) {
      _isDarkTheme = saved;
      notifyListeners();
    }
  }

  void toggleTheme(bool isDark) {
    if (_isDarkTheme == isDark) return;
    _isDarkTheme = isDark;
    _prefs?.setBool('isDarkTheme', isDark);
    notifyListeners();
  }

  // -------- ТЕМЫ (как у тебя) --------

  static final darkTheme = ThemeData(
    scaffoldBackgroundColor: Colors.black,
    primaryColor: Colors.white,
    appBarTheme: const AppBarTheme(
      backgroundColor: Colors.black,
      foregroundColor: Colors.white,
      iconTheme: IconThemeData(color: Colors.white),
    ),
    textTheme: const TextTheme(
      bodyLarge: TextStyle(color: Colors.white),
      bodyMedium: TextStyle(color: Colors.white),
      titleLarge: TextStyle(color: Colors.white),
    ),
    iconTheme: const IconThemeData(color: Colors.white),
    dialogTheme: DialogThemeData(
      backgroundColor: Colors.grey[900],
      titleTextStyle: const TextStyle(color: Colors.white, fontSize: 20),
      contentTextStyle: const TextStyle(color: Colors.white),
    ),
    bottomSheetTheme: BottomSheetThemeData(
      backgroundColor: Colors.grey[900],
    ),
    drawerTheme: const DrawerThemeData(backgroundColor: Colors.black),
    switchTheme: SwitchThemeData(
      thumbColor: MaterialStateProperty.resolveWith((_) => Colors.white),
      trackColor: MaterialStateProperty.resolveWith(
            (s) => s.contains(MaterialState.selected) ? Colors.white70 : Colors.grey,
      ),
    ),
    colorScheme: const ColorScheme.dark(),
  );

  static final lightTheme = ThemeData(
    scaffoldBackgroundColor: Colors.white,
    primaryColor: Colors.black,
    appBarTheme: const AppBarTheme(
      backgroundColor: Colors.white,
      foregroundColor: Colors.black,
      iconTheme: IconThemeData(color: Colors.black),
    ),
    textTheme: const TextTheme(
      bodyLarge: TextStyle(color: Colors.black),
      bodyMedium: TextStyle(color: Colors.black),
      titleLarge: TextStyle(color: Colors.black),
    ),
    iconTheme: const IconThemeData(color: Colors.black),
    dialogTheme: const DialogThemeData(
      backgroundColor: Colors.white,
      titleTextStyle: TextStyle(color: Colors.black, fontSize: 20),
      contentTextStyle: TextStyle(color: Colors.black),
    ),
    bottomSheetTheme: const BottomSheetThemeData(backgroundColor: Colors.white),
    drawerTheme: const DrawerThemeData(backgroundColor: Colors.white),
    switchTheme: SwitchThemeData(
      thumbColor: MaterialStateProperty.resolveWith((_) => Colors.black),
      trackColor: MaterialStateProperty.resolveWith(
            (s) => s.contains(MaterialState.selected) ? Colors.black87 : Colors.grey,
      ),
    ),
    colorScheme: const ColorScheme.light(),
  );
}
