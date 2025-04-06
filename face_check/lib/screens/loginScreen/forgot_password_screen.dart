import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

import '../../services/ApiService.dart';

class ForgotPasswordScreen extends StatefulWidget {
  @override
  _ForgotPasswordScreenState createState() => _ForgotPasswordScreenState();
}

class _ForgotPasswordScreenState extends State<ForgotPasswordScreen> {
  final _emailController = TextEditingController();
  final _codeController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();

  bool _isLoading = false;
  int _currentStep = 0; // 0 - email, 1 - code, 2 - new password
  String? _email;
  String? _verificationCode;

  Widget _buildInputField({
    required String label,
    required String hint,
    required TextEditingController controller,
    bool isPassword = false,
    TextInputType? keyboardType,
    int? maxLength,
    bool isSmallScreen = false,
  }) {
    return TextField(
      controller: controller,
      obscureText: isPassword,
      maxLength: maxLength,
      keyboardType: keyboardType ?? TextInputType.text,
      style: TextStyle(
        color: Colors.black,
        fontSize: isSmallScreen ? 14 : 16,
        fontWeight: FontWeight.w500,
      ),
      decoration: InputDecoration(
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: const BorderSide(
            color: Colors.black,
            width: 1.5,
          ),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: const BorderSide(
            color: Colors.black,
            width: 1.5,
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: const BorderSide(
            color: Colors.black,
            width: 2,
          ),
        ),
        labelText: label,
        labelStyle: TextStyle(
          fontSize: isSmallScreen ? 13 : 15,
          fontWeight: FontWeight.w500,
          color: Colors.black87,
        ),
        hintText: hint,
        hintStyle: TextStyle(
          fontSize: isSmallScreen ? 12 : 14,
          fontWeight: FontWeight.normal,
          color: Colors.black54,
        ),
        filled: true,
        fillColor: Colors.white,
        contentPadding: EdgeInsets.symmetric(
          horizontal: 12,
          vertical: isSmallScreen ? 12 : 16,
        ),
        counterText: "",
      ),
    );
  }

  void _showMessage(String message, {bool isError = false}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(
          message,
          style: const TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.w500,
            letterSpacing: 0.25,
          ),
        ),
        backgroundColor: isError ? Colors.red : Colors.green,
        duration: Duration(milliseconds: isError ? 4000 : 300),
      ),
    );
  }

  Future<void> _handleEmailSubmit() async {
    if (_emailController.text.isEmpty) {
      _showMessage('Please enter your email', isError: true);
      return;
    }

    setState(() => _isLoading = true);

    try {
      await ApiService.instance.sendEmail(_emailController.text);
      setState(() {
        _email = _emailController.text;
        _currentStep = 1;
      });
      _showMessage('Verification code sent to your email');
    } catch (e) {
      _showMessage(e.toString(), isError: true);
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _handleCodeVerification() async {
    if (_codeController.text.isEmpty) {
      _showMessage('Please enter verification code', isError: true);
      return;
    }

    setState(() => _isLoading = true);

    try {
      await ApiService.instance.verifyCode(_email!, _codeController.text);
      setState(() {
        _currentStep = 2;
        _verificationCode = _codeController.text;
      });
      _showMessage('Code verified successfully');
    } catch (e) {
      _showMessage(e.toString(), isError: true);
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _handlePasswordReset() async {
    if (_passwordController.text.isEmpty || _confirmPasswordController.text.isEmpty) {
      _showMessage('Please fill all fields', isError: true);
      return;
    }

    if (_passwordController.text != _confirmPasswordController.text) {
      _showMessage('Passwords do not match', isError: true);
      return;
    }

    setState(() => _isLoading = true);

    try {
      await ApiService.instance.resetPassword(
          _email!,
          _passwordController.text,
          _confirmPasswordController.text,
          _verificationCode!
      );

      _showMessage('Password successfully reset');
      await Future.delayed(const Duration(milliseconds: 1000));

      if (mounted && context.mounted) {
        Navigator.of(context).pop();
      }
    } catch (e) {
      _showMessage(e.toString(), isError: true);
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    // Получаем размеры экрана
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Scaffold(
      resizeToAvoidBottomInset: true,
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/facecheck2.jpg'),
            fit: BoxFit.cover,
          ),
        ),
        child: Center(
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Адаптивная высота вместо фиксированной
                SizedBox(height: screenSize.height * 0.25),

                // Используем ConstrainedBox для установки максимальной ширины
                ConstrainedBox(
                  constraints: const BoxConstraints(maxWidth: 400),
                  child: Container(
                    // Адаптивная ширина вместо фиксированной
                    width: screenSize.width * 0.9,
                    padding: EdgeInsets.symmetric(
                      horizontal: isSmallScreen ? 15 : 20,
                      vertical: isSmallScreen ? 30 : 40,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        const Text(
                          'Change Password',
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 18,
                            fontWeight: FontWeight.w600,
                            letterSpacing: 0.5,
                          ),
                        ),
                        SizedBox(height: isSmallScreen ? 40 : 60),

                        if (_currentStep == 0) ...[
                          // Используем ConstrainedBox для полей ввода
                          ConstrainedBox(
                            constraints: const BoxConstraints(maxWidth: 330),
                            child: SizedBox(
                              width: screenSize.width * 0.8,
                              child: _buildInputField(
                                label: 'Email',
                                hint: 'Enter your email',
                                controller: _emailController,
                                keyboardType: TextInputType.emailAddress,
                                isSmallScreen: isSmallScreen,
                              ),
                            ),
                          ),
                        ],

                        if (_currentStep == 1) ...[
                          ConstrainedBox(
                            constraints: const BoxConstraints(maxWidth: 300),
                            child: SizedBox(
                              width: screenSize.width * 0.75,
                              child: _buildInputField(
                                label: 'Verification Code',
                                hint: 'Enter 6-digit code',
                                controller: _codeController,
                                keyboardType: TextInputType.number,
                                maxLength: 6,
                                isSmallScreen: isSmallScreen,
                              ),
                            ),
                          ),
                        ],

                        if (_currentStep == 2) ...[
                          ConstrainedBox(
                            constraints: const BoxConstraints(maxWidth: 300),
                            child: SizedBox(
                              width: screenSize.width * 0.75,
                              child: _buildInputField(
                                label: 'New Password',
                                hint: 'Enter new password',
                                controller: _passwordController,
                                isPassword: true,
                                isSmallScreen: isSmallScreen,
                              ),
                            ),
                          ),
                          SizedBox(height: isSmallScreen ? 20 : 35),
                          ConstrainedBox(
                            constraints: const BoxConstraints(maxWidth: 300),
                            child: SizedBox(
                              width: screenSize.width * 0.75,
                              child: _buildInputField(
                                label: 'Confirm Password',
                                hint: 'Confirm new password',
                                controller: _confirmPasswordController,
                                isPassword: true,
                                isSmallScreen: isSmallScreen,
                              ),
                            ),
                          ),
                        ],

                        SizedBox(height: isSmallScreen ? 40 : 68),

                        // Кнопка с ограничением ширины
                        ConstrainedBox(
                          constraints: const BoxConstraints(maxWidth: 330),
                          child: SizedBox(
                            width: screenSize.width * 0.8,
                            height: 47,
                            child: ElevatedButton(
                              onPressed: _isLoading
                                  ? null
                                  : _currentStep == 0
                                  ? _handleEmailSubmit
                                  : _currentStep == 1
                                  ? _handleCodeVerification
                                  : _handlePasswordReset,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.blue,
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(8),
                                ),
                              ),
                              child: _isLoading
                                  ? const CircularProgressIndicator(color: Colors.white)
                                  : Text(
                                _currentStep == 0
                                    ? 'Send Code'
                                    : _currentStep == 1
                                    ? 'Verify Code'
                                    : 'Reset Password',
                                style: TextStyle(
                                  color: Colors.black,
                                  fontSize: isSmallScreen ? 14 : 16,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _emailController.dispose();
    _codeController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }
}