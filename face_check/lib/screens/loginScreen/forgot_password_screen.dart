import 'package:flutter/material.dart';
import '../../services/ApiService.dart';

class ForgotPasswordScreen extends StatefulWidget {
  const ForgotPasswordScreen({super.key});

  @override
  State<ForgotPasswordScreen> createState() => _ForgotPasswordScreenState();
}

class _ForgotPasswordScreenState extends State<ForgotPasswordScreen> {
  // Controllers
  late final TextEditingController _emailController;
  late final TextEditingController _codeController;
  late final TextEditingController _passwordController;
  late final TextEditingController _confirmPasswordController;

  // ValueNotifiers для локальных обновлений (избегаем лишних rebuild)
  late final ValueNotifier<bool> _isLoading;
  late final ValueNotifier<int> _currentStep; // 0 - email, 1 - code, 2 - new password
  late final ValueNotifier<bool> _obscureNew;
  late final ValueNotifier<bool> _obscureConfirm;

  // Данные для процесса восстановления
  String? _email;
  String? _verificationCode;

  // Кэшированные значения MediaQuery
  late Size _screenSize;
  late double _screenWidth;
  late double _screenHeight;
  late double _keyboardHeight;
  late bool _isSmallScreen;

  // Константы UI
  static const Color _sheetBg = Color(0xFFFDFDFC);
  static const Color _fieldBg = Color(0xFFFAFAFA);
  static const double _smallScreenThreshold = 360.0;
  static const double _maxFormWidth = 440.0;
  static const double _maxFieldWidth = 360.0;
  static const double _maxCodeFieldWidth = 320.0;

  // Предопределенные стили (создаются один раз)
  static const TextStyle _titleStyle = TextStyle(
    color: Colors.black,
    fontSize: 20,
    fontWeight: FontWeight.w700,
    letterSpacing: 0.2,
  );

  static const TextStyle _subtitleStyle = TextStyle(
    color: Colors.black54,
    fontSize: 14,
    fontWeight: FontWeight.w500,
  );

  static const TextStyle _snackBarStyle = TextStyle(
    fontSize: 14,
    fontWeight: FontWeight.w500,
    letterSpacing: 0.25,
  );

  // Константы для анимаций и задержек
  static const Duration _successSnackBarDuration = Duration(milliseconds: 500);
  static const Duration _errorSnackBarDuration = Duration(milliseconds: 4000);
  static const Duration _navigationDelay = Duration(milliseconds: 1000);
  static const EdgeInsets _snackBarMargin = EdgeInsets.all(12);

  @override
  void initState() {
    super.initState();

    // Инициализация контроллеров
    _emailController = TextEditingController();
    _codeController = TextEditingController();
    _passwordController = TextEditingController();
    _confirmPasswordController = TextEditingController();

    // Инициализация ValueNotifiers
    _isLoading = ValueNotifier<bool>(false);
    _currentStep = ValueNotifier<int>(0);
    _obscureNew = ValueNotifier<bool>(true);
    _obscureConfirm = ValueNotifier<bool>(true);

    // Предзагрузка изображения
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        precacheImage(const AssetImage('assets/images/facecheck2.jpg'), context);
      }
    });
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateScreenMetrics();
  }

  void _updateScreenMetrics() {
    final mediaQuery = MediaQuery.of(context);
    _screenSize = mediaQuery.size;
    _screenWidth = _screenSize.width;
    _screenHeight = _screenSize.height;
    _keyboardHeight = mediaQuery.viewInsets.bottom;
    _isSmallScreen = _screenWidth < _smallScreenThreshold;
  }

  @override
  void dispose() {
    // Очищаем все ресурсы
    _emailController.dispose();
    _codeController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _isLoading.dispose();
    _currentStep.dispose();
    _obscureNew.dispose();
    _obscureConfirm.dispose();
    super.dispose();
  }

  void _showMessage(String message, {bool isError = false}) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message, style: _snackBarStyle),
        backgroundColor: isError ? Colors.red : Colors.green,
        behavior: SnackBarBehavior.floating,
        margin: _snackBarMargin,
        duration: isError ? _errorSnackBarDuration : _successSnackBarDuration,
      ),
    );
  }

  Future<void> _handleEmailSubmit() async {
    final email = _emailController.text.trim();

    if (email.isEmpty) {
      _showMessage('Please enter your email', isError: true);
      return;
    }

    _isLoading.value = true;

    try {
      await ApiService.instance.sendEmail(email);

      if (mounted) {
        _email = email;
        _currentStep.value = 1;
        _showMessage('Verification code sent to your email');
      }
    } catch (e) {
      _showMessage(e.toString(), isError: true);
    } finally {
      if (mounted) {
        _isLoading.value = false;
      }
    }
  }

  Future<void> _handleCodeVerification() async {
    final code = _codeController.text.trim();

    if (code.isEmpty) {
      _showMessage('Please enter verification code', isError: true);
      return;
    }

    _isLoading.value = true;

    try {
      await ApiService.instance.verifyCode(_email!, code);

      if (mounted) {
        _verificationCode = code;
        _currentStep.value = 2;
        _showMessage('Code verified successfully');
      }
    } catch (e) {
      _showMessage(e.toString(), isError: true);
    } finally {
      if (mounted) {
        _isLoading.value = false;
      }
    }
  }

  Future<void> _handlePasswordReset() async {
    final password = _passwordController.text.trim();
    final confirmPassword = _confirmPasswordController.text.trim();

    if (password.isEmpty || confirmPassword.isEmpty) {
      _showMessage('Please fill all fields', isError: true);
      return;
    }

    if (password != confirmPassword) {
      _showMessage('Passwords do not match', isError: true);
      return;
    }

    _isLoading.value = true;

    try {
      await ApiService.instance.resetPassword(
        _email!,
        password,
        confirmPassword,
        _verificationCode!,
      );

      _showMessage('Password successfully reset');
      await Future.delayed(_navigationDelay);

      if (mounted) {
        Navigator.of(context).pop();
      }
    } catch (e) {
      _showMessage(e.toString(), isError: true);
    } finally {
      if (mounted) {
        _isLoading.value = false;
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    _updateScreenMetrics();

    // Локальная тема
    final theme = Theme.of(context).copyWith(
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.black,
          foregroundColor: Colors.white,
          elevation: 0,
          minimumSize: const Size.fromHeight(52),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(14),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );

    return Theme(
      data: theme,
      child: Scaffold(
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
        body: Stack(
          fit: StackFit.expand,
          children: [
            // Фоновое изображение с изоляцией
            const RepaintBoundary(
              child: _BackgroundImage(),
            ),

            // Оверлей
            Container(color: Colors.black.withOpacity(0.18)),

            // Основной контент
            SafeArea(
              child: LayoutBuilder(
                builder: (context, constraints) {
                  return SingleChildScrollView(
                    physics: const ClampingScrollPhysics(),
                    padding: EdgeInsets.fromLTRB(
                      16,
                      _screenHeight * 0.35,
                      16,
                      _keyboardHeight > 0 ? _keyboardHeight + 16 : 24,
                    ),
                    child: Center(
                      child: ConstrainedBox(
                        constraints: const BoxConstraints(maxWidth: _maxFormWidth),
                        child: _buildForm(),
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildForm() {
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
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Handle bar
          const _HandleBar(),

          // Заголовок
          const Text(
            'Change Password',
            style: _titleStyle,
            textAlign: TextAlign.left,
          ),

          const SizedBox(height: 8),

          // Подзаголовок
          const Text(
            'Guide you through three quick steps',
          style: _subtitleStyle,
          ),

          const SizedBox(height: 18),

          // Динамический контент в зависимости от шага
          ValueListenableBuilder<int>(
            valueListenable: _currentStep,
            builder: (context, step, _) {
              return AnimatedSwitcher(
                duration: const Duration(milliseconds: 300),
                child: _buildStepContent(step),
              );
            },
          ),

          const SizedBox(height: 18),

          // Кнопка действия
          ValueListenableBuilder<int>(
            valueListenable: _currentStep,
            builder: (context, step, _) {
              return ValueListenableBuilder<bool>(
                valueListenable: _isLoading,
                builder: (context, isLoading, _) {
                  return _ActionButton(
                    isLoading: isLoading,
                    step: step,
                    onPressed: isLoading ? null : _getActionForStep(step),
                  );
                },
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildStepContent(int step) {
    switch (step) {
      case 0:
        return ConstrainedBox(
          key: const ValueKey(0),
          constraints: const BoxConstraints(maxWidth: _maxFieldWidth),
          child: _InputField(
            label: 'Email',
            hint: 'Enter your email',
            controller: _emailController,
            keyboardType: TextInputType.emailAddress,
            isSmallScreen: _isSmallScreen,
            prefixIcon: Icons.alternate_email,
            textInputAction: TextInputAction.done,
            onSubmitted: (_) => _handleEmailSubmit(),
          ),
        );

      case 1:
        return ConstrainedBox(
          key: const ValueKey(1),
          constraints: const BoxConstraints(maxWidth: _maxCodeFieldWidth),
          child: _InputField(
            label: 'Verification Code',
            hint: 'Enter 6-digit code',
            controller: _codeController,
            keyboardType: TextInputType.number,
            maxLength: 6,
            isSmallScreen: _isSmallScreen,
            prefixIcon: Icons.pin_outlined,
            textInputAction: TextInputAction.done,
            onSubmitted: (_) => _handleCodeVerification(),
          ),
        );

      case 2:
        return Column(
          key: const ValueKey(2),
          children: [
            ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: _maxFieldWidth),
              child: ValueListenableBuilder<bool>(
                valueListenable: _obscureNew,
                builder: (context, obscure, _) {
                  return _InputField(
                    label: 'New Password',
                    hint: 'Enter new password',
                    controller: _passwordController,
                    isPassword: true,
                    isSmallScreen: _isSmallScreen,
                    prefixIcon: Icons.lock_outline,
                    obscureText: obscure,
                    textInputAction: TextInputAction.next,
                    suffixIcon: IconButton(
                      tooltip: obscure ? 'Show password' : 'Hide password',
                      onPressed: () => _obscureNew.value = !obscure,
                      icon: Icon(obscure ? Icons.visibility_off : Icons.visibility),
                    ),
                  );
                },
              ),
            ),
            const SizedBox(height: 12),
            ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: _maxFieldWidth),
              child: ValueListenableBuilder<bool>(
                valueListenable: _obscureConfirm,
                builder: (context, obscure, _) {
                  return _InputField(
                    label: 'Confirm Password',
                    hint: 'Confirm new password',
                    controller: _confirmPasswordController,
                    isPassword: true,
                    isSmallScreen: _isSmallScreen,
                    prefixIcon: Icons.lock_outline,
                    obscureText: obscure,
                    textInputAction: TextInputAction.done,
                    onSubmitted: (_) => _handlePasswordReset(),
                    suffixIcon: IconButton(
                      tooltip: obscure ? 'Show password' : 'Hide password',
                      onPressed: () => _obscureConfirm.value = !obscure,
                      icon: Icon(obscure ? Icons.visibility_off : Icons.visibility),
                    ),
                  );
                },
              ),
            ),
          ],
        );

      default:
        return const SizedBox.shrink();
    }
  }

  VoidCallback _getActionForStep(int step) {
    switch (step) {
      case 0:
        return _handleEmailSubmit;
      case 1:
        return _handleCodeVerification;
      case 2:
        return _handlePasswordReset;
      default:
        return () {};
    }
  }
}

// ============= ОТДЕЛЬНЫЕ ОПТИМИЗИРОВАННЫЕ ВИДЖЕТЫ =============

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

// Handle bar (статичный элемент)
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

// Оптимизированное поле ввода
class _InputField extends StatelessWidget {
  final String label;
  final String hint;
  final TextEditingController controller;
  final bool isPassword;
  final TextInputType? keyboardType;
  final int? maxLength;
  final bool isSmallScreen;
  final IconData? prefixIcon;
  final Widget? suffixIcon;
  final bool obscureText;
  final TextInputAction? textInputAction;
  final Function(String)? onSubmitted;

  const _InputField({
    required this.label,
    required this.hint,
    required this.controller,
    required this.isSmallScreen,
    this.isPassword = false,
    this.keyboardType,
    this.maxLength,
    this.prefixIcon,
    this.suffixIcon,
    this.obscureText = false,
    this.textInputAction,
    this.onSubmitted,
  });

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: controller,
      obscureText: obscureText || isPassword,
      maxLength: maxLength,
      keyboardType: keyboardType ?? TextInputType.text,
      textInputAction: textInputAction,
      onSubmitted: onSubmitted,
      style: TextStyle(
        color: Colors.black,
        fontSize: isSmallScreen ? 14 : 16,
        fontWeight: FontWeight.w500,
      ),
      decoration: InputDecoration(
        prefixIcon: prefixIcon != null ? Icon(prefixIcon) : null,
        suffixIcon: suffixIcon,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: BorderSide(
            color: Colors.black.withOpacity(0.12),
            width: 1,
          ),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: BorderSide(
            color: Colors.black.withOpacity(0.12),
            width: 1,
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: const BorderSide(
            color: Colors.black,
            width: 1.4,
          ),
        ),
        labelText: label,
        labelStyle: TextStyle(
          fontSize: isSmallScreen ? 13 : 15,
          fontWeight: FontWeight.w600,
          color: Colors.black87,
        ),
        hintText: hint,
        hintStyle: TextStyle(
          fontSize: isSmallScreen ? 12 : 14,
          fontWeight: FontWeight.normal,
          color: Colors.black54,
        ),
        filled: true,
        fillColor: const Color(0xFFFAFAFA),
        contentPadding: EdgeInsets.symmetric(
          horizontal: 14,
          vertical: isSmallScreen ? 12 : 14,
        ),
        counterText: "",
      ),
    );
  }
}

// Оптимизированная кнопка действия
class _ActionButton extends StatelessWidget {
  final bool isLoading;
  final int step;
  final VoidCallback? onPressed;

  const _ActionButton({
    required this.isLoading,
    required this.step,
    required this.onPressed,
  });

  String _getButtonText() {
    switch (step) {
      case 0:
        return 'Send Code';
      case 1:
        return 'Verify Code';
      case 2:
        return 'Reset Password';
      default:
        return '';
    }
  }

  @override
  Widget build(BuildContext context) {
    return ConstrainedBox(
      constraints: const BoxConstraints(maxWidth: 360),
      child: SizedBox(
        height: 52,
        child: ElevatedButton(
          onPressed: onPressed,
          child: AnimatedSwitcher(
            duration: const Duration(milliseconds: 200),
            child: isLoading
                ? const SizedBox(
              key: ValueKey('loading'),
              width: 22,
              height: 22,
              child: CircularProgressIndicator(
                strokeWidth: 2,
                color: Colors.white,
              ),
            )
                : Text(
              key: ValueKey('text_$step'),
              _getButtonText(),
            ),
          ),
        ),
      ),
    );
  }
}

// ============= STEP INDICATOR (БОНУС) =============

// Опциональный индикатор шагов для лучшего UX
class _StepIndicator extends StatelessWidget {
  final int currentStep;

  const _StepIndicator({required this.currentStep});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(3, (index) {
        final isActive = index == currentStep;
        final isCompleted = index < currentStep;

        return AnimatedContainer(
          duration: const Duration(milliseconds: 300),
          width: isActive ? 24 : 8,
          height: 8,
          margin: const EdgeInsets.symmetric(horizontal: 4),
          decoration: BoxDecoration(
            color: isActive
                ? Colors.black
                : isCompleted
                ? Colors.black54
                : Colors.black26,
            borderRadius: BorderRadius.circular(4),
          ),
        );
      }),
    );
  }
}