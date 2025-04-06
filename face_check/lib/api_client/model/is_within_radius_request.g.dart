// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'is_within_radius_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$IsWithinRadiusRequest extends IsWithinRadiusRequest {
  @override
  final double latitude;
  @override
  final double longitude;

  factory _$IsWithinRadiusRequest(
          [void Function(IsWithinRadiusRequestBuilder)? updates]) =>
      (new IsWithinRadiusRequestBuilder()..update(updates))._build();

  _$IsWithinRadiusRequest._({required this.latitude, required this.longitude})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        latitude, r'IsWithinRadiusRequest', 'latitude');
    BuiltValueNullFieldError.checkNotNull(
        longitude, r'IsWithinRadiusRequest', 'longitude');
  }

  @override
  IsWithinRadiusRequest rebuild(
          void Function(IsWithinRadiusRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  IsWithinRadiusRequestBuilder toBuilder() =>
      new IsWithinRadiusRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is IsWithinRadiusRequest &&
        latitude == other.latitude &&
        longitude == other.longitude;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, latitude.hashCode);
    _$hash = $jc(_$hash, longitude.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'IsWithinRadiusRequest')
          ..add('latitude', latitude)
          ..add('longitude', longitude))
        .toString();
  }
}

class IsWithinRadiusRequestBuilder
    implements Builder<IsWithinRadiusRequest, IsWithinRadiusRequestBuilder> {
  _$IsWithinRadiusRequest? _$v;

  double? _latitude;
  double? get latitude => _$this._latitude;
  set latitude(double? latitude) => _$this._latitude = latitude;

  double? _longitude;
  double? get longitude => _$this._longitude;
  set longitude(double? longitude) => _$this._longitude = longitude;

  IsWithinRadiusRequestBuilder() {
    IsWithinRadiusRequest._defaults(this);
  }

  IsWithinRadiusRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _latitude = $v.latitude;
      _longitude = $v.longitude;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(IsWithinRadiusRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$IsWithinRadiusRequest;
  }

  @override
  void update(void Function(IsWithinRadiusRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  IsWithinRadiusRequest build() => _build();

  _$IsWithinRadiusRequest _build() {
    final _$result = _$v ??
        new _$IsWithinRadiusRequest._(
            latitude: BuiltValueNullFieldError.checkNotNull(
                latitude, r'IsWithinRadiusRequest', 'latitude'),
            longitude: BuiltValueNullFieldError.checkNotNull(
                longitude, r'IsWithinRadiusRequest', 'longitude'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
