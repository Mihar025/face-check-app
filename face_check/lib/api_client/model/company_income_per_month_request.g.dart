// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'company_income_per_month_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CompanyIncomePerMonthRequest extends CompanyIncomePerMonthRequest {
  @override
  final num? companyIncomePerMonth;

  factory _$CompanyIncomePerMonthRequest(
          [void Function(CompanyIncomePerMonthRequestBuilder)? updates]) =>
      (new CompanyIncomePerMonthRequestBuilder()..update(updates))._build();

  _$CompanyIncomePerMonthRequest._({this.companyIncomePerMonth}) : super._();

  @override
  CompanyIncomePerMonthRequest rebuild(
          void Function(CompanyIncomePerMonthRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CompanyIncomePerMonthRequestBuilder toBuilder() =>
      new CompanyIncomePerMonthRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CompanyIncomePerMonthRequest &&
        companyIncomePerMonth == other.companyIncomePerMonth;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, companyIncomePerMonth.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CompanyIncomePerMonthRequest')
          ..add('companyIncomePerMonth', companyIncomePerMonth))
        .toString();
  }
}

class CompanyIncomePerMonthRequestBuilder
    implements
        Builder<CompanyIncomePerMonthRequest,
            CompanyIncomePerMonthRequestBuilder> {
  _$CompanyIncomePerMonthRequest? _$v;

  num? _companyIncomePerMonth;
  num? get companyIncomePerMonth => _$this._companyIncomePerMonth;
  set companyIncomePerMonth(num? companyIncomePerMonth) =>
      _$this._companyIncomePerMonth = companyIncomePerMonth;

  CompanyIncomePerMonthRequestBuilder() {
    CompanyIncomePerMonthRequest._defaults(this);
  }

  CompanyIncomePerMonthRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _companyIncomePerMonth = $v.companyIncomePerMonth;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CompanyIncomePerMonthRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CompanyIncomePerMonthRequest;
  }

  @override
  void update(void Function(CompanyIncomePerMonthRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CompanyIncomePerMonthRequest build() => _build();

  _$CompanyIncomePerMonthRequest _build() {
    final _$result = _$v ??
        new _$CompanyIncomePerMonthRequest._(
            companyIncomePerMonth: companyIncomePerMonth);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
