import 'package:flutter/material.dart';

class FaceCheckButton extends StatelessWidget {
  final VoidCallback onPressed;
  final bool isSmallScreen;

  const FaceCheckButton({
    required this.onPressed,
    this.isSmallScreen = false,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final buttonSize = isSmallScreen ? 60.0 : 70.0;
    final iconSize = isSmallScreen ? 40.0 : 50.0;
    final borderRadius = isSmallScreen ? 6.0 : 8.0;

    return Container(
      decoration: BoxDecoration(
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            spreadRadius: isSmallScreen ? 1 : 2,
            blurRadius: isSmallScreen ? 4 : 5,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      width: buttonSize,
      height: buttonSize,
      child: FloatingActionButton(
        elevation: 0,
        onPressed: onPressed,
        backgroundColor: Colors.blue,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(borderRadius),
        ),
        child: Container(
          width: buttonSize,
          height: buttonSize,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(borderRadius),
            color: Colors.blue,
          ),
          child: Icon(
            Icons.fingerprint,
            size: iconSize,
            color: Colors.white,
          ),
        ),
      ),
    );
  }
}