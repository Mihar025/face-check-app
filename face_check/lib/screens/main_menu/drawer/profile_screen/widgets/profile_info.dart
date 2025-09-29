import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../../../providers/localization_provider.dart';
import '../../../../../api_client/model/user_full_contact_information.dart';

class ProfileInfo extends StatelessWidget {
  final UserFullContactInformation? userInfo;

  const ProfileInfo({
    required this.userInfo,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final l10n = context.read<LocalizationProvider>().localizations;
    final theme = Theme.of(context);

    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        _buildInfoCard(
          context: context,
          icon: Icons.person_outline_rounded,
          iconColor: Colors.blue,
          label: l10n.get('fullName'),
          value: userInfo?.fullName ?? l10n.get('na'),
          isSmallScreen: isSmallScreen,
        ),
        SizedBox(height: isSmallScreen ? 10 : 12),
        _buildInfoCard(
          context: context,
          icon: Icons.email_outlined,
          iconColor: Colors.green,
          label: l10n.get('email'),
          value: userInfo?.email ?? l10n.get('na'),
          isSmallScreen: isSmallScreen,
        ),
        SizedBox(height: isSmallScreen ? 10 : 12),
        _buildInfoCard(
          context: context,
          icon: Icons.phone_outlined,
          iconColor: Colors.orange,
          label: l10n.get('phoneNumber'),
          value: userInfo?.phoneNumber ?? l10n.get('na'),
          isSmallScreen: isSmallScreen,
        ),
        SizedBox(height: isSmallScreen ? 10 : 12),
        _buildInfoCard(
          context: context,
          icon: Icons.location_on_outlined,
          iconColor: Colors.purple,
          label: l10n.get('address'),
          value: userInfo?.address ?? l10n.get('na'),
          isSmallScreen: isSmallScreen,
        ),
      ],
    );
  }

  Widget _buildInfoCard({
    required BuildContext context,
    required IconData icon,
    required Color iconColor,
    required String label,
    required String value,
    required bool isSmallScreen,
  }) {
    final theme = Theme.of(context);

    return Container(
      padding: EdgeInsets.all(isSmallScreen ? 14 : 16),
      decoration: BoxDecoration(
        color: theme.brightness == Brightness.dark
            ? Colors.white.withOpacity(0.05)
            : Colors.white,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(
          color: theme.brightness == Brightness.dark
              ? Colors.white.withOpacity(0.1)
              : Colors.grey.withOpacity(0.15),
          width: 1,
        ),
        boxShadow: theme.brightness == Brightness.light
            ? [
          BoxShadow(
            color: Colors.black.withOpacity(0.03),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ]
            : null,
      ),
      child: Row(
        children: [
          Container(
            width: isSmallScreen ? 40 : 48,
            height: isSmallScreen ? 40 : 48,
            decoration: BoxDecoration(
              color: iconColor.withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(
              icon,
              color: iconColor,
              size: isSmallScreen ? 20 : 24,
            ),
          ),
          SizedBox(width: isSmallScreen ? 12 : 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: TextStyle(
                    color: theme.textTheme.bodyMedium?.color?.withOpacity(0.6),
                    fontSize: isSmallScreen ? 12 : 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                SizedBox(height: isSmallScreen ? 2 : 4),
                Text(
                  value,
                  style: TextStyle(
                    color: theme.textTheme.bodyLarge?.color,
                    fontSize: isSmallScreen ? 15 : 17,
                    fontWeight: FontWeight.w500,
                  ),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}