import 'package:flutter/material.dart';
import 'dart:math' as math;

class ProgressPainter extends CustomPainter {
  final List<Animation<double>> animations;
  final List<Color> colors;
  final bool isSmallScreen;

  ProgressPainter({
    required this.animations,
    required this.colors,
    this.isSmallScreen = false,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = math.min(size.width, size.height) / 2;

    // Адаптивная ширина линии для разных размеров экрана
    final strokeWidth = isSmallScreen ? 18.0 : 22.0;

    // Background circle
    final backgroundPaint = Paint()
      ..color = Colors.grey[300]!
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth;

    canvas.drawCircle(center, radius - strokeWidth / 2, backgroundPaint);

    final rect = Rect.fromCircle(center: center, radius: radius - strokeWidth / 2);

    // Draw segments
    double startAngle = -math.pi / 2;
    double totalSweepAngle = 0.0;

    for (int i = 0; i < animations.length; i++) {
      final sweepAngle = 2 * math.pi * animations[i].value;
      totalSweepAngle += sweepAngle;

      final paint = Paint()
        ..color = colors[i]
        ..style = PaintingStyle.stroke
        ..strokeWidth = strokeWidth
        ..strokeCap = StrokeCap.butt;

      canvas.drawArc(
        rect,
        startAngle,
        sweepAngle,
        false,
        paint,
      );

      startAngle += sweepAngle;
    }
  }

  @override
  bool shouldRepaint(ProgressPainter oldDelegate) {
    return animations != oldDelegate.animations ||
        colors != oldDelegate.colors ||
        isSmallScreen != oldDelegate.isSmallScreen;
  }
}