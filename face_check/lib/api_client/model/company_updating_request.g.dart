// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'company_updating_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CompanyUpdatingRequest extends CompanyUpdatingRequest {
  @override
  final String companyName;
  @override
  final String companyAddress;
  @override
  final String companyPhone;
  @override
  final String companyEmail;
  @override
  final int workersQuantity;

  factory _$CompanyUpdatingRequest(
          [void Function(CompanyUpdatingRequestBuilder)? updates]) =>
      (new CompanyUpdatingRequestBuilder()..update(updates))._build();

  _$CompanyUpdatingRequest._(
      {required this.companyName,
      required this.companyAddress,
      required this.companyPhone,
      required this.companyEmail,
      required this.workersQuantity})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        companyName, r'CompanyUpdatingRequest', 'companyName');
    BuiltValueNullFieldError.checkNotNull(
        companyAddress, r'CompanyUpdatingRequest', 'companyAddress');
    BuiltValueNullFieldError.checkNotNull(
        companyPhone, r'CompanyUpdatingRequest', 'companyPhone');
    BuiltValueNullFieldError.checkNotNull(
        companyEmail, r'CompanyUpdatingRequest', 'companyEmail');
    BuiltValueNullFieldError.checkNotNull(
        workersQuantity, r'CompanyUpdatingRequest', 'workersQuantity');
  }

  @override
  CompanyUpdatingRequest rebuild(
          void Function(CompanyUpdatingRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CompanyUpdatingRequestBuilder toBuilder() =>
      new CompanyUpdatingRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CompanyUpdatingRequest &&
        companyName == other.companyName &&
        companyAddress == other.companyAddress &&
        companyPhone == other.companyPhone &&
        companyEmail == other.companyEmail &&
        workersQuantity == other.workersQuantity;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, companyName.hashCode);
    _$hash = $jc(_$hash, companyAddress.hashCode);
    _$hash = $jc(_$hash, companyPhone.hashCode);
    _$hash = $jc(_$hash, companyEmail.hashCode);
    _$hash = $jc(_$hash, workersQuantity.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CompanyUpdatingRequest')
          ..add('companyName', companyName)
          ..add('companyAddress', companyAddress)
          ..add('companyPhone', companyPhone)
          ..add('companyEmail', companyEmail)
          ..add('workersQuantity', workersQuantity))
        .toString();
  }
}

class CompanyUpdatingRequestBuilder
    implements Builder<CompanyUpdatingRequest, CompanyUpdatingRequestBuilder> {
  _$CompanyUpdatingRequest? _$v;

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

  int? _workersQuantity;
  int? get workersQuantity => _$this._workersQuantity;
  set workersQuantity(int? workersQuantity) =>
      _$this._workersQuantity = workersQuantity;

  CompanyUpdatingRequestBuilder() {
    CompanyUpdatingRequest._defaults(this);
  }

  CompanyUpdatingRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _companyName = $v.companyName;
      _companyAddress = $v.companyAddress;
      _companyPhone = $v.companyPhone;
      _companyEmail = $v.companyEmail;
      _workersQuantity = $v.workersQuantity;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CompanyUpdatingRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CompanyUpdatingRequest;
  }

  @override
  void update(void Function(CompanyUpdatingRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CompanyUpdatingRequest build() => _build();

  _$CompanyUpdatingRequest _build() {
    final _$result = _$v ??
        new _$CompanyUpdatingRequest._(
            companyName: BuiltValueNullFieldError.checkNotNull(
                companyName, r'CompanyUpdatingRequest', 'companyName'),
            companyAddress: BuiltValueNullFieldError.checkNotNull(
                companyAddress, r'CompanyUpdatingRequest', 'companyAddress'),
            companyPhone: BuiltValueNullFieldError.checkNotNull(
                companyPhone, r'CompanyUpdatingRequest', 'companyPhone'),
            companyEmail: BuiltValueNullFieldError.checkNotNull(
                companyEmail, r'CompanyUpdatingRequest', 'companyEmail'),
            workersQuantity: BuiltValueNullFieldError.checkNotNull(
                workersQuantity, r'CompanyUpdatingRequest', 'workersQuantity'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
