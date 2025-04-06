// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'company_tax_calculation_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CompanyTaxCalculationRequest extends CompanyTaxCalculationRequest {
  @override
  final int year;
  @override
  final int month;

  factory _$CompanyTaxCalculationRequest(
          [void Function(CompanyTaxCalculationRequestBuilder)? updates]) =>
      (new CompanyTaxCalculationRequestBuilder()..update(updates))._build();

  _$CompanyTaxCalculationRequest._({required this.year, required this.month})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        year, r'CompanyTaxCalculationRequest', 'year');
    BuiltValueNullFieldError.checkNotNull(
        month, r'CompanyTaxCalculationRequest', 'month');
  }

  @override
  CompanyTaxCalculationRequest rebuild(
          void Function(CompanyTaxCalculationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CompanyTaxCalculationRequestBuilder toBuilder() =>
      new CompanyTaxCalculationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CompanyTaxCalculationRequest &&
        year == other.year &&
        month == other.month;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, year.hashCode);
    _$hash = $jc(_$hash, month.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CompanyTaxCalculationRequest')
          ..add('year', year)
          ..add('month', month))
        .toString();
  }
}

class CompanyTaxCalculationRequestBuilder
    implements
        Builder<CompanyTaxCalculationRequest,
            CompanyTaxCalculationRequestBuilder> {
  _$CompanyTaxCalculationRequest? _$v;

  int? _year;
  int? get year => _$this._year;
  set year(int? year) => _$this._year = year;

  int? _month;
  int? get month => _$this._month;
  set month(int? month) => _$this._month = month;

  CompanyTaxCalculationRequestBuilder() {
    CompanyTaxCalculationRequest._defaults(this);
  }

  CompanyTaxCalculationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _year = $v.year;
      _month = $v.month;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CompanyTaxCalculationRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CompanyTaxCalculationRequest;
  }

  @override
  void update(void Function(CompanyTaxCalculationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CompanyTaxCalculationRequest build() => _build();

  _$CompanyTaxCalculationRequest _build() {
    final _$result = _$v ??
        new _$CompanyTaxCalculationRequest._(
            year: BuiltValueNullFieldError.checkNotNull(
                year, r'CompanyTaxCalculationRequest', 'year'),
            month: BuiltValueNullFieldError.checkNotNull(
                month, r'CompanyTaxCalculationRequest', 'month'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
