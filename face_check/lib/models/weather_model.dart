class WeatherModel {
  final double temperature;
  final String description;
  final String icon;

  WeatherModel({
    required this.temperature,
    required this.description,
    required this.icon,
  });

  factory WeatherModel.fromJson(Map<String, dynamic> json) {
    return WeatherModel(

      temperature: json['current']['temp_c'].toDouble(),

      description: json['current']['condition']['text'],

      icon: 'https:${json['current']['condition']['icon']}',
    );
  }
}