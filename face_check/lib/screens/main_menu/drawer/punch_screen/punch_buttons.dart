import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../../../providers/localization_provider.dart';

class PunchButtons extends StatelessWidget {
  final VoidCallback? onPunchIn;
  final VoidCallback? onPunchOut;
  final Color? backgroundColor;
  final Color? buttonColor;
  final Color? textColor;

  const PunchButtons({
    required this.onPunchIn,
    required this.onPunchOut,
    this.backgroundColor,
    this.buttonColor,
    this.textColor,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;
    final isSmallScreen = MediaQuery.of(context).size.width < 360;

    return Container(
      color: backgroundColor ?? theme.scaffoldBackgroundColor,
      padding: EdgeInsets.only(bottom: isSmallScreen ? 16 : 20),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          _buildPunchButton(
            onTap: onPunchIn,
            color: Colors.green,
            icon: Icons.login,
            label: l10n.get('punchIn'),
            isSmallScreen: isSmallScreen,
          ),
          SizedBox(width: isSmallScreen ? 12 : 20),
          _buildPunchButton(
            onTap: onPunchOut,
            color: Colors.blue,
            icon: Icons.logout,
            label: l10n.get('punchOut'),
            isSmallScreen: isSmallScreen,
          ),
        ],
      ),
    );
  }

  Widget _buildPunchButton({
    required VoidCallback? onTap,
    required Color color,
    required IconData icon,
    required String label,
    required bool isSmallScreen,
  }) {
    return Material(
      color: buttonColor ?? Colors.transparent,
      borderRadius: BorderRadius.circular(isSmallScreen ? 22 : 25),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(isSmallScreen ? 22 : 25),
        child: Container(
          width: isSmallScreen ? 65 : 75,
          height: isSmallScreen ? 65 : 75,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: color.withOpacity(0.2),
            border: Border.all(
              color: color,
              width: isSmallScreen ? 1.5 : 2,
            ),
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                icon,
                color: color,
                size: isSmallScreen ? 20 : 24,
              ),
              SizedBox(height: isSmallScreen ? 2 : 4),
              Text(
                label,
                style: TextStyle(
                  color: color,
                  fontSize: isSmallScreen ? 10 : 12,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}