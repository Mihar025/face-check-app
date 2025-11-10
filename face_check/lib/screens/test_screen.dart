import 'package:flutter/material.dart';

class TestScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.green,
      body: Center(
        child: Text(
          "APP WORKS!",
          style: TextStyle(fontSize: 50, color: Colors.white),
        ),
      ),
    );
  }
}