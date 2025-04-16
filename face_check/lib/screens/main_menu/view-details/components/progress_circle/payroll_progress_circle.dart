import 'package:flutter/material.dart';
import 'dart:math' as math;

class PayrollProgressCircle extends StatefulWidget {
  final double baseRate;
  final double grossAmount;
  final double taxesAmount;
  final double netAmount;
  final double maxAmount;
  final double? size;

  const PayrollProgressCircle({
    super.key,
    required this.baseRate,
    required this.grossAmount,
    required this.taxesAmount,
    required this.netAmount,
    required this.maxAmount,
    this.size,
  });

  @override
  State<PayrollProgressCircle> createState() => _PayrollProgressCircleState();
}

class _PayrollProgressCircleState extends State<PayrollProgressCircle> with SingleTickerProviderStateMixin {
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
                painter: AnimatedPayrollPainter(
                  baseRate: widget.baseRate,
                  grossAmount: widget.grossAmount,
                  taxesAmount: widget.taxesAmount,
                  netAmount: widget.netAmount,
                  maxAmount: widget.maxAmount,
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
                  '\$${widget.netAmount.toStringAsFixed(2)}',
                  style: TextStyle(
                    fontSize: fontSize,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  'Net Income',
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

class AnimatedPayrollPainter extends CustomPainter {
  final double baseRate;
  final double grossAmount;
  final double taxesAmount;
  final double netAmount;
  final double maxAmount;
  final double progress;
  final bool isSmallScreen;

  AnimatedPayrollPainter({
    required this.baseRate,
    required this.grossAmount,
    required this.taxesAmount,
    required this.netAmount,
    required this.maxAmount,
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

    segments.add(baseRate / maxAmount);
    colors.add(Colors.purpleAccent);

    segments.add((grossAmount - baseRate) / maxAmount);
    colors.add(Colors.indigo);

    segments.add((taxesAmount - grossAmount) / maxAmount);
    colors.add(Colors.lightBlue);

    segments.add((netAmount - taxesAmount) / maxAmount);
    colors.add(Colors.indigoAccent);

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
  bool shouldRepaint(AnimatedPayrollPainter oldDelegate) {
    return baseRate != oldDelegate.baseRate ||
        grossAmount != oldDelegate.grossAmount ||
        taxesAmount != oldDelegate.taxesAmount ||
        netAmount != oldDelegate.netAmount ||
        maxAmount != oldDelegate.maxAmount ||
        progress != oldDelegate.progress ||
        isSmallScreen != oldDelegate.isSmallScreen;
  }
}