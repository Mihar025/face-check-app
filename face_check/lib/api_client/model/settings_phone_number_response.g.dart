// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'settings_phone_number_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializer<SettingsPhoneNumberResponse>
    _$settingsPhoneNumberResponseSerializer =
    new _$SettingsPhoneNumberResponseSerializer();

class _$SettingsPhoneNumberResponseSerializer
    implements StructuredSerializer<SettingsPhoneNumberResponse> {
  @override
  final Iterable<Type> types = const [
    SettingsPhoneNumberResponse,
    _$SettingsPhoneNumberResponse
  ];
  @override
  final String wireName = 'SettingsPhoneNumberResponse';

  @override
  Iterable<Object?> serialize(
      Serializers serializers, SettingsPhoneNumberResponse object,
      {FullType specifiedType = FullType.unspecified}) {
    final result = <Object?>[
      'phoneNumber',
      serializers.serialize(object.phoneNumber,
          specifiedType: const FullType(String)),
    ];

    return result;
  }

  @override
  SettingsPhoneNumberResponse deserialize(
      Serializers serializers, Iterable<Object?> serialized,
      {FullType specifiedType = FullType.unspecified}) {
    final result = new SettingsPhoneNumberResponseBuilder();

    final iterator = serialized.iterator;
    while (iterator.moveNext()) {
      final key = iterator.current! as String;
      iterator.moveNext();
      final Object? value = iterator.current;
      switch (key) {
        case 'phoneNumber':
          result.phoneNumber = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
      }
    }

    return result.build();
  }
}

class _$SettingsPhoneNumberResponse extends SettingsPhoneNumberResponse {
  @override
  final String phoneNumber;

  factory _$SettingsPhoneNumberResponse(
          [void Function(SettingsPhoneNumberResponseBuilder)? updates]) =>
      (new SettingsPhoneNumberResponseBuilder()..update(updates))._build();

  _$SettingsPhoneNumberResponse._({required this.phoneNumber}) : super._() {
    BuiltValueNullFieldError.checkNotNull(
        phoneNumber, r'SettingsPhoneNumberResponse', 'phoneNumber');
  }

  @override
  SettingsPhoneNumberResponse rebuild(
          void Function(SettingsPhoneNumberResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SettingsPhoneNumberResponseBuilder toBuilder() =>
      new SettingsPhoneNumberResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SettingsPhoneNumberResponse &&
        phoneNumber == other.phoneNumber;
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
    return (newBuiltValueToStringHelper(r'SettingsPhoneNumberResponse')
          ..add('phoneNumber', phoneNumber))
        .toString();
  }
}

class SettingsPhoneNumberResponseBuilder
    implements
        Builder<SettingsPhoneNumberResponse,
            SettingsPhoneNumberResponseBuilder> {
  _$SettingsPhoneNumberResponse? _$v;

  String? _phoneNumber;
  String? get phoneNumber => _$this._phoneNumber;
  set phoneNumber(String? phoneNumber) => _$this._phoneNumber = phoneNumber;

  SettingsPhoneNumberResponseBuilder();

  SettingsPhoneNumberResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _phoneNumber = $v.phoneNumber;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SettingsPhoneNumberResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SettingsPhoneNumberResponse;
  }

  @override
  void update(void Function(SettingsPhoneNumberResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SettingsPhoneNumberResponse build() => _build();

  _$SettingsPhoneNumberResponse _build() {
    final _$result = _$v ??
        new _$SettingsPhoneNumberResponse._(
            phoneNumber: BuiltValueNullFieldError.checkNotNull(
                phoneNumber, r'SettingsPhoneNumberResponse', 'phoneNumber'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
