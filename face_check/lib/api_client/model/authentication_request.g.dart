// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'authentication_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$AuthenticationRequest extends AuthenticationRequest {
  @override
  final String? email;
  @override
  final String? password;

  factory _$AuthenticationRequest(
          [void Function(AuthenticationRequestBuilder)? updates]) =>
      (new AuthenticationRequestBuilder()..update(updates))._build();

  _$AuthenticationRequest._({this.email, this.password}) : super._();

  @override
  AuthenticationRequest rebuild(
          void Function(AuthenticationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AuthenticationRequestBuilder toBuilder() =>
      new AuthenticationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AuthenticationRequest &&
        email == other.email &&
        password == other.password;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, password.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'AuthenticationRequest')
          ..add('email', email)
          ..add('password', password))
        .toString();
  }
}

class AuthenticationRequestBuilder
    implements Builder<AuthenticationRequest, AuthenticationRequestBuilder> {
  _$AuthenticationRequest? _$v;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  String? _password;
  String? get password => _$this._password;
  set password(String? password) => _$this._password = password;

  AuthenticationRequestBuilder() {
    AuthenticationRequest._defaults(this);
  }

  AuthenticationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _email = $v.email;
      _password = $v.password;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AuthenticationRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$AuthenticationRequest;
  }

  @override
  void update(void Function(AuthenticationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AuthenticationRequest build() => _build();

  _$AuthenticationRequest _build() {
    final _$result =
        _$v ?? new _$AuthenticationRequest._(email: email, password: password);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
