// lib/theme/app_theme.dart:
import 'package:flutter/material.dart';

class AppTheme {
  // Основные цвета для элементов интерфейса
  static const primaryColor = Colors.white; // Для основных элементов
  static const backgroundColor = Colors.black; // Прозрачный, так как будет фоновая картинка
  static const textColor = Colors.white; // Для текста

  static ThemeData get theme {
    return ThemeData(
      useMaterial3: false,
      primaryColor: primaryColor,
      scaffoldBackgroundColor: backgroundColor, // Прозрачный фон для Scaffold
      appBarTheme: const AppBarTheme(
        backgroundColor: Colors.transparent, // Прозрачный AppBar
        elevation: 0, // Убираем тень
        iconTheme: IconThemeData(color: textColor),
        titleTextStyle: TextStyle(
          color: textColor,
          fontSize: 20,
          fontWeight: FontWeight.w500,
        ),
        centerTitle: false,
      ),
      iconTheme: const IconThemeData(
        color: textColor,
      ),
      textTheme: const TextTheme(
        bodyLarge: TextStyle(
          color: textColor,
          fontSize: 16,
        ),
        bodyMedium: TextStyle(
          color: textColor,
          fontSize: 14,
        ),
        titleLarge: TextStyle(
          color: textColor,
          fontSize: 22,
          fontWeight: FontWeight.bold,
        ),
      ),
      // Настройки для кнопок
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.white.withOpacity(0.2), // Полупрозрачный фон
          foregroundColor: Colors.white,
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
          ),
        ),
      ),
    );
  }
}