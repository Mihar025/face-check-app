import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:local_auth/local_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../api_client/api/authentication_api.dart';
import '../../api_client/model/authentication_request.dart';
import '../../services/jwt_service.dart';
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
  final LocalAuthentication _localAuth = LocalAuthentication();
  bool _isLoading = false;
  bool _isFastLogin = false;
  String? _savedUserName;

  @override
  void initState() {
    super.initState();
    _loadSavedCredentials();
  }

  Future<void> _loadSavedCredentials() async {
    final prefs = await SharedPreferences.getInstance();
    final savedEmail = prefs.getString('saved_email');
    final savedName = prefs.getString('user_name');

    if (savedEmail != null) {
      setState(() {
        _emailController.text = savedEmail;
        _savedUserName = savedName;
        _isFastLogin = true;
      });
    }
  }

  Future<void> _saveCredentials(String email) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('saved_email', email);
    final displayName = email.split('@')[0];
    await prefs.setString('user_name', displayName);
  }

  Future<void> _authenticateWithBiometrics() async {
    try {
      final canCheckBiometrics = await _localAuth.canCheckBiometrics;
      final isDeviceSupported = await _localAuth.isDeviceSupported();

      if (!canCheckBiometrics || !isDeviceSupported) {
        _showError('Biometric authentication is not available');
        return;
      }

      final didAuthenticate = await _localAuth.authenticate(
        localizedReason: 'Please authenticate to login',
        options: const AuthenticationOptions(
          stickyAuth: true,
          biometricOnly: true,
        ),
      );

      if (didAuthenticate) {
        await _handleLogin(useSavedPassword: true);
      }
    } catch (e) {
      _showError('Biometric authentication failed');
    }
  }

  Future<void> _handleLogin({bool useSavedPassword = false}) async {
    if (_emailController.text.isEmpty ||
        (!useSavedPassword && _passwordController.text.isEmpty)) {
      _showError('Please fill all fields');
      return;
    }

    setState(() => _isLoading = true);

    try {
      // Изменяем формат отправки данных
      final response = await widget.authApi.authenticate(
        authenticationRequest: AuthenticationRequest((b) => b
          ..email = _emailController.text.trim() // добавляем trim()
          ..password = _passwordController.text.trim() // добавляем trim()
        ),
      );

      // Добавляем проверку на null и логирование
      print('Response: ${response.data}');

      if (response.data?.token == null) {
        throw DioException(
          requestOptions: RequestOptions(path: ''),
          error: 'Token is missing from response',
        );
      }

      await JwtService.decodeAndSaveRole(response.data!.token!);
      await _saveCredentials(_emailController.text);

      // Проверяем refreshToken
      final refreshToken = response.data?.refreshToken ?? '';

      await ApiService.instance.setAuthToken(
        response.data!.token!,
        refreshToken,
      );

      if (!mounted) return;

      _passwordController.clear();
      _showSuccess('Authentication successful');

      await Future.delayed(const Duration(milliseconds: 300));

      if(!mounted) return;
      Navigator.of(context).pushReplacementNamed('/main');

    } on DioException catch (e) {
      // Добавляем подробное логирование ошибки
      print('DioError details:');
      print('Status code: ${e.response?.statusCode}');
      print('Response data: ${e.response?.data}');
      print('Error type: ${e.type}');
      print('Error message: ${e.message}');

      String errorMessage = 'Authentication failed';

      if (e.response?.statusCode == 500) {
        errorMessage = 'Server error, please try again';
      } else if (e.response?.statusCode == 401) {
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
      print('Unexpected error: $e');
      _showError('Unexpected error: ${e.toString()}');
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }
  void _showError(String message) {
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
        backgroundColor: Colors.red,
      ),
    );
  }

  void _showSuccess(String message) {
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
        duration: const Duration(milliseconds: 300),
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
                SizedBox(height: screenSize.height * 0.25), // Относительная высота вместо фиксированной
                Container(
                  width: screenSize.width * 0.9, // 90% ширины экрана
                  constraints: BoxConstraints(
                    maxWidth: 400, // Максимальная ширина для больших экранов
                  ),
                  padding: EdgeInsets.symmetric(
                    horizontal: screenSize.width * 0.05,
                    vertical: screenSize.height * 0.04,
                  ),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      if (_isFastLogin) ...[
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text(
                                    'Welcome,',
                                    style: TextStyle(
                                      color: Colors.black,
                                      fontSize: 14,
                                      fontWeight: FontWeight.w600,
                                      letterSpacing: 0.5,
                                    ),
                                  ),
                                  const SizedBox(height: 4),
                                  Text(
                                    _savedUserName ?? "User",
                                    overflow: TextOverflow.ellipsis, // Добавлено
                                    style: const TextStyle(
                                      color: Colors.black87,
                                      fontSize: 12,
                                      fontWeight: FontWeight.w500,
                                      letterSpacing: 0.3,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            TextButton(
                              onPressed: () async {
                                final prefs = await SharedPreferences.getInstance();
                                await prefs.remove('saved_email');
                                await prefs.remove('user_name');
                                setState(() {
                                  _isFastLogin = false;
                                  _emailController.clear();
                                  _passwordController.clear();
                                  _savedUserName = null;
                                });
                              },
                              child: const Text(
                                'Not you?',
                                style: TextStyle(
                                  color: Colors.blue,
                                  fontSize: 14,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            ),
                          ],
                        ),
                        SizedBox(height: screenSize.height * 0.02),
                      ],
                      if (!_isFastLogin)
                        _buildInputField(
                          label: 'Email',
                          hint: 'Enter your email',
                          controller: _emailController,
                        ),
                      SizedBox(height: isSmallScreen ? 20 : 35),
                      _buildInputField(
                        label: 'Password',
                        hint: 'Enter secure password',
                        controller: _passwordController,
                        isPassword: true,
                      ),
                      SizedBox(height: isSmallScreen ? 20 : 35),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          TextButton(
                            onPressed: () {
                              Navigator.of(context).pushNamed('forgot-password');
                            },
                            child: Text(
                              'Forgot Password?',
                              style: TextStyle(
                                color: Colors.blue,
                                fontSize: isSmallScreen ? 12 : 14,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                          ),
                          IconButton(
                            onPressed: _authenticateWithBiometrics,
                            icon: Icon(
                              Icons.fingerprint,
                              size: isSmallScreen ? 30 : 40,
                              color: Colors.black,
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: screenSize.height * 0.03),
                      SizedBox(
                        width: double.infinity, // На всю ширину контейнера
                        height: 47,
                        child: ElevatedButton(
                          onPressed: _isLoading ? null : () => _handleLogin(),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.blue,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(8),
                            ),
                          ),
                          child: _isLoading
                              ? const CircularProgressIndicator(color: Colors.white)
                              : const Text(
                            'Login',
                            style: TextStyle(
                              color: Colors.black,
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
  Widget _buildInputField({
    required String label,
    required String hint,
    required TextEditingController controller,
    bool isPassword = false,
  }) {
    return TextField(
      controller: controller,
      obscureText: isPassword,
      keyboardType: isPassword ? TextInputType.visiblePassword : TextInputType.emailAddress,
      style: const TextStyle(
        color: Colors.black,
        fontSize: 16,
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
        labelStyle: const TextStyle(
          fontSize: 15,
          fontWeight: FontWeight.w500,
          color: Colors.black87,
        ),
        hintText: hint,
        hintStyle: const TextStyle(
          fontSize: 14,
          fontWeight: FontWeight.normal,
          color: Colors.black54,
        ),
        filled: true,
        fillColor: Colors.white,
      ),
    );
  }
}