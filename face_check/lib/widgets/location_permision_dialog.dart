import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';
import 'dart:io';

/// Показывает красивый pre-permission диалог ОДИН раз
/// Объясняет зачем нужна локация ДО системного запроса
/// Apple требует это для App Store / Unlisted ревью
class LocationPermissionHelper {
  static const String _permissionShownKey = 'location_permission_dialog_shown';

  /// Проверить и показать диалог если нужно
  /// Возвращает true если permissions получены
  static Future<bool> checkAndRequestIfNeeded(BuildContext context) async {
    // 1. Проверяем — может permissions уже есть
    final currentPermission = await Geolocator.checkPermission();
    if (currentPermission == LocationPermission.always) {
      return true; // Уже есть "Always" — ничего показывать не нужно
    }

    // 2. Проверяем — показывали ли мы уже диалог
    final prefs = await SharedPreferences.getInstance();
    final alreadyShown = prefs.getBool(_permissionShownKey) ?? false;
    if (alreadyShown) {
      return currentPermission == LocationPermission.whileInUse ||
          currentPermission == LocationPermission.always;
    }

    // 3. Показываем наш кастомный диалог
    if (!context.mounted) return false;

    final userAccepted = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => const _LocationPermissionDialog(),
    );

    // 4. Сохраняем что показали
    await prefs.setBool(_permissionShownKey, true);

    if (userAccepted != true) return false;

    // 5. Запрашиваем системные permissions
    return await _requestSystemPermissions();
  }

  static Future<bool> _requestSystemPermissions() async {
    // Шаг 1: "When In Use"
    LocationPermission permission = await Geolocator.checkPermission();

    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) return false;
    }

    if (permission == LocationPermission.deniedForever) {
      await openAppSettings();
      return false;
    }

    // Шаг 2: "Always" (нужно для фонового трекинга)
    if (Platform.isAndroid || Platform.isIOS) {
      final alwaysStatus = await Permission.locationAlways.status;
      if (!alwaysStatus.isGranted) {
        final result = await Permission.locationAlways.request();
        // Даже если "Always" не дали — "When In Use" достаточно для начала
        return result.isGranted ||
            permission == LocationPermission.whileInUse ||
            permission == LocationPermission.always;
      }
    }

    return true;
  }
}

class _LocationPermissionDialog extends StatelessWidget {
  const _LocationPermissionDialog();

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Dialog(
      backgroundColor: isDark ? const Color(0xFF1E1E1E) : Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      insetPadding: const EdgeInsets.symmetric(horizontal: 24, vertical: 40),
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Icon
            Container(
              width: 72,
              height: 72,
              decoration: BoxDecoration(
                color: Colors.blue.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: const Icon(
                Icons.location_on_rounded,
                color: Colors.blue,
                size: 36,
              ),
            ),

            const SizedBox(height: 20),

            // Title
            Text(
              'Location Access Required',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: isDark ? Colors.white : Colors.black87,
              ),
            ),

            const SizedBox(height: 16),

            // Description
            Text(
              'FaceCheck uses your location to verify attendance at work sites during your shift.',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 15,
                height: 1.5,
                color: isDark ? Colors.grey[300] : Colors.grey[700],
              ),
            ),

            const SizedBox(height: 20),

            // Info cards
            _InfoCard(
              icon: Icons.access_time_rounded,
              text: 'Location is tracked only between Punch In and Punch Out',
              isDark: isDark,
            ),

            const SizedBox(height: 10),

            _InfoCard(
              icon: Icons.lock_rounded,
              text: 'Your location data is securely sent to your employer',
              isDark: isDark,
            ),

            const SizedBox(height: 10),

            _InfoCard(
              icon: Icons.battery_saver,
              text: 'Minimal battery usage with optimized tracking',
              isDark: isDark,
            ),

            const SizedBox(height: 24),

            // Important note about "Always Allow"
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.orange.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: Colors.orange.withOpacity(0.3),
                ),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Icon(
                    Icons.info_outline_rounded,
                    color: Colors.orange,
                    size: 20,
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      'Please select "Always Allow" on the next screen so tracking works when the app is in the background.',
                      style: TextStyle(
                        fontSize: 13,
                        height: 1.4,
                        color: isDark ? Colors.orange[200] : Colors.orange[800],
                      ),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // Buttons
            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                onPressed: () => Navigator.of(context).pop(true),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(14),
                  ),
                  elevation: 0,
                ),
                child: const Text(
                  'Allow Location Access',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),

            const SizedBox(height: 10),

            TextButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: Text(
                'Not Now',
                style: TextStyle(
                  fontSize: 14,
                  color: isDark ? Colors.grey[400] : Colors.grey[600],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _InfoCard extends StatelessWidget {
  final IconData icon;
  final String text;
  final bool isDark;

  const _InfoCard({
    required this.icon,
    required this.text,
    required this.isDark,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
      decoration: BoxDecoration(
        color: isDark ? Colors.white.withOpacity(0.05) : Colors.grey[50],
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Icon(
            icon,
            size: 20,
            color: Colors.blue.withOpacity(0.7),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Text(
              text,
              style: TextStyle(
                fontSize: 13,
                height: 1.3,
                color: isDark ? Colors.grey[300] : Colors.grey[700],
              ),
            ),
          ),
        ],
      ),
    );
  }
}