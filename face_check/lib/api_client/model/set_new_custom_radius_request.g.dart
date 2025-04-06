// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'set_new_custom_radius_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SetNewCustomRadiusRequest extends SetNewCustomRadiusRequest {
  @override
  final double customRadius;

  factory _$SetNewCustomRadiusRequest(
          [void Function(SetNewCustomRadiusRequestBuilder)? updates]) =>
      (new SetNewCustomRadiusRequestBuilder()..update(updates))._build();

  _$SetNewCustomRadiusRequest._({required this.customRadius}) : super._() {
    BuiltValueNullFieldError.checkNotNull(
        customRadius, r'SetNewCustomRadiusRequest', 'customRadius');
  }

  @override
  SetNewCustomRadiusRequest rebuild(
          void Function(SetNewCustomRadiusRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SetNewCustomRadiusRequestBuilder toBuilder() =>
      new SetNewCustomRadiusRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SetNewCustomRadiusRequest &&
        customRadius == other.customRadius;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, customRadius.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SetNewCustomRadiusRequest')
          ..add('customRadius', customRadius))
        .toString();
  }
}

class SetNewCustomRadiusRequestBuilder
    implements
        Builder<SetNewCustomRadiusRequest, SetNewCustomRadiusRequestBuilder> {
  _$SetNewCustomRadiusRequest? _$v;

  double? _customRadius;
  double? get customRadius => _$this._customRadius;
  set customRadius(double? customRadius) => _$this._customRadius = customRadius;

  SetNewCustomRadiusRequestBuilder() {
    SetNewCustomRadiusRequest._defaults(this);
  }

  SetNewCustomRadiusRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _customRadius = $v.customRadius;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SetNewCustomRadiusRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SetNewCustomRadiusRequest;
  }

  @override
  void update(void Function(SetNewCustomRadiusRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SetNewCustomRadiusRequest build() => _build();

  _$SetNewCustomRadiusRequest _build() {
    final _$result = _$v ??
        new _$SetNewCustomRadiusRequest._(
            customRadius: BuiltValueNullFieldError.checkNotNull(
                customRadius, r'SetNewCustomRadiusRequest', 'customRadius'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
