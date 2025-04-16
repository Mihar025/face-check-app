import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../providers/localization_provider.dart';
import 'progress_painter.dart';

class TimeCircle extends StatelessWidget {
  final String time;
  final double workedHours;
  final bool isSmallScreen;

  const TimeCircle({
    super.key,
    required this.time,
    required this.workedHours,
    this.isSmallScreen = false,
  });

  List<double> _calculateSegments() {
    final normalHours = workedHours.clamp(0.0, 40.0);
    final overtimeHours = workedHours > 40 ? workedHours - 40 : 0.0;
    double totalValue = workedHours >= 40 ? workedHours : 40.0;

    final segments = <double>[];

    if (workedHours > 0) {
      segments.add((normalHours / totalValue).toDouble());
    }

    if (overtimeHours > 0) {
      segments.add((overtimeHours / totalValue).toDouble());
    }

    if (workedHours < 40) {
      final remainingPercentage = (40 - workedHours) / totalValue;
      segments.add(remainingPercentage);
    }

    return segments;
  }

  List<Color> _getColors() {
    final colors = <Color>[];

    if (workedHours > 0) {
      colors.add(Colors.green);
    }

    if (workedHours > 40) {
      colors.add(Colors.red);
    }

    if (workedHours < 40) {
      colors.add(Colors.grey.withOpacity(0.2));
    }

    return colors;
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    final containerWidth = isSmallScreen ? 220.0 : 250.0;
    final containerHeight = isSmallScreen ? 230.0 : 260.0;
    final circleSize = isSmallScreen ? 280.0 : 320.0;
    final innerCircleSize = isSmallScreen ? 160.0 : 180.0;

    return Container(
      width: containerWidth,
      height: containerHeight,
      child: Stack(
        alignment: Alignment.center,
        children: [
          SizedBox(
            width: circleSize,
            height: circleSize,
            child: CustomPaint(
              painter: ProgressPainter(
                segments: _calculateSegments(),
                colors: _getColors(),
                strokeWidth: isSmallScreen ? 50.0 : 60.0,
              ),
            ),
          ),
          Container(
            width: innerCircleSize,
            height: innerCircleSize,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: theme.brightness == Brightness.dark
                  ? Colors.black
                  : Colors.white,
            ),
          ),
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                l10n.get('workedHours'),
                style: TextStyle(
                  color: theme.brightness == Brightness.dark
                      ? Colors.white
                      : Colors.black,
                  fontSize: isSmallScreen ? 12 : 14,
                  fontWeight: FontWeight.w500,
                ),
              ),
              SizedBox(height: isSmallScreen ? 6 : 8),
              Text(
                time,
                style: TextStyle(
                  color: theme.brightness == Brightness.dark
                      ? Colors.white
                      : Colors.black,
                  fontSize: isSmallScreen ? 22 : 25,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}