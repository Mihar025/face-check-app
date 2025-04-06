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
    print('=== CustomDrawer: Loading user role ===');
    final role = await JwtService.getUserRole();
    print('CustomDrawer received role: $role');

    setState(() {
      _userRole = role;
      print('CustomDrawer state updated with role: $_userRole');
    });
    print('=== CustomDrawer: Role loading completed ===');
  }

  // Добавляем функцию для обновления состояния приложения
  Future<void> _refreshApplication() async {
    try {
      // Закрываем drawer
      Navigator.pop(context);

      // Показываем индикатор загрузки
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return const Center(
            child: CircularProgressIndicator(),
          );
        },
      );

      // Обновляем необходимые данные
      await _controller.loadUserInfo();
      await _loadUserRole();

      // Можно добавить другие необходимые обновления
      // Например, обновление данных из API
      // await ApiService.instance.refreshData();

      // Закрываем индикатор загрузки
      if (context.mounted) {
        Navigator.pop(context);

        // Перезагружаем главный экран
        Navigator.pushReplacementNamed(context, '/main');
      }
    } catch (e) {
      print('Error refreshing application: $e');
      if (context.mounted) {
        Navigator.pop(context); // Закрываем индикатор загрузки
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

    return Drawer(
      backgroundColor: theme.scaffoldBackgroundColor,
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          ValueListenableBuilder(
            valueListenable: _controller.state,
            builder: (context, ProfileState state, _) {
              return Container(
                padding: const EdgeInsets.only(top: 50, bottom: 20),
                child: Column(
                  children: [
                    Container(
                      width: 100,
                      height: 100,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        border: Border.all(
                          color: Colors.blue,
                          width: 2,
                        ),
                        color: theme.brightness == Brightness.dark
                            ? Colors.grey[800]
                            : Colors.grey[200],
                        image: state.userInfo?.photoUrl != null &&
                            state.userInfo!.photoUrl!.isNotEmpty
                            ? DecorationImage(
                          image: NetworkImage(state.userInfo!.photoUrl!),
                          fit: BoxFit.cover,
                        )
                            : null,
                      ),
                      child: state.userInfo?.photoUrl == null ||
                          state.userInfo!.photoUrl!.isEmpty
                          ? Icon(
                        Icons.person,
                        size: 50,
                        color: theme.iconTheme.color,
                      )
                          : null,
                    ),
                    const SizedBox(height: 10),
                    Text(
                      state.userInfo?.fullName ?? 'Loading...',
                      style: GoogleFonts.montserrat(
                        color: theme.textTheme.bodyLarge?.color,
                        fontSize: 20,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              );
            },
          ),
          const Divider(height: 1, thickness: 1, color: Colors.white24),
          const SizedBox(height: 20),
          _buildMenuItem(
            context: context,
            icon: Icons.home_rounded,
            title: l10n.get('home'),
            onTap: _refreshApplication,
            theme: theme,
          ),
          _buildDivider(),
          _buildMenuItem(
            context: context,
            icon: Icons.fingerprint_rounded,
            title: l10n.get('punch'),
            onTap: () {
              Navigator.pop(context);
              Navigator.pushNamed(context, '/drawer/punch');
            },
            theme: theme,
          ),
          _buildDivider(),
          _buildMenuItem(
            context: context,
            icon: Icons.person_rounded,
            title: l10n.get('profile'),
            onTap: () {
              Navigator.pop(context);
              Navigator.pushNamed(context, '/profile');
            },
            theme: theme,
          ),
          _buildDivider(),
          if (_userRole == 'ADMIN' || _userRole == 'FOREMAN') ...[
            _buildMenuItem(
              context: context,
              icon: Icons.people_rounded,
              title: l10n.get('employee'),
              onTap: () {
                Navigator.pop(context);
                Navigator.pushNamed(context, '/employee');
              },
              theme: theme,
            ),
            _buildDivider(),
          ],
          _buildMenuItem(
            context: context,
            icon: Icons.attach_money_rounded,
            title: l10n.get('finance'),
            onTap: () {
              Navigator.pop(context);
              Navigator.pushNamed(context, '/finance');
            },
            theme: theme,
          ),
          _buildDivider(),
          _buildMenuItem(
            context: context,
            icon: Icons.settings_rounded,
            title: l10n.get('settings'),
            onTap: () {
              Navigator.pop(context);
              Navigator.pushNamed(context, '/settings');
            },
            theme: theme,
          ),
          _buildDivider(),
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
          ),
        ],
      ),
    );
  }

  Widget _buildMenuItem({
    required BuildContext context,
    required IconData icon,
    required String title,
    required VoidCallback onTap,
    required ThemeData theme,
    bool isLogout = false,
  }) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(12),
      ),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
        leading: Icon(
          icon,
          color: isLogout ? Colors.redAccent : theme.iconTheme.color,
          size: 28,
        ),
        title: Text(
          title,
          style: GoogleFonts.poppins(
            color: isLogout ? Colors.redAccent : theme.textTheme.bodyLarge?.color,
            fontSize: 17,
            fontWeight: FontWeight.w500,
          ),
        ),
        onTap: onTap,
      ),
    );
  }

  Widget _buildDivider() {
    return const Divider(
      height: 1,
      thickness: 0.5,
      indent: 12,
      endIndent: 12,
      color: Colors.white24,
    );
  }
}