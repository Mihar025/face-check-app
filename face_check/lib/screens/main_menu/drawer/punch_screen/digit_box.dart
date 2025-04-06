import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class DigitBox extends StatelessWidget {
  final String digit;
  final Color? textColor;
  final bool isSmallScreen;

  const DigitBox({
    required this.digit,
    this.textColor,
    this.isSmallScreen = false,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final color = textColor ?? Theme.of(context).textTheme.bodyLarge?.color ?? Colors.white;

    return Container(
      width: isSmallScreen ? 34 : 40,
      height: isSmallScreen ? 50 : 60,
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(isSmallScreen ? 6 : 8),
      ),
      child: Center(
        child: Text(
          digit,
          style: TextStyle(
            color: color,
            fontSize: isSmallScreen ? 28 : 32,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }
}