// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_update_location_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteUpdateLocationResponse extends WorkSiteUpdateLocationResponse {
  @override
  final int? workSiteId;
  @override
  final double? newLatitude;
  @override
  final double? newLongitude;
  @override
  final double? newRadius;

  factory _$WorkSiteUpdateLocationResponse(
          [void Function(WorkSiteUpdateLocationResponseBuilder)? updates]) =>
      (new WorkSiteUpdateLocationResponseBuilder()..update(updates))._build();

  _$WorkSiteUpdateLocationResponse._(
      {this.workSiteId, this.newLatitude, this.newLongitude, this.newRadius})
      : super._();

  @override
  WorkSiteUpdateLocationResponse rebuild(
          void Function(WorkSiteUpdateLocationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteUpdateLocationResponseBuilder toBuilder() =>
      new WorkSiteUpdateLocationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteUpdateLocationResponse &&
        workSiteId == other.workSiteId &&
        newLatitude == other.newLatitude &&
        newLongitude == other.newLongitude &&
        newRadius == other.newRadius;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, newLatitude.hashCode);
    _$hash = $jc(_$hash, newLongitude.hashCode);
    _$hash = $jc(_$hash, newRadius.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteUpdateLocationResponse')
          ..add('workSiteId', workSiteId)
          ..add('newLatitude', newLatitude)
          ..add('newLongitude', newLongitude)
          ..add('newRadius', newRadius))
        .toString();
  }
}

class WorkSiteUpdateLocationResponseBuilder
    implements
        Builder<WorkSiteUpdateLocationResponse,
            WorkSiteUpdateLocationResponseBuilder> {
  _$WorkSiteUpdateLocationResponse? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  double? _newLatitude;
  double? get newLatitude => _$this._newLatitude;
  set newLatitude(double? newLatitude) => _$this._newLatitude = newLatitude;

  double? _newLongitude;
  double? get newLongitude => _$this._newLongitude;
  set newLongitude(double? newLongitude) => _$this._newLongitude = newLongitude;

  double? _newRadius;
  double? get newRadius => _$this._newRadius;
  set newRadius(double? newRadius) => _$this._newRadius = newRadius;

  WorkSiteUpdateLocationResponseBuilder() {
    WorkSiteUpdateLocationResponse._defaults(this);
  }

  WorkSiteUpdateLocationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _newLatitude = $v.newLatitude;
      _newLongitude = $v.newLongitude;
      _newRadius = $v.newRadius;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteUpdateLocationResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteUpdateLocationResponse;
  }

  @override
  void update(void Function(WorkSiteUpdateLocationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteUpdateLocationResponse build() => _build();

  _$WorkSiteUpdateLocationResponse _build() {
    final _$result = _$v ??
        new _$WorkSiteUpdateLocationResponse._(
            workSiteId: workSiteId,
            newLatitude: newLatitude,
            newLongitude: newLongitude,
            newRadius: newRadius);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
