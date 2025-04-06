// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'company_updating_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CompanyUpdatingResponse extends CompanyUpdatingResponse {
  @override
  final int? companyId;
  @override
  final String? companyName;
  @override
  final String? companyAddress;
  @override
  final String? companyPhone;
  @override
  final String? companyEmail;
  @override
  final int? workersQuantity;

  factory _$CompanyUpdatingResponse(
          [void Function(CompanyUpdatingResponseBuilder)? updates]) =>
      (new CompanyUpdatingResponseBuilder()..update(updates))._build();

  _$CompanyUpdatingResponse._(
      {this.companyId,
      this.companyName,
      this.companyAddress,
      this.companyPhone,
      this.companyEmail,
      this.workersQuantity})
      : super._();

  @override
  CompanyUpdatingResponse rebuild(
          void Function(CompanyUpdatingResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CompanyUpdatingResponseBuilder toBuilder() =>
      new CompanyUpdatingResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CompanyUpdatingResponse &&
        companyId == other.companyId &&
        companyName == other.companyName &&
        companyAddress == other.companyAddress &&
        companyPhone == other.companyPhone &&
        companyEmail == other.companyEmail &&
        workersQuantity == other.workersQuantity;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, companyId.hashCode);
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
    return (newBuiltValueToStringHelper(r'CompanyUpdatingResponse')
          ..add('companyId', companyId)
          ..add('companyName', companyName)
          ..add('companyAddress', companyAddress)
          ..add('companyPhone', companyPhone)
          ..add('companyEmail', companyEmail)
          ..add('workersQuantity', workersQuantity))
        .toString();
  }
}

class CompanyUpdatingResponseBuilder
    implements
        Builder<CompanyUpdatingResponse, CompanyUpdatingResponseBuilder> {
  _$CompanyUpdatingResponse? _$v;

  int? _companyId;
  int? get companyId => _$this._companyId;
  set companyId(int? companyId) => _$this._companyId = companyId;

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

  CompanyUpdatingResponseBuilder() {
    CompanyUpdatingResponse._defaults(this);
  }

  CompanyUpdatingResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _companyId = $v.companyId;
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
  void replace(CompanyUpdatingResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CompanyUpdatingResponse;
  }

  @override
  void update(void Function(CompanyUpdatingResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CompanyUpdatingResponse build() => _build();

  _$CompanyUpdatingResponse _build() {
    final _$result = _$v ??
        new _$CompanyUpdatingResponse._(
            companyId: companyId,
            companyName: companyName,
            companyAddress: companyAddress,
            companyPhone: companyPhone,
            companyEmail: companyEmail,
            workersQuantity: workersQuantity);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
