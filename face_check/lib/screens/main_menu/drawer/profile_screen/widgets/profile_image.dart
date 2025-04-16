import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../../../providers/localization_provider.dart';
import '../../../../../api_client/model/user_full_contact_information.dart';
import '../profile_controller/profile_controller.dart';

class ProfileImage extends StatelessWidget {
  final ProfileController controller;
  final UserFullContactInformation? userInfo;
  final bool isUploading;

  const ProfileImage({
    required this.controller,
    required this.userInfo,
    required this.isUploading,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Column(
      children: [
        GestureDetector(
          onTap: isUploading ? null : () => controller.pickAndUploadImage(context),
          child: Container(
            width: isSmallScreen ? 80 : 100,
            height: isSmallScreen ? 80 : 100,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              border: Border.all(
                color: Colors.blue,
                width: isSmallScreen ? 1.5 : 2,
              ),
              color: theme.brightness == Brightness.dark
                  ? Colors.grey[800]
                  : Colors.grey[200],
              image: controller.imageFile != null
                  ? DecorationImage(
                image: FileImage(controller.imageFile!),
                fit: BoxFit.cover,
              )
                  : userInfo?.photoUrl != null &&
                  userInfo!.photoUrl!.isNotEmpty
                  ? DecorationImage(
                image: NetworkImage(userInfo!.photoUrl!),
                fit: BoxFit.cover,
              )
                  : null,
            ),
            child: _buildImageChild(theme, isSmallScreen),
          ),
        ),
        SizedBox(height: isSmallScreen ? 8 : 10),
        ElevatedButton(
          onPressed: isUploading ? null : () => controller.pickAndUploadImage(context),
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.blue,
            padding: EdgeInsets.symmetric(
              horizontal: isSmallScreen ? 8 : 10,
              vertical: isSmallScreen ? 4 : 5,
            ),
            minimumSize: Size(isSmallScreen ? 80 : 100, isSmallScreen ? 26 : 30),
          ),
          child: _buildButtonChild(theme, l10n, isSmallScreen),
        ),
      ],
    );
  }

  Widget _buildImageChild(ThemeData theme, bool isSmallScreen) {
    if (controller.imageFile == null &&
        (userInfo?.photoUrl == null || userInfo!.photoUrl!.isEmpty)) {
      return Icon(
        Icons.person,
        size: isSmallScreen ? 40 : 50,
        color: theme.iconTheme.color,
      );
    }

    if (isUploading) {
      return Center(
        child: CircularProgressIndicator(
          color: theme.iconTheme.color,
          strokeWidth: isSmallScreen ? 2 : 3,
        ),
      );
    }

    return const SizedBox.shrink();
  }

  Widget _buildButtonChild(ThemeData theme, dynamic l10n, bool isSmallScreen) {
    if (isUploading) {
      return SizedBox(
        width: isSmallScreen ? 16 : 20,
        height: isSmallScreen ? 16 : 20,
        child: CircularProgressIndicator(
          color: theme.brightness == Brightness.dark
              ? Colors.white
              : Colors.black,
          strokeWidth: isSmallScreen ? 1.5 : 2,
        ),
      );
    }

    return Text(
      l10n.get('updateImage'),
      style: TextStyle(
        color: theme.brightness == Brightness.dark
            ? Colors.white
            : Colors.black,
        fontSize: isSmallScreen ? 10 : 12,
      ),
    );
  }
}