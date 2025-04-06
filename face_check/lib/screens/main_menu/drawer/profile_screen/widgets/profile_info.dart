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

    // Определяем размер экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildInfoItem(
            l10n.get('fullName'),
            userInfo?.fullName ?? l10n.get('na'),
            isSmallScreen
        ),
        _buildInfoItem(
            l10n.get('email'),
            userInfo?.email ?? l10n.get('na'),
            isSmallScreen
        ),
        _buildInfoItem(
            l10n.get('phoneNumber'),
            userInfo?.phoneNumber ?? l10n.get('na'),
            isSmallScreen
        ),
        _buildInfoItem(
            l10n.get('address'),
            userInfo?.address ?? l10n.get('na'),
            isSmallScreen
        ),
      ],
    );
  }

  Widget _buildInfoItem(String label, String value, bool isSmallScreen) {
    return Builder(
        builder: (context) {
          final theme = Theme.of(context);

          return Padding(
            padding: EdgeInsets.symmetric(vertical: isSmallScreen ? 6 : 8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: TextStyle(
                    color: theme.brightness == Brightness.dark
                        ? Colors.grey
                        : Colors.grey[700],
                    fontSize: isSmallScreen ? 14 : 16,
                  ),
                ),
                SizedBox(height: isSmallScreen ? 2 : 4),
                Text(
                  value,
                  style: TextStyle(
                    color: theme.textTheme.bodyLarge?.color,
                    fontSize: isSmallScreen ? 16 : 18,
                  ),
                ),
              ],
            ),
          );
        }
    );
  }
}