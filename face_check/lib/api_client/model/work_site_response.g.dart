// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteResponse extends WorkSiteResponse {
  @override
  final int? workSiteId;
  @override
  final String? workSiteName;
  @override
  final String? address;
  @override
  final double? latitude;
  @override
  final double? longitude;
  @override
  final double? allowedRadius;
  @override
  final LocalTime? workDayStart;
  @override
  final LocalTime? workDayEnd;

  factory _$WorkSiteResponse(
          [void Function(WorkSiteResponseBuilder)? updates]) =>
      (new WorkSiteResponseBuilder()..update(updates))._build();

  _$WorkSiteResponse._(
      {this.workSiteId,
      this.workSiteName,
      this.address,
      this.latitude,
      this.longitude,
      this.allowedRadius,
      this.workDayStart,
      this.workDayEnd})
      : super._();

  @override
  WorkSiteResponse rebuild(void Function(WorkSiteResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteResponseBuilder toBuilder() =>
      new WorkSiteResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteResponse &&
        workSiteId == other.workSiteId &&
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
    _$hash = $jc(_$hash, workSiteId.hashCode);
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
    return (newBuiltValueToStringHelper(r'WorkSiteResponse')
          ..add('workSiteId', workSiteId)
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

class WorkSiteResponseBuilder
    implements Builder<WorkSiteResponse, WorkSiteResponseBuilder> {
  _$WorkSiteResponse? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

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

  WorkSiteResponseBuilder() {
    WorkSiteResponse._defaults(this);
  }

  WorkSiteResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _workSiteName = $v.workSiteName;
      _address = $v.address;
      _latitude = $v.latitude;
      _longitude = $v.longitude;
      _allowedRadius = $v.allowedRadius;
      _workDayStart = $v.workDayStart?.toBuilder();
      _workDayEnd = $v.workDayEnd?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteResponse;
  }

  @override
  void update(void Function(WorkSiteResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteResponse build() => _build();

  _$WorkSiteResponse _build() {
    _$WorkSiteResponse _$result;
    try {
      _$result = _$v ??
          new _$WorkSiteResponse._(
              workSiteId: workSiteId,
              workSiteName: workSiteName,
              address: address,
              latitude: latitude,
              longitude: longitude,
              allowedRadius: allowedRadius,
              workDayStart: _workDayStart?.build(),
              workDayEnd: _workDayEnd?.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'workDayStart';
        _workDayStart?.build();
        _$failedField = 'workDayEnd';
        _workDayEnd?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WorkSiteResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
