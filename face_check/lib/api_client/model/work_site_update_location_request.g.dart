// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_update_location_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteUpdateLocationRequest extends WorkSiteUpdateLocationRequest {
  @override
  final double newLatitude;
  @override
  final double newLongitude;
  @override
  final double newRadius;

  factory _$WorkSiteUpdateLocationRequest(
          [void Function(WorkSiteUpdateLocationRequestBuilder)? updates]) =>
      (new WorkSiteUpdateLocationRequestBuilder()..update(updates))._build();

  _$WorkSiteUpdateLocationRequest._(
      {required this.newLatitude,
      required this.newLongitude,
      required this.newRadius})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        newLatitude, r'WorkSiteUpdateLocationRequest', 'newLatitude');
    BuiltValueNullFieldError.checkNotNull(
        newLongitude, r'WorkSiteUpdateLocationRequest', 'newLongitude');
    BuiltValueNullFieldError.checkNotNull(
        newRadius, r'WorkSiteUpdateLocationRequest', 'newRadius');
  }

  @override
  WorkSiteUpdateLocationRequest rebuild(
          void Function(WorkSiteUpdateLocationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteUpdateLocationRequestBuilder toBuilder() =>
      new WorkSiteUpdateLocationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteUpdateLocationRequest &&
        newLatitude == other.newLatitude &&
        newLongitude == other.newLongitude &&
        newRadius == other.newRadius;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, newLatitude.hashCode);
    _$hash = $jc(_$hash, newLongitude.hashCode);
    _$hash = $jc(_$hash, newRadius.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteUpdateLocationRequest')
          ..add('newLatitude', newLatitude)
          ..add('newLongitude', newLongitude)
          ..add('newRadius', newRadius))
        .toString();
  }
}

class WorkSiteUpdateLocationRequestBuilder
    implements
        Builder<WorkSiteUpdateLocationRequest,
            WorkSiteUpdateLocationRequestBuilder> {
  _$WorkSiteUpdateLocationRequest? _$v;

  double? _newLatitude;
  double? get newLatitude => _$this._newLatitude;
  set newLatitude(double? newLatitude) => _$this._newLatitude = newLatitude;

  double? _newLongitude;
  double? get newLongitude => _$this._newLongitude;
  set newLongitude(double? newLongitude) => _$this._newLongitude = newLongitude;

  double? _newRadius;
  double? get newRadius => _$this._newRadius;
  set newRadius(double? newRadius) => _$this._newRadius = newRadius;

  WorkSiteUpdateLocationRequestBuilder() {
    WorkSiteUpdateLocationRequest._defaults(this);
  }

  WorkSiteUpdateLocationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _newLatitude = $v.newLatitude;
      _newLongitude = $v.newLongitude;
      _newRadius = $v.newRadius;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteUpdateLocationRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteUpdateLocationRequest;
  }

  @override
  void update(void Function(WorkSiteUpdateLocationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteUpdateLocationRequest build() => _build();

  _$WorkSiteUpdateLocationRequest _build() {
    final _$result = _$v ??
        new _$WorkSiteUpdateLocationRequest._(
            newLatitude: BuiltValueNullFieldError.checkNotNull(
                newLatitude, r'WorkSiteUpdateLocationRequest', 'newLatitude'),
            newLongitude: BuiltValueNullFieldError.checkNotNull(
                newLongitude, r'WorkSiteUpdateLocationRequest', 'newLongitude'),
            newRadius: BuiltValueNullFieldError.checkNotNull(
                newRadius, r'WorkSiteUpdateLocationRequest', 'newRadius'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
