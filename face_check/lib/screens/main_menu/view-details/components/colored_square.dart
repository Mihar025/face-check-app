import 'package:flutter/material.dart';

class ColoredSquare extends StatelessWidget {
  final Color color;
  final double? size; // Добавляем опциональный параметр размера

  const ColoredSquare({
    super.key,
    required this.color,
    this.size,
  });

  @override
  Widget build(BuildContext context) {
    // Проверяем, не задан ли размер напрямую
    // Если нет, проверяем размер экрана и выбираем подходящий размер
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;
    final squareSize = size ?? (isSmallScreen ? 12.0 : 15.0);

    // Адаптивный отступ
    final rightMargin = isSmallScreen ? 8.0 : 10.0;

    return Container(
      width: squareSize,
      height: squareSize,
      margin: EdgeInsets.only(right: rightMargin),
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(squareSize / 5), // Пропорциональный радиус закругления
      ),
    );
  }
}