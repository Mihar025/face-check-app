import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

import '../../../../../api_client/model/user_full_contact_information.dart';
import '../../../../../services/ApiService.dart';

class ProfileState {
  final bool isLoading;
  final bool isUploading;
  final String? error;
  final UserFullContactInformation? userInfo;

  ProfileState({
    this.isLoading = false,
    this.isUploading = false,
    this.error,
    this.userInfo,
  });

  ProfileState copyWith({
    bool? isLoading,
    bool? isUploading,
    String? error,
    UserFullContactInformation? userInfo,
  }) {
    return ProfileState(
      isLoading: isLoading ?? this.isLoading,
      isUploading: isUploading ?? this.isUploading,
      // если явно передали null — очищаем ошибку
      error: error,
      userInfo: userInfo ?? this.userInfo,
    );
  }
}

class ProfileController {
  final _state = ValueNotifier(ProfileState());
  final ImagePicker _picker = ImagePicker();
  File? imageFile;

  ValueNotifier<ProfileState> get state => _state;

  Future<void> loadUserInfo() async {
    try {
      _state.value = _state.value.copyWith(isLoading: true, error: null);

      final response = await ApiService.instance.userApi
          .findWorkerFullContactInformation();

      _state.value = _state.value.copyWith(
        userInfo: response.data,
        isLoading: false,
      );
    } catch (e) {
      _state.value = _state.value.copyWith(
        error: 'Failed to load user information',
        isLoading: false,
      );
      print('Error loading user info: $e');
    }
  }

  // ---------- CAMERA PERMISSION ----------
  Future<bool> _ensureCameraPermission(BuildContext context) async {
    var status = await Permission.camera.status;
    print('📸 Initial camera status: $status');

    if (status.isGranted) {
      return true;
    }

    // Если уже permanentlyDenied, не пытаемся запросить
    if (status.isPermanentlyDenied) {
      print('📸 Camera is permanently denied');
      // Показываем диалог с предложением открыть настройки
      if (context.mounted) {
        await showDialog(
          context: context,
          builder: (dialogContext) => AlertDialog(
            title: const Text('Camera access'),
            content: const Text(
              'Camera access was previously denied. '
                  'Please go to Settings > FaceCheck > Camera and enable access.',
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.of(dialogContext).pop(),
                child: const Text('Cancel'),
              ),
              TextButton(
                onPressed: () async {
                  Navigator.of(dialogContext).pop();
                  await openAppSettings(); // Открыть настройки
                },
                child: const Text('Open Settings'),
              ),
            ],
          ),
        );
      }
      return false;
    }

    // Запрашиваем только если статус isDenied или isRestricted
    print('📸 Requesting camera permission...');
    final newStatus = await Permission.camera.request();
    print('📸 New camera status after request: $newStatus');

    if (newStatus.isGranted) {
      return true;
    }

    // Показываем мягкий диалог
    if (context.mounted) {
      await showDialog(
        context: context,
        builder: (dialogContext) => AlertDialog(
          title: const Text('Camera access'),
          content: const Text(
            'To take photos, please allow camera access. '
                'You can continue using the app without this feature.',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('OK'),
            ),
          ],
        ),
      );
    }

    return false;
  }

  // ---------- PICK + UPLOAD ----------
  Future<void> pickAndUploadImage(BuildContext context) async {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    // 1. Сначала — permission камеры
    final allowed = await _ensureCameraPermission(context);
    if (!allowed) {
      // пользователь отказал → просто выходим, БЕЗ ошибок
      return;
    }

    try {
      // 2. Открываем камеру
      final XFile? image = await _picker.pickImage(
        source: ImageSource.camera, // только камера
        maxWidth: 800,
        maxHeight: 800,
        imageQuality: 80,
      );

      // user мог нажать Cancel → это НЕ ошибка
      if (image == null) {
        return;
      }

      imageFile = File(image.path);

      _state.value = _state.value.copyWith(
        isUploading: true,
        error: null,
      );

      try {
        // 3. Загружаем файл
        final response = await ApiService.instance.fileApi.uploadPhoto(
          photo: imageFile!,
          email: _state.value.userInfo?.email ?? '',
          prefix: 'profile',
          onSendProgress: (int sent, int total) {
            final progress = (sent / total * 100).toStringAsFixed(2);
            print('Upload progress: $progress%');
          },
        );

        if (response.statusCode == 200) {
          final String photoUrl = response.data ?? '';

          // обновляем userInfo локально
          if (_state.value.userInfo != null) {
            final updatedUserInfo = _state.value.userInfo!.rebuild(
                  (b) => b..photoUrl = photoUrl,
            );
            _state.value = _state.value.copyWith(userInfo: updatedUserInfo);
          }

          // или перезагружаем из API (как у тебя)
          await loadUserInfo();

          if (context.mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(
                  'Profile photo updated successfully',
                  style: TextStyle(fontSize: isSmallScreen ? 12 : 14),
                ),
                backgroundColor: Colors.green,
                behavior: SnackBarBehavior.floating,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10),
                ),
                margin: EdgeInsets.all(isSmallScreen ? 8 : 12),
              ),
            );
          }
        } else {
          // реальный фейл ответа сервера
          _state.value = _state.value.copyWith(
            error: 'Failed to upload profile photo',
          );

          if (context.mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(
                  'Failed to upload profile photo. Please try again.',
                  style: TextStyle(fontSize: isSmallScreen ? 12 : 14),
                ),
                backgroundColor: Colors.red,
                behavior: SnackBarBehavior.floating,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10),
                ),
                margin: EdgeInsets.all(isSmallScreen ? 8 : 12),
              ),
            );
          }
        }
      } catch (e) {
        // ошибка ЗАГРУЗКИ, а не permission
        print('Error uploading file: $e');
        _state.value = _state.value.copyWith(
          error: 'Failed to upload profile photo',
        );

        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(
                'Failed to upload profile photo. Please try again.',
                style: TextStyle(fontSize: isSmallScreen ? 12 : 14),
              ),
              backgroundColor: Colors.red,
              behavior: SnackBarBehavior.floating,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
              margin: EdgeInsets.all(isSmallScreen ? 8 : 12),
            ),
          );
        }
      } finally {
        _state.value = _state.value.copyWith(isUploading: false);
      }
    } catch (e) {
      // это уже какая-то странная ошибка работы плагина, но НЕ permission-denied,
      // потому что разрешение мы запросили заранее
      print('Error picking image: $e');
      _state.value = _state.value.copyWith(
        isUploading: false,
        // можно вообще не трогать error здесь, чтобы не засорять UI
      );

      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              'Could not open camera. Please try again.',
              style: TextStyle(fontSize: isSmallScreen ? 12 : 14),
            ),
            backgroundColor: Colors.red,
            behavior: SnackBarBehavior.floating,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
            margin: EdgeInsets.all(isSmallScreen ? 8 : 12),
          ),
        );
      }
    }
  }
}
