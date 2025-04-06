import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class TimeSeparator extends StatelessWidget {
  final Color? color;
  final bool isSmallScreen;

  const TimeSeparator({
    this.color,
    this.isSmallScreen = false,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final finalColor = color ?? Theme.of(context).textTheme.bodyLarge?.color ?? Colors.white;

    // Адаптируем размер шрифта в зависимости от размера экрана
    final screenSize = MediaQuery.of(context).size;
    final reallySmallScreen = screenSize.width < 320; // Для очень маленьких экранов

    // Выбираем размер в зависимости от размера экрана
    double fontSize;
    if (reallySmallScreen) {
      fontSize = 24; // Очень маленький экран
    } else if (isSmallScreen) {
      fontSize = 28; // Маленький экран
    } else {
      fontSize = 32; // Нормальный экран
    }

    return Padding(
      padding: EdgeInsets.symmetric(
          horizontal: reallySmallScreen ? 4 : (isSmallScreen ? 6 : 8)
      ),
      child: Text(
        ':',
        style: TextStyle(
          color: finalColor,
          fontSize: fontSize,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }
}