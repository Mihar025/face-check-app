// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_email_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UserEmailResponse extends UserEmailResponse {
  @override
  final String? email;

  factory _$UserEmailResponse(
          [void Function(UserEmailResponseBuilder)? updates]) =>
      (new UserEmailResponseBuilder()..update(updates))._build();

  _$UserEmailResponse._({this.email}) : super._();

  @override
  UserEmailResponse rebuild(void Function(UserEmailResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UserEmailResponseBuilder toBuilder() =>
      new UserEmailResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UserEmailResponse && email == other.email;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UserEmailResponse')
          ..add('email', email))
        .toString();
  }
}

class UserEmailResponseBuilder
    implements Builder<UserEmailResponse, UserEmailResponseBuilder> {
  _$UserEmailResponse? _$v;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  UserEmailResponseBuilder() {
    UserEmailResponse._defaults(this);
  }

  UserEmailResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _email = $v.email;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UserEmailResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UserEmailResponse;
  }

  @override
  void update(void Function(UserEmailResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UserEmailResponse build() => _build();

  _$UserEmailResponse _build() {
    final _$result = _$v ?? new _$UserEmailResponse._(email: email);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
