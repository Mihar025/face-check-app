import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

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
  // Controllers
  late final TextEditingController _emailController;
  late final TextEditingController _passwordController;

  // ValueNotifiers для локальных обновлений (избегаем лишних rebuild)
  late final ValueNotifier<bool> _isLoading;

  // Кэшированные значения MediaQuery (чтобы не вызывать постоянно)
  late Size _screenSize;
  late double _screenWidth;
  late double _screenHeight;
  late bool _isSmallScreen;
  late double _keyboardHeight;

  // Константы для производительности
  static const double _smallScreenThreshold = 360.0;
  static const double _maxFormWidth = 380.0;
  static const double _maxButtonWidth = 360.0;

  // Предопределенные стили (создаются один раз)
  static const TextStyle _blackTextStyle = TextStyle(color: Colors.black);
  static const TextStyle _whiteTextStyle = TextStyle(color: Colors.white);
  static const TextStyle _errorSnackBarStyle = TextStyle(
    fontSize: 14,
    fontWeight: FontWeight.w500,
  );
  static const TextStyle _titleStyle = TextStyle(
    color: Colors.white,
    fontWeight: FontWeight.bold,
    fontSize: 24,
  );

  // Константы для UI
  static const EdgeInsets _snackBarMargin = EdgeInsets.all(12);
  static const Duration _successDuration = Duration(milliseconds: 500);
  static const Duration _errorDuration = Duration(seconds: 3);
  static const Duration _navigationDelay = Duration(milliseconds: 100);

  @override
  void initState() {
    super.initState();

    // Инициализация контроллеров
    _emailController = TextEditingController();
    _passwordController = TextEditingController();

    // Инициализация ValueNotifier
    _isLoading = ValueNotifier<bool>(false);

    // Предзагрузка изображения для плавной работы
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        precacheImage(const AssetImage('assets/images/facecheck.jpg'), context);
      }
    });

    // Проверка аутентификации
    _checkAuthentication();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateScreenMetrics();
  }

  // Кэшируем метрики экрана чтобы не вызывать MediaQuery постоянно
  void _updateScreenMetrics() {
    final mediaQuery = MediaQuery.of(context);
    _screenSize = mediaQuery.size;
    _screenWidth = _screenSize.width;
    _screenHeight = _screenSize.height;
    _isSmallScreen = _screenWidth < _smallScreenThreshold;
    _keyboardHeight = mediaQuery.viewInsets.bottom;
  }

  @override
  void dispose() {
    // Очищаем все ресурсы
    _emailController.dispose();
    _passwordController.dispose();
    _isLoading.dispose();
    super.dispose();
  }

  Future<void> _checkAuthentication() async {
    if (await ApiService.instance.isAuthenticated()) {
      if (!mounted) return;
      Navigator.of(context).pushReplacementNamed('/main');
    }
  }

  Future<void> _handleLogin() async {
    final email = _emailController.text.trim();
    final password = _passwordController.text.trim();

    if (email.isEmpty || password.isEmpty) {
      _showError('Please fill all fields');
      return;
    }

    _isLoading.value = true;

    try {
      final response = await widget.authApi.authenticate(
        authenticationRequest: AuthenticationRequest((b) => b
          ..email = email
          ..password = password),
      );

      if (response.data?.token == null) {
        _showError('Authentication failed: Token is missing');
        return;
      }

      await ApiService.instance.setAuthToken(
        response.data!.token!,
        response.data!.refreshToken ?? '',
      );

      if (!mounted) return;

      _emailController.clear();
      _passwordController.clear();

      _showSuccess('Authentication successful');

      await Future.delayed(_navigationDelay);

      if (!mounted) return;
      Navigator.of(context).pushReplacementNamed('/main');

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
      if (mounted) {
        _isLoading.value = false;
      }
    }
  }

  void _showError(String message) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message, style: _errorSnackBarStyle),
        backgroundColor: Colors.red,
        behavior: SnackBarBehavior.floating,
        margin: _snackBarMargin,
        duration: _errorDuration,
      ),
    );
  }

  void _showSuccess(String message) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message, style: _errorSnackBarStyle),
        backgroundColor: Colors.green,
        behavior: SnackBarBehavior.floating,
        margin: _snackBarMargin,
        duration: _successDuration,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    _updateScreenMetrics();

    return Scaffold(
      resizeToAvoidBottomInset: true,
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text('', style: _titleStyle),
      ),
      body: Stack(
        fit: StackFit.expand,
        children: [
          // Фоновое изображение с RepaintBoundary для изоляции перерисовок
          const RepaintBoundary(
            child: _BackgroundImage(),
          ),

          // Основной контент
          LayoutBuilder(
            builder: (context, constraints) {
              return SingleChildScrollView(
                physics: const ClampingScrollPhysics(),
                child: ConstrainedBox(
                  constraints: BoxConstraints(
                    minHeight: constraints.maxHeight,
                  ),
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        // Адаптивная высота
                        SizedBox(height: _screenHeight * 0.35),

                        // Форма логина
                        _buildLoginForm(),

                        SizedBox(height: _screenHeight * 0.02),

                        // Кнопка логина
                        ValueListenableBuilder<bool>(
                          valueListenable: _isLoading,
                          builder: (context, isLoading, child) {
                            return _LoginButton(
                              isLoading: isLoading,
                              onPressed: isLoading ? null : _handleLogin,
                              screenWidth: _screenWidth,
                              isSmallScreen: _isSmallScreen,
                            );
                          },
                        ),

                        SizedBox(height: _screenHeight * 0.02),

                        // Дополнительные ссылки
                        _AdditionalLinks(isSmallScreen: _isSmallScreen),
                      ],
                    ),
                  ),
                ),
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildLoginForm() {
    return Container(
      width: _screenWidth * 0.9,
      constraints: const BoxConstraints(maxWidth: _maxFormWidth),
      padding: EdgeInsets.all(_isSmallScreen ? 15 : 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        children: [
          _InputField(
            label: 'Email',
            hint: 'Enter your email',
            controller: _emailController,
            isPassword: false,
            isSmallScreen: _isSmallScreen,
            keyboardType: TextInputType.emailAddress,
            textInputAction: TextInputAction.next,
          ),
          _InputField(
            label: 'Password',
            hint: 'Enter secure password',
            controller: _passwordController,
            isPassword: true,
            isSmallScreen: _isSmallScreen,
            keyboardType: TextInputType.visiblePassword,
            textInputAction: TextInputAction.done,
            onSubmitted: (_) => _handleLogin(),
          ),
        ],
      ),
    );
  }
}

// ============= ОТДЕЛЬНЫЕ ОПТИМИЗИРОВАННЫЕ ВИДЖЕТЫ =============

// Фоновое изображение (изолировано от перерисовок)
class _BackgroundImage extends StatelessWidget {
  const _BackgroundImage();

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        image: DecorationImage(
          image: AssetImage('assets/images/facecheck.jpg'),
          fit: BoxFit.cover,
        ),
      ),
    );
  }
}

// Оптимизированное поле ввода
class _InputField extends StatelessWidget {
  final String label;
  final String hint;
  final TextEditingController controller;
  final bool isPassword;
  final bool isSmallScreen;
  final TextInputType? keyboardType;
  final TextInputAction? textInputAction;
  final Function(String)? onSubmitted;

  const _InputField({
    required this.label,
    required this.hint,
    required this.controller,
    required this.isPassword,
    required this.isSmallScreen,
    this.keyboardType,
    this.textInputAction,
    this.onSubmitted,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.symmetric(
        horizontal: isSmallScreen ? 10 : 15,
        vertical: isSmallScreen ? 5 : 7.5,
      ),
      child: TextField(
        controller: controller,
        obscureText: isPassword,
        keyboardType: keyboardType ?? TextInputType.text,
        textInputAction: textInputAction,
        onSubmitted: onSubmitted,
        style: const TextStyle(color: Colors.black),
        decoration: InputDecoration(
          border: const OutlineInputBorder(),
          labelText: label,
          hintText: hint,
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
}

// Оптимизированная кнопка логина
class _LoginButton extends StatelessWidget {
  final bool isLoading;
  final VoidCallback? onPressed;
  final double screenWidth;
  final bool isSmallScreen;

  const _LoginButton({
    required this.isLoading,
    required this.onPressed,
    required this.screenWidth,
    required this.isSmallScreen,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: isSmallScreen ? 55 : 65,
      width: screenWidth * 0.85,
      constraints: const BoxConstraints(maxWidth: 360),
      padding: EdgeInsets.only(top: isSmallScreen ? 15.0 : 20.0),
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.white,
          foregroundColor: Colors.black,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
          ),
        ),
        child: AnimatedSwitcher(
          duration: const Duration(milliseconds: 200),
          child: isLoading
              ? const SizedBox(
            width: 24,
            height: 24,
            child: CircularProgressIndicator(
              color: Colors.black,
              strokeWidth: 2,
            ),
          )
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
}

// Оптимизированные дополнительные ссылки
class _AdditionalLinks extends StatelessWidget {
  final bool isSmallScreen;

  const _AdditionalLinks({required this.isSmallScreen});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        _LinkButton(
          text: 'Forgot Password',
          isSmallScreen: isSmallScreen,
          onTap: () {
            Navigator.of(context).pushNamed('/forgot-password');
          },
        ),
        _LinkButton(
          text: 'Forgot an email',
          isSmallScreen: isSmallScreen,
          onTap: () {
            Navigator.of(context).pushNamed('/forgot-email');
          },
        ),
      ],
    );
  }
}

// Отдельный виджет для ссылок
class _LinkButton extends StatelessWidget {
  final String text;
  final bool isSmallScreen;
  final VoidCallback onTap;

  const _LinkButton({
    required this.text,
    required this.isSmallScreen,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(8),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Text(
          text,
          style: TextStyle(
            color: Colors.white,
            fontSize: isSmallScreen ? 12 : 14,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    );
  }
}

// ============= ДОПОЛНИТЕЛЬНЫЕ HELPER КЛАССЫ =============

// Класс для адаптивных размеров (можно использовать во всем приложении)
class AdaptiveSize {
  static double fontSize(BuildContext context, {
    required double mobile,
    double? tablet,
    double? desktop,
  }) {
    final width = MediaQuery.of(context).size.width;
    if (width >= 1024) return desktop ?? tablet ?? mobile;
    if (width >= 600) return tablet ?? mobile;
    return mobile;
  }

  static EdgeInsets padding(BuildContext context, {
    required EdgeInsets mobile,
    EdgeInsets? tablet,
    EdgeInsets? desktop,
  }) {
    final width = MediaQuery.of(context).size.width;
    if (width >= 1024) return desktop ?? tablet ?? mobile;
    if (width >= 600) return tablet ?? mobile;
    return mobile;
  }

  static double spacing(BuildContext context, {
    required double mobile,
    double? tablet,
    double? desktop,
  }) {
    final width = MediaQuery.of(context).size.width;
    if (width >= 1024) return desktop ?? tablet ?? mobile;
    if (width >= 600) return tablet ?? mobile;
    return mobile;
  }
}