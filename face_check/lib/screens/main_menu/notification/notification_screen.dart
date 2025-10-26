import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
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
  final List<PendingNotificationRequest> _notifications = [];
  List<ServerNotification> _serverNotifications = [];
  int? _companyId;
  bool _isLoading = false;
  bool _isRefreshing = false;  // Добавим флаг для рефреша

  late final AnimationController _animationController;
  final FlutterLocalNotificationsPlugin _notificationsPlugin =
  FlutterLocalNotificationsPlugin();

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

  // Новый метод инициализации
  Future<void> _initializeNotifications() async {
    // Сначала загружаем из кеша для быстрого отображения
    await _loadFromCache();

    // Загружаем локальные уведомления
    await _loadNotifications();

    // Потом загружаем с сервера в фоне
    _loadServerNotifications(updateCache: true);
  }

  // Загрузка из кеша
  Future<void> _loadFromCache() async {
    try {
      final cachedNotifications =
      await NotificationCacheService.loadCachedNotifications();

      if (cachedNotifications != null && mounted) {
        setState(() {
          _serverNotifications = cachedNotifications;
        });

        // Запускаем анимацию
        _animationController.reset();
        _animationController.forward();
      }
    } catch (e) {
      print('Error loading from cache: $e');
    }
  }

  // Обновленный метод загрузки с сервера
  Future<void> _loadServerNotifications({bool updateCache = false}) async {
    // Не показываем индикатор загрузки если есть кешированные данные
    if (_serverNotifications.isEmpty && mounted) {
      setState(() {
        _isLoading = true;
      });
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

        // Сохраняем в кеш если нужно
        if (updateCache) {
          await NotificationCacheService.saveNotifications(
            notificationsResponse.content,
          );
        }

        // Запускаем анимацию только если это первая загрузка
        if (!updateCache) {
          _animationController.reset();
          _animationController.forward();
        }
      }
    } catch (e) {
      print('Error loading server notifications: $e');
      if (mounted) {
        setState(() {
          _isLoading = false;
          _isRefreshing = false;
        });
      }

      // Если ошибка, пробуем загрузить из кеша
      if (_serverNotifications.isEmpty) {
        await _loadFromCache();
      }
    }
  }

  Future<void> _loadNotifications() async {
    try {
      final notifications = await _notificationsPlugin
          .pendingNotificationRequests();

      if (mounted) {
        setState(() {
          _notifications.clear();
          _notifications.addAll(notifications);
        });
      }
    } catch (e) {
      print('Error loading notifications: $e');
    }
  }

  // Метод для pull-to-refresh
  Future<void> _onRefresh() async {
    setState(() {
      _isRefreshing = true;
    });

    await Future.wait([
      _loadNotifications(),
      _loadServerNotifications(updateCache: true),
    ]);
  }

  Future<void> _clearAllNotifications() async {
    try {
      await _notificationsPlugin.cancelAll();
      await NotificationCacheService.clearCache();  // Очищаем кеш тоже
      await _loadNotifications();

      setState(() {
        _serverNotifications.clear();
      });
    } catch (_) {}
  }

  Future<void> _cancelNotification(int id) async {
    try {
      await _notificationsPlugin.cancel(id);
      await _loadNotifications();
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

    // Размеры для адаптивности
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
          if (_notifications.isNotEmpty || _serverNotifications.isNotEmpty)
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
              : (_notifications.isEmpty && _serverNotifications.isEmpty)
              ? _buildEmptyState(
              theme, l10n, iconSizeLarge,
              isSmallScreen, fontSize, fontSizeSmall
          )
              : _buildNotificationList(
              theme, l10n, isSmallScreen, padding,
              verticalPadding, iconSizeMedium,
              fontSize, fontSizeSmall
          ),
        ),
      ),
      floatingActionButton:
      (_notifications.isNotEmpty || _serverNotifications.isNotEmpty)
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

  // Вынесем построение пустого состояния в отдельный метод
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

  // Вынесем построение списка уведомлений
  Widget _buildNotificationList(
      ThemeData theme,
      dynamic l10n,
      bool isSmallScreen,
      double padding,
      double verticalPadding,
      double iconSizeMedium,
      double fontSize,
      double fontSizeSmall,
      ) {
    return ListView.builder(
      key: const ValueKey('list'),
      itemCount: _serverNotifications.length + _notifications.length,
      padding: EdgeInsets.all(padding),
      cacheExtent: 800,
      itemBuilder: (context, index) {
        if (index < _serverNotifications.length) {
          return _buildServerNotificationTile(
            index,
            theme,
            isSmallScreen,
            verticalPadding,
            iconSizeMedium,
            fontSize,
            fontSizeSmall,
          );
        }

        final localIndex = index - _serverNotifications.length;
        return _buildLocalNotificationTile(
          index,
          localIndex,
          theme,
          l10n,
          isSmallScreen,
          verticalPadding,
          iconSizeMedium,
          fontSize,
          fontSizeSmall,
        );
      },
    );
  }

  // Метод для серверных уведомлений
  Widget _buildServerNotificationTile(
      int index,
      ThemeData theme,
      bool isSmallScreen,
      double verticalPadding,
      double iconSizeMedium,
      double fontSize,
      double fontSizeSmall,
      ) {
    final serverNotif = _serverNotifications[index];
    final isNew = DateTime.now().difference(serverNotif.createdAt).inMinutes < 5;

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
                      child: Text(
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

  // Метод для локальных уведомлений
  Widget _buildLocalNotificationTile(
      int index,
      int localIndex,
      ThemeData theme,
      dynamic l10n,
      bool isSmallScreen,
      double verticalPadding,
      double iconSizeMedium,
      double fontSize,
      double fontSizeSmall,
      ) {
    final notification = _notifications[localIndex];

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
                  style: TextStyle(fontSize: fontSizeSmall),
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