import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../models/server_notification.dart';
import '../../../providers/localization_provider.dart';
import '../../../services/ApiService.dart';
import 'notification_cache_service.dart';

class NotificationScreen extends StatefulWidget {
  const NotificationScreen({super.key});

  @override
  State<NotificationScreen> createState() => _NotificationScreenState();
}

class _NotificationScreenState extends State<NotificationScreen>
    with SingleTickerProviderStateMixin {
  List<ServerNotification> _serverNotifications = [];
  int? _companyId;
  bool _isLoading = false;
  bool _isRefreshing = false;

  late final AnimationController _animationController;

  late final _slideInTween = Tween<Offset>(
    begin: const Offset(0.05, 0),
    end: Offset.zero,
  );

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 500),
      vsync: this,
    );

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _initializeNotifications();
    });
  }

  // ==================== LOCAL REMINDERS ====================
  // Генерируем reminders по текущему времени, а не через pendingNotificationRequests()
  List<_LocalReminder> _getLocalReminders() {
    final now = DateTime.now();
    final reminders = <_LocalReminder>[];

    // Только в будние дни (Mon-Fri)
    if (now.weekday >= DateTime.monday && now.weekday <= DateTime.friday) {
      // После 7:00 AM — показываем "Don't forget to punch in"
      if (now.hour >= 7 && now.hour < 16) {
        reminders.add(_LocalReminder(
          title: 'dailyPunchIn.title',
          body: 'dailyPunchIn.body',
          icon: Icons.login_rounded,
          color: Colors.green,
          time: DateTime(now.year, now.month, now.day, 7, 0),
        ));
      }

      // После 4:00 PM — показываем "Don't forget to punch out"
      if (now.hour >= 16) {
        reminders.add(_LocalReminder(
          title: 'dailyPunchOut.title',
          body: 'dailyPunchOut.body',
          icon: Icons.logout_rounded,
          color: Colors.orange,
          time: DateTime(now.year, now.month, now.day, 16, 0),
        ));
      }
    }

    // Пятница после 3:00 PM — "Check your weekly hours"
    if (now.weekday == DateTime.friday && now.hour >= 15) {
      reminders.add(_LocalReminder(
        title: 'weeklyHoursCheck.title',
        body: 'weeklyHoursCheck.body',
        icon: Icons.schedule,
        color: Colors.blue,
        time: DateTime(now.year, now.month, now.day, 15, 0),
      ));
    }

    return reminders;
  }

  // ==================== SERVER NOTIFICATIONS ====================

  Future<void> _initializeNotifications() async {
    await _loadFromCache();
    _loadServerNotifications(updateCache: true);
  }

  Future<void> _loadFromCache() async {
    try {
      final cachedNotifications =
      await NotificationCacheService.loadCachedNotifications();

      if (cachedNotifications != null && mounted) {
        setState(() {
          _serverNotifications = cachedNotifications;
        });
        _animationController.reset();
        _animationController.forward();
      }
    } catch (e) {
      print('Error loading from cache: $e');
    }
  }

  Future<void> _loadServerNotifications({bool updateCache = false}) async {
    if (_serverNotifications.isEmpty && mounted) {
      setState(() => _isLoading = true);
    }

    try {
      _companyId = await ApiService.instance.getCompanyId();

      if (_companyId != null) {
        final notificationsResponse = await ApiService.instance
            .getTodaysNotifications(
          companyId: _companyId!,
          page: 0,
          size: 20,
        );

        if (mounted) {
          setState(() {
            _serverNotifications = notificationsResponse.content;
            _isLoading = false;
            _isRefreshing = false;
          });
        }

        if (updateCache) {
          await NotificationCacheService.saveNotifications(
            notificationsResponse.content,
          );
        }

        _animationController.reset();
        _animationController.forward();
      }
    } catch (e) {
      print('Error loading server notifications: $e');
      if (mounted) {
        setState(() {
          _isLoading = false;
          _isRefreshing = false;
        });
      }

      if (_serverNotifications.isEmpty) {
        await _loadFromCache();
      }
    }
  }

  Future<void> _onRefresh() async {
    setState(() => _isRefreshing = true);
    await _loadServerNotifications(updateCache: true);
  }

  Future<void> _clearAllNotifications() async {
    try {
      await NotificationCacheService.clearCache();
      setState(() {
        _serverNotifications.clear();
      });
    } catch (_) {}
  }

  Animation<Offset> _getSlideAnimation(int index) {
    return _slideInTween.animate(
      CurvedAnimation(
        parent: _animationController,
        curve: Interval(
          index * 0.05,
          0.6 + index * 0.05,
          curve: Curves.easeOutCubic,
        ),
      ),
    );
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    final iconSizeLarge = isSmallScreen ? 48.0 : 64.0;
    final iconSizeMedium = isSmallScreen ? 18.0 : 22.0;
    final iconSizeSmall = isSmallScreen ? 16.0 : 18.0;
    final fontSize = isSmallScreen ? 14.0 : 16.0;
    final fontSizeSmall = isSmallScreen ? 12.0 : 14.0;
    final padding = isSmallScreen ? 8.0 : 12.0;
    final verticalPadding = isSmallScreen ? 6.0 : 8.0;

    final localReminders = _getLocalReminders();
    final totalItems = localReminders.length + _serverNotifications.length;

    return Scaffold(
      backgroundColor: theme.scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
        elevation: 1,
        title: Text(
          l10n.get('notifications.title'),
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.w500,
            fontSize: isSmallScreen ? 16 : 18,
          ),
        ),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () => Navigator.of(context).pop(),
        ),
        actions: [
          if (_serverNotifications.isNotEmpty)
            TextButton.icon(
              icon: Icon(
                Icons.clear_all,
                size: iconSizeSmall,
                color: Colors.white,
              ),
              label: Text(
                l10n.get('notifications.clearAll'),
                style: TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.w500,
                  fontSize: fontSizeSmall,
                ),
              ),
              onPressed: _clearAllNotifications,
            ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _onRefresh,
        child: AnimatedSwitcher(
          duration: const Duration(milliseconds: 300),
          switchInCurve: Curves.easeOutCubic,
          switchOutCurve: Curves.easeInCubic,
          child: _isLoading && _serverNotifications.isEmpty
              ? const Center(
            key: ValueKey('loading'),
            child: CircularProgressIndicator(color: Colors.blue),
          )
              : totalItems == 0
              ? _buildEmptyState(
              theme, l10n, iconSizeLarge,
              isSmallScreen, fontSize, fontSizeSmall)
              : _buildNotificationList(
              theme, l10n, localReminders, isSmallScreen,
              padding, verticalPadding, iconSizeMedium,
              fontSize, fontSizeSmall),
        ),
      ),
      floatingActionButton: totalItems > 0
          ? FloatingActionButton(
        mini: true,
        onPressed: _onRefresh,
        backgroundColor: Colors.blue,
        child: _isRefreshing
            ? const SizedBox(
          width: 20,
          height: 20,
          child: CircularProgressIndicator(
            color: Colors.white,
            strokeWidth: 2,
          ),
        )
            : const Icon(Icons.refresh),
      )
          : null,
    );
  }

  Widget _buildEmptyState(
      ThemeData theme,
      dynamic l10n,
      double iconSizeLarge,
      bool isSmallScreen,
      double fontSize,
      double fontSizeSmall,
      ) {
    return Center(
      key: const ValueKey('empty'),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.notifications_off_outlined,
            size: iconSizeLarge,
            color: theme.colorScheme.onSurface.withOpacity(0.5),
          ),
          SizedBox(height: isSmallScreen ? 12 : 16),
          Text(
            l10n.get('notifications.noNotifications'),
            style: TextStyle(
              color: theme.textTheme.bodyLarge?.color,
              fontSize: fontSize,
              fontWeight: FontWeight.w400,
            ),
          ),
          SizedBox(height: isSmallScreen ? 8 : 12),
          TextButton.icon(
            icon: const Icon(Icons.refresh, color: Colors.blue),
            label: Text(
              l10n.get('refresh'),
              style: TextStyle(color: Colors.blue, fontSize: fontSizeSmall),
            ),
            onPressed: _onRefresh,
          ),
        ],
      ),
    );
  }

  Widget _buildNotificationList(
      ThemeData theme,
      dynamic l10n,
      List<_LocalReminder> localReminders,
      bool isSmallScreen,
      double padding,
      double verticalPadding,
      double iconSizeMedium,
      double fontSize,
      double fontSizeSmall,
      ) {
    final totalItems = localReminders.length + _serverNotifications.length;

    return ListView.builder(
      key: const ValueKey('list'),
      itemCount: totalItems,
      padding: EdgeInsets.all(padding),
      cacheExtent: 800,
      itemBuilder: (context, index) {
        // Сначала local reminders
        if (index < localReminders.length) {
          return _buildLocalReminderTile(
            index,
            localReminders[index],
            l10n,
            theme,
            isSmallScreen,
            verticalPadding,
            iconSizeMedium,
            fontSize,
            fontSizeSmall,
          );
        }

        // Потом server notifications
        final serverIndex = index - localReminders.length;
        return _buildServerNotificationTile(
          index,
          serverIndex,
          theme,
          isSmallScreen,
          verticalPadding,
          iconSizeMedium,
          fontSize,
          fontSizeSmall,
        );
      },
    );
  }

  // ==================== LOCAL REMINDER TILE ====================
  Widget _buildLocalReminderTile(
      int animIndex,
      _LocalReminder reminder,
      dynamic l10n,
      ThemeData theme,
      bool isSmallScreen,
      double verticalPadding,
      double iconSizeMedium,
      double fontSize,
      double fontSizeSmall,
      ) {
    return SlideTransition(
      position: _getSlideAnimation(animIndex),
      child: FadeTransition(
        opacity: CurvedAnimation(
          parent: _animationController,
          curve: Interval(
            animIndex * 0.05,
            0.6 + animIndex * 0.05,
            curve: Curves.easeOut,
          ),
        ),
        child: Card(
          margin: EdgeInsets.only(bottom: isSmallScreen ? 6 : 8),
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
            side: BorderSide(
              color: reminder.color.withOpacity(0.3),
              width: 1.0,
            ),
          ),
          child: ListTile(
            contentPadding: EdgeInsets.symmetric(
              horizontal: isSmallScreen ? 12 : 16,
              vertical: verticalPadding,
            ),
            leading: Container(
              padding: EdgeInsets.all(isSmallScreen ? 6 : 8),
              decoration: BoxDecoration(
                color: reminder.color.withOpacity(0.15),
                shape: BoxShape.circle,
              ),
              child: Icon(
                reminder.icon,
                color: reminder.color,
                size: iconSizeMedium,
              ),
            ),
            title: Text(
              l10n.get(reminder.title),
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: fontSize,
              ),
            ),
            subtitle: Padding(
              padding: EdgeInsets.only(top: verticalPadding),
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 6,
                      vertical: 2,
                    ),
                    decoration: BoxDecoration(
                      color: reminder.color.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: Text(
                      'REMINDER',
                      style: TextStyle(
                        color: reminder.color,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      l10n.get(reminder.body),
                      style: TextStyle(fontSize: fontSizeSmall),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  // ==================== SERVER NOTIFICATION TILE ====================
  Widget _buildServerNotificationTile(
      int animIndex,
      int serverIndex,
      ThemeData theme,
      bool isSmallScreen,
      double verticalPadding,
      double iconSizeMedium,
      double fontSize,
      double fontSizeSmall,
      ) {
    final serverNotif = _serverNotifications[serverIndex];
    final isNew = DateTime.now().difference(serverNotif.createdAt).inMinutes < 5;

    return SlideTransition(
      position: _getSlideAnimation(animIndex),
      child: FadeTransition(
        opacity: CurvedAnimation(
          parent: _animationController,
          curve: Interval(
            animIndex * 0.05,
            0.6 + animIndex * 0.05,
            curve: Curves.easeOut,
          ),
        ),
        child: Card(
          margin: EdgeInsets.only(bottom: isSmallScreen ? 6 : 8),
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
            side: BorderSide(
              color: isNew
                  ? Colors.blue.withOpacity(0.3)
                  : theme.dividerColor,
              width: isNew ? 1.0 : 0.5,
            ),
          ),
          child: ListTile(
            contentPadding: EdgeInsets.symmetric(
              horizontal: isSmallScreen ? 12 : 16,
              vertical: verticalPadding,
            ),
            leading: Container(
              padding: EdgeInsets.all(isSmallScreen ? 6 : 8),
              decoration: BoxDecoration(
                color: isNew
                    ? Colors.blue.withOpacity(0.15)
                    : Colors.orange.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.cloud,
                color: isNew ? Colors.blue : Colors.orange,
                size: iconSizeMedium,
              ),
            ),
            title: Text(
              serverNotif.message,
              style: TextStyle(
                fontWeight: isNew ? FontWeight.bold : FontWeight.w500,
                fontSize: fontSize,
              ),
            ),
            subtitle: Padding(
              padding: EdgeInsets.only(top: verticalPadding),
              child: Row(
                children: [
                  if (isNew) ...[
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 6,
                        vertical: 2,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.blue.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: const Text(
                        'NEW',
                        style: TextStyle(
                          color: Colors.blue,
                          fontSize: 10,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                  ],
                  Text(
                    _formatTime(serverNotif.createdAt),
                    style: TextStyle(fontSize: fontSizeSmall),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  String _formatTime(DateTime createdAt) {
    final now = DateTime.now();
    final diff = now.difference(createdAt);

    if (diff.inMinutes < 1) {
      return 'Just now';
    } else if (diff.inMinutes < 60) {
      return '${diff.inMinutes} min ago';
    } else if (diff.inHours < 24) {
      return '${diff.inHours}h ago';
    } else {
      final hour = createdAt.hour;
      final period = hour >= 12 ? 'PM' : 'AM';
      final displayHour = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
      return '${createdAt.month}/${createdAt.day} '
          '${displayHour}:${createdAt.minute.toString().padLeft(2, '0')} $period';
    }
  }
}

// ==================== LOCAL REMINDER MODEL ====================
class _LocalReminder {
  final String title;
  final String body;
  final IconData icon;
  final Color color;
  final DateTime time;

  _LocalReminder({
    required this.title,
    required this.body,
    required this.icon,
    required this.color,
    required this.time,
  });
}