import 'dart:ui';
import 'dart:math' as math;
import 'package:flutter/material.dart';

class ProgressPainter extends CustomPainter {
  final List<double> segments;
  final List<Color> colors;
  final double strokeWidth;

  ProgressPainter({
    required this.segments,
    required this.colors,
    this.strokeWidth = 60.0,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = math.min(size.width, size.height) / 2;

    canvas.saveLayer(Rect.fromLTWH(0, 0, size.width, size.height), Paint());

    final backgroundPaint = Paint()
      ..color = Colors.grey.withOpacity(0.2)
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.butt
      ..isAntiAlias = true;

    canvas.drawCircle(center, radius - strokeWidth / 2, backgroundPaint);

    final rect = Rect.fromCircle(center: center, radius: radius - strokeWidth / 2);

    double startAngle = -math.pi / 2;

    for (int i = 0; i < segments.length; i++) {
      final sweepAngle = 2 * math.pi * segments[i];

      final paint = Paint()
        ..color = colors[i]
        ..style = PaintingStyle.stroke
        ..strokeWidth = strokeWidth
        ..strokeCap = StrokeCap.butt
        ..isAntiAlias = true;

      canvas.drawArc(
        rect,
        startAngle,
        sweepAngle,
        false,
        paint,
      );

      startAngle += sweepAngle;
    }

    canvas.restore();
  }

  @override
  bool shouldRepaint(ProgressPainter oldDelegate) {
    return segments != oldDelegate.segments ||
        colors != oldDelegate.colors ||
        strokeWidth != oldDelegate.strokeWidth;
  }
}