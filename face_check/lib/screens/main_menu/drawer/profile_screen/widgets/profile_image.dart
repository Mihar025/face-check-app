import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../../../providers/localization_provider.dart';
import '../../../../../api_client/model/user_full_contact_information.dart';
import '../profile_controller/profile_controller.dart';
import 'dart:io';

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
        Stack(
          alignment: Alignment.center,
          children: [
            // Avatar Container
            Container(
              width: isSmallScreen ? 100 : 120,
              height: isSmallScreen ? 100 : 120,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: theme.brightness == Brightness.dark
                    ? Colors.grey[800]
                    : Colors.grey[100],
                boxShadow: [
                  BoxShadow(
                    color: Colors.blue.withOpacity(0.15),
                    blurRadius: 20,
                    spreadRadius: 2,
                  ),
                ],
              ),
              child: Stack(
                children: [
                  // Image or Icon
                  Container(
                    width: isSmallScreen ? 100 : 120,
                    height: isSmallScreen ? 100 : 120,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      border: Border.all(
                        color: Colors.blue.withOpacity(0.3),
                        width: isSmallScreen ? 2 : 3,
                      ),
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
                    child: ClipOval(
                      child: _buildImageChild(theme, isSmallScreen),
                    ),
                  ),

                  // Edit Button Overlay
                  if (!isUploading)
                    Positioned(
                      bottom: 0,
                      right: 0,
                      child: GestureDetector(
                        onTap: () => controller.pickAndUploadImage(context),
                        child: Container(
                          width: isSmallScreen ? 32 : 36,
                          height: isSmallScreen ? 32 : 36,
                          decoration: BoxDecoration(
                            color: Colors.blue,
                            shape: BoxShape.circle,
                            border: Border.all(
                              color: theme.scaffoldBackgroundColor,
                              width: isSmallScreen ? 2 : 3,
                            ),
                            boxShadow: [
                              BoxShadow(
                                color: Colors.blue.withOpacity(0.3),
                                blurRadius: 8,
                                offset: const Offset(0, 2),
                              ),
                            ],
                          ),
                          child: Icon(
                            Icons.camera_alt_rounded,
                            color: Colors.white,
                            size: isSmallScreen ? 16 : 18,
                          ),
                        ),
                      ),
                    ),
                ],
              ),
            ),

            // Loading Overlay
            if (isUploading)
              Container(
                width: isSmallScreen ? 100 : 120,
                height: isSmallScreen ? 100 : 120,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: Colors.black.withOpacity(0.5),
                ),
                child: Center(
                  child: CircularProgressIndicator(
                    color: Colors.white,
                    strokeWidth: isSmallScreen ? 2 : 3,
                  ),
                ),
              ),
          ],
        ),

        SizedBox(height: isSmallScreen ? 12 : 16),

        // Update Button
        TextButton(
          onPressed: isUploading ? null : () => controller.pickAndUploadImage(context),
          style: TextButton.styleFrom(
            padding: EdgeInsets.symmetric(
              horizontal: isSmallScreen ? 16 : 20,
              vertical: isSmallScreen ? 8 : 10,
            ),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (!isUploading) ...[
                Icon(
                  Icons.edit_rounded,
                  color: Colors.blue,
                  size: isSmallScreen ? 16 : 18,
                ),
                SizedBox(width: isSmallScreen ? 6 : 8),
              ],
              _buildButtonChild(theme, l10n, isSmallScreen),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildImageChild(ThemeData theme, bool isSmallScreen) {
    if (controller.imageFile == null &&
        (userInfo?.photoUrl == null || userInfo!.photoUrl!.isEmpty)) {
      return Container(
        color: theme.brightness == Brightness.dark
            ? Colors.grey[800]
            : Colors.grey[100],
        child: Icon(
          Icons.person_rounded,
          size: isSmallScreen ? 50 : 60,
          color: theme.brightness == Brightness.dark
              ? Colors.grey[600]
              : Colors.grey[400],
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
          color: Colors.blue,
          strokeWidth: isSmallScreen ? 1.5 : 2,
        ),
      );
    }

    return Text(
      l10n.get('updateImage'),
      style: TextStyle(
        color: Colors.blue,
        fontSize: isSmallScreen ? 13 : 15,
        fontWeight: FontWeight.w500,
      ),
    );
  }
}