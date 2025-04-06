// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'settings_email_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializer<SettingsEmailRequest> _$settingsEmailRequestSerializer =
    new _$SettingsEmailRequestSerializer();

class _$SettingsEmailRequestSerializer
    implements StructuredSerializer<SettingsEmailRequest> {
  @override
  final Iterable<Type> types = const [
    SettingsEmailRequest,
    _$SettingsEmailRequest
  ];
  @override
  final String wireName = 'SettingsEmailRequest';

  @override
  Iterable<Object?> serialize(
      Serializers serializers, SettingsEmailRequest object,
      {FullType specifiedType = FullType.unspecified}) {
    final result = <Object?>[
      'currentEmail',
      serializers.serialize(object.currentEmail,
          specifiedType: const FullType(String)),
      'newEmail',
      serializers.serialize(object.newEmail,
          specifiedType: const FullType(String)),
      'confirmNewEmail',
      serializers.serialize(object.confirmNewEmail,
          specifiedType: const FullType(String)),
    ];

    return result;
  }

  @override
  SettingsEmailRequest deserialize(
      Serializers serializers, Iterable<Object?> serialized,
      {FullType specifiedType = FullType.unspecified}) {
    final result = new SettingsEmailRequestBuilder();

    final iterator = serialized.iterator;
    while (iterator.moveNext()) {
      final key = iterator.current! as String;
      iterator.moveNext();
      final Object? value = iterator.current;
      switch (key) {
        case 'currentEmail':
          result.currentEmail = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
        case 'newEmail':
          result.newEmail = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
        case 'confirmNewEmail':
          result.confirmNewEmail = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
      }
    }

    return result.build();
  }
}

class _$SettingsEmailRequest extends SettingsEmailRequest {
  @override
  final String currentEmail;
  @override
  final String newEmail;
  @override
  final String confirmNewEmail;

  factory _$SettingsEmailRequest(
          [void Function(SettingsEmailRequestBuilder)? updates]) =>
      (new SettingsEmailRequestBuilder()..update(updates))._build();

  _$SettingsEmailRequest._(
      {required this.currentEmail,
      required this.newEmail,
      required this.confirmNewEmail})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        currentEmail, r'SettingsEmailRequest', 'currentEmail');
    BuiltValueNullFieldError.checkNotNull(
        newEmail, r'SettingsEmailRequest', 'newEmail');
    BuiltValueNullFieldError.checkNotNull(
        confirmNewEmail, r'SettingsEmailRequest', 'confirmNewEmail');
  }

  @override
  SettingsEmailRequest rebuild(
          void Function(SettingsEmailRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SettingsEmailRequestBuilder toBuilder() =>
      new SettingsEmailRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SettingsEmailRequest &&
        currentEmail == other.currentEmail &&
        newEmail == other.newEmail &&
        confirmNewEmail == other.confirmNewEmail;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, currentEmail.hashCode);
    _$hash = $jc(_$hash, newEmail.hashCode);
    _$hash = $jc(_$hash, confirmNewEmail.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SettingsEmailRequest')
          ..add('currentEmail', currentEmail)
          ..add('newEmail', newEmail)
          ..add('confirmNewEmail', confirmNewEmail))
        .toString();
  }
}

class SettingsEmailRequestBuilder
    implements Builder<SettingsEmailRequest, SettingsEmailRequestBuilder> {
  _$SettingsEmailRequest? _$v;

  String? _currentEmail;
  String? get currentEmail => _$this._currentEmail;
  set currentEmail(String? currentEmail) => _$this._currentEmail = currentEmail;

  String? _newEmail;
  String? get newEmail => _$this._newEmail;
  set newEmail(String? newEmail) => _$this._newEmail = newEmail;

  String? _confirmNewEmail;
  String? get confirmNewEmail => _$this._confirmNewEmail;
  set confirmNewEmail(String? confirmNewEmail) =>
      _$this._confirmNewEmail = confirmNewEmail;

  SettingsEmailRequestBuilder();

  SettingsEmailRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _currentEmail = $v.currentEmail;
      _newEmail = $v.newEmail;
      _confirmNewEmail = $v.confirmNewEmail;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SettingsEmailRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SettingsEmailRequest;
  }

  @override
  void update(void Function(SettingsEmailRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SettingsEmailRequest build() => _build();

  _$SettingsEmailRequest _build() {
    final _$result = _$v ??
        new _$SettingsEmailRequest._(
            currentEmail: BuiltValueNullFieldError.checkNotNull(
                currentEmail, r'SettingsEmailRequest', 'currentEmail'),
            newEmail: BuiltValueNullFieldError.checkNotNull(
                newEmail, r'SettingsEmailRequest', 'newEmail'),
            confirmNewEmail: BuiltValueNullFieldError.checkNotNull(
                confirmNewEmail, r'SettingsEmailRequest', 'confirmNewEmail'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
