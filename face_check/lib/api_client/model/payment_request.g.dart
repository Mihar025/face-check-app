// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentRequest extends PaymentRequest {
  @override
  final num hourRate;
  @override
  final num overtimeRate;

  factory _$PaymentRequest([void Function(PaymentRequestBuilder)? updates]) =>
      (new PaymentRequestBuilder()..update(updates))._build();

  _$PaymentRequest._({required this.hourRate, required this.overtimeRate})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        hourRate, r'PaymentRequest', 'hourRate');
    BuiltValueNullFieldError.checkNotNull(
        overtimeRate, r'PaymentRequest', 'overtimeRate');
  }

  @override
  PaymentRequest rebuild(void Function(PaymentRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentRequestBuilder toBuilder() =>
      new PaymentRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentRequest &&
        hourRate == other.hourRate &&
        overtimeRate == other.overtimeRate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, hourRate.hashCode);
    _$hash = $jc(_$hash, overtimeRate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentRequest')
          ..add('hourRate', hourRate)
          ..add('overtimeRate', overtimeRate))
        .toString();
  }
}

class PaymentRequestBuilder
    implements Builder<PaymentRequest, PaymentRequestBuilder> {
  _$PaymentRequest? _$v;

  num? _hourRate;
  num? get hourRate => _$this._hourRate;
  set hourRate(num? hourRate) => _$this._hourRate = hourRate;

  num? _overtimeRate;
  num? get overtimeRate => _$this._overtimeRate;
  set overtimeRate(num? overtimeRate) => _$this._overtimeRate = overtimeRate;

  PaymentRequestBuilder() {
    PaymentRequest._defaults(this);
  }

  PaymentRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _hourRate = $v.hourRate;
      _overtimeRate = $v.overtimeRate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$PaymentRequest;
  }

  @override
  void update(void Function(PaymentRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentRequest build() => _build();

  _$PaymentRequest _build() {
    final _$result = _$v ??
        new _$PaymentRequest._(
            hourRate: BuiltValueNullFieldError.checkNotNull(
                hourRate, r'PaymentRequest', 'hourRate'),
            overtimeRate: BuiltValueNullFieldError.checkNotNull(
                overtimeRate, r'PaymentRequest', 'overtimeRate'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
