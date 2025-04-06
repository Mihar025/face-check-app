import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import '../services/weather_service.dart';
import '../models/weather_model.dart';

class WeatherWidget extends StatefulWidget {
  @override
  _WeatherWidgetState createState() => _WeatherWidgetState();
}

class _WeatherWidgetState extends State<WeatherWidget> {
  num? temperature;
  String? condition;
  String? iconUrl;
  final weatherService = WeatherService();

  @override
  void initState() {
    super.initState();
    _getCurrentWeather();
  }

  Future<void> _getCurrentWeather() async {
    try {
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) return;
      }

      Position position = await Geolocator.getCurrentPosition();
      final weatherData = await weatherService.getWeather(
        position.latitude,
        position.longitude,
      );

      setState(() {
        temperature = weatherData['current']['temp_c'];
        condition = weatherData['current']['condition']['text'];
        iconUrl = 'https:${weatherData['current']['condition']['icon']}';
      });
    } catch (e) {
      print('Ошибка: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Container(
      padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      child: temperature != null
          ? Row(
        mainAxisSize: MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          if (iconUrl != null)
            Image.network(
              iconUrl!,
              width: 28,  // Немного увеличил для лучшей видимости
              height: 28,
              loadingBuilder: (context, child, loadingProgress) {
                if (loadingProgress == null) return child;
                return SizedBox(
                  width: 28,
                  height: 28,
                );
              },
              errorBuilder: (context, error, stackTrace) {
                return SizedBox(
                  width: 28,
                  height: 28,
                );
              },
            ),
          SizedBox(width: 4),
          Text(
            '${temperature!.round()}°',
            style: TextStyle(
              fontSize: 18,  // Такой же размер как у даты и времени
              fontWeight: FontWeight.w500,  // Такой же вес как у других элементов
              color: theme.textTheme.bodyLarge?.color,  // Использует цвет из темы
            ),
          ),
        ],
      )
          : SizedBox(
        width: 20,
        height: 20,
        child: CircularProgressIndicator(
          strokeWidth: 2,
          valueColor: AlwaysStoppedAnimation<Color>(
            theme.textTheme.bodyLarge?.color ?? Colors.white,
          ),
        ),
      ),
    );
  }
}
