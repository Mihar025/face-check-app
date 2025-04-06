import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:shared_preferences/shared_preferences.dart';

class JwtService {
  static const String _roleKey = 'user_role';

  // Декодирует JWT токен и сохраняет роль пользователя
  static Future<String?> decodeAndSaveRole(String token) async {
    try {
      print('=== JWT Service: Starting token decode ===');
      print('Token to decode: $token');

      final Map<String, dynamic> decodedToken = JwtDecoder.decode(token);
      print('Decoded token payload: $decodedToken');

      // Получаем роль из поля authorities
      final List<dynamic> authorities = decodedToken['authorities'] ?? [];
      print('Extracted authorities: $authorities');

      final String role = authorities.isNotEmpty ? authorities[0].toString() : 'USER';
      print('Determined role: $role');

      // Сохраняем роль в SharedPreferences
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_roleKey, role);

      // Проверяем что роль сохранилась
      final savedRole = await prefs.getString(_roleKey);
      print('Role saved in preferences: $savedRole');
      print('=== JWT Service: Token decode completed ===');

      return role;
    } catch (e) {
      print('=== JWT Service: Error during token decode ===');
      print('Error details: $e');
      print('Stack trace: ${StackTrace.current}');
      return null;
    }
  }

  // Получает сохраненную роль пользователя
  static Future<String> getUserRole() async {
    try {
      print('=== JWT Service: Getting user role ===');
      final prefs = await SharedPreferences.getInstance();
      final role = prefs.getString(_roleKey) ?? 'USER';
      print('Retrieved role from preferences: $role');
      print('=== JWT Service: Role retrieval completed ===');
      return role;
    } catch (e) {
      print('=== JWT Service: Error getting user role ===');
      print('Error details: $e');
      print('Stack trace: ${StackTrace.current}');
      return 'USER';
    }
  }

  // Очищает сохраненную роль при выходе
  static Future<void> clearRole() async {
    try {
      print('=== JWT Service: Clearing user role ===');
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(_roleKey);

      // Проверяем что роль действительно удалена
      final roleAfterClear = await prefs.getString(_roleKey);
      print('Role after clearing: $roleAfterClear');
      print('=== JWT Service: Role clear completed ===');
    } catch (e) {
      print('=== JWT Service: Error clearing role ===');
      print('Error details: $e');
      print('Stack trace: ${StackTrace.current}');
    }
  }
}