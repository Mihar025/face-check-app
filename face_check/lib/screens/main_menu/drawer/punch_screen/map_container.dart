import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';

class MapContainer extends StatefulWidget {
  final Position? currentPosition;
  final Function(GoogleMapController) onMapCreated;

  const MapContainer({
    super.key,
    required this.currentPosition,
    required this.onMapCreated,
  });

  @override
  State<MapContainer> createState() => _MapContainerState();
}

class _MapContainerState extends State<MapContainer> with AutomaticKeepAliveClientMixin {
  Set<Marker>? _markers;
  CameraPosition? _initialPosition;


  static const double _defaultHeight = 200.0;
  static const double _smallScreenHeight = 180.0;
  static const double _defaultMargin = 20.0;
  static const double _smallScreenMargin = 16.0;
  static const double _borderRadius = 12.0;

  @override
  void initState() {
    super.initState();
    _updatePositionData();
  }

  @override
  void didUpdateWidget(MapContainer oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.currentPosition?.latitude != widget.currentPosition?.latitude ||
        oldWidget.currentPosition?.longitude != widget.currentPosition?.longitude) {
      _updatePositionData();
    }
  }

  void _updatePositionData() {
    final position = widget.currentPosition;
    if (position != null) {
      final latLng = LatLng(position.latitude, position.longitude);
      _initialPosition = CameraPosition(target: latLng, zoom: 15);
      _markers = {
        Marker(
          markerId: const MarkerId('current_location'),
          position: latLng,
        ),
      };
    }
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);


    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;


    final height = isSmallScreen ? _smallScreenHeight : _defaultHeight;
    final margin = isSmallScreen ? _smallScreenMargin : _defaultMargin;

    return Container(
      height: height,
      margin: EdgeInsets.symmetric(horizontal: margin),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(_borderRadius),
        border: Border.all(
          color: Colors.white.withOpacity(0.1),
        ),
      ),
      child: RepaintBoundary(
        child: ClipRRect(
          borderRadius: BorderRadius.circular(_borderRadius),
          child: widget.currentPosition == null
              ? const Center(
            child: CircularProgressIndicator(),
          )
              : GoogleMap(
            onMapCreated: widget.onMapCreated,
            initialCameraPosition: _initialPosition!,
            myLocationEnabled: true,
            myLocationButtonEnabled: true,
            zoomControlsEnabled: false,
            markers: _markers!,
            compassEnabled: false, // Отключаем компас для повышения производительности
            buildingsEnabled: false, // Отключаем 3D здания для повышения производительности
            mapToolbarEnabled: false, // Отключаем тулбар для повышения производительности
          ),
        ),
      ),
    );
  }

  @override
  bool get wantKeepAlive => true;
}