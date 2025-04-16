import 'package:flutter/material.dart';
import 'dart:io';
import 'package:image_picker/image_picker.dart';
import '../../../../../api_client/model/user_full_contact_information.dart';
import '../../../../../services/ApiService.dart';
import 'image_picker_bottom_sheet.dart';

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

  Future<void> pickAndUploadImage(BuildContext context) async {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    try {
      final ImageSource? source = await showModalBottomSheet<ImageSource>(
        context: context,
        backgroundColor: Colors.transparent,
        isScrollControlled: true,
        builder: (BuildContext context) => const ImagePickerBottomSheet(),
      );

      if (source == null) return;

      final XFile? image = await _picker.pickImage(
        source: source,
        maxWidth: 800,
        maxHeight: 800,
        imageQuality: 80,
      );

      if (image != null) {
        imageFile = File(image.path);
        _state.value = _state.value.copyWith(
          isUploading: true,
          error: null,
        );

        try {
          final response = await ApiService.instance.fileApi.uploadPhoto(
            photo: imageFile!,
            email: _state.value.userInfo?.email ?? '',
            prefix: 'profile',
            onSendProgress: (int sent, int total) {
              print('Upload progress: ${(sent / total * 100).toStringAsFixed(2)}%');
            },
          );

          if (response.statusCode == 200) {
            final String photoUrl = response.data ?? '';
            if (_state.value.userInfo != null) {
              final updatedUserInfo = _state.value.userInfo!.rebuild((b) => b
                ..photoUrl = photoUrl);
              _state.value = _state.value.copyWith(userInfo: updatedUserInfo);
            }
            await loadUserInfo();

            if (context.mounted) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(
                    'Profile photo updated successfully',
                    style: TextStyle(
                      fontSize: isSmallScreen ? 12 : 14,
                    ),
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
          }
        } catch (e) {
          print('Error uploading file: $e');
          _state.value = _state.value.copyWith(
            error: 'Failed to upload profile photo',
          );

          if (context.mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(
                  'Failed to upload profile photo: $e',
                  style: TextStyle(
                    fontSize: isSmallScreen ? 12 : 14,
                  ),
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

        _state.value = _state.value.copyWith(isUploading: false);
      }
    } catch (e) {
      _state.value = _state.value.copyWith(
        isUploading: false,
        error: 'Error picking image',
      );
      print('Error picking image: $e');

      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              'Error: $e',
              style: TextStyle(
                fontSize: isSmallScreen ? 12 : 14,
              ),
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