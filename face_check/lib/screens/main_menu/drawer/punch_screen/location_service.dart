import 'package:geolocator/geolocator.dart';

class LocationService {
  Position? _lastKnownPosition;
  DateTime? _lastLocationTime;


  static const int _maxLocationAgeSeconds = 30;

  Future<Position?> getCurrentLocation() async {

    if (_lastKnownPosition != null && _lastLocationTime != null) {
      final difference = DateTime.now().difference(_lastLocationTime!).inSeconds;
      if (difference < _maxLocationAgeSeconds) {
        return _lastKnownPosition;
      }
    }


    final serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return _lastKnownPosition;
    }


    final permission = await Geolocator.checkPermission();


    if (permission == LocationPermission.denied) {
      final newPermission = await Geolocator.requestPermission();
      if (newPermission == LocationPermission.denied ||
          newPermission == LocationPermission.deniedForever) {
        return _lastKnownPosition;
      }
    } else if (permission == LocationPermission.deniedForever) {
      return _lastKnownPosition;
    }

    try {
      final lastPosition = await Geolocator.getLastKnownPosition();

      if (lastPosition != null) {
        _lastKnownPosition = lastPosition;
        _lastLocationTime = DateTime.now();
      }

      Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: const Duration(seconds: 5),
      ).then((position) {

        _lastKnownPosition = position;
        _lastLocationTime = DateTime.now();
      }).catchError((e) {
        print("Error getting precise location: $e");
      });


      return _lastKnownPosition;
    } catch (e) {
      print("Error getting location: $e");
      return _lastKnownPosition;
    }
  }


  Future<Position?> forceLocationUpdate() async {
    _lastKnownPosition = null;
    _lastLocationTime = null;
    return getCurrentLocation();
  }
}