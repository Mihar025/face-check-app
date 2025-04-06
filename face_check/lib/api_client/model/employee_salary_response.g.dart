// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'employee_salary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$EmployeeSalaryResponse extends EmployeeSalaryResponse {
  @override
  final int? employeeId;
  @override
  final String? firstName;
  @override
  final String? lastName;
  @override
  final String? email;
  @override
  final num? baseHourlyRate;

  factory _$EmployeeSalaryResponse(
          [void Function(EmployeeSalaryResponseBuilder)? updates]) =>
      (new EmployeeSalaryResponseBuilder()..update(updates))._build();

  _$EmployeeSalaryResponse._(
      {this.employeeId,
      this.firstName,
      this.lastName,
      this.email,
      this.baseHourlyRate})
      : super._();

  @override
  EmployeeSalaryResponse rebuild(
          void Function(EmployeeSalaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  EmployeeSalaryResponseBuilder toBuilder() =>
      new EmployeeSalaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is EmployeeSalaryResponse &&
        employeeId == other.employeeId &&
        firstName == other.firstName &&
        lastName == other.lastName &&
        email == other.email &&
        baseHourlyRate == other.baseHourlyRate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, employeeId.hashCode);
    _$hash = $jc(_$hash, firstName.hashCode);
    _$hash = $jc(_$hash, lastName.hashCode);
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, baseHourlyRate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'EmployeeSalaryResponse')
          ..add('employeeId', employeeId)
          ..add('firstName', firstName)
          ..add('lastName', lastName)
          ..add('email', email)
          ..add('baseHourlyRate', baseHourlyRate))
        .toString();
  }
}

class EmployeeSalaryResponseBuilder
    implements Builder<EmployeeSalaryResponse, EmployeeSalaryResponseBuilder> {
  _$EmployeeSalaryResponse? _$v;

  int? _employeeId;
  int? get employeeId => _$this._employeeId;
  set employeeId(int? employeeId) => _$this._employeeId = employeeId;

  String? _firstName;
  String? get firstName => _$this._firstName;
  set firstName(String? firstName) => _$this._firstName = firstName;

  String? _lastName;
  String? get lastName => _$this._lastName;
  set lastName(String? lastName) => _$this._lastName = lastName;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  num? _baseHourlyRate;
  num? get baseHourlyRate => _$this._baseHourlyRate;
  set baseHourlyRate(num? baseHourlyRate) =>
      _$this._baseHourlyRate = baseHourlyRate;

  EmployeeSalaryResponseBuilder() {
    EmployeeSalaryResponse._defaults(this);
  }

  EmployeeSalaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _employeeId = $v.employeeId;
      _firstName = $v.firstName;
      _lastName = $v.lastName;
      _email = $v.email;
      _baseHourlyRate = $v.baseHourlyRate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(EmployeeSalaryResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$EmployeeSalaryResponse;
  }

  @override
  void update(void Function(EmployeeSalaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  EmployeeSalaryResponse build() => _build();

  _$EmployeeSalaryResponse _build() {
    final _$result = _$v ??
        new _$EmployeeSalaryResponse._(
            employeeId: employeeId,
            firstName: firstName,
            lastName: lastName,
            email: email,
            baseHourlyRate: baseHourlyRate);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
