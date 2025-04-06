// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'authentication_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$AuthenticationResponse extends AuthenticationResponse {
  @override
  final String? token;
  @override
  final String? refreshToken;

  factory _$AuthenticationResponse(
          [void Function(AuthenticationResponseBuilder)? updates]) =>
      (new AuthenticationResponseBuilder()..update(updates))._build();

  _$AuthenticationResponse._({this.token, this.refreshToken}) : super._();

  @override
  AuthenticationResponse rebuild(
          void Function(AuthenticationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AuthenticationResponseBuilder toBuilder() =>
      new AuthenticationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AuthenticationResponse &&
        token == other.token &&
        refreshToken == other.refreshToken;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, token.hashCode);
    _$hash = $jc(_$hash, refreshToken.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'AuthenticationResponse')
          ..add('token', token)
          ..add('refreshToken', refreshToken))
        .toString();
  }
}

class AuthenticationResponseBuilder
    implements Builder<AuthenticationResponse, AuthenticationResponseBuilder> {
  _$AuthenticationResponse? _$v;

  String? _token;
  String? get token => _$this._token;
  set token(String? token) => _$this._token = token;

  String? _refreshToken;
  String? get refreshToken => _$this._refreshToken;
  set refreshToken(String? refreshToken) => _$this._refreshToken = refreshToken;

  AuthenticationResponseBuilder() {
    AuthenticationResponse._defaults(this);
  }

  AuthenticationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _token = $v.token;
      _refreshToken = $v.refreshToken;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AuthenticationResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$AuthenticationResponse;
  }

  @override
  void update(void Function(AuthenticationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AuthenticationResponse build() => _build();

  _$AuthenticationResponse _build() {
    final _$result = _$v ??
        new _$AuthenticationResponse._(
            token: token, refreshToken: refreshToken);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
