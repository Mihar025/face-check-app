// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'set_new_custom_radius_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SetNewCustomRadiusResponse extends SetNewCustomRadiusResponse {
  @override
  final int? workSiteId;
  @override
  final double? customRadius;

  factory _$SetNewCustomRadiusResponse(
          [void Function(SetNewCustomRadiusResponseBuilder)? updates]) =>
      (new SetNewCustomRadiusResponseBuilder()..update(updates))._build();

  _$SetNewCustomRadiusResponse._({this.workSiteId, this.customRadius})
      : super._();

  @override
  SetNewCustomRadiusResponse rebuild(
          void Function(SetNewCustomRadiusResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SetNewCustomRadiusResponseBuilder toBuilder() =>
      new SetNewCustomRadiusResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SetNewCustomRadiusResponse &&
        workSiteId == other.workSiteId &&
        customRadius == other.customRadius;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, customRadius.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SetNewCustomRadiusResponse')
          ..add('workSiteId', workSiteId)
          ..add('customRadius', customRadius))
        .toString();
  }
}

class SetNewCustomRadiusResponseBuilder
    implements
        Builder<SetNewCustomRadiusResponse, SetNewCustomRadiusResponseBuilder> {
  _$SetNewCustomRadiusResponse? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  double? _customRadius;
  double? get customRadius => _$this._customRadius;
  set customRadius(double? customRadius) => _$this._customRadius = customRadius;

  SetNewCustomRadiusResponseBuilder() {
    SetNewCustomRadiusResponse._defaults(this);
  }

  SetNewCustomRadiusResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _customRadius = $v.customRadius;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SetNewCustomRadiusResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SetNewCustomRadiusResponse;
  }

  @override
  void update(void Function(SetNewCustomRadiusResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SetNewCustomRadiusResponse build() => _build();

  _$SetNewCustomRadiusResponse _build() {
    final _$result = _$v ??
        new _$SetNewCustomRadiusResponse._(
            workSiteId: workSiteId, customRadius: customRadius);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
