// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'punch_out_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PunchOutRequest extends PunchOutRequest {
  @override
  final int? workSiteId;
  @override
  final String? photoBase64;
  @override
  final double? latitude;
  @override
  final double? longitude;

  factory _$PunchOutRequest([void Function(PunchOutRequestBuilder)? updates]) =>
      (new PunchOutRequestBuilder()..update(updates))._build();

  _$PunchOutRequest._(
      {this.workSiteId, this.photoBase64, this.latitude, this.longitude})
      : super._();

  @override
  PunchOutRequest rebuild(void Function(PunchOutRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PunchOutRequestBuilder toBuilder() =>
      new PunchOutRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PunchOutRequest &&
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
    return (newBuiltValueToStringHelper(r'PunchOutRequest')
          ..add('workSiteId', workSiteId)
          ..add('photoBase64', photoBase64)
          ..add('latitude', latitude)
          ..add('longitude', longitude))
        .toString();
  }
}

class PunchOutRequestBuilder
    implements Builder<PunchOutRequest, PunchOutRequestBuilder> {
  _$PunchOutRequest? _$v;

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

  PunchOutRequestBuilder() {
    PunchOutRequest._defaults(this);
  }

  PunchOutRequestBuilder get _$this {
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
  void replace(PunchOutRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$PunchOutRequest;
  }

  @override
  void update(void Function(PunchOutRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PunchOutRequest build() => _build();

  _$PunchOutRequest _build() {
    final _$result = _$v ??
        new _$PunchOutRequest._(
            workSiteId: workSiteId,
            photoBase64: photoBase64,
            latitude: latitude,
            longitude: longitude);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
