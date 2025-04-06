// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'settings_password_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializer<SettingsPasswordRequest> _$settingsPasswordRequestSerializer =
    new _$SettingsPasswordRequestSerializer();

class _$SettingsPasswordRequestSerializer
    implements StructuredSerializer<SettingsPasswordRequest> {
  @override
  final Iterable<Type> types = const [
    SettingsPasswordRequest,
    _$SettingsPasswordRequest
  ];
  @override
  final String wireName = 'SettingsPasswordRequest';

  @override
  Iterable<Object?> serialize(
      Serializers serializers, SettingsPasswordRequest object,
      {FullType specifiedType = FullType.unspecified}) {
    final result = <Object?>[
      'currentPassword',
      serializers.serialize(object.currentPassword,
          specifiedType: const FullType(String)),
      'newPassword',
      serializers.serialize(object.newPassword,
          specifiedType: const FullType(String)),
      'confirmPassword',
      serializers.serialize(object.confirmPassword,
          specifiedType: const FullType(String)),
    ];

    return result;
  }

  @override
  SettingsPasswordRequest deserialize(
      Serializers serializers, Iterable<Object?> serialized,
      {FullType specifiedType = FullType.unspecified}) {
    final result = new SettingsPasswordRequestBuilder();

    final iterator = serialized.iterator;
    while (iterator.moveNext()) {
      final key = iterator.current! as String;
      iterator.moveNext();
      final Object? value = iterator.current;
      switch (key) {
        case 'currentPassword':
          result.currentPassword = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
        case 'newPassword':
          result.newPassword = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
        case 'confirmPassword':
          result.confirmPassword = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
      }
    }

    return result.build();
  }
}

class _$SettingsPasswordRequest extends SettingsPasswordRequest {
  @override
  final String currentPassword;
  @override
  final String newPassword;
  @override
  final String confirmPassword;

  factory _$SettingsPasswordRequest(
          [void Function(SettingsPasswordRequestBuilder)? updates]) =>
      (new SettingsPasswordRequestBuilder()..update(updates))._build();

  _$SettingsPasswordRequest._(
      {required this.currentPassword,
      required this.newPassword,
      required this.confirmPassword})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        currentPassword, r'SettingsPasswordRequest', 'currentPassword');
    BuiltValueNullFieldError.checkNotNull(
        newPassword, r'SettingsPasswordRequest', 'newPassword');
    BuiltValueNullFieldError.checkNotNull(
        confirmPassword, r'SettingsPasswordRequest', 'confirmPassword');
  }

  @override
  SettingsPasswordRequest rebuild(
          void Function(SettingsPasswordRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SettingsPasswordRequestBuilder toBuilder() =>
      new SettingsPasswordRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SettingsPasswordRequest &&
        currentPassword == other.currentPassword &&
        newPassword == other.newPassword &&
        confirmPassword == other.confirmPassword;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, currentPassword.hashCode);
    _$hash = $jc(_$hash, newPassword.hashCode);
    _$hash = $jc(_$hash, confirmPassword.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SettingsPasswordRequest')
          ..add('currentPassword', currentPassword)
          ..add('newPassword', newPassword)
          ..add('confirmPassword', confirmPassword))
        .toString();
  }
}

class SettingsPasswordRequestBuilder
    implements
        Builder<SettingsPasswordRequest, SettingsPasswordRequestBuilder> {
  _$SettingsPasswordRequest? _$v;

  String? _currentPassword;
  String? get currentPassword => _$this._currentPassword;
  set currentPassword(String? currentPassword) =>
      _$this._currentPassword = currentPassword;

  String? _newPassword;
  String? get newPassword => _$this._newPassword;
  set newPassword(String? newPassword) => _$this._newPassword = newPassword;

  String? _confirmPassword;
  String? get confirmPassword => _$this._confirmPassword;
  set confirmPassword(String? confirmPassword) =>
      _$this._confirmPassword = confirmPassword;

  SettingsPasswordRequestBuilder();

  SettingsPasswordRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _currentPassword = $v.currentPassword;
      _newPassword = $v.newPassword;
      _confirmPassword = $v.confirmPassword;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SettingsPasswordRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SettingsPasswordRequest;
  }

  @override
  void update(void Function(SettingsPasswordRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SettingsPasswordRequest build() => _build();

  _$SettingsPasswordRequest _build() {
    final _$result = _$v ??
        new _$SettingsPasswordRequest._(
            currentPassword: BuiltValueNullFieldError.checkNotNull(
                currentPassword, r'SettingsPasswordRequest', 'currentPassword'),
            newPassword: BuiltValueNullFieldError.checkNotNull(
                newPassword, r'SettingsPasswordRequest', 'newPassword'),
            confirmPassword: BuiltValueNullFieldError.checkNotNull(
                confirmPassword,
                r'SettingsPasswordRequest',
                'confirmPassword'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
