class LocationUpdateDto {
  final double latitude;
  final double longitude;
  final DateTime timestamp;
  final double? accuracy;
  final double? speed;
  final double? bearing;
  final double? altitude;
  final String? provider;
  final int? batteryLevel;

  LocationUpdateDto({
    required this.latitude,
    required this.longitude,
    required this.timestamp,
    this.accuracy,
    this.speed,
    this.bearing,
    this.altitude,
    this.provider,
    this.batteryLevel,
  });

  Map<String, dynamic> toJson() {
    return {
      'latitude': latitude,
      'longitude': longitude,
      'timestamp': timestamp.toUtc().toIso8601String(),
      'accuracy': accuracy,
      'speed': speed,
      'bearing': bearing,
      'altitude': altitude,
      'provider': provider,
      'batteryLevel': batteryLevel,
    };
  }

  factory LocationUpdateDto.fromJson(Map<String, dynamic> json) {
    return LocationUpdateDto(
      latitude: json['latitude']?.toDouble() ?? 0.0,
      longitude: json['longitude']?.toDouble() ?? 0.0,
      timestamp: DateTime.parse(json['timestamp']),
      accuracy: json['accuracy']?.toDouble(),
      speed: json['speed']?.toDouble(),
      bearing: json['bearing']?.toDouble(),
      altitude: json['altitude']?.toDouble(),
      provider: json['provider'],
      batteryLevel: json['batteryLevel']?.toInt(),
    );
  }
}