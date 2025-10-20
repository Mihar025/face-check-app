import 'package:face_check/services/ApiService.dart';
import 'package:face_check/services/jwt_service.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../providers/localization_provider.dart';
import '../drawer/profile_screen/profile_controller/profile_controller.dart';

class CustomDrawer extends StatefulWidget {
  const CustomDrawer({super.key});

  @override
  State<CustomDrawer> createState() => _CustomDrawerState();
}

class _CustomDrawerState extends State<CustomDrawer> {
  static final ProfileController _sharedController = ProfileController();
  static String _cachedUserRole = 'USER';
  static bool _isFirstLoad = true;
  bool _isRefreshing = false;

  @override
  void initState() {
    super.initState();
    if (_isFirstLoad) {
      _sharedController.loadUserInfo();
      _loadUserRole();
      _isFirstLoad = false;
    }
  }

  Future<void> _loadUserRole() async {
    final role = await JwtService.getUserRole();
    if (!mounted) return;
    setState(() {
      _cachedUserRole = role;
    });
  }

  Future<void> _fullReload(BuildContext context) async {
    if (_isRefreshing) return;

    setState(() => _isRefreshing = true);

    try {
      // Очищаем кеш главного экрана
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove('cached_worked_hours');
      await prefs.remove('cached_last_punch_date');
      await prefs.remove('cached_last_punch_time');
      await prefs.remove('cache_timestamp');

      // Перезагружаем данные профиля
      await _sharedController.loadUserInfo();
      await _loadUserRole();

      // Отправляем уведомление главному экрану для перезагрузки
      MainScreenReloadNotification().dispatch(context);

      if (mounted) {
        final lang = context.read<LocalizationProvider>().localizations.languageCode;
        final message = _getLocalizedMessage(lang, 'dataUpdated');

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('✓ $message'),
            duration: const Duration(seconds: 2),
            backgroundColor: Colors.green,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        final lang = context.read<LocalizationProvider>().localizations.languageCode;
        final error = _getLocalizedMessage(lang, 'error');

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('$error: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isRefreshing = false);
    }
  }



  String _getLocalizedMessage(String lang, String key) {
    final messages = {
      'en': {
        'dataUpdated': 'Data updated',
        'error': 'Error',
        'loading': 'Loading...',
      },
      'es': {
        'dataUpdated': 'Datos actualizados',
        'error': 'Error',
        'loading': 'Cargando...',
      },
      'ru': {
        'dataUpdated': 'Данные обновлены',
        'error': 'Ошибка',
        'loading': 'Загрузка...',
      }
    };
    return messages[lang]?[key] ?? messages['en']?[key] ?? key;
  }

  String _getLocalizedRole(String role, String lang) {
    if (lang == 'ru') {
      switch (role) {
        case 'ADMIN': return 'АДМИН';
        case 'FOREMAN': return 'БРИГАДИР';
        case 'USER': return 'РАБОТНИК';
        default: return role;
      }
    } else if (lang == 'es') {
      switch (role) {
        case 'ADMIN': return 'ADMIN';
        case 'FOREMAN': return 'CAPATAZ';
        case 'USER': return 'USUARIO';
        default: return role;
      }
    }
    return role;
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final l10n = context.read<LocalizationProvider>().localizations;
    final lang = l10n.languageCode;

    return Drawer(
      backgroundColor: theme.scaffoldBackgroundColor,
      child: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(12),
          children: [
            _buildHeader(theme, isDark, l10n, lang),
            const SizedBox(height: 16),
            const Divider(height: 1),
            const SizedBox(height: 8),
            _buildMenuItems(theme, isDark, l10n),
            const SizedBox(height: 8),
            const Divider(height: 1),
            const SizedBox(height: 8),
            _buildLogout(theme, isDark, l10n),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(ThemeData theme, bool isDark, dynamic l10n, String lang) {
    return ValueListenableBuilder(
      valueListenable: _sharedController.state,
      builder: (context, ProfileState state, _) {
        final photo = state.userInfo?.photoUrl;
        final name = state.userInfo?.fullName ??
            (state.isLoading
                ? _getLocalizedMessage(lang, 'loading')
                : 'User');

        return Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: isDark ? Colors.white.withOpacity(0.06) : Colors.white,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(
              color: isDark ? Colors.white.withOpacity(0.1) : Colors.black.withOpacity(0.06),
            ),
            boxShadow: isDark ? null : [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 12,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: Row(
            children: [
              _buildAvatar(photo, isDark),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      name,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: GoogleFonts.montserrat(
                        fontSize: 18,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    const SizedBox(height: 8),
                    _RoleChip(
                      role: _getLocalizedRole(_cachedUserRole, lang),
                      originalRole: _cachedUserRole,
                      isDark: isDark,
                    ),
                  ],
                ),
              ),
              _buildRefreshButton(theme, l10n),
            ],
          ),
        );
      },
    );
  }

  Widget _buildAvatar(String? photo, bool isDark) {
    return Container(
      width: 64,
      height: 64,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: isDark ? Colors.grey[800] : Colors.grey[200],
        border: Border.all(
          color: isDark ? Colors.white.withOpacity(0.1) : Colors.black.withOpacity(0.06),
        ),
        image: (photo != null && photo.isNotEmpty)
            ? DecorationImage(image: NetworkImage(photo), fit: BoxFit.cover)
            : null,
      ),
      child: (photo == null || photo.isEmpty)
          ? const Icon(Icons.person, size: 32)
          : null,
    );
  }

  Widget _buildRefreshButton(ThemeData theme, dynamic l10n) {
    return Stack(
      alignment: Alignment.center,
      children: [
        IconButton(
          tooltip: l10n.get('refresh'),
          onPressed: _isRefreshing ? null : () async {
            await _fullReload(context);
          },
          icon: Icon(
            Icons.refresh_rounded,
            color: _isRefreshing
                ? theme.iconTheme.color?.withOpacity(0.3)
                : theme.iconTheme.color,
          ),
        ),
        if (_isRefreshing)
          const SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(strokeWidth: 2),
          ),
      ],
    );
  }

  Widget _buildMenuItems(ThemeData theme, bool isDark, dynamic l10n) {
    return Column(
      children: [
        _MenuItem(
          icon: Icons.home_rounded,
          title: l10n.get('home'),
          onTap: () async {
            Navigator.pop(context);
            await _fullReload(context);
          },
          isDark: isDark,
        ),
        _MenuItem(
          icon: Icons.fingerprint_rounded,
          title: l10n.get('punch'),
          onTap: () {
            Navigator.pop(context);
            Navigator.pushNamed(context, '/drawer/punch');
          },
          isDark: isDark,
        ),
        _MenuItem(
          icon: Icons.person_rounded,
          title: l10n.get('profile'),
          onTap: () {
            Navigator.pop(context);
            Navigator.pushNamed(context, '/profile');
          },
          isDark: isDark,
        ),
        if (_cachedUserRole == 'ADMIN' || _cachedUserRole == 'FOREMAN')
          _MenuItem(
            icon: Icons.people_rounded,
            title: l10n.get('employee'),
            onTap: () {
              Navigator.pop(context);
              Navigator.pushNamed(context, '/employee');
            },
            isDark: isDark,
          ),
        _MenuItem(
          icon: Icons.attach_money_rounded,
          title: l10n.get('finance'),
          onTap: () {
            Navigator.pop(context);
            Navigator.pushNamed(context, '/finance');
          },
          isDark: isDark,
        ),
        _MenuItem(
          icon: Icons.settings_rounded,
          title: l10n.get('settings'),
          onTap: () {
            Navigator.pop(context);
            Navigator.pushNamed(context, '/settings');
          },
          isDark: isDark,
        ),
      ],
    );
  }

  Widget _buildLogout(ThemeData theme, bool isDark, dynamic l10n) {
    return _MenuItem(
      icon: Icons.logout_rounded,
      title: l10n.get('logout'),
      onTap: () async {
        _isFirstLoad = true;
        await ApiService.instance.logout();
        if (mounted) {
          Navigator.of(context).pushNamedAndRemoveUntil('/', (route) => false);
        }
      },
      isDark: isDark,
      isLogout: true,
    );
  }
}

// Notification для триггера перезагрузки главного экрана
class MainScreenReloadNotification extends Notification {}

class _MenuItem extends StatelessWidget {
  final IconData icon;
  final String title;
  final VoidCallback onTap;
  final bool isDark;
  final bool isLogout;

  const _MenuItem({
    required this.icon,
    required this.title,
    required this.onTap,
    required this.isDark,
    this.isLogout = false,
  });

  @override
  Widget build(BuildContext context) {
    final color = isLogout ? Colors.redAccent : null;

    return InkWell(
      borderRadius: BorderRadius.circular(12),
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
        margin: const EdgeInsets.symmetric(vertical: 4),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: isLogout
                    ? Colors.redAccent.withOpacity(0.08)
                    : isDark
                    ? Colors.white.withOpacity(0.06)
                    : Colors.black.withOpacity(0.04),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, size: 22, color: color),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                title,
                style: GoogleFonts.poppins(
                  color: color,
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            if (!isLogout)
              const Icon(Icons.chevron_right_rounded, size: 22),
          ],
        ),
      ),
    );
  }
}

class _RoleChip extends StatelessWidget {
  final String role;
  final String originalRole;
  final bool isDark;

  const _RoleChip({
    required this.role,
    required this.originalRole,
    required this.isDark,
  });

  @override
  Widget build(BuildContext context) {
    final color = originalRole == 'ADMIN'
        ? Colors.orange
        : originalRole == 'FOREMAN'
        ? Colors.blue
        : Colors.green;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(999),
        border: Border.all(
          color: isDark ? Colors.white.withOpacity(0.1) : Colors.black.withOpacity(0.06),
        ),
        color: isDark ? color.withOpacity(0.16) : color.withOpacity(0.10),
      ),
      child: Text(
        role,
        style: GoogleFonts.poppins(
          fontSize: 12,
          letterSpacing: 0.6,
          fontWeight: FontWeight.w600,
          color: color,
        ),
      ),
    );
  }
}