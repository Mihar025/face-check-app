import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../../api_client/api/authentication_api.dart';
import '../../api_client/model/authentication_request.dart';
import '../../services/ApiService.dart';

class LoginScreen extends StatefulWidget {
  final AuthenticationApi authApi;

  const LoginScreen({
    super.key,
    required this.authApi,
  });

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _checkAuthentication();
  }

  Future<void> _checkAuthentication() async {
    if (await ApiService.instance.isAuthenticated()) {
      if (!mounted) return;
      Navigator.of(context).pushReplacementNamed('/main');
    }
  }

  Future<void> _handleLogin() async {
    if (_emailController.text.isEmpty || _passwordController.text.isEmpty) {
      _showError('Please fill all fields');
      return;
    }

    setState(() => _isLoading = true);

    try {
      final response = await widget.authApi.authenticate(
        authenticationRequest: AuthenticationRequest((b) => b
          ..email = _emailController.text
          ..password = _passwordController.text),
      );

      if (response.data != null && response.data!.token != null) {
        await ApiService.instance.setAuthToken(
          response.data!.token!,
          response.data!.refreshToken ?? '',
        );

        if (!mounted) return;

        _emailController.clear();
        _passwordController.clear();

        _showSuccess('Authentication successful');

        await Future.delayed(const Duration(milliseconds: 100));

        if(!mounted) return;
        Navigator.of(context).pushReplacementNamed('/main');
      } else {
        _showError('Authentication failed: Token is missing');
      }
    } on DioException catch (e) {
      String errorMessage = 'Authentication failed';

      if (e.response?.statusCode == 401) {
        errorMessage = 'Invalid email or password';
      } else if (e.response?.statusCode == 400) {
        errorMessage = 'Invalid input data';
      } else if (e.type == DioExceptionType.connectionTimeout) {
        errorMessage = 'Connection timeout. Please try again';
      } else if (e.type == DioExceptionType.connectionError) {
        errorMessage = 'No internet connection';
      }

      _showError(errorMessage);
    } catch (e) {
      _showError('Unexpected error: ${e.toString()}');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
      ),
    );
  }

  void _showSuccess(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.green,
      ),
    );
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
        title: const Text(
          '',
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
            fontSize: 24,
          ),
        ),
      ),
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/facecheck.jpg'),
            fit: BoxFit.cover,
          ),
        ),
        child: Stack(
          children: [
            SingleChildScrollView(
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    // Адаптивная высота вместо фиксированной
                    SizedBox(height: screenSize.height * 0.35),

                    // Контейнер для полей ввода
                    Container(
                      // Адаптивная ширина вместо фиксированной
                      width: screenSize.width * 0.9,
                      constraints: const BoxConstraints(
                        maxWidth: 380, // Максимальная ширина для больших экранов
                      ),
                      padding: EdgeInsets.all(isSmallScreen ? 15 : 20),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Column(
                        children: [
                          _buildInputField(
                            label: 'Email',
                            hint: 'Enter your email',
                            controller: _emailController,
                            isSmallScreen: isSmallScreen,
                          ),
                          _buildInputField(
                            label: 'Password',
                            hint: 'Enter secure password',
                            controller: _passwordController,
                            isPassword: true,
                            isSmallScreen: isSmallScreen,
                          ),
                        ],
                      ),
                    ),

                    SizedBox(height: screenSize.height * 0.02),
                    _buildLoginButton(screenSize, isSmallScreen),
                    SizedBox(height: screenSize.height * 0.02),
                    _buildAdditionalLinks(isSmallScreen),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInputField({
    required String label,
    required String hint,
    required TextEditingController controller,
    bool isPassword = false,
    bool isSmallScreen = false,
  }) {
    return Padding(
      padding: EdgeInsets.symmetric(
        horizontal: isSmallScreen ? 10 : 15,
        vertical: isSmallScreen ? 5 : 7.5,
      ),
      child: TextField(
        controller: controller,
        obscureText: isPassword,
        keyboardType: isPassword ? TextInputType.visiblePassword : TextInputType.emailAddress,
        style: const TextStyle(color: Colors.black),
        decoration: InputDecoration(
          border: const OutlineInputBorder(),
          labelText: label,
          hintText: hint,
          // Адаптируем размер текста для маленьких экранов
          labelStyle: TextStyle(
            fontSize: isSmallScreen ? 14 : 16,
          ),
          hintStyle: TextStyle(
            fontSize: isSmallScreen ? 12 : 14,
          ),
          contentPadding: EdgeInsets.symmetric(
            horizontal: 12,
            vertical: isSmallScreen ? 12 : 16,
          ),
          filled: true,
          fillColor: Colors.white,
        ),
      ),
    );
  }

  Widget _buildLoginButton(Size screenSize, bool isSmallScreen) {
    return Container(
      // Адаптивная высота и ширина
      height: isSmallScreen ? 55 : 65,
      width: screenSize.width * 0.85,
      constraints: const BoxConstraints(
        maxWidth: 360, // Максимальная ширина для больших экранов
      ),
      child: Padding(
        padding: EdgeInsets.only(top: isSmallScreen ? 15.0 : 20.0),
        child: ElevatedButton(
          onPressed: _isLoading ? null : _handleLogin,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.white,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
          ),
          child: _isLoading
              ? const CircularProgressIndicator(color: Colors.black)
              : Text(
            'Login',
            style: TextStyle(
              color: Colors.black,
              fontSize: isSmallScreen ? 18 : 20,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildAdditionalLinks(bool isSmallScreen) {
    return Column(
      children: [
        InkWell(
          onTap: () {
            print('Navigate to forgot password');
          },
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              'Forgot Password',
              style: TextStyle(
                  color: Colors.white,
                  fontSize: isSmallScreen ? 12 : 14
              ),
            ),
          ),
        ),
        InkWell(
          onTap: () {
            print('Navigate to forgot email');
          },
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              'Forgot an email',
              style: TextStyle(
                  color: Colors.white,
                  fontSize: isSmallScreen ? 12 : 14
              ),
            ),
          ),
        ),
      ],
    );
  }
}