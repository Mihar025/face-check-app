// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'company_tax_calculation_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CompanyTaxCalculationResponse extends CompanyTaxCalculationResponse {
  @override
  final int? companyId;
  @override
  final String? companyName;
  @override
  final num? monthlyIncome;
  @override
  final num? totalTaxes;
  @override
  final num? socialSecurityTax;
  @override
  final num? medicareTax;
  @override
  final num? federalUnemploymentTax;
  @override
  final num? nyUnemploymentTax;
  @override
  final num? nyDisabilityInsurance;
  @override
  final num? workersCompensation;
  @override
  final int? employeeCount;
  @override
  final num? totalPayroll;
  @override
  final Date? calculationDate;

  factory _$CompanyTaxCalculationResponse(
          [void Function(CompanyTaxCalculationResponseBuilder)? updates]) =>
      (new CompanyTaxCalculationResponseBuilder()..update(updates))._build();

  _$CompanyTaxCalculationResponse._(
      {this.companyId,
      this.companyName,
      this.monthlyIncome,
      this.totalTaxes,
      this.socialSecurityTax,
      this.medicareTax,
      this.federalUnemploymentTax,
      this.nyUnemploymentTax,
      this.nyDisabilityInsurance,
      this.workersCompensation,
      this.employeeCount,
      this.totalPayroll,
      this.calculationDate})
      : super._();

  @override
  CompanyTaxCalculationResponse rebuild(
          void Function(CompanyTaxCalculationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CompanyTaxCalculationResponseBuilder toBuilder() =>
      new CompanyTaxCalculationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CompanyTaxCalculationResponse &&
        companyId == other.companyId &&
        companyName == other.companyName &&
        monthlyIncome == other.monthlyIncome &&
        totalTaxes == other.totalTaxes &&
        socialSecurityTax == other.socialSecurityTax &&
        medicareTax == other.medicareTax &&
        federalUnemploymentTax == other.federalUnemploymentTax &&
        nyUnemploymentTax == other.nyUnemploymentTax &&
        nyDisabilityInsurance == other.nyDisabilityInsurance &&
        workersCompensation == other.workersCompensation &&
        employeeCount == other.employeeCount &&
        totalPayroll == other.totalPayroll &&
        calculationDate == other.calculationDate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, companyId.hashCode);
    _$hash = $jc(_$hash, companyName.hashCode);
    _$hash = $jc(_$hash, monthlyIncome.hashCode);
    _$hash = $jc(_$hash, totalTaxes.hashCode);
    _$hash = $jc(_$hash, socialSecurityTax.hashCode);
    _$hash = $jc(_$hash, medicareTax.hashCode);
    _$hash = $jc(_$hash, federalUnemploymentTax.hashCode);
    _$hash = $jc(_$hash, nyUnemploymentTax.hashCode);
    _$hash = $jc(_$hash, nyDisabilityInsurance.hashCode);
    _$hash = $jc(_$hash, workersCompensation.hashCode);
    _$hash = $jc(_$hash, employeeCount.hashCode);
    _$hash = $jc(_$hash, totalPayroll.hashCode);
    _$hash = $jc(_$hash, calculationDate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CompanyTaxCalculationResponse')
          ..add('companyId', companyId)
          ..add('companyName', companyName)
          ..add('monthlyIncome', monthlyIncome)
          ..add('totalTaxes', totalTaxes)
          ..add('socialSecurityTax', socialSecurityTax)
          ..add('medicareTax', medicareTax)
          ..add('federalUnemploymentTax', federalUnemploymentTax)
          ..add('nyUnemploymentTax', nyUnemploymentTax)
          ..add('nyDisabilityInsurance', nyDisabilityInsurance)
          ..add('workersCompensation', workersCompensation)
          ..add('employeeCount', employeeCount)
          ..add('totalPayroll', totalPayroll)
          ..add('calculationDate', calculationDate))
        .toString();
  }
}

class CompanyTaxCalculationResponseBuilder
    implements
        Builder<CompanyTaxCalculationResponse,
            CompanyTaxCalculationResponseBuilder> {
  _$CompanyTaxCalculationResponse? _$v;

  int? _companyId;
  int? get companyId => _$this._companyId;
  set companyId(int? companyId) => _$this._companyId = companyId;

  String? _companyName;
  String? get companyName => _$this._companyName;
  set companyName(String? companyName) => _$this._companyName = companyName;

  num? _monthlyIncome;
  num? get monthlyIncome => _$this._monthlyIncome;
  set monthlyIncome(num? monthlyIncome) =>
      _$this._monthlyIncome = monthlyIncome;

  num? _totalTaxes;
  num? get totalTaxes => _$this._totalTaxes;
  set totalTaxes(num? totalTaxes) => _$this._totalTaxes = totalTaxes;

  num? _socialSecurityTax;
  num? get socialSecurityTax => _$this._socialSecurityTax;
  set socialSecurityTax(num? socialSecurityTax) =>
      _$this._socialSecurityTax = socialSecurityTax;

  num? _medicareTax;
  num? get medicareTax => _$this._medicareTax;
  set medicareTax(num? medicareTax) => _$this._medicareTax = medicareTax;

  num? _federalUnemploymentTax;
  num? get federalUnemploymentTax => _$this._federalUnemploymentTax;
  set federalUnemploymentTax(num? federalUnemploymentTax) =>
      _$this._federalUnemploymentTax = federalUnemploymentTax;

  num? _nyUnemploymentTax;
  num? get nyUnemploymentTax => _$this._nyUnemploymentTax;
  set nyUnemploymentTax(num? nyUnemploymentTax) =>
      _$this._nyUnemploymentTax = nyUnemploymentTax;

  num? _nyDisabilityInsurance;
  num? get nyDisabilityInsurance => _$this._nyDisabilityInsurance;
  set nyDisabilityInsurance(num? nyDisabilityInsurance) =>
      _$this._nyDisabilityInsurance = nyDisabilityInsurance;

  num? _workersCompensation;
  num? get workersCompensation => _$this._workersCompensation;
  set workersCompensation(num? workersCompensation) =>
      _$this._workersCompensation = workersCompensation;

  int? _employeeCount;
  int? get employeeCount => _$this._employeeCount;
  set employeeCount(int? employeeCount) =>
      _$this._employeeCount = employeeCount;

  num? _totalPayroll;
  num? get totalPayroll => _$this._totalPayroll;
  set totalPayroll(num? totalPayroll) => _$this._totalPayroll = totalPayroll;

  Date? _calculationDate;
  Date? get calculationDate => _$this._calculationDate;
  set calculationDate(Date? calculationDate) =>
      _$this._calculationDate = calculationDate;

  CompanyTaxCalculationResponseBuilder() {
    CompanyTaxCalculationResponse._defaults(this);
  }

  CompanyTaxCalculationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _companyId = $v.companyId;
      _companyName = $v.companyName;
      _monthlyIncome = $v.monthlyIncome;
      _totalTaxes = $v.totalTaxes;
      _socialSecurityTax = $v.socialSecurityTax;
      _medicareTax = $v.medicareTax;
      _federalUnemploymentTax = $v.federalUnemploymentTax;
      _nyUnemploymentTax = $v.nyUnemploymentTax;
      _nyDisabilityInsurance = $v.nyDisabilityInsurance;
      _workersCompensation = $v.workersCompensation;
      _employeeCount = $v.employeeCount;
      _totalPayroll = $v.totalPayroll;
      _calculationDate = $v.calculationDate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CompanyTaxCalculationResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CompanyTaxCalculationResponse;
  }

  @override
  void update(void Function(CompanyTaxCalculationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CompanyTaxCalculationResponse build() => _build();

  _$CompanyTaxCalculationResponse _build() {
    final _$result = _$v ??
        new _$CompanyTaxCalculationResponse._(
            companyId: companyId,
            companyName: companyName,
            monthlyIncome: monthlyIncome,
            totalTaxes: totalTaxes,
            socialSecurityTax: socialSecurityTax,
            medicareTax: medicareTax,
            federalUnemploymentTax: federalUnemploymentTax,
            nyUnemploymentTax: nyUnemploymentTax,
            nyDisabilityInsurance: nyDisabilityInsurance,
            workersCompensation: workersCompensation,
            employeeCount: employeeCount,
            totalPayroll: totalPayroll,
            calculationDate: calculationDate);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
