// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'employee_raise_hour_rate_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$EmployeeRaiseHourRateRequest extends EmployeeRaiseHourRateRequest {
  @override
  final num baseHourlyRate;

  factory _$EmployeeRaiseHourRateRequest(
          [void Function(EmployeeRaiseHourRateRequestBuilder)? updates]) =>
      (new EmployeeRaiseHourRateRequestBuilder()..update(updates))._build();

  _$EmployeeRaiseHourRateRequest._({required this.baseHourlyRate}) : super._() {
    BuiltValueNullFieldError.checkNotNull(
        baseHourlyRate, r'EmployeeRaiseHourRateRequest', 'baseHourlyRate');
  }

  @override
  EmployeeRaiseHourRateRequest rebuild(
          void Function(EmployeeRaiseHourRateRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  EmployeeRaiseHourRateRequestBuilder toBuilder() =>
      new EmployeeRaiseHourRateRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is EmployeeRaiseHourRateRequest &&
        baseHourlyRate == other.baseHourlyRate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, baseHourlyRate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'EmployeeRaiseHourRateRequest')
          ..add('baseHourlyRate', baseHourlyRate))
        .toString();
  }
}

class EmployeeRaiseHourRateRequestBuilder
    implements
        Builder<EmployeeRaiseHourRateRequest,
            EmployeeRaiseHourRateRequestBuilder> {
  _$EmployeeRaiseHourRateRequest? _$v;

  num? _baseHourlyRate;
  num? get baseHourlyRate => _$this._baseHourlyRate;
  set baseHourlyRate(num? baseHourlyRate) =>
      _$this._baseHourlyRate = baseHourlyRate;

  EmployeeRaiseHourRateRequestBuilder() {
    EmployeeRaiseHourRateRequest._defaults(this);
  }

  EmployeeRaiseHourRateRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _baseHourlyRate = $v.baseHourlyRate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(EmployeeRaiseHourRateRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$EmployeeRaiseHourRateRequest;
  }

  @override
  void update(void Function(EmployeeRaiseHourRateRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  EmployeeRaiseHourRateRequest build() => _build();

  _$EmployeeRaiseHourRateRequest _build() {
    final _$result = _$v ??
        new _$EmployeeRaiseHourRateRequest._(
            baseHourlyRate: BuiltValueNullFieldError.checkNotNull(
                baseHourlyRate,
                r'EmployeeRaiseHourRateRequest',
                'baseHourlyRate'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
