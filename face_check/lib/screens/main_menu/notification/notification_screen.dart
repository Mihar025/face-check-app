import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:provider/provider.dart';

import '../../../providers/localization_provider.dart';

class NotificationScreen extends StatefulWidget {
  const NotificationScreen({super.key});

  @override
  State<NotificationScreen> createState() => _NotificationScreenState();
}

class _NotificationScreenState extends State<NotificationScreen> with SingleTickerProviderStateMixin {
  final List<PendingNotificationRequest> _notifications = [];
  bool _isLoading = false;
  late final AnimationController _animationController;
  final FlutterLocalNotificationsPlugin _notificationsPlugin = FlutterLocalNotificationsPlugin();

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
      _loadNotifications();
    });
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  Future<void> _loadNotifications() async {
    if (mounted) {
      setState(() {
        _isLoading = true;
      });
    }

    try {
      final notifications = await _notificationsPlugin.pendingNotificationRequests();

      if (mounted) {
        setState(() {
          _notifications.clear();
          _notifications.addAll(notifications);
          _isLoading = false;
        });

        _animationController.reset();
        _animationController.forward();
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  Future<void> _clearAllNotifications() async {
    try {
      await _notificationsPlugin.cancelAll();
      await _loadNotifications();
    } catch (_) {
    }
  }

  Future<void> _cancelNotification(int id) async {
    try {
      await _notificationsPlugin.cancel(id);
      await _loadNotifications();
    } catch (_) {
    }
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
          if (_notifications.isNotEmpty)
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
      body: AnimatedSwitcher(
        duration: const Duration(milliseconds: 300),
        switchInCurve: Curves.easeOutCubic,
        switchOutCurve: Curves.easeInCubic,
        child: _isLoading
            ? const Center(
          key: ValueKey('loading'),
          child: CircularProgressIndicator(
            color: Colors.blue,
          ),
        )
            : _notifications.isEmpty
            ? Center(
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
                  style: TextStyle(
                    color: Colors.blue,
                    fontSize: fontSizeSmall,
                  ),
                ),
                onPressed: _loadNotifications,
              ),
            ],
          ),
        )
            : ListView.builder(
          key: const ValueKey('list'),
          itemCount: _notifications.length,
          padding: EdgeInsets.all(padding),
          cacheExtent: 800,
          itemBuilder: (context, index) {
            final notification = _notifications[index];

            return SlideTransition(
              position: _getSlideAnimation(index),
              child: FadeTransition(
                opacity: CurvedAnimation(
                  parent: _animationController,
                  curve: Interval(
                    index * 0.05,
                    0.6 + index * 0.05,
                    curve: Curves.easeOut,
                  ),
                ),
                child: Dismissible(
                  key: Key('notification_${notification.id}'),
                  background: Container(
                    alignment: Alignment.centerRight,
                    padding: EdgeInsets.only(right: isSmallScreen ? 15 : 20),
                    color: Colors.redAccent.withOpacity(0.8),
                    child: Icon(
                      Icons.delete_outline,
                      color: Colors.white,
                      size: isSmallScreen ? 20 : 24,
                    ),
                  ),
                  direction: DismissDirection.endToStart,
                  onDismissed: (direction) => _cancelNotification(notification.id),
                  child: Card(
                    margin: EdgeInsets.only(bottom: isSmallScreen ? 6 : 8),
                    elevation: 0,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                      side: BorderSide(
                        color: theme.dividerColor,
                        width: 0.5,
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
                          color: Colors.blue.withOpacity(0.1),
                          shape: BoxShape.circle,
                        ),
                        child: Icon(
                          Icons.notifications,
                          color: Colors.blue,
                          size: iconSizeMedium,
                        ),
                      ),
                      title: Text(
                        l10n.get(notification.title ?? ''),
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: fontSize,
                        ),
                      ),
                      subtitle: Padding(
                        padding: EdgeInsets.only(top: verticalPadding),
                        child: Text(
                          l10n.get(notification.body ?? ''),
                          style: TextStyle(
                            fontSize: fontSizeSmall,
                          ),
                        ),
                      ),
                      trailing: IconButton(
                        icon: Icon(
                          Icons.clear,
                          size: isSmallScreen ? 18 : 20,
                        ),
                        onPressed: () => _cancelNotification(notification.id),
                      ),
                    ),
                  ),
                ),
              ),
            );
          },
        ),
      ),
      floatingActionButton: _notifications.isNotEmpty
          ? FloatingActionButton(
        mini: true,
        onPressed: _loadNotifications,
        backgroundColor: Colors.blue,
        child: const Icon(Icons.refresh),
      )
          : null,
    );
  }
}