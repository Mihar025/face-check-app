// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteRequest extends WorkSiteRequest {
  @override
  final String workSiteName;
  @override
  final String address;
  @override
  final double latitude;
  @override
  final double longitude;
  @override
  final double allowedRadius;
  @override
  final LocalTime workDayStart;
  @override
  final LocalTime workDayEnd;

  factory _$WorkSiteRequest([void Function(WorkSiteRequestBuilder)? updates]) =>
      (new WorkSiteRequestBuilder()..update(updates))._build();

  _$WorkSiteRequest._(
      {required this.workSiteName,
      required this.address,
      required this.latitude,
      required this.longitude,
      required this.allowedRadius,
      required this.workDayStart,
      required this.workDayEnd})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        workSiteName, r'WorkSiteRequest', 'workSiteName');
    BuiltValueNullFieldError.checkNotNull(
        address, r'WorkSiteRequest', 'address');
    BuiltValueNullFieldError.checkNotNull(
        latitude, r'WorkSiteRequest', 'latitude');
    BuiltValueNullFieldError.checkNotNull(
        longitude, r'WorkSiteRequest', 'longitude');
    BuiltValueNullFieldError.checkNotNull(
        allowedRadius, r'WorkSiteRequest', 'allowedRadius');
    BuiltValueNullFieldError.checkNotNull(
        workDayStart, r'WorkSiteRequest', 'workDayStart');
    BuiltValueNullFieldError.checkNotNull(
        workDayEnd, r'WorkSiteRequest', 'workDayEnd');
  }

  @override
  WorkSiteRequest rebuild(void Function(WorkSiteRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteRequestBuilder toBuilder() =>
      new WorkSiteRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteRequest &&
        workSiteName == other.workSiteName &&
        address == other.address &&
        latitude == other.latitude &&
        longitude == other.longitude &&
        allowedRadius == other.allowedRadius &&
        workDayStart == other.workDayStart &&
        workDayEnd == other.workDayEnd;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteName.hashCode);
    _$hash = $jc(_$hash, address.hashCode);
    _$hash = $jc(_$hash, latitude.hashCode);
    _$hash = $jc(_$hash, longitude.hashCode);
    _$hash = $jc(_$hash, allowedRadius.hashCode);
    _$hash = $jc(_$hash, workDayStart.hashCode);
    _$hash = $jc(_$hash, workDayEnd.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteRequest')
          ..add('workSiteName', workSiteName)
          ..add('address', address)
          ..add('latitude', latitude)
          ..add('longitude', longitude)
          ..add('allowedRadius', allowedRadius)
          ..add('workDayStart', workDayStart)
          ..add('workDayEnd', workDayEnd))
        .toString();
  }
}

class WorkSiteRequestBuilder
    implements Builder<WorkSiteRequest, WorkSiteRequestBuilder> {
  _$WorkSiteRequest? _$v;

  String? _workSiteName;
  String? get workSiteName => _$this._workSiteName;
  set workSiteName(String? workSiteName) => _$this._workSiteName = workSiteName;

  String? _address;
  String? get address => _$this._address;
  set address(String? address) => _$this._address = address;

  double? _latitude;
  double? get latitude => _$this._latitude;
  set latitude(double? latitude) => _$this._latitude = latitude;

  double? _longitude;
  double? get longitude => _$this._longitude;
  set longitude(double? longitude) => _$this._longitude = longitude;

  double? _allowedRadius;
  double? get allowedRadius => _$this._allowedRadius;
  set allowedRadius(double? allowedRadius) =>
      _$this._allowedRadius = allowedRadius;

  LocalTimeBuilder? _workDayStart;
  LocalTimeBuilder get workDayStart =>
      _$this._workDayStart ??= new LocalTimeBuilder();
  set workDayStart(LocalTimeBuilder? workDayStart) =>
      _$this._workDayStart = workDayStart;

  LocalTimeBuilder? _workDayEnd;
  LocalTimeBuilder get workDayEnd =>
      _$this._workDayEnd ??= new LocalTimeBuilder();
  set workDayEnd(LocalTimeBuilder? workDayEnd) =>
      _$this._workDayEnd = workDayEnd;

  WorkSiteRequestBuilder() {
    WorkSiteRequest._defaults(this);
  }

  WorkSiteRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteName = $v.workSiteName;
      _address = $v.address;
      _latitude = $v.latitude;
      _longitude = $v.longitude;
      _allowedRadius = $v.allowedRadius;
      _workDayStart = $v.workDayStart.toBuilder();
      _workDayEnd = $v.workDayEnd.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteRequest;
  }

  @override
  void update(void Function(WorkSiteRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteRequest build() => _build();

  _$WorkSiteRequest _build() {
    _$WorkSiteRequest _$result;
    try {
      _$result = _$v ??
          new _$WorkSiteRequest._(
              workSiteName: BuiltValueNullFieldError.checkNotNull(
                  workSiteName, r'WorkSiteRequest', 'workSiteName'),
              address: BuiltValueNullFieldError.checkNotNull(
                  address, r'WorkSiteRequest', 'address'),
              latitude: BuiltValueNullFieldError.checkNotNull(
                  latitude, r'WorkSiteRequest', 'latitude'),
              longitude: BuiltValueNullFieldError.checkNotNull(
                  longitude, r'WorkSiteRequest', 'longitude'),
              allowedRadius: BuiltValueNullFieldError.checkNotNull(
                  allowedRadius, r'WorkSiteRequest', 'allowedRadius'),
              workDayStart: workDayStart.build(),
              workDayEnd: workDayEnd.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'workDayStart';
        workDayStart.build();
        _$failedField = 'workDayEnd';
        workDayEnd.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WorkSiteRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
