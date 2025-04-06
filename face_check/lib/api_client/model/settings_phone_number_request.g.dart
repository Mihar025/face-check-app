// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'settings_phone_number_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializer<SettingsPhoneNumberRequest> _$settingsPhoneNumberRequestSerializer =
    new _$SettingsPhoneNumberRequestSerializer();

class _$SettingsPhoneNumberRequestSerializer
    implements StructuredSerializer<SettingsPhoneNumberRequest> {
  @override
  final Iterable<Type> types = const [
    SettingsPhoneNumberRequest,
    _$SettingsPhoneNumberRequest
  ];
  @override
  final String wireName = 'SettingsPhoneNumberRequest';

  @override
  Iterable<Object?> serialize(
      Serializers serializers, SettingsPhoneNumberRequest object,
      {FullType specifiedType = FullType.unspecified}) {
    final result = <Object?>[
      'currentPhoneNumber',
      serializers.serialize(object.currentPhoneNumber,
          specifiedType: const FullType(String)),
      'newPhoneNumber',
      serializers.serialize(object.newPhoneNumber,
          specifiedType: const FullType(String)),
      'confirmNewPhoneNumber',
      serializers.serialize(object.confirmNewPhoneNumber,
          specifiedType: const FullType(String)),
    ];

    return result;
  }

  @override
  SettingsPhoneNumberRequest deserialize(
      Serializers serializers, Iterable<Object?> serialized,
      {FullType specifiedType = FullType.unspecified}) {
    final result = new SettingsPhoneNumberRequestBuilder();

    final iterator = serialized.iterator;
    while (iterator.moveNext()) {
      final key = iterator.current! as String;
      iterator.moveNext();
      final Object? value = iterator.current;
      switch (key) {
        case 'currentPhoneNumber':
          result.currentPhoneNumber = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
        case 'newPhoneNumber':
          result.newPhoneNumber = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
        case 'confirmNewPhoneNumber':
          result.confirmNewPhoneNumber = serializers.deserialize(value,
              specifiedType: const FullType(String))! as String;
          break;
      }
    }

    return result.build();
  }
}

class _$SettingsPhoneNumberRequest extends SettingsPhoneNumberRequest {
  @override
  final String currentPhoneNumber;
  @override
  final String newPhoneNumber;
  @override
  final String confirmNewPhoneNumber;

  factory _$SettingsPhoneNumberRequest(
          [void Function(SettingsPhoneNumberRequestBuilder)? updates]) =>
      (new SettingsPhoneNumberRequestBuilder()..update(updates))._build();

  _$SettingsPhoneNumberRequest._(
      {required this.currentPhoneNumber,
      required this.newPhoneNumber,
      required this.confirmNewPhoneNumber})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(currentPhoneNumber,
        r'SettingsPhoneNumberRequest', 'currentPhoneNumber');
    BuiltValueNullFieldError.checkNotNull(
        newPhoneNumber, r'SettingsPhoneNumberRequest', 'newPhoneNumber');
    BuiltValueNullFieldError.checkNotNull(confirmNewPhoneNumber,
        r'SettingsPhoneNumberRequest', 'confirmNewPhoneNumber');
  }

  @override
  SettingsPhoneNumberRequest rebuild(
          void Function(SettingsPhoneNumberRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SettingsPhoneNumberRequestBuilder toBuilder() =>
      new SettingsPhoneNumberRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SettingsPhoneNumberRequest &&
        currentPhoneNumber == other.currentPhoneNumber &&
        newPhoneNumber == other.newPhoneNumber &&
        confirmNewPhoneNumber == other.confirmNewPhoneNumber;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, currentPhoneNumber.hashCode);
    _$hash = $jc(_$hash, newPhoneNumber.hashCode);
    _$hash = $jc(_$hash, confirmNewPhoneNumber.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SettingsPhoneNumberRequest')
          ..add('currentPhoneNumber', currentPhoneNumber)
          ..add('newPhoneNumber', newPhoneNumber)
          ..add('confirmNewPhoneNumber', confirmNewPhoneNumber))
        .toString();
  }
}

class SettingsPhoneNumberRequestBuilder
    implements
        Builder<SettingsPhoneNumberRequest, SettingsPhoneNumberRequestBuilder> {
  _$SettingsPhoneNumberRequest? _$v;

  String? _currentPhoneNumber;
  String? get currentPhoneNumber => _$this._currentPhoneNumber;
  set currentPhoneNumber(String? currentPhoneNumber) =>
      _$this._currentPhoneNumber = currentPhoneNumber;

  String? _newPhoneNumber;
  String? get newPhoneNumber => _$this._newPhoneNumber;
  set newPhoneNumber(String? newPhoneNumber) =>
      _$this._newPhoneNumber = newPhoneNumber;

  String? _confirmNewPhoneNumber;
  String? get confirmNewPhoneNumber => _$this._confirmNewPhoneNumber;
  set confirmNewPhoneNumber(String? confirmNewPhoneNumber) =>
      _$this._confirmNewPhoneNumber = confirmNewPhoneNumber;

  SettingsPhoneNumberRequestBuilder();

  SettingsPhoneNumberRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _currentPhoneNumber = $v.currentPhoneNumber;
      _newPhoneNumber = $v.newPhoneNumber;
      _confirmNewPhoneNumber = $v.confirmNewPhoneNumber;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SettingsPhoneNumberRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SettingsPhoneNumberRequest;
  }

  @override
  void update(void Function(SettingsPhoneNumberRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SettingsPhoneNumberRequest build() => _build();

  _$SettingsPhoneNumberRequest _build() {
    final _$result = _$v ??
        new _$SettingsPhoneNumberRequest._(
            currentPhoneNumber: BuiltValueNullFieldError.checkNotNull(
                currentPhoneNumber,
                r'SettingsPhoneNumberRequest',
                'currentPhoneNumber'),
            newPhoneNumber: BuiltValueNullFieldError.checkNotNull(
                newPhoneNumber,
                r'SettingsPhoneNumberRequest',
                'newPhoneNumber'),
            confirmNewPhoneNumber: BuiltValueNullFieldError.checkNotNull(
                confirmNewPhoneNumber,
                r'SettingsPhoneNumberRequest',
                'confirmNewPhoneNumber'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
