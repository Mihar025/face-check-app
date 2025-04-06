import 'package:flutter/material.dart';

class PunchSuccessDialog extends StatelessWidget {
  final bool isPunchIn;
  final String time;
  final VoidCallback onOk;

  const PunchSuccessDialog({
    Key? key,
    required this.isPunchIn,
    required this.time,
    required this.onOk,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isSmallScreen = MediaQuery.of(context).size.width < 360;

    return AlertDialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      backgroundColor: theme.dialogBackgroundColor,
      title: Row(
        children: [
          Icon(
            isPunchIn ? Icons.login : Icons.logout,
            color: isPunchIn ? Colors.green : Colors.blue,
            size: 24,
          ),
          const SizedBox(width: 10),
          Text(
            isPunchIn ? 'Punch In Success' : 'Punch Out Success',
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: isSmallScreen ? 18 : 20,
              color: theme.textTheme.titleLarge?.color,
            ),
          ),
        ],
      ),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            isPunchIn
                ? 'You have successfully punched in at:'
                : 'You have successfully punched out at:',
            style: TextStyle(
              fontSize: isSmallScreen ? 14 : 16,
              color: theme.textTheme.bodyMedium?.color,
            ),
          ),
          const SizedBox(height: 15),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(vertical: 15, horizontal: 20),
            decoration: BoxDecoration(
              color: (isPunchIn ? Colors.green : Colors.blue).withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: (isPunchIn ? Colors.green : Colors.blue).withOpacity(0.3),
              ),
            ),
            child: Center(
              child: Text(
                time,
                style: TextStyle(
                  fontSize: isSmallScreen ? 20 : 24,
                  fontWeight: FontWeight.bold,
                  color: isPunchIn ? Colors.green : Colors.blue,
                ),
              ),
            ),
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: onOk,
          style: TextButton.styleFrom(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
            backgroundColor: (isPunchIn ? Colors.green : Colors.blue).withOpacity(0.1),
          ),
          child: Text(
            'OK',
            style: TextStyle(
              fontWeight: FontWeight.bold,
              color: isPunchIn ? Colors.green : Colors.blue,
              fontSize: isSmallScreen ? 14 : 16,
            ),
          ),
        ),
      ],
    );
  }
}