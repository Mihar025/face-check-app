// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'punch_in_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PunchInRequest extends PunchInRequest {
  @override
  final int? workSiteId;
  @override
  final String? photoBase64;
  @override
  final double? latitude;
  @override
  final double? longitude;

  factory _$PunchInRequest([void Function(PunchInRequestBuilder)? updates]) =>
      (new PunchInRequestBuilder()..update(updates))._build();

  _$PunchInRequest._(
      {this.workSiteId, this.photoBase64, this.latitude, this.longitude})
      : super._();

  @override
  PunchInRequest rebuild(void Function(PunchInRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PunchInRequestBuilder toBuilder() =>
      new PunchInRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PunchInRequest &&
        workSiteId == other.workSiteId &&
        photoBase64 == other.photoBase64 &&
        latitude == other.latitude &&
        longitude == other.longitude;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, photoBase64.hashCode);
    _$hash = $jc(_$hash, latitude.hashCode);
    _$hash = $jc(_$hash, longitude.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PunchInRequest')
          ..add('workSiteId', workSiteId)
          ..add('photoBase64', photoBase64)
          ..add('latitude', latitude)
          ..add('longitude', longitude))
        .toString();
  }
}

class PunchInRequestBuilder
    implements Builder<PunchInRequest, PunchInRequestBuilder> {
  _$PunchInRequest? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  String? _photoBase64;
  String? get photoBase64 => _$this._photoBase64;
  set photoBase64(String? photoBase64) => _$this._photoBase64 = photoBase64;

  double? _latitude;
  double? get latitude => _$this._latitude;
  set latitude(double? latitude) => _$this._latitude = latitude;

  double? _longitude;
  double? get longitude => _$this._longitude;
  set longitude(double? longitude) => _$this._longitude = longitude;

  PunchInRequestBuilder() {
    PunchInRequest._defaults(this);
  }

  PunchInRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _photoBase64 = $v.photoBase64;
      _latitude = $v.latitude;
      _longitude = $v.longitude;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PunchInRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$PunchInRequest;
  }

  @override
  void update(void Function(PunchInRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PunchInRequest build() => _build();

  _$PunchInRequest _build() {
    final _$result = _$v ??
        new _$PunchInRequest._(
            workSiteId: workSiteId,
            photoBase64: photoBase64,
            latitude: latitude,
            longitude: longitude);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
