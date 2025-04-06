// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_full_contact_information.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UserFullContactInformation extends UserFullContactInformation {
  @override
  final String? fullName;
  @override
  final String? phoneNumber;
  @override
  final String? email;
  @override
  final String? address;
  @override
  final String? photoFileName;
  @override
  final String? photoUrl;

  factory _$UserFullContactInformation(
          [void Function(UserFullContactInformationBuilder)? updates]) =>
      (new UserFullContactInformationBuilder()..update(updates))._build();

  _$UserFullContactInformation._(
      {this.fullName,
      this.phoneNumber,
      this.email,
      this.address,
      this.photoFileName,
      this.photoUrl})
      : super._();

  @override
  UserFullContactInformation rebuild(
          void Function(UserFullContactInformationBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UserFullContactInformationBuilder toBuilder() =>
      new UserFullContactInformationBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UserFullContactInformation &&
        fullName == other.fullName &&
        phoneNumber == other.phoneNumber &&
        email == other.email &&
        address == other.address &&
        photoFileName == other.photoFileName &&
        photoUrl == other.photoUrl;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, phoneNumber.hashCode);
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, address.hashCode);
    _$hash = $jc(_$hash, photoFileName.hashCode);
    _$hash = $jc(_$hash, photoUrl.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UserFullContactInformation')
          ..add('fullName', fullName)
          ..add('phoneNumber', phoneNumber)
          ..add('email', email)
          ..add('address', address)
          ..add('photoFileName', photoFileName)
          ..add('photoUrl', photoUrl))
        .toString();
  }
}

class UserFullContactInformationBuilder
    implements
        Builder<UserFullContactInformation, UserFullContactInformationBuilder> {
  _$UserFullContactInformation? _$v;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _phoneNumber;
  String? get phoneNumber => _$this._phoneNumber;
  set phoneNumber(String? phoneNumber) => _$this._phoneNumber = phoneNumber;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  String? _address;
  String? get address => _$this._address;
  set address(String? address) => _$this._address = address;

  String? _photoFileName;
  String? get photoFileName => _$this._photoFileName;
  set photoFileName(String? photoFileName) =>
      _$this._photoFileName = photoFileName;

  String? _photoUrl;
  String? get photoUrl => _$this._photoUrl;
  set photoUrl(String? photoUrl) => _$this._photoUrl = photoUrl;

  UserFullContactInformationBuilder() {
    UserFullContactInformation._defaults(this);
  }

  UserFullContactInformationBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fullName = $v.fullName;
      _phoneNumber = $v.phoneNumber;
      _email = $v.email;
      _address = $v.address;
      _photoFileName = $v.photoFileName;
      _photoUrl = $v.photoUrl;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UserFullContactInformation other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UserFullContactInformation;
  }

  @override
  void update(void Function(UserFullContactInformationBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UserFullContactInformation build() => _build();

  _$UserFullContactInformation _build() {
    final _$result = _$v ??
        new _$UserFullContactInformation._(
            fullName: fullName,
            phoneNumber: phoneNumber,
            email: email,
            address: address,
            photoFileName: photoFileName,
            photoUrl: photoUrl);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
