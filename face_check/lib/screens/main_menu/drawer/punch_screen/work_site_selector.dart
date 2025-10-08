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

    final txtColor = textColor ?? theme.textTheme.bodyLarge?.color ?? Colors.white;
    final bgColor = backgroundColor ?? (theme.brightness == Brightness.dark
        ? Colors.white.withOpacity(0.05)
        : Colors.white);

    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Container(
          padding: EdgeInsets.all(isSmallScreen ? 14 : 16),
          decoration: BoxDecoration(
            color: bgColor,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(
              color: selectedWorkSite != null
                  ? Colors.blue.withOpacity(0.3)
                  : (theme.brightness == Brightness.dark
                  ? Colors.white.withOpacity(0.1)
                  : Colors.grey.withOpacity(0.2)),
              width: 1,
            ),
            boxShadow: theme.brightness == Brightness.light
                ? [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 8,
                offset: const Offset(0, 2),
              ),
            ]
                : null,
          ),
          child: Row(
            children: [
              Container(
                padding: EdgeInsets.all(isSmallScreen ? 10 : 12),
                decoration: BoxDecoration(
                  color: selectedWorkSite != null
                      ? Colors.blue.withOpacity(0.1)
                      : txtColor.withOpacity(0.05),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  selectedWorkSite != null
                      ? Icons.location_on_rounded
                      : Icons.location_searching_rounded,
                  color: selectedWorkSite != null
                      ? Colors.blue
                      : txtColor.withOpacity(0.5),
                  size: isSmallScreen ? 20 : 24,
                ),
              ),
              SizedBox(width: isSmallScreen ? 12 : 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      selectedWorkSite != null
                          ? l10n.get('workSite')
                          : l10n.get('selectWorkSite'),
                      style: TextStyle(
                        color: txtColor.withOpacity(0.6),
                        fontSize: isSmallScreen ? 12 : 14,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    if (selectedWorkSite != null) ...[
                      SizedBox(height: 2),
                      Text(
                        selectedWorkSite!.workSiteName ?? '',
                        style: TextStyle(
                          color: txtColor,
                          fontSize: isSmallScreen ? 15 : 17,
                          fontWeight: FontWeight.w600,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ],
                ),
              ),
              Icon(
                Icons.chevron_right_rounded,
                color: txtColor.withOpacity(0.4),
                size: isSmallScreen ? 20 : 24,
              ),
            ],
          ),
        ),
      ),
    );
  }
}