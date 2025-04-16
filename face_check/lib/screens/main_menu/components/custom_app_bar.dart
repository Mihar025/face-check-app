import 'package:flutter/material.dart';

class CustomAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String currentDate;
  final String currentTime;
  final VoidCallback onMenuPressed;

  const CustomAppBar({
    super.key,
    required this.currentDate,
    required this.currentTime,
    required this.onMenuPressed,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return AppBar(
      backgroundColor: theme.scaffoldBackgroundColor,
      elevation: 0,
      centerTitle: false,
      automaticallyImplyLeading: false,
      title: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Main Menu',
            style: TextStyle(
              color: theme.textTheme.titleLarge?.color,
              fontSize: isSmallScreen ? 18 : 20,
            ),
          ),
          Text(
            '$currentDate $currentTime',
            style: TextStyle(
              fontSize: isSmallScreen ? 12 : 14,
              fontWeight: FontWeight.normal,
              color: theme.textTheme.bodyMedium?.color,
            ),
          ),
        ],
      ),
      leading: IconButton(
        icon: Icon(
          Icons.menu,
          color: theme.iconTheme.color,
          size: isSmallScreen ? 22 : 24,
        ),
        onPressed: onMenuPressed,
      ),
      actions: [
        IconButton(
          icon: Icon(
            Icons.notifications,
            color: theme.iconTheme.color,
            size: isSmallScreen ? 22 : 24,
          ),
          onPressed: () {
            Navigator.pushNamed(context, '/notifications');
          },
          padding: EdgeInsets.symmetric(
              horizontal: isSmallScreen ? 12 : 16
          ),
        ),
      ],
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}