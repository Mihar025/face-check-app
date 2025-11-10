import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';

class MapContainer extends StatefulWidget {
  final Position? currentPosition;
  final Function(GoogleMapController)? onMapCreated;

  const MapContainer({
    super.key,
    required this.currentPosition,
    this.onMapCreated,
  });

  @override
  State<MapContainer> createState() => _MapContainerState();
}

class _MapContainerState extends State<MapContainer>
    with AutomaticKeepAliveClientMixin {
  GoogleMapController? _controller;
  CameraPosition? _initialPosition;
  Set<Marker> _markers = {};

  // показываем ли карту (true) или лоадер (false)
  bool _showMap = false;

  // дефолт — если вообще не дали локацию
  static const LatLng _fallbackLatLng = LatLng(40.7128, -74.0060); // NYC

  static const double _defaultHeight = 200.0;
  static const double _smallScreenHeight = 180.0;
  static const double _defaultMargin = 20.0;
  static const double _smallScreenMargin = 16.0;
  static const double _borderRadius = 12.0;

  @override
  void initState() {
    super.initState();
    _setFromPosition(widget.currentPosition);

    // если позиция уже есть — сразу показываем
    if (widget.currentPosition != null) {
      _showMap = true;
    } else {
      // если нет — можно через небольшую задержку всё равно показать карту с дефолтом
      Future.delayed(const Duration(milliseconds: 800), () {
        if (!mounted) return;
        if (widget.currentPosition == null) {
          // всё ещё нет позиции — показываем дефолт
          setState(() {
            _initialPosition = const CameraPosition(
              target: _fallbackLatLng,
              zoom: 14,
            );
            _markers = {};
            _showMap = true;
          });
        }
      });
    }
  }

  @override
  void didUpdateWidget(covariant MapContainer oldWidget) {
    super.didUpdateWidget(oldWidget);

    // если пришла НОВАЯ реальная позиция — переставим маркер и подвинем камеру
    final oldPos = oldWidget.currentPosition;
    final newPos = widget.currentPosition;

    final changed = oldPos?.latitude != newPos?.latitude ||
        oldPos?.longitude != newPos?.longitude;

    if (changed && newPos != null) {
      _setFromPosition(newPos);

      // если карта уже создана — плавно двигаем
      if (_controller != null) {
        _controller!.animateCamera(
          CameraUpdate.newCameraPosition(
            CameraPosition(
              target: LatLng(newPos.latitude, newPos.longitude),
              zoom: 15,
            ),
          ),
        );
      }

      // если до этого лоадер был — покажем карту
      if (!_showMap) {
        setState(() {
          _showMap = true;
        });
      } else {
        setState(() {}); // просто обновим маркеры
      }
    }
  }

  void _setFromPosition(Position? position) {
    if (position == null) return;

    final latLng = LatLng(position.latitude, position.longitude);
    _initialPosition = CameraPosition(target: latLng, zoom: 15);
    _markers = {
      Marker(
        markerId: const MarkerId('current_location'),
        position: latLng,
      ),
    };
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
      child: ClipRRect(
        borderRadius: BorderRadius.circular(_borderRadius),
        child: !_showMap || _initialPosition == null
            ? _buildLoader()
            : GoogleMap(
          onMapCreated: (c) {
            _controller = c;
            if (widget.onMapCreated != null) {
              widget.onMapCreated!(c);
            }
          },
          initialCameraPosition: _initialPosition!,
          myLocationEnabled: widget.currentPosition != null,
          myLocationButtonEnabled: true,
          zoomControlsEnabled: false,
          markers: _markers,
          compassEnabled: false,
          buildingsEnabled: false,
          mapToolbarEnabled: false,
        ),
      ),
    );
  }

  Widget _buildLoader() {
    return Container(
      color: Colors.grey[200],
      child: const Center(
        child: CircularProgressIndicator(),
      ),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
