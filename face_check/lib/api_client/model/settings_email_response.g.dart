// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'settings_email_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializer<SettingsEmailResponse> _$settingsEmailResponseSerializer =
    new _$SettingsEmailResponseSerializer();

class _$SettingsEmailResponseSerializer
    implements StructuredSerializer<SettingsEmailResponse> {
  @override
  final Iterable<Type> types = const [
    SettingsEmailResponse,
    _$SettingsEmailResponse
  ];
  @override
  final String wireName = 'SettingsEmailResponse';

  @override
  Iterable<Object?> serialize(
      Serializers serializers, SettingsEmailResponse object,
      {FullType specifiedType = FullType.unspecified}) {
    final result = <Object?>[
      'email',
      serializers.serialize(object.email,
          specifiedType: const FullType(String)),
    ];

    return result;
  }

  @override
  SettingsEmailResponse deserialize(
      Serializers serializers, Iterable<Object?> serialized,
      {FullType specifiedType = FullType.unspecified}) {
    final result = new SettingsEmailResponseBuilder();

    final iterator = serialized.iterator;
    while (iterator.moveNext()) {
      final key = iterator.current! as String;
      iterator.moveNext();
      final Object? value = iterator.current;
      switch (key) {
        case 'email':
          result.email = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
      }
    }

    return result.build();
  }
}

class _$SettingsEmailResponse extends SettingsEmailResponse {
  @override
  final String email;

  factory _$SettingsEmailResponse(
          [void Function(SettingsEmailResponseBuilder)? updates]) =>
      (new SettingsEmailResponseBuilder()..update(updates))._build();

  _$SettingsEmailResponse._({required this.email}) : super._() {
    BuiltValueNullFieldError.checkNotNull(
        email, r'SettingsEmailResponse', 'email');
  }

  @override
  SettingsEmailResponse rebuild(
          void Function(SettingsEmailResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SettingsEmailResponseBuilder toBuilder() =>
      new SettingsEmailResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SettingsEmailResponse && email == other.email;
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
    return (newBuiltValueToStringHelper(r'SettingsEmailResponse')
          ..add('email', email))
        .toString();
  }
}

class SettingsEmailResponseBuilder
    implements Builder<SettingsEmailResponse, SettingsEmailResponseBuilder> {
  _$SettingsEmailResponse? _$v;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  SettingsEmailResponseBuilder();

  SettingsEmailResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _email = $v.email;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SettingsEmailResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SettingsEmailResponse;
  }

  @override
  void update(void Function(SettingsEmailResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SettingsEmailResponse build() => _build();

  _$SettingsEmailResponse _build() {
    final _$result = _$v ??
        new _$SettingsEmailResponse._(
            email: BuiltValueNullFieldError.checkNotNull(
                email, r'SettingsEmailResponse', 'email'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
