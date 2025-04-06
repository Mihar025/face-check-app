import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'settings_phone_number_request.g.dart';

abstract class SettingsPhoneNumberRequest implements Built<SettingsPhoneNumberRequest, SettingsPhoneNumberRequestBuilder> {
  // Фабрика должна быть первой
  factory SettingsPhoneNumberRequest([void Function(SettingsPhoneNumberRequestBuilder)? updates]) = _$SettingsPhoneNumberRequest;

  SettingsPhoneNumberRequest._();

  @BuiltValueField(wireName: 'currentPhoneNumber')
  String get currentPhoneNumber;

  @BuiltValueField(wireName: 'newPhoneNumber')
  String get newPhoneNumber;

  @BuiltValueField(wireName: 'confirmNewPhoneNumber')
  String get confirmNewPhoneNumber;

  static Serializer<SettingsPhoneNumberRequest> get serializer => _$settingsPhoneNumberRequestSerializer;
}

