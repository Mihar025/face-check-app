import 'package:face_check/services/ApiService.dart';
import 'package:face_check/services/jwt_service.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../../providers/localization_provider.dart';
import '../drawer/profile_screen/profile_controller/profile_controller.dart';

class CustomDrawer extends StatefulWidget {
  const CustomDrawer({super.key});

  @override
  State<CustomDrawer> createState() => _CustomDrawerState();
}

class _CustomDrawerState extends State<CustomDrawer> {
  final ProfileController _controller = ProfileController();
  String _userRole = 'USER';

  @override
  void initState() {
    super.initState();
    _controller.loadUserInfo();
    _loadUserRole();
  }

  Future<void> _loadUserRole() async {
    final role = await JwtService.getUserRole();
    if (!mounted) return;
    setState(() {
      _userRole = role;
    });
  }

  Future<void> _refreshApplication() async {
    try {
      Navigator.pop(context);
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) => const Center(child: CircularProgressIndicator()),
      );

      await _controller.loadUserInfo();
      await _loadUserRole();

      if (context.mounted) {
        Navigator.pop(context);
        Navigator.pushReplacementNamed(context, '/main');
      }
    } catch (e) {
      if (context.mounted) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Ошибка при обновлении: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    // Цвета/стили под темы
    final cardColor = theme.brightness == Brightness.dark
        ? Colors.white.withOpacity(0.06)
        : Colors.white;
    final borderColor = theme.brightness == Brightness.dark
        ? Colors.white.withOpacity(0.10)
        : Colors.black.withOpacity(0.06);
    final dividerColor = theme.brightness == Brightness.dark
        ? Colors.white24
        : Colors.black12;
    final tileBgPressed = theme.brightness == Brightness.dark
        ? Colors.white.withOpacity(0.08)
        : Colors.black.withOpacity(0.04);

    return Drawer(
      backgroundColor: theme.scaffoldBackgroundColor,
      child: SafeArea(
        child: ListView(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
          children: [
            // ===== Header Card =====
            ValueListenableBuilder(
              valueListenable: _controller.state,
              builder: (context, ProfileState state, _) {
                final photo = state.userInfo?.photoUrl;
                final name = state.userInfo?.fullName ?? 'Loading...';

                return Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: cardColor,
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: borderColor, width: 1),
                    boxShadow: theme.brightness == Brightness.light
                        ? [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.05),
                        blurRadius: 12,
                        offset: const Offset(0, 4),
                      ),
                    ]
                        : null,
                  ),
                  child: Row(
                    children: [
                      // Аватар
                      Container(
                        width: 64,
                        height: 64,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: theme.brightness == Brightness.dark
                              ? Colors.grey[800]
                              : Colors.grey[200],
                          border: Border.all(color: borderColor, width: 1),
                          image: (photo != null && photo.isNotEmpty)
                              ? DecorationImage(
                            image: NetworkImage(photo),
                            fit: BoxFit.cover,
                          )
                              : null,
                        ),
                        child: (photo == null || photo.isEmpty)
                            ? Icon(Icons.person, size: 32, color: theme.iconTheme.color)
                            : null,
                      ),
                      const SizedBox(width: 14),
                      // Имя + роль
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              name,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              style: GoogleFonts.montserrat(
                                color: theme.textTheme.bodyLarge?.color,
                                fontSize: 18,
                                fontWeight: FontWeight.w700,
                              ),
                            ),
                            const SizedBox(height: 8),
                            _RoleChip(role: _userRole, borderColor: borderColor),
                          ],
                        ),
                      ),
                      // Кнопка рефреша
                      IconButton(
                        tooltip: l10n.get('home'),
                        onPressed: _refreshApplication,
                        icon: Icon(Icons.refresh_rounded, color: theme.iconTheme.color),
                        splashRadius: 22,
                      ),
                    ],
                  ),
                );
              },
            ),

            const SizedBox(height: 16),
            Divider(height: 1, thickness: 1, color: dividerColor),
            const SizedBox(height: 8),

            // ===== Меню =====
            _MenuSection(
              children: [
                _buildMenuItem(
                  context: context,
                  icon: Icons.home_rounded,
                  title: l10n.get('home'),
                  onTap: _refreshApplication,
                  theme: theme,
                  tileBgPressed: tileBgPressed,
                ),
                _buildMenuItem(
                  context: context,
                  icon: Icons.fingerprint_rounded,
                  title: l10n.get('punch'),
                  onTap: () {
                    Navigator.pop(context);
                    Navigator.pushNamed(context, '/drawer/punch');
                  },
                  theme: theme,
                  tileBgPressed: tileBgPressed,
                ),
                _buildMenuItem(
                  context: context,
                  icon: Icons.person_rounded,
                  title: l10n.get('profile'),
                  onTap: () {
                    Navigator.pop(context);
                    Navigator.pushNamed(context, '/profile');
                  },
                  theme: theme,
                  tileBgPressed: tileBgPressed,
                ),
                if (_userRole == 'ADMIN' || _userRole == 'FOREMAN')
                  _buildMenuItem(
                    context: context,
                    icon: Icons.people_rounded,
                    title: l10n.get('employee'),
                    onTap: () {
                      Navigator.pop(context);
                      Navigator.pushNamed(context, '/employee');
                    },
                    theme: theme,
                    tileBgPressed: tileBgPressed,
                  ),
                _buildMenuItem(
                  context: context,
                  icon: Icons.attach_money_rounded,
                  title: l10n.get('finance'),
                  onTap: () {
                    Navigator.pop(context);
                    Navigator.pushNamed(context, '/finance');
                  },
                  theme: theme,
                  tileBgPressed: tileBgPressed,
                ),
                _buildMenuItem(
                  context: context,
                  icon: Icons.settings_rounded,
                  title: l10n.get('settings'),
                  onTap: () {
                    Navigator.pop(context);
                    Navigator.pushNamed(context, '/settings');
                  },
                  theme: theme,
                  tileBgPressed: tileBgPressed,
                ),
              ],
            ),

            const SizedBox(height: 8),
            Divider(height: 1, thickness: 1, color: dividerColor),
            const SizedBox(height: 8),

            // ===== Logout =====
            _MenuSection(
              children: [
                _buildMenuItem(
                  context: context,
                  icon: Icons.logout_rounded,
                  title: l10n.get('logout'),
                  onTap: () async {
                    await ApiService.instance.logout();
                    if (!context.mounted) return;
                    Navigator.of(context).pushNamedAndRemoveUntil('/', (route) => false);
                  },
                  theme: theme,
                  isLogout: true,
                  tileBgPressed: tileBgPressed,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMenuItem({
    required BuildContext context,
    required IconData icon,
    required String title,
    required VoidCallback onTap,
    required ThemeData theme,
    required Color tileBgPressed,
    bool isLogout = false,
  }) {
    final textColor = isLogout ? Colors.redAccent : theme.textTheme.bodyLarge?.color;
    final iconColor = isLogout ? Colors.redAccent : theme.iconTheme.color;

    return InkWell(
      borderRadius: BorderRadius.circular(12),
      onTap: onTap,
      splashColor: tileBgPressed.withOpacity(0.15),
      highlightColor: tileBgPressed.withOpacity(0.10),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
        margin: const EdgeInsets.symmetric(vertical: 4),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
        ),
        child: Row(
          children: [
            // Иконка в мягком кружке
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: isLogout
                    ? Colors.redAccent.withOpacity(0.08)
                    : theme.brightness == Brightness.dark
                    ? Colors.white.withOpacity(0.06)
                    : Colors.black.withOpacity(0.04),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, size: 22, color: iconColor),
            ),
            const SizedBox(width: 12),
            // Текст
            Expanded(
              child: Text(
                title,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: GoogleFonts.poppins(
                  color: textColor,
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  letterSpacing: 0.2,
                ),
              ),
            ),
            // Стрелочка (кроме logout)
            if (!isLogout)
              Icon(Icons.chevron_right_rounded, color: theme.iconTheme.color, size: 22),
          ],
        ),
      ),
    );
  }
}

class _RoleChip extends StatelessWidget {
  final String role;
  final Color borderColor;

  const _RoleChip({required this.role, required this.borderColor});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final color = role == 'ADMIN'
        ? Colors.orange
        : role == 'FOREMAN'
        ? Colors.blue
        : Colors.green;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(999),
        border: Border.all(color: borderColor, width: 1),
        color: theme.brightness == Brightness.dark
            ? color.withOpacity(0.16)
            : color.withOpacity(0.10),
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

class _MenuSection extends StatelessWidget {
  final List<Widget> children;
  const _MenuSection({required this.children});

  @override
  Widget build(BuildContext context) {
    return Column(children: children);
  }


}
