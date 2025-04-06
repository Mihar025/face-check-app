// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_home_address_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UserHomeAddressResponse extends UserHomeAddressResponse {
  @override
  final String? homeAddress;

  factory _$UserHomeAddressResponse(
          [void Function(UserHomeAddressResponseBuilder)? updates]) =>
      (new UserHomeAddressResponseBuilder()..update(updates))._build();

  _$UserHomeAddressResponse._({this.homeAddress}) : super._();

  @override
  UserHomeAddressResponse rebuild(
          void Function(UserHomeAddressResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UserHomeAddressResponseBuilder toBuilder() =>
      new UserHomeAddressResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UserHomeAddressResponse && homeAddress == other.homeAddress;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, homeAddress.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UserHomeAddressResponse')
          ..add('homeAddress', homeAddress))
        .toString();
  }
}

class UserHomeAddressResponseBuilder
    implements
        Builder<UserHomeAddressResponse, UserHomeAddressResponseBuilder> {
  _$UserHomeAddressResponse? _$v;

  String? _homeAddress;
  String? get homeAddress => _$this._homeAddress;
  set homeAddress(String? homeAddress) => _$this._homeAddress = homeAddress;

  UserHomeAddressResponseBuilder() {
    UserHomeAddressResponse._defaults(this);
  }

  UserHomeAddressResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _homeAddress = $v.homeAddress;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UserHomeAddressResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UserHomeAddressResponse;
  }

  @override
  void update(void Function(UserHomeAddressResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UserHomeAddressResponse build() => _build();

  _$UserHomeAddressResponse _build() {
    final _$result =
        _$v ?? new _$UserHomeAddressResponse._(homeAddress: homeAddress);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
