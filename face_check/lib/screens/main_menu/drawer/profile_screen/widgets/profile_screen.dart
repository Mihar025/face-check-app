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

    // Определяем размер экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: theme.scaffoldBackgroundColor,
        title: Text(
          l10n.get('profile'),
          style: TextStyle(
            color: theme.textTheme.titleLarge?.color,
            fontSize: isSmallScreen ? 18 : 20,
          ),
        ),
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: theme.iconTheme.color,
            size: isSmallScreen ? 20 : 24,
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: Padding(
        padding: EdgeInsets.all(isSmallScreen ? 16 : 20),
        child: ValueListenableBuilder(
          valueListenable: _controller.state,
          builder: (context, ProfileState state, _) {
            if (state.isLoading) {
              return Center(
                child: CircularProgressIndicator(
                  strokeWidth: isSmallScreen ? 3 : 4,
                ),
              );
            }

            if (state.error != null) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      state.error!,
                      style: TextStyle(
                        color: Colors.red,
                        fontSize: isSmallScreen ? 14 : 16,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    SizedBox(height: isSmallScreen ? 12 : 16),
                    ElevatedButton(
                      onPressed: _controller.loadUserInfo,
                      style: ElevatedButton.styleFrom(
                        padding: EdgeInsets.symmetric(
                          horizontal: isSmallScreen ? 16 : 20,
                          vertical: isSmallScreen ? 8 : 10,
                        ),
                      ),
                      child: Text(
                        l10n.get('retry'),
                        style: TextStyle(
                          fontSize: isSmallScreen ? 12 : 14,
                        ),
                      ),
                    ),
                  ],
                ),
              );
            }

            // Определяем, нужно ли использовать колонку вместо строки на очень маленьких экранах
            final bool useColumn = screenSize.width < 480;

            return SingleChildScrollView(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    l10n.get('profile'),
                    style: TextStyle(
                      color: theme.textTheme.titleLarge?.color,
                      fontSize: isSmallScreen ? 24 : 28,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  SizedBox(height: isSmallScreen ? 16 : 20),
                  if (useColumn)
                  // Вертикальное расположение для маленьких экранов
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        ProfileImage(
                          controller: _controller,
                          userInfo: state.userInfo,
                          isUploading: state.isUploading,
                        ),
                        SizedBox(height: isSmallScreen ? 16 : 20),
                        ProfileInfo(userInfo: state.userInfo),
                      ],
                    )
                  else
                  // Горизонтальное расположение для больших экранов
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        ProfileImage(
                          controller: _controller,
                          userInfo: state.userInfo,
                          isUploading: state.isUploading,
                        ),
                        SizedBox(width: isSmallScreen ? 16 : 20),
                        Expanded(
                          child: ProfileInfo(userInfo: state.userInfo),
                        ),
                      ],
                    ),
                ],
              ),
            );
          },
        ),
      ),
    );
  }
}