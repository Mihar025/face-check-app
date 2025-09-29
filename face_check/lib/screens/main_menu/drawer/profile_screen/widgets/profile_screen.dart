import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../../../providers/localization_provider.dart';
import '../profile_controller/profile_controller.dart';
import '../widgets/profile_image.dart';
import '../widgets/profile_info.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final ProfileController _controller = ProfileController();

  @override
  void initState() {
    super.initState();
    _controller.loadUserInfo();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: theme.scaffoldBackgroundColor,
        elevation: 0,
        title: Text(
          l10n.get('profile'),
          style: TextStyle(
            color: theme.textTheme.titleLarge?.color,
            fontSize: isSmallScreen ? 20 : 22,
            fontWeight: FontWeight.w600,
          ),
        ),
        leading: IconButton(
          icon: Container(
            padding: EdgeInsets.all(isSmallScreen ? 6 : 8),
            decoration: BoxDecoration(
              color: theme.brightness == Brightness.dark
                  ? Colors.white.withOpacity(0.05)
                  : Colors.black.withOpacity(0.05),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(
              Icons.arrow_back_ios_new_rounded,
              color: theme.iconTheme.color,
              size: isSmallScreen ? 18 : 20,
            ),
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: ValueListenableBuilder(
        valueListenable: _controller.state,
        builder: (context, ProfileState state, _) {
          if (state.isLoading) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  CircularProgressIndicator(
                    strokeWidth: isSmallScreen ? 3 : 4,
                    color: Colors.blue,
                  ),
                  SizedBox(height: isSmallScreen ? 16 : 20),
                  Text(
                    'Loading profile...',
                    style: TextStyle(
                      color: theme.textTheme.bodyMedium?.color?.withOpacity(0.7),
                      fontSize: isSmallScreen ? 14 : 16,
                    ),
                  ),
                ],
              ),
            );
          }

          if (state.error != null) {
            return Center(
              child: Container(
                margin: EdgeInsets.all(isSmallScreen ? 20 : 24),
                padding: EdgeInsets.all(isSmallScreen ? 20 : 24),
                decoration: BoxDecoration(
                  color: theme.brightness == Brightness.dark
                      ? Colors.red.withOpacity(0.1)
                      : Colors.red.withOpacity(0.05),
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(
                    color: Colors.red.withOpacity(0.2),
                    width: 1,
                  ),
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      Icons.error_outline_rounded,
                      color: Colors.red,
                      size: isSmallScreen ? 48 : 56,
                    ),
                    SizedBox(height: isSmallScreen ? 12 : 16),
                    Text(
                      state.error!,
                      style: TextStyle(
                        color: theme.textTheme.bodyLarge?.color,
                        fontSize: isSmallScreen ? 14 : 16,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    SizedBox(height: isSmallScreen ? 16 : 20),
                    ElevatedButton(
                      onPressed: _controller.loadUserInfo,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.blue,
                        foregroundColor: Colors.white,
                        padding: EdgeInsets.symmetric(
                          horizontal: isSmallScreen ? 24 : 32,
                          vertical: isSmallScreen ? 12 : 14,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: Text(
                        l10n.get('retry'),
                        style: TextStyle(
                          fontSize: isSmallScreen ? 14 : 16,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            );
          }

          final bool useColumn = screenSize.width < 600;

          return SingleChildScrollView(
            physics: const BouncingScrollPhysics(),
            child: Padding(
              padding: EdgeInsets.all(isSmallScreen ? 16 : 20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Profile Header Card
                  Container(
                    width: double.infinity,
                    padding: EdgeInsets.all(isSmallScreen ? 20 : 24),
                    decoration: BoxDecoration(
                      color: theme.brightness == Brightness.dark
                          ? Colors.white.withOpacity(0.05)
                          : Colors.white,
                      borderRadius: BorderRadius.circular(20),
                      boxShadow: theme.brightness == Brightness.light
                          ? [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.05),
                          blurRadius: 10,
                          offset: const Offset(0, 4),
                        ),
                      ]
                          : null,
                      border: theme.brightness == Brightness.dark
                          ? Border.all(
                        color: Colors.white.withOpacity(0.1),
                        width: 1,
                      )
                          : null,
                    ),
                    child: useColumn
                        ? Column(
                      children: [
                        ProfileImage(
                          controller: _controller,
                          userInfo: state.userInfo,
                          isUploading: state.isUploading,
                        ),
                        SizedBox(height: isSmallScreen ? 24 : 28),
                        if (state.userInfo != null)
                          Column(
                            children: [
                              Text(
                                state.userInfo!.fullName ?? 'User Name',
                                style: TextStyle(
                                  color: theme.textTheme.titleLarge?.color,
                                  fontSize: isSmallScreen ? 22 : 26,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              SizedBox(height: isSmallScreen ? 4 : 6),
                              Text(
                                state.userInfo!.email ?? '',
                                style: TextStyle(
                                  color: theme.textTheme.bodyMedium?.color?.withOpacity(0.7),
                                  fontSize: isSmallScreen ? 14 : 16,
                                ),
                              ),
                            ],
                          ),
                      ],
                    )
                        : Row(
                      children: [
                        ProfileImage(
                          controller: _controller,
                          userInfo: state.userInfo,
                          isUploading: state.isUploading,
                        ),
                        SizedBox(width: isSmallScreen ? 24 : 32),
                        if (state.userInfo != null)
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  state.userInfo!.fullName ?? 'User Name',
                                  style: TextStyle(
                                    color: theme.textTheme.titleLarge?.color,
                                    fontSize: isSmallScreen ? 22 : 26,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                SizedBox(height: isSmallScreen ? 4 : 6),
                                Text(
                                  state.userInfo!.email ?? '',
                                  style: TextStyle(
                                    color: theme.textTheme.bodyMedium?.color?.withOpacity(0.7),
                                    fontSize: isSmallScreen ? 14 : 16,
                                  ),
                                ),
                              ],
                            ),
                          ),
                      ],
                    ),
                  ),

                  SizedBox(height: isSmallScreen ? 20 : 24),

                  // Information Section
                  Text(
                    l10n.get('contactInformation'),
                    style: TextStyle(
                      color: theme.textTheme.titleMedium?.color,
                      fontSize: isSmallScreen ? 18 : 20,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  SizedBox(height: isSmallScreen ? 12 : 16),

                  ProfileInfo(userInfo: state.userInfo),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}