import 'package:flutter/material.dart';
import 'dart:math' as math;

class ProgressCircle extends StatefulWidget {
  final double workedHours;
  final double? size;

  const ProgressCircle({
    super.key,
    required this.workedHours,
    this.size,
  });

  @override
  State<ProgressCircle> createState() => _ProgressCircleState();
}

class _ProgressCircleState extends State<ProgressCircle> with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );

    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    final circleSize = widget.size ?? (isSmallScreen ? 130.0 : 150.0);

    final fontSize = isSmallScreen ? 16.0 : 18.0;
    final labelSize = isSmallScreen ? 12.0 : 14.0;

    return SizedBox(
      width: circleSize,
      height: circleSize,
      child: Stack(
        children: [
          AnimatedBuilder(
            animation: _controller,
            builder: (context, child) {
              return CustomPaint(
                size: Size(circleSize, circleSize),
                painter: AnimatedProgressPainter(
                  workedHours: widget.workedHours,
                  progress: _controller.value,
                  isSmallScreen: isSmallScreen,
                ),
              );
            },
          ),
          Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  '${widget.workedHours.toStringAsFixed(1)}',
                  style: TextStyle(
                    fontSize: fontSize,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  'Hours',
                  style: TextStyle(
                    fontSize: labelSize,
                    color: Colors.grey,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class AnimatedProgressPainter extends CustomPainter {
  final double workedHours;
  final double progress;
  final bool isSmallScreen;

  AnimatedProgressPainter({
    required this.workedHours,
    required this.progress,
    this.isSmallScreen = false,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = math.min(size.width, size.height) / 2;

    final strokeWidth = isSmallScreen ? 18.0 : 22.0;

    final backgroundPaint = Paint()
      ..color = Colors.grey[300]!
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth;

    canvas.drawCircle(center, radius - strokeWidth / 2, backgroundPaint);

    final rect = Rect.fromCircle(center: center, radius: radius - strokeWidth / 2);

    final List<double> segments = [];
    final List<Color> colors = [];

    final normalHours = workedHours.clamp(0.0, 40.0);
    final overtimeHours = workedHours > 40 ? workedHours - 40 : 0.0;
    double totalValue = workedHours >= 40 ? workedHours : 40.0;

    if (workedHours > 0) {
      segments.add((normalHours / totalValue));
      colors.add(Colors.orange);
    }

    if (overtimeHours > 0) {
      segments.add((overtimeHours / totalValue));
      colors.add(Colors.red);
    }

    if (workedHours < 40) {
      final remainingPercentage = (40 - workedHours) / totalValue;
      segments.add(remainingPercentage);
      colors.add(Colors.yellow);
    }

    double startAngle = -math.pi / 2;
    for (int i = 0; i < segments.length; i++) {
      final sweepAngle = 2 * math.pi * segments[i] * progress;

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
  bool shouldRepaint(AnimatedProgressPainter oldDelegate) {
    return workedHours != oldDelegate.workedHours ||
        progress != oldDelegate.progress ||
        isSmallScreen != oldDelegate.isSmallScreen;
  }
}