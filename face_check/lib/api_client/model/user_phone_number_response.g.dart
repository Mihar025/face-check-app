// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_phone_number_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UserPhoneNumberResponse extends UserPhoneNumberResponse {
  @override
  final String? phoneNumber;

  factory _$UserPhoneNumberResponse(
          [void Function(UserPhoneNumberResponseBuilder)? updates]) =>
      (new UserPhoneNumberResponseBuilder()..update(updates))._build();

  _$UserPhoneNumberResponse._({this.phoneNumber}) : super._();

  @override
  UserPhoneNumberResponse rebuild(
          void Function(UserPhoneNumberResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UserPhoneNumberResponseBuilder toBuilder() =>
      new UserPhoneNumberResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UserPhoneNumberResponse && phoneNumber == other.phoneNumber;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, phoneNumber.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UserPhoneNumberResponse')
          ..add('phoneNumber', phoneNumber))
        .toString();
  }
}

class UserPhoneNumberResponseBuilder
    implements
        Builder<UserPhoneNumberResponse, UserPhoneNumberResponseBuilder> {
  _$UserPhoneNumberResponse? _$v;

  String? _phoneNumber;
  String? get phoneNumber => _$this._phoneNumber;
  set phoneNumber(String? phoneNumber) => _$this._phoneNumber = phoneNumber;

  UserPhoneNumberResponseBuilder() {
    UserPhoneNumberResponse._defaults(this);
  }

  UserPhoneNumberResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _phoneNumber = $v.phoneNumber;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UserPhoneNumberResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UserPhoneNumberResponse;
  }

  @override
  void update(void Function(UserPhoneNumberResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UserPhoneNumberResponse build() => _build();

  _$UserPhoneNumberResponse _build() {
    final _$result =
        _$v ?? new _$UserPhoneNumberResponse._(phoneNumber: phoneNumber);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
