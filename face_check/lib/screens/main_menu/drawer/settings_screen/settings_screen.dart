import 'dart:math';

import 'package:face_check/providers/localization_provider.dart';
import 'package:face_check/services/ApiService.dart';
import 'package:face_check/screens/main_menu/drawer/profile_screen/profile_controller/profile_controller.dart';
import 'package:face_check/screens/main_menu/drawer/settings_screen/settings_controller.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../theme/theme_provider.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool is24HourFormat = true;
  bool isDarkTheme = true;
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  final ProfileController _profileController = ProfileController();
  final SettingsController _settingsController = SettingsController(ApiService.instance.authenticationApi);
  String _selectedLanguage = 'en';

  @override
  void initState() {
    super.initState();
    _loadSettings();
    _profileController.loadUserInfo();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      final themeProvider = Provider.of<ThemeProvider>(context, listen: false);
      isDarkTheme = themeProvider.isDarkTheme;

      final localizationProvider = Provider.of<LocalizationProvider>(context, listen: false);
      _selectedLanguage = localizationProvider.currentLanguage;
    });
  }

  Future<void> _loadSettings() async {
    final SharedPreferences prefs = await _prefs;
    setState(() {
      is24HourFormat = prefs.getBool('is24HourFormat') ?? true;
    });
  }

  Future<void> _saveTimeFormat(bool value) async {
    final SharedPreferences prefs = await _prefs;
    setState(() {
      is24HourFormat = value;
      prefs.setBool('is24HourFormat', value);
    });
  }

  Future<void> _saveTheme(bool value) async {
    final themeProvider = Provider.of<ThemeProvider>(context, listen: false);
    setState(() {
      isDarkTheme = value;
    });
    themeProvider.toggleTheme(value);
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
        elevation: 0,
        backgroundColor: theme.scaffoldBackgroundColor,
        title: Text(
          l10n.get('settings'),
          style: TextStyle(
            color: theme.textTheme.titleLarge?.color,
            fontSize: isSmallScreen ? 20 : 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back_ios_new,
            color: theme.iconTheme.color,
            size: isSmallScreen ? 20 : 22,
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: ValueListenableBuilder(
        valueListenable: _profileController.state,
        builder: (context, ProfileState state, _) {
          if (state.isLoading) {
            return Center(
              child: CircularProgressIndicator(
                color: theme.primaryColor,
                strokeWidth: isSmallScreen ? 3 : 4,
              ),
            );
          }

          return ListView(
            children: [
              SizedBox(height: isSmallScreen ? 16 : 20),
              _buildSectionHeader(l10n.get('profile'), theme, isSmallScreen),
              SizedBox(height: isSmallScreen ? 12 : 16),
              _buildProfileSection(state, theme, l10n, isSmallScreen),
              SizedBox(height: isSmallScreen ? 24 : 32),
              _buildSectionHeader(l10n.get('preferences'), theme, isSmallScreen),
              SizedBox(height: isSmallScreen ? 12 : 16),
              _buildPreferencesSection(theme, l10n, isSmallScreen),
            ],
          );
        },
      ),
    );
  }

  Widget _buildSectionHeader(String title, ThemeData theme, bool isSmallScreen) {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: isSmallScreen ? 20 : 24),
      child: Text(
        title,
        style: TextStyle(
          color: theme.textTheme.titleLarge?.color,
          fontSize: isSmallScreen ? 18 : 20,
          fontWeight: FontWeight.w700,
          letterSpacing: 0.5,
        ),
      ),
    );
  }

  Widget _buildProfileSection(ProfileState state, ThemeData theme, dynamic l10n, bool isSmallScreen) {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: isSmallScreen ? 20 : 24),
      decoration: BoxDecoration(
        color: theme.cardColor,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: theme.shadowColor.withOpacity(0.1),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        children: [
          _buildSettingTile(
            icon: Icons.phone_outlined,
            title: l10n.get('phoneNumber'),
            subtitle: state.userInfo?.phoneNumber ?? 'Not set',
            onTap: () => _showEditDialog(
              l10n.get('editPhoneNumber'),
              state.userInfo?.phoneNumber ?? '',
              'phone number',
            ),
            theme: theme,
            isSmallScreen: isSmallScreen,
          ),
          _buildDivider(theme),
          _buildSettingTile(
            icon: Icons.email_outlined,
            title: l10n.get('email'),
            subtitle: state.userInfo?.email ?? 'Not set',
            onTap: () => _showEditDialog(
              l10n.get('editEmail'),
              state.userInfo?.email ?? '',
              'email',
            ),
            theme: theme,
            isSmallScreen: isSmallScreen,
          ),
          _buildDivider(theme),
          _buildSettingTile(
            icon: Icons.lock_outline,
            title: l10n.get('password'),
            subtitle: '••••••••',
            onTap: () => _showPasswordDialog(),
            theme: theme,
            isSmallScreen: isSmallScreen,
          ),
        ],
      ),
    );
  }

  Widget _buildPreferencesSection(ThemeData theme, dynamic l10n, bool isSmallScreen) {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: isSmallScreen ? 20 : 24),
      decoration: BoxDecoration(
        color: theme.cardColor,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: theme.shadowColor.withOpacity(0.1),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        children: [
          _buildSettingTile(
            icon: Icons.access_time,
            title: l10n.get('timeFormat'),
            subtitle: is24HourFormat ? '24-hour' : '12-hour',
            onTap: () {
              setState(() {
                is24HourFormat = !is24HourFormat;
                _saveTimeFormat(is24HourFormat);
              });
            },
            trailing: Switch.adaptive(
              value: is24HourFormat,
              onChanged: _saveTimeFormat,
              activeColor: theme.primaryColor,
            ),
            theme: theme,
            isSmallScreen: isSmallScreen,
          ),
          _buildDivider(theme),
          _buildSettingTile(
            icon: isDarkTheme ? Icons.dark_mode : Icons.light_mode,
            title: l10n.get('theme'),
            subtitle: isDarkTheme ? l10n.get('dark') : l10n.get('white'),
            onTap: () {
              setState(() {
                isDarkTheme = !isDarkTheme;
                _saveTheme(isDarkTheme);
              });
            },
            trailing: Switch.adaptive(
              value: isDarkTheme,
              onChanged: _saveTheme,
              activeColor: theme.primaryColor,
            ),
            theme: theme,
            isSmallScreen: isSmallScreen,
          ),
          _buildDivider(theme),
          Consumer<LocalizationProvider>(
            builder: (context, localizationProvider, _) {
              return _buildSettingTile(
                icon: Icons.language,
                title: localizationProvider.localizations.get('language'),
                subtitle: _getLanguageName(_selectedLanguage, localizationProvider),
                onTap: () {}, // Empty onTap since we're handling language change in dropdown
                trailing: _buildLanguageDropdown(theme, localizationProvider, isSmallScreen),
                theme: theme,
                isSmallScreen: isSmallScreen,
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildSettingTile({
    required IconData icon,
    required String title,
    required String subtitle,
    required ThemeData theme,
    required VoidCallback onTap,
    required bool isSmallScreen,
    Widget? trailing,
  }) {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: EdgeInsets.symmetric(
              horizontal: isSmallScreen ? 16 : 20,
              vertical: isSmallScreen ? 12 : 16
          ),
          child: Row(
            children: [
              Container(
                padding: EdgeInsets.all(isSmallScreen ? 8 : 10),
                decoration: BoxDecoration(
                  color: theme.primaryColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  icon,
                  color: theme.primaryColor,
                  size: isSmallScreen ? 20 : 22,
                ),
              ),
              SizedBox(width: isSmallScreen ? 12 : 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: TextStyle(
                        color: theme.textTheme.titleMedium?.color,
                        fontSize: isSmallScreen ? 14 : 16,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    SizedBox(height: isSmallScreen ? 2 : 4),
                    Text(
                      subtitle,
                      style: TextStyle(
                        color: theme.textTheme.bodyMedium?.color?.withOpacity(0.7),
                        fontSize: isSmallScreen ? 12 : 14,
                      ),
                    ),
                  ],
                ),
              ),
              if (trailing != null) trailing,
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDivider(ThemeData theme) {
    return Divider(
      color: theme.dividerColor.withOpacity(0.1),
      height: 1,
      thickness: 1,
      indent: 20,
      endIndent: 20,
    );
  }

  Widget _buildLanguageDropdown(ThemeData theme, LocalizationProvider provider, bool isSmallScreen) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: isSmallScreen ? 8 : 12),
      decoration: BoxDecoration(
        color: theme.primaryColor.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
      ),
      child: DropdownButton<String>(
        value: _selectedLanguage,
        icon: Icon(
          Icons.arrow_drop_down,
          color: theme.primaryColor,
          size: isSmallScreen ? 20 : 24,
        ),
        underline: const SizedBox(),
        items: [
          _buildDropdownItem('en', provider.localizations.get('english'), theme, isSmallScreen),
          _buildDropdownItem('es', provider.localizations.get('spanish'), theme, isSmallScreen),
          _buildDropdownItem('ru', provider.localizations.get('russian'), theme, isSmallScreen),
        ],
        onChanged: (String? newValue) {
          if (newValue != null) {
            setState(() {
              _selectedLanguage = newValue;
            });
            provider.changeLanguage(newValue);
          }
        },
      ),
    );
  }

  DropdownMenuItem<String> _buildDropdownItem(
      String value,
      String text,
      ThemeData theme,
      bool isSmallScreen,
      ) {
    return DropdownMenuItem<String>(
      value: value,
      child: Text(
        text,
        style: TextStyle(
          color: theme.textTheme.bodyLarge?.color,
          fontSize: isSmallScreen ? 12 : 14,
        ),
      ),
    );
  }

  String _getLanguageName(String code, LocalizationProvider provider) {
    switch (code) {
      case 'en':
        return provider.localizations.get('english');
      case 'es':
        return provider.localizations.get('spanish');
      case 'ru':
        return provider.localizations.get('russian');
      default:
        return provider.localizations.get('english');
    }
  }

  void _showEditDialog(String title, String currentValue, String fieldType) {
    final TextEditingController currentController = TextEditingController(text: currentValue);
    final TextEditingController newController = TextEditingController();
    final TextEditingController confirmController = TextEditingController();
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    // Определяем размер экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          backgroundColor: theme.cardColor,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(20),
          ),
          title: Text(
            title,
            style: TextStyle(
              color: theme.textTheme.titleLarge?.color,
              fontSize: isSmallScreen ? 18 : 20,
              fontWeight: FontWeight.w600,
            ),
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              _buildTextField(
                controller: currentController,
                label: fieldType == 'phone number'
                    ? l10n.get('currentPhoneNumber')
                    : l10n.get('currentEmail'),
                hint: fieldType == 'phone number'
                    ? l10n.get('currentPhoneNumberHint')
                    : l10n.get('currentEmailHint'),
                theme: theme,
                isSmallScreen: isSmallScreen,
              ),
              SizedBox(height: isSmallScreen ? 12 : 16),
              _buildTextField(
                controller: newController,
                label: fieldType == 'phone number'
                    ? l10n.get('newPhoneNumber')
                    : l10n.get('new'),
                hint: fieldType == 'phone number'
                    ? l10n.get('newPhoneNumberHint')
                    : l10n.get('new'),
                theme: theme,
                isSmallScreen: isSmallScreen,
              ),
              SizedBox(height: isSmallScreen ? 12 : 16),
              _buildTextField(
                controller: confirmController,
                label: fieldType == 'phone number'
                    ? l10n.get('confirmNewPhoneNumber')
                    : l10n.get('сonfirmNew'),
                hint: fieldType == 'phone number'
                    ? l10n.get('confirmNewPhoneNumberHint')
                    : l10n.get('сonfirmNew'),
                theme: theme,
                isSmallScreen: isSmallScreen,
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: Text(
                l10n.get('cancel'),
                style: TextStyle(
                  color: theme.textTheme.bodyLarge?.color?.withOpacity(0.7),
                  fontSize: isSmallScreen ? 14 : 16,
                ),
              ),
            ),
            ElevatedButton(
              onPressed: () async {
                if (newController.text != confirmController.text) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        "Try again later! ",
                        style: TextStyle(
                          fontSize: isSmallScreen ? 12 : 14,
                        ),
                      ),
                      behavior: SnackBarBehavior.floating,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  );
                  return;
                }

                try {
                  if (fieldType == 'email') {
                    await _settingsController.updateEmail(
                      currentEmail: currentController.text,
                      newEmail: newController.text,
                      confirmEmail: confirmController.text,
                    );
                  } else if (fieldType == 'phone number') {
                    await _settingsController.updatePhoneNumber(
                      currentPhoneNumber: currentController.text,
                      newPhoneNumber: newController.text,
                      confirmPhoneNumber: confirmController.text,
                    );
                  }

                  Navigator.of(context).pop();
                  _profileController.loadUserInfo();
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        fieldType == 'phone number'
                            ? l10n.get('phoneNumberUpdated')
                            : l10n.get('emailUpdated'),
                        style: TextStyle(
                          fontSize: isSmallScreen ? 12 : 14,
                        ),
                      ),
                      backgroundColor: Colors.green,
                      behavior: SnackBarBehavior.floating,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  );
                } catch (e) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        e.toString(),
                        style: TextStyle(
                          fontSize: isSmallScreen ? 12 : 14,
                        ),
                      ),
                      backgroundColor: Colors.red,
                      behavior: SnackBarBehavior.floating,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  );
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: theme.primaryColor,
                foregroundColor: Colors.black,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10),
                ),
                padding: EdgeInsets.symmetric(
                  horizontal: isSmallScreen ? 12 : 16,
                  vertical: isSmallScreen ? 6 : 8,
                ),
              ),
              child: Text(
                l10n.get('save'),
                style: TextStyle(
                  color: theme.primaryColor.computeLuminance() > 0.5 ? Colors.black : Colors.white,
                  fontSize: isSmallScreen ? 14 : 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildTextField({
    required TextEditingController controller,
    required String label,
    required String hint,
    required ThemeData theme,
    required bool isSmallScreen,
    bool obscureText = false,
  }) {
    return TextField(
      controller: controller,
      obscureText: obscureText,
      style: TextStyle(
        color: theme.textTheme.bodyLarge?.color,
        fontSize: isSmallScreen ? 14 : 16,
      ),
      decoration: InputDecoration(
        labelText: label,
        hintText: hint,
        labelStyle: TextStyle(
          color: theme.textTheme.bodyMedium?.color?.withOpacity(0.7),
          fontSize: isSmallScreen ? 12 : 14,
        ),
        hintStyle: TextStyle(
          color: theme.textTheme.bodyMedium?.color?.withOpacity(0.5),
          fontSize: isSmallScreen ? 12 : 14,
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: theme.dividerColor.withOpacity(0.2),
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: theme.primaryColor,
          ),
        ),
        filled: true,
        fillColor: theme.cardColor,
        contentPadding: EdgeInsets.symmetric(
          horizontal: isSmallScreen ? 12 : 16,
          vertical: isSmallScreen ? 10 : 12,
        ),
      ),
    );
  }

  void _showPasswordDialog() {
    final TextEditingController currentPasswordController = TextEditingController();
    final TextEditingController newPasswordController = TextEditingController();
    final TextEditingController confirmPasswordController = TextEditingController();
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    // Определяем размер экрана для адаптивности
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          backgroundColor: theme.cardColor,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(20),
          ),
          title: Text(
            l10n.get('changePassword'),
            style: TextStyle(
              color: theme.textTheme.titleLarge?.color,
              fontSize: isSmallScreen ? 18 : 20,
              fontWeight: FontWeight.w600,
            ),
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              _buildTextField(
                controller: currentPasswordController,
                label: l10n.get('currentPassword'),
                hint: l10n.get('currentPassword'),
                theme: theme,
                isSmallScreen: isSmallScreen,
                obscureText: true,
              ),
              SizedBox(height: isSmallScreen ? 12 : 16),
              _buildTextField(
                controller: newPasswordController,
                label: l10n.get('newPassword'),
                hint: l10n.get('newPassword'),
                theme: theme,
                isSmallScreen: isSmallScreen,
                obscureText: true,
              ),
              SizedBox(height: isSmallScreen ? 12 : 16),
              _buildTextField(
                controller: confirmPasswordController,
                label: l10n.get('confirmNewPassword'),
                hint: l10n.get('confirmNewPassword'),
                theme: theme,
                isSmallScreen: isSmallScreen,
                obscureText: true,
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: Text(
                l10n.get('cancel'),
                style: TextStyle(
                  color: theme.textTheme.bodyLarge?.color?.withOpacity(0.7),
                  fontSize: isSmallScreen ? 14 : 16,
                ),
              ),
            ),
            ElevatedButton(
              onPressed: () async {
                if (newPasswordController.text != confirmPasswordController.text) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        l10n.get('passwordsDoNotMatch'),
                        style: TextStyle(
                          fontSize: isSmallScreen ? 12 : 14,
                        ),
                      ),
                      backgroundColor: Colors.red,
                      behavior: SnackBarBehavior.floating,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  );
                  return;
                }

                try {
                  await _settingsController.updatePassword(
                    currentPassword: currentPasswordController.text,
                    newPassword: newPasswordController.text,
                    confirmPassword: confirmPasswordController.text,
                  );

                  Navigator.of(context).pop();
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        l10n.get('passwordUpdated'),
                        style: TextStyle(
                          fontSize: isSmallScreen ? 12 : 14,
                        ),
                      ),
                      backgroundColor: Colors.green,
                      behavior: SnackBarBehavior.floating,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  );
                } catch (e) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        e.toString(),
                        style: TextStyle(
                          fontSize: isSmallScreen ? 12 : 14,
                        ),
                      ),
                      backgroundColor: Colors.red,
                      behavior: SnackBarBehavior.floating,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  );
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: theme.primaryColor,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10),
                ),
                padding: EdgeInsets.symmetric(
                  horizontal: isSmallScreen ? 12 : 16,
                  vertical: isSmallScreen ? 6 : 8,
                ),
              ),
              child: Text(
                l10n.get('save'),
                style: TextStyle(
                  color: theme.primaryColor.computeLuminance() > 0.5 ? Colors.black : Colors.white,
                  fontSize: isSmallScreen ? 14 : 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ],
        );
      },
    );
  }
}