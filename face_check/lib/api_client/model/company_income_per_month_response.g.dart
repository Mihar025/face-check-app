// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'company_income_per_month_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CompanyIncomePerMonthResponse extends CompanyIncomePerMonthResponse {
  @override
  final int? companyId;
  @override
  final num? companyIncomePerMonth;

  factory _$CompanyIncomePerMonthResponse(
          [void Function(CompanyIncomePerMonthResponseBuilder)? updates]) =>
      (new CompanyIncomePerMonthResponseBuilder()..update(updates))._build();

  _$CompanyIncomePerMonthResponse._(
      {this.companyId, this.companyIncomePerMonth})
      : super._();

  @override
  CompanyIncomePerMonthResponse rebuild(
          void Function(CompanyIncomePerMonthResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CompanyIncomePerMonthResponseBuilder toBuilder() =>
      new CompanyIncomePerMonthResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CompanyIncomePerMonthResponse &&
        companyId == other.companyId &&
        companyIncomePerMonth == other.companyIncomePerMonth;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, companyId.hashCode);
    _$hash = $jc(_$hash, companyIncomePerMonth.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CompanyIncomePerMonthResponse')
          ..add('companyId', companyId)
          ..add('companyIncomePerMonth', companyIncomePerMonth))
        .toString();
  }
}

class CompanyIncomePerMonthResponseBuilder
    implements
        Builder<CompanyIncomePerMonthResponse,
            CompanyIncomePerMonthResponseBuilder> {
  _$CompanyIncomePerMonthResponse? _$v;

  int? _companyId;
  int? get companyId => _$this._companyId;
  set companyId(int? companyId) => _$this._companyId = companyId;

  num? _companyIncomePerMonth;
  num? get companyIncomePerMonth => _$this._companyIncomePerMonth;
  set companyIncomePerMonth(num? companyIncomePerMonth) =>
      _$this._companyIncomePerMonth = companyIncomePerMonth;

  CompanyIncomePerMonthResponseBuilder() {
    CompanyIncomePerMonthResponse._defaults(this);
  }

  CompanyIncomePerMonthResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _companyId = $v.companyId;
      _companyIncomePerMonth = $v.companyIncomePerMonth;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CompanyIncomePerMonthResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CompanyIncomePerMonthResponse;
  }

  @override
  void update(void Function(CompanyIncomePerMonthResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CompanyIncomePerMonthResponse build() => _build();

  _$CompanyIncomePerMonthResponse _build() {
    final _$result = _$v ??
        new _$CompanyIncomePerMonthResponse._(
            companyId: companyId, companyIncomePerMonth: companyIncomePerMonth);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
