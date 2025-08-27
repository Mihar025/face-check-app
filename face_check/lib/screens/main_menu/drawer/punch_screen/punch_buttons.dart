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
      padding: EdgeInsets.symmetric(
        horizontal: isSmallScreen ? 20 : 24,
        vertical: isSmallScreen ? 16 : 20,
      ),
      child: Row(
        children: [
          Expanded(
            child: _buildPunchButton(
              context: context,
              onTap: onPunchIn,
              color: Colors.green,
              icon: Icons.login_rounded,
              label: l10n.get('punchIn'),
              isSmallScreen: isSmallScreen,
              theme: theme,
            ),
          ),
          SizedBox(width: isSmallScreen ? 12 : 16),
          Expanded(
            child: _buildPunchButton(
              context: context,
              onTap: onPunchOut,
              color: Colors.blue,
              icon: Icons.logout_rounded,
              label: l10n.get('punchOut'),
              isSmallScreen: isSmallScreen,
              theme: theme,
            ),
          ),
        ],
      ),
    );
  }
  Widget _buildPunchButton({
    required BuildContext context,
    required VoidCallback? onTap,
    required Color color,
    required IconData icon,
    required String label,
    required bool isSmallScreen,
    required ThemeData theme,
  }) {
    final isDisabled = onTap == null;

    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: isDisabled ? null : onTap,
        borderRadius: BorderRadius.circular(14), // было 16
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          height: isSmallScreen ? 46 : 52, // было 56 / 64 → меньше
          decoration: BoxDecoration(
            color: isDisabled
                ? (theme.brightness == Brightness.dark
                ? Colors.grey[800]
                : Colors.grey[200])
                : (buttonColor ?? (theme.brightness == Brightness.dark
                ? color.withOpacity(0.15)
                : color.withOpacity(0.1))),
            borderRadius: BorderRadius.circular(14),
            border: Border.all(
              color: isDisabled
                  ? Colors.grey.withOpacity(0.3)
                  : color.withOpacity(0.3),
              width: 1.5,
            ),
            boxShadow: isDisabled
                ? null
                : [
              BoxShadow(
                color: color.withOpacity(0.15),
                blurRadius: 6, // было 8
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: EdgeInsets.all(isSmallScreen ? 6 : 8), // было 8 / 10
                decoration: BoxDecoration(
                  color: isDisabled
                      ? Colors.grey.withOpacity(0.1)
                      : color.withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
                child: Icon(
                  icon,
                  color: isDisabled ? Colors.grey : color,
                  size: isSmallScreen ? 16 : 20, // было 20 / 24
                ),
              ),
              SizedBox(width: isSmallScreen ? 8 : 10), // было 10 / 12
              Text(
                label.toUpperCase(),
                style: TextStyle(
                  color: isDisabled ? Colors.grey : color,
                  fontSize: isSmallScreen ? 11 : 13, // было 13 / 15
                  fontWeight: FontWeight.w600,
                  letterSpacing: 1,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}