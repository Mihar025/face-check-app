import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:timezone/timezone.dart' as tz;

class ClockDisplay extends StatelessWidget {
  final Color? textColor;
  final bool isSmallScreen;
  final Stream<tz.TZDateTime>? timeStream;

  const ClockDisplay({
    super.key,
    this.textColor,
    this.isSmallScreen = false,
    this.timeStream,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final color = textColor ?? theme.textTheme.bodyLarge?.color ?? Colors.white;
    final ny = tz.getLocation('America/New_York');

    return StreamBuilder<tz.TZDateTime>(
      stream: timeStream ?? Stream.periodic(const Duration(seconds: 1), (_) => tz.TZDateTime.now(ny)),
      builder: (context, snapshot) {
        final now = snapshot.data ?? tz.TZDateTime.now(ny);

        final date = DateFormat('EEEE, dd MMMM').format(now);
        final time = DateFormat('HH:mm:ss').format(now);
        final hour = time.substring(0, 2);
        final minute = time.substring(3, 5);
        final second = time.substring(6, 8);

        return Column(
          children: [
            Text(
              date.toUpperCase(),
              style: TextStyle(
                color: color.withOpacity(0.6),
                fontSize: isSmallScreen ? 12 : 14,
                fontWeight: FontWeight.w600,
                letterSpacing: isSmallScreen ? 1.5 : 2,
              ),
            ),
            SizedBox(height: isSmallScreen ? 16 : 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                _buildTimeSection(hour, color, isSmallScreen),
                _buildSeparator(color, isSmallScreen),
                _buildTimeSection(minute, color, isSmallScreen),
                _buildSeparator(color, isSmallScreen),
                _buildTimeSection(second, color, isSmallScreen, isSeconds: true),
              ],
            ),
          ],
        );
      },
    );
  }

  Widget _buildTimeSection(String value, Color color, bool isSmallScreen, {bool isSeconds = false}) {
    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: isSmallScreen ? 8 : 12,
        vertical: isSmallScreen ? 10 : 14,
      ),
      decoration: BoxDecoration(
        color: color.withOpacity(0.05),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.1), width: 1),
      ),
      child: Text(
        value,
        style: TextStyle(
          color: isSeconds ? color.withOpacity(0.7) : color,
          fontSize: isSmallScreen ? 32 : 40,
          fontWeight: FontWeight.w300,
          fontFeatures: const [FontFeature.tabularFigures()],
        ),
      ),
    );
  }

  Widget _buildSeparator(Color color, bool isSmallScreen) {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: isSmallScreen ? 6 : 8),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          _dot(color, isSmallScreen),
          SizedBox(height: isSmallScreen ? 8 : 10),
          _dot(color, isSmallScreen),
        ],
      ),
    );
  }

  Widget _dot(Color color, bool isSmallScreen) => Container(
    width: isSmallScreen ? 4 : 5,
    height: isSmallScreen ? 4 : 5,
    decoration: BoxDecoration(color: color.withOpacity(0.8), shape: BoxShape.circle),
  );
}
