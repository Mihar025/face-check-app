import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../../../providers/localization_provider.dart';
import '../../../../../api_client/model/work_site_response.dart';

class WorkSiteSelectorButton extends StatelessWidget {
  final WorkSiteResponse? selectedWorkSite;
  final VoidCallback onTap;
  final Color? backgroundColor;
  final Color? textColor;
  final bool isSmallScreen;

  const WorkSiteSelectorButton({
    required this.selectedWorkSite,
    required this.onTap,
    this.backgroundColor,
    this.textColor,
    this.isSmallScreen = false,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;
    final screenSize = MediaQuery.of(context).size.width;
    final isVerySmallScreen = screenSize < 320;

    final txtColor = textColor ?? theme.textTheme.bodyLarge?.color ?? Colors.white;
    final bgColor = backgroundColor ?? (theme.brightness == Brightness.dark
        ? Colors.white.withOpacity(0.1)
        : Colors.black.withOpacity(0.1));

    final sizeFactor = isVerySmallScreen ? 0.75 : (isSmallScreen ? 0.9 : 1.0);
    final double fontSize = 16.0 * sizeFactor;
    final double padding = 16.0 * sizeFactor;
    final double borderRadius = 12.0 * sizeFactor;
    final double iconSize = 18.0 * sizeFactor;
    final double margin = 20.0 * sizeFactor;

    return Container(
      margin: EdgeInsets.symmetric(horizontal: margin),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(borderRadius),
          child: Container(
            padding: EdgeInsets.all(padding),
            decoration: BoxDecoration(
              color: bgColor,
              borderRadius: BorderRadius.circular(borderRadius),
              border: Border.all(
                color: theme.brightness == Brightness.dark
                    ? Colors.white.withOpacity(0.1)
                    : Colors.black.withOpacity(0.1),
              ),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Text(
                    selectedWorkSite?.workSiteName ?? l10n.get('selectWorkSite'),
                    style: TextStyle(
                      color: txtColor,
                      fontSize: fontSize,
                      fontWeight: selectedWorkSite != null ? FontWeight.w600 : FontWeight.w500,
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                Icon(
                  Icons.arrow_forward_ios,
                  color: txtColor.withOpacity(0.5),
                  size: iconSize,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
