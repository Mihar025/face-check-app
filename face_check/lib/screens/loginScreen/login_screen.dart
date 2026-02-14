import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:local_auth/local_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../api_client/api/authentication_api.dart';
import '../../api_client/model/authentication_request.dart';
import '../../services/ApiService.dart';
import '../../services/fcm_service.dart';
import '../../services/jwt_service.dart';
import 'legal_screen.dart';

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
  late final GlobalKey<FormState> _formKey;
  late final TextEditingController _emailController;
  late final TextEditingController _passwordController;
  late final LocalAuthentication _localAuth;

  // ValueNotifiers для локальных обновлений (избегаем лишних rebuild)
  late final ValueNotifier<bool> _isLoading;
  late final ValueNotifier<bool> _obscurePassword;
  late final ValueNotifier<bool> _isFastLogin;
  late final ValueNotifier<String?> _savedUserName;

  // Кэшированные значения MediaQuery (чтобы не вызывать постоянно)
  late Size _screenSize;
  late double _screenWidth;
  late double _screenHeight;
  late double _keyboardHeight;
  late bool _isSmallScreen;
  late EdgeInsets _viewPadding;

  // Константы для UI (вынесены чтобы не создавать каждый раз)
  static const Color _sheetBg = Color(0xFFFDFDFC);
  static const Color _fieldBg = Color(0xFFFAFAFA);
  static const double _maxFormWidth = 440.0;
  static const double _smallScreenThreshold = 360.0;

  // Предварительно создаем стили чтобы не пересоздавать
  static const TextStyle _titleStyle = TextStyle(
    color: Colors.black87,
    fontSize: 26,
    fontWeight: FontWeight.w700,
    letterSpacing: 0.2,
  );

  static const TextStyle _subtitleStyle = TextStyle(
    color: Colors.black54,
    fontSize: 14,
  );

  static const TextStyle _errorStyle = TextStyle(
    fontSize: 14,
    fontWeight: FontWeight.w500,
    letterSpacing: 0.25,
  );

  @override
  void initState() {
    super.initState();

    // Инициализация контроллеров
    _formKey = GlobalKey<FormState>();
    _emailController = TextEditingController();
    _passwordController = TextEditingController();
    _localAuth = LocalAuthentication();

    // Инициализация ValueNotifiers
    _isLoading = ValueNotifier<bool>(false);
    _obscurePassword = ValueNotifier<bool>(true);
    _isFastLogin = ValueNotifier<bool>(false);
    _savedUserName = ValueNotifier<String?>(null);

    // Предзагрузка изображения для плавной работы
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        precacheImage(const AssetImage('assets/images/facecheck2.jpg'), context);
      }
    });

    // Загрузка сохраненных данных
    _loadSavedCredentials();
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
    _keyboardHeight = mediaQuery.viewInsets.bottom;
    _isSmallScreen = _screenWidth < _smallScreenThreshold;
    _viewPadding = mediaQuery.viewPadding;
  }

  @override
  void dispose() {
    // Очищаем все ресурсы
    _emailController.dispose();
    _passwordController.dispose();
    _isLoading.dispose();
    _obscurePassword.dispose();
    _isFastLogin.dispose();
    _savedUserName.dispose();
    super.dispose();
  }

  Future<void> _loadSavedCredentials() async {
    final prefs = await SharedPreferences.getInstance();
    final savedEmail = prefs.getString('saved_email');
    final savedName = prefs.getString('user_name');

    if (savedEmail != null && mounted) {
      _emailController.text = savedEmail;
      _savedUserName.value = savedName;
      _isFastLogin.value = true;
    }
  }

  Future<void> _saveCredentials(String email) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('saved_email', email);
    final displayName = email.split('@').first;
    await prefs.setString('user_name', displayName);
  }

  Future<void> _resetCredentials() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('saved_email');
    await prefs.remove('user_name');

    if (mounted) {
      _isFastLogin.value = false;
      _emailController.clear();
      _passwordController.clear();
      _savedUserName.value = null;
    }
  }



  Future<void> _handleLogin({bool useSavedPassword = false}) async {
    if (!(_formKey.currentState?.validate() ?? false)) {
      return;
    }

    _isLoading.value = true;

    try {
      final response = await widget.authApi.authenticate(
        authenticationRequest: AuthenticationRequest((b) => b
          ..email = _emailController.text.trim()
          ..password = _passwordController.text.trim()),
      );

      if (response.data?.token == null) {
        throw DioException(
          requestOptions: RequestOptions(path: ''),
          error: 'Token is missing from response',
        );
      }

      await JwtService.decodeAndSaveRole(response.data!.token!);
      await _saveCredentials(_emailController.text);

      final refreshToken = response.data?.refreshToken ?? '';
      await ApiService.instance.setAuthToken(response.data!.token!, refreshToken);

      await FcmService.instance.syncTokenToServer();

      if (!mounted) return;

      _passwordController.clear();
      _showSuccess('Authentication successful');

      await Future.delayed(const Duration(milliseconds: 300));


      if (!mounted) return;

      Navigator.of(context).pushReplacementNamed('/main');



    } on DioException catch (e) {
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
        content: Text(message, style: _errorStyle),
        backgroundColor: Colors.red,
        behavior: SnackBarBehavior.floating,
        margin: const EdgeInsets.all(12),
        duration: const Duration(seconds: 3),
      ),
    );
  }

  void _showSuccess(String message) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message, style: _errorStyle),
        duration: const Duration(milliseconds: 500),
        backgroundColor: Colors.green,
        behavior: SnackBarBehavior.floating,
        margin: const EdgeInsets.all(12),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    _updateScreenMetrics();

    return Scaffold(
      resizeToAvoidBottomInset: true,
      extendBodyBehindAppBar: true,
      body: Stack(
        fit: StackFit.expand,
        children: [
          // Фоновое изображение с RepaintBoundary для изоляции перерисовок
          const RepaintBoundary(
            child: _BackgroundImage(),
          ),

          // Оверлей
          Container(
            color: Colors.black.withOpacity(0.18),
          ),

          // ✅ ИСПРАВЛЕННЫЙ layout - форма по центру
          SafeArea(
            child: Center(
              child: SingleChildScrollView(
                physics: const BouncingScrollPhysics(),
                padding: EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: _keyboardHeight > 0 ? 16 : 24,
                ),
                child: ConstrainedBox(
                  constraints: const BoxConstraints(maxWidth: _maxFormWidth),
                  child: _buildLoginForm(),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLoginForm() {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: _sheetBg,
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(24),
          topRight: Radius.circular(24),
          bottomLeft: Radius.circular(16),
          bottomRight: Radius.circular(16),
        ),
        border: Border.all(color: Colors.black.withOpacity(0.08)),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.14),
            blurRadius: 26,
            offset: const Offset(0, 18),
          ),
        ],
      ),
      padding: EdgeInsets.symmetric(
        horizontal: _isSmallScreen ? 14 : 18,
        vertical: _isSmallScreen ? 14 : 18,
      ),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          mainAxisSize: MainAxisSize.min,
          children: [
            // Handle bar
            const _HandleBar(),

            // Fast login header (только если есть сохраненный пользователь)
            ValueListenableBuilder<bool>(
              valueListenable: _isFastLogin,
              builder: (context, isFastLogin, child) {
                if (!isFastLogin) return const SizedBox.shrink();

                return ValueListenableBuilder<String?>(
                  valueListenable: _savedUserName,
                  builder: (context, userName, _) {
                    return Column(
                      children: [
                        _FastLoginHeader(
                          name: userName ?? 'User',
                          onReset: _resetCredentials,
                        ),
                        const SizedBox(height: 10),
                      ],
                    );
                  },
                );
              },
            ),

            // Заголовок
            Text(
              'Sign in',
              style: _isSmallScreen
                  ? _titleStyle.copyWith(fontSize: 22)
                  : _titleStyle,
            ),

            const SizedBox(height: 4),

            Text(
              'Use your email and password',
              style: _isSmallScreen
                  ? _subtitleStyle.copyWith(fontSize: 13)
                  : _subtitleStyle,
            ),

            const SizedBox(height: 16),

            // Email поле (скрыто при fast login)
            ValueListenableBuilder<bool>(
              valueListenable: _isFastLogin,
              builder: (context, isFastLogin, child) {
                if (isFastLogin) return const SizedBox.shrink();

                return Padding(
                  padding: const EdgeInsets.symmetric(vertical: 8),
                  child: _EmailField(
                    controller: _emailController,
                    isSmallScreen: _isSmallScreen,
                  ),
                );
              },
            ),

            // Password поле
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 8),
              child: ValueListenableBuilder<bool>(
                valueListenable: _obscurePassword,
                builder: (context, obscure, _) {
                  return _PasswordField(
                    controller: _passwordController,
                    obscurePassword: obscure,
                    onToggleObscure: () => _obscurePassword.value = !obscure,
                    onSubmitted: (_) => _handleLogin(),
                    isSmallScreen: _isSmallScreen,
                  );
                },
              ),
            ),


            const SizedBox(height: 4),

            // Forgot password и биометрия
            _buildBottomActions(),

            const SizedBox(height: 12),

            // Кнопка логина
            ValueListenableBuilder<bool>(
              valueListenable: _isLoading,
              builder: (context, isLoading, child) {
                return _LoginButton(
                  isLoading: isLoading,
                  onPressed: isLoading ? null : _handleLogin,
                );
              },
            ),
            const SizedBox(height: 16),
            Center(
              child: TextButton(
                onPressed: () {
                  Navigator.of(context).push(
                    MaterialPageRoute(
                      builder: (context) => const LegalScreen(),
                    ),
                  );
                },
                style: TextButton.styleFrom(
                  foregroundColor: Colors.black54,
                  textStyle: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                child: const Text('Privacy & Terms'),
              ),
            ),

          ],
        ),
      ),
    );
  }

  Widget _buildBottomActions() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        TextButton(
          onPressed: () => Navigator.of(context).pushNamed('forgot-password'),
          style: TextButton.styleFrom(
            foregroundColor: Colors.black87,
            textStyle: const TextStyle(fontWeight: FontWeight.w600),
          ),
          child: const Text('Forgot password?'),
        ),
      ],
    );
  }
}


// Фоновое изображение (изолировано от перерисовок)
class _BackgroundImage extends StatelessWidget {
  const _BackgroundImage();

  @override
  Widget build(BuildContext context) {
    return const Image(
      image: AssetImage('assets/images/facecheck2.jpg'),
      fit: BoxFit.cover,
    );
  }
}

// Handle bar (постоянный элемент)
class _HandleBar extends StatelessWidget {
  const _HandleBar();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 44,
        height: 4.5,
        margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(
          color: Colors.black.withOpacity(0.18),
          borderRadius: BorderRadius.circular(99),
        ),
      ),
    );
  }
}

// Fast login header
class _FastLoginHeader extends StatelessWidget {
  final String name;
  final VoidCallback onReset;

  const _FastLoginHeader({
    required this.name,
    required this.onReset,
  });

  @override
  Widget build(BuildContext context) {
    final initials = name.isNotEmpty ? name[0].toUpperCase() : 'U';

    return Row(
      children: [
        CircleAvatar(
          radius: 18,
          backgroundColor: Colors.black,
          child: Text(
            initials,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.w700,
            ),
          ),
        ),
        const SizedBox(width: 10),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Welcome,',
                style: TextStyle(
                  fontSize: 13,
                  fontWeight: FontWeight.w600,
                  color: Colors.black87,
                ),
              ),
              Text(
                name,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  fontSize: 12,
                  color: Colors.black54,
                ),
              ),
            ],
          ),
        ),
        TextButton(
          onPressed: onReset,
          style: TextButton.styleFrom(
            foregroundColor: Colors.black87,
            textStyle: const TextStyle(fontWeight: FontWeight.w600),
          ),
          child: const Text('Not you?'),
        ),
      ],
    );
  }
}

// Email поле (оптимизировано)
class _EmailField extends StatelessWidget {
  final TextEditingController controller;
  final bool isSmallScreen;

  const _EmailField({
    required this.controller,
    required this.isSmallScreen,
  });

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: controller,
      style: const TextStyle(color: Colors.black),
      keyboardType: TextInputType.emailAddress,
      textInputAction: TextInputAction.next,
      autofillHints: const [AutofillHints.email, AutofillHints.username],
      validator: (v) {
        final value = (v ?? '').trim();
        if (value.isEmpty) return 'Email is required';
        if (!value.contains('@') || !value.contains('.')) {
          return 'Enter a valid email';
        }
        return null;
      },
      decoration: InputDecoration(
        labelText: 'Email',
        hintText: 'email@gmail.com',
        prefixIcon: const Icon(
          Icons.alternate_email,
          color: Colors.black87,
          size: 20,
        ),
        filled: true,
        fillColor: const Color(0xFFFAFAFA),
        contentPadding: EdgeInsets.symmetric(
          horizontal: 14,
          vertical: isSmallScreen ? 12 : 14,
        ),
        labelStyle: const TextStyle(
          color: Colors.black87,
          fontWeight: FontWeight.w600,
        ),
        hintStyle: const TextStyle(color: Colors.black54),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: BorderSide(
            color: Colors.black.withOpacity(0.12),
            width: 1,
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: const BorderSide(color: Colors.black, width: 1.4),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: const BorderSide(color: Colors.red, width: 1),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: const BorderSide(color: Colors.red, width: 1.2),
        ),
      ),
    );
  }
}

// Password поле (оптимизировано)
class _PasswordField extends StatelessWidget {
  final TextEditingController controller;
  final bool obscurePassword;
  final VoidCallback onToggleObscure;
  final Function(String)? onSubmitted;
  final bool isSmallScreen;

  const _PasswordField({
    required this.controller,
    required this.obscurePassword,
    required this.onToggleObscure,
    required this.isSmallScreen,
    this.onSubmitted,
  });

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: controller,
      style: const TextStyle(color: Colors.black),
      obscureText: obscurePassword,
      textInputAction: TextInputAction.done,
      autofillHints: const [AutofillHints.password],
      onFieldSubmitted: onSubmitted,
      validator: (v) {
        final value = (v ?? '').trim();
        if (value.isEmpty) return 'Password is required';
        if (value.length < 6) return 'At least 6 characters';
        return null;
      },
      decoration: InputDecoration(
        labelText: 'Password',
        hintText: 'Your password',
        prefixIcon: const Icon(
          Icons.lock_outline,
          color: Colors.black87,
          size: 20,
        ),
        suffixIcon: IconButton(
          tooltip: obscurePassword ? 'Show password' : 'Hide password',
          onPressed: onToggleObscure,
          icon: Icon(
            obscurePassword ? Icons.visibility_off : Icons.visibility,
            color: Colors.black87,
            size: 20,
          ),
        ),
        filled: true,
        fillColor: const Color(0xFFFAFAFA),
        contentPadding: EdgeInsets.symmetric(
          horizontal: 14,
          vertical: isSmallScreen ? 12 : 14,
        ),
        labelStyle: const TextStyle(
          color: Colors.black87,
          fontWeight: FontWeight.w600,
        ),
        hintStyle: const TextStyle(color: Colors.black54),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: BorderSide(
            color: Colors.black.withOpacity(0.12),
            width: 1,
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: const BorderSide(color: Colors.black, width: 1.4),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: const BorderSide(color: Colors.red, width: 1),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: const BorderSide(color: Colors.red, width: 1.2),
        ),
      ),
    );
  }
}

// Кнопка логина (оптимизирована)
class _LoginButton extends StatelessWidget {
  final bool isLoading;
  final VoidCallback? onPressed;

  const _LoginButton({
    required this.isLoading,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 52,
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.black,
          foregroundColor: Colors.white,
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(14),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
        child: isLoading
            ? const SizedBox(
          width: 22,
          height: 22,
          child: CircularProgressIndicator(
            strokeWidth: 2,
            color: Colors.white,
          ),
        )
            : const Text('Login'),
      ),
    );
  }
}