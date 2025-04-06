import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

class ImagePickerBottomSheet extends StatelessWidget {
  const ImagePickerBottomSheet({super.key});

  Widget _buildSourceButton(
      BuildContext context,
      IconData icon,
      String title,
      String subtitle,
      ImageSource source,
      Color color,
      ) {
    final theme = Theme.of(context);

    // Определяем размер экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return InkWell(
      onTap: () => Navigator.pop(context, source),
      borderRadius: BorderRadius.circular(16),
      child: Container(
        width: isSmallScreen ? 130 : 150,
        padding: EdgeInsets.all(isSmallScreen ? 12 : 16),
        decoration: BoxDecoration(
          color: theme.brightness == Brightness.dark
              ? Colors.grey[850]
              : Colors.grey[200],
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: theme.brightness == Brightness.dark
                ? Colors.grey[800]!
                : Colors.grey[300]!,
            width: 1,
          ),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: EdgeInsets.all(isSmallScreen ? 10 : 12),
              decoration: BoxDecoration(
                color: color.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: Icon(
                icon,
                size: isSmallScreen ? 28 : 32,
                color: color,
              ),
            ),
            SizedBox(height: isSmallScreen ? 8 : 12),
            Text(
              title,
              style: TextStyle(
                color: theme.textTheme.bodyLarge?.color,
                fontSize: isSmallScreen ? 14 : 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            SizedBox(height: isSmallScreen ? 2 : 4),
            Text(
              subtitle,
              style: TextStyle(
                color: theme.textTheme.bodyMedium?.color?.withOpacity(0.7),
                fontSize: isSmallScreen ? 10 : 12,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    // Определяем размер экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Container(
      height: isSmallScreen ? 280 : 320,
      decoration: BoxDecoration(
        color: theme.brightness == Brightness.dark
            ? const Color(0xFF1E1E1E)
            : Colors.white,
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(24),
          topRight: Radius.circular(24),
        ),
      ),
      child: Column(
        children: [
          Container(
            margin: EdgeInsets.only(top: isSmallScreen ? 10 : 12),
            height: 4,
            width: 40,
            decoration: BoxDecoration(
              color: theme.brightness == Brightness.dark
                  ? Colors.grey[600]
                  : Colors.grey[400],
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          Padding(
            padding: EdgeInsets.only(
                top: isSmallScreen ? 20 : 24,
                bottom: isSmallScreen ? 16 : 20
            ),
            child: Text(
              'Choose image source',
              style: TextStyle(
                color: theme.textTheme.titleLarge?.color,
                fontSize: isSmallScreen ? 18 : 20,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              _buildSourceButton(
                context,
                Icons.camera_alt_rounded,
                'Camera',
                'Take a new photo',
                ImageSource.camera,
                const Color(0xFF2196F3),
              ),
              _buildSourceButton(
                context,
                Icons.photo_library_rounded,
                'Gallery',
                'Choose existing',
                ImageSource.gallery,
                const Color(0xFF4CAF50),
              ),
            ],
          ),
          Padding(
            padding: EdgeInsets.all(isSmallScreen ? 16 : 20),
            child: TextButton(
              onPressed: () => Navigator.pop(context),
              style: TextButton.styleFrom(
                padding: EdgeInsets.symmetric(
                    vertical: isSmallScreen ? 12 : 16
                ),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: Text(
                'Cancel',
                style: TextStyle(
                  color: theme.brightness == Brightness.dark
                      ? Colors.grey
                      : Colors.grey[700],
                  fontSize: isSmallScreen ? 14 : 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}