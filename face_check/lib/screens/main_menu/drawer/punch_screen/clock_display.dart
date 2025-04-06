import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'digit_box.dart';
import 'time_separator.dart';

class ClockDisplay extends StatelessWidget {
  final Color? textColor;
  final bool isSmallScreen;

  const ClockDisplay({
    super.key,
    this.textColor,
    this.isSmallScreen = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final color = textColor ?? theme.textTheme.bodyLarge?.color ?? Colors.white;

    return StreamBuilder(
      stream: Stream.periodic(const Duration(seconds: 1)),
      builder: (context, snapshot) {
        final now = DateTime.now();
        final date = DateFormat('dd MMMM').format(now);
        final time = DateFormat('HH:mm:ss').format(now);

        return Column(
          children: [
            Text(
              date,
              style: TextStyle(
                color: color,
                fontSize: isSmallScreen ? 20 : 24,
                fontWeight: FontWeight.w200,
                letterSpacing: isSmallScreen ? 1 : 2,
              ),
            ),
            SizedBox(height: isSmallScreen ? 16 : 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Row(
                  children: [
                    DigitBox(
                      digit: time.substring(0, 1),
                      textColor: color,
                      isSmallScreen: isSmallScreen,
                    ),
                    SizedBox(width: isSmallScreen ? 3 : 4),
                    DigitBox(
                      digit: time.substring(1, 2),
                      textColor: color,
                      isSmallScreen: isSmallScreen,
                    ),
                  ],
                ),
                TimeSeparator(
                  color: color,
                  isSmallScreen: isSmallScreen,
                ),
                Row(
                  children: [
                    DigitBox(
                      digit: time.substring(3, 4),
                      textColor: color,
                      isSmallScreen: isSmallScreen,
                    ),
                    SizedBox(width: isSmallScreen ? 3 : 4),
                    DigitBox(
                      digit: time.substring(4, 5),
                      textColor: color,
                      isSmallScreen: isSmallScreen,
                    ),
                  ],
                ),
                TimeSeparator(
                  color: color,
                  isSmallScreen: isSmallScreen,
                ),
                Row(
                  children: [
                    DigitBox(
                      digit: time.substring(6, 7),
                      textColor: color,
                      isSmallScreen: isSmallScreen,
                    ),
                    SizedBox(width: isSmallScreen ? 3 : 4),
                    DigitBox(
                      digit: time.substring(7, 8),
                      textColor: color,
                      isSmallScreen: isSmallScreen,
                    ),
                  ],
                ),
              ],
            ),
          ],
        );
      },
    );
  }
}