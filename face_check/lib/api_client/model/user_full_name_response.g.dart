// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_full_name_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UserFullNameResponse extends UserFullNameResponse {
  @override
  final String? fullName;

  factory _$UserFullNameResponse(
          [void Function(UserFullNameResponseBuilder)? updates]) =>
      (new UserFullNameResponseBuilder()..update(updates))._build();

  _$UserFullNameResponse._({this.fullName}) : super._();

  @override
  UserFullNameResponse rebuild(
          void Function(UserFullNameResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UserFullNameResponseBuilder toBuilder() =>
      new UserFullNameResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UserFullNameResponse && fullName == other.fullName;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UserFullNameResponse')
          ..add('fullName', fullName))
        .toString();
  }
}

class UserFullNameResponseBuilder
    implements Builder<UserFullNameResponse, UserFullNameResponseBuilder> {
  _$UserFullNameResponse? _$v;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  UserFullNameResponseBuilder() {
    UserFullNameResponse._defaults(this);
  }

  UserFullNameResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fullName = $v.fullName;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UserFullNameResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UserFullNameResponse;
  }

  @override
  void update(void Function(UserFullNameResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UserFullNameResponse build() => _build();

  _$UserFullNameResponse _build() {
    final _$result = _$v ?? new _$UserFullNameResponse._(fullName: fullName);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
