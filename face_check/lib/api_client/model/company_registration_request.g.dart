// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'company_registration_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CompanyRegistrationRequest extends CompanyRegistrationRequest {
  @override
  final String companyName;
  @override
  final String companyAddress;
  @override
  final String companyPhone;
  @override
  final String companyEmail;

  factory _$CompanyRegistrationRequest(
          [void Function(CompanyRegistrationRequestBuilder)? updates]) =>
      (new CompanyRegistrationRequestBuilder()..update(updates))._build();

  _$CompanyRegistrationRequest._(
      {required this.companyName,
      required this.companyAddress,
      required this.companyPhone,
      required this.companyEmail})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        companyName, r'CompanyRegistrationRequest', 'companyName');
    BuiltValueNullFieldError.checkNotNull(
        companyAddress, r'CompanyRegistrationRequest', 'companyAddress');
    BuiltValueNullFieldError.checkNotNull(
        companyPhone, r'CompanyRegistrationRequest', 'companyPhone');
    BuiltValueNullFieldError.checkNotNull(
        companyEmail, r'CompanyRegistrationRequest', 'companyEmail');
  }

  @override
  CompanyRegistrationRequest rebuild(
          void Function(CompanyRegistrationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CompanyRegistrationRequestBuilder toBuilder() =>
      new CompanyRegistrationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CompanyRegistrationRequest &&
        companyName == other.companyName &&
        companyAddress == other.companyAddress &&
        companyPhone == other.companyPhone &&
        companyEmail == other.companyEmail;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, companyName.hashCode);
    _$hash = $jc(_$hash, companyAddress.hashCode);
    _$hash = $jc(_$hash, companyPhone.hashCode);
    _$hash = $jc(_$hash, companyEmail.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CompanyRegistrationRequest')
          ..add('companyName', companyName)
          ..add('companyAddress', companyAddress)
          ..add('companyPhone', companyPhone)
          ..add('companyEmail', companyEmail))
        .toString();
  }
}

class CompanyRegistrationRequestBuilder
    implements
        Builder<CompanyRegistrationRequest, CompanyRegistrationRequestBuilder> {
  _$CompanyRegistrationRequest? _$v;

  String? _companyName;
  String? get companyName => _$this._companyName;
  set companyName(String? companyName) => _$this._companyName = companyName;

  String? _companyAddress;
  String? get companyAddress => _$this._companyAddress;
  set companyAddress(String? companyAddress) =>
      _$this._companyAddress = companyAddress;

  String? _companyPhone;
  String? get companyPhone => _$this._companyPhone;
  set companyPhone(String? companyPhone) => _$this._companyPhone = companyPhone;

  String? _companyEmail;
  String? get companyEmail => _$this._companyEmail;
  set companyEmail(String? companyEmail) => _$this._companyEmail = companyEmail;

  CompanyRegistrationRequestBuilder() {
    CompanyRegistrationRequest._defaults(this);
  }

  CompanyRegistrationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _companyName = $v.companyName;
      _companyAddress = $v.companyAddress;
      _companyPhone = $v.companyPhone;
      _companyEmail = $v.companyEmail;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CompanyRegistrationRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CompanyRegistrationRequest;
  }

  @override
  void update(void Function(CompanyRegistrationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CompanyRegistrationRequest build() => _build();

  _$CompanyRegistrationRequest _build() {
    final _$result = _$v ??
        new _$CompanyRegistrationRequest._(
            companyName: BuiltValueNullFieldError.checkNotNull(
                companyName, r'CompanyRegistrationRequest', 'companyName'),
            companyAddress: BuiltValueNullFieldError.checkNotNull(
                companyAddress,
                r'CompanyRegistrationRequest',
                'companyAddress'),
            companyPhone: BuiltValueNullFieldError.checkNotNull(
                companyPhone, r'CompanyRegistrationRequest', 'companyPhone'),
            companyEmail: BuiltValueNullFieldError.checkNotNull(
                companyEmail, r'CompanyRegistrationRequest', 'companyEmail'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
