import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:shared_preferences/shared_preferences.dart';

class JwtService {
  static const String _roleKey = 'user_role';

  static Future<String?> decodeAndSaveRole(String token) async {
    try {
      final Map<String, dynamic> decodedToken = JwtDecoder.decode(token);
      final List<dynamic> authorities = decodedToken['authorities'] ?? [];

      final String role = authorities.isNotEmpty ? authorities[0].toString() : 'USER';

      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_roleKey, role);

       await prefs.getString(_roleKey);

      return role;
    } catch (e) {
      print(e);
      return null;
    }
  }

  static Future<String> getUserRole() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final role = prefs.getString(_roleKey) ?? 'USER';
      return role;
    } catch (e) {
      print('Error details: $e');
      return 'USER';
    }
  }

  static Future<void> clearRole() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(_roleKey);
      await prefs.getString(_roleKey);

    } catch (e) {
      print('Error details: $e');
      print('Stack trace: ${StackTrace.current}');
    }
  }
}