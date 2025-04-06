// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'related_user_in_company_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RelatedUserInCompanyResponse extends RelatedUserInCompanyResponse {
  @override
  final int? workerId;
  @override
  final int? companyId;
  @override
  final String? firstName;
  @override
  final String? lastName;
  @override
  final String? email;
  @override
  final num? baseHourlyRate;

  factory _$RelatedUserInCompanyResponse(
          [void Function(RelatedUserInCompanyResponseBuilder)? updates]) =>
      (new RelatedUserInCompanyResponseBuilder()..update(updates))._build();

  _$RelatedUserInCompanyResponse._(
      {this.workerId,
      this.companyId,
      this.firstName,
      this.lastName,
      this.email,
      this.baseHourlyRate})
      : super._();

  @override
  RelatedUserInCompanyResponse rebuild(
          void Function(RelatedUserInCompanyResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RelatedUserInCompanyResponseBuilder toBuilder() =>
      new RelatedUserInCompanyResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RelatedUserInCompanyResponse &&
        workerId == other.workerId &&
        companyId == other.companyId &&
        firstName == other.firstName &&
        lastName == other.lastName &&
        email == other.email &&
        baseHourlyRate == other.baseHourlyRate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workerId.hashCode);
    _$hash = $jc(_$hash, companyId.hashCode);
    _$hash = $jc(_$hash, firstName.hashCode);
    _$hash = $jc(_$hash, lastName.hashCode);
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, baseHourlyRate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RelatedUserInCompanyResponse')
          ..add('workerId', workerId)
          ..add('companyId', companyId)
          ..add('firstName', firstName)
          ..add('lastName', lastName)
          ..add('email', email)
          ..add('baseHourlyRate', baseHourlyRate))
        .toString();
  }
}

class RelatedUserInCompanyResponseBuilder
    implements
        Builder<RelatedUserInCompanyResponse,
            RelatedUserInCompanyResponseBuilder> {
  _$RelatedUserInCompanyResponse? _$v;

  int? _workerId;
  int? get workerId => _$this._workerId;
  set workerId(int? workerId) => _$this._workerId = workerId;

  int? _companyId;
  int? get companyId => _$this._companyId;
  set companyId(int? companyId) => _$this._companyId = companyId;

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

  RelatedUserInCompanyResponseBuilder() {
    RelatedUserInCompanyResponse._defaults(this);
  }

  RelatedUserInCompanyResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workerId = $v.workerId;
      _companyId = $v.companyId;
      _firstName = $v.firstName;
      _lastName = $v.lastName;
      _email = $v.email;
      _baseHourlyRate = $v.baseHourlyRate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RelatedUserInCompanyResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$RelatedUserInCompanyResponse;
  }

  @override
  void update(void Function(RelatedUserInCompanyResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RelatedUserInCompanyResponse build() => _build();

  _$RelatedUserInCompanyResponse _build() {
    final _$result = _$v ??
        new _$RelatedUserInCompanyResponse._(
            workerId: workerId,
            companyId: companyId,
            firstName: firstName,
            lastName: lastName,
            email: email,
            baseHourlyRate: baseHourlyRate);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
