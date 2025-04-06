import 'package:http/http.dart' as http;
import 'dart:convert';

class WeatherService {
  final String apiKey = 'fa0837be680849aea2c235711250202';

  Future<Map<String, dynamic>> getWeather(double lat, double lon) async {
    final response = await http.get(
        Uri.parse('http://api.weatherapi.com/v1/current.json?key=$apiKey&q=$lat,$lon&aqi=no')
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Ошибка получения погоды: ${response.statusCode}');
    }
  }
}