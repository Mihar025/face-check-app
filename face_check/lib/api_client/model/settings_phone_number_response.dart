import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'settings_phone_number_response.g.dart';

abstract class SettingsPhoneNumberResponse implements Built<SettingsPhoneNumberResponse, SettingsPhoneNumberResponseBuilder> {
  @BuiltValueField(wireName: 'phoneNumber')
  String get phoneNumber;

  SettingsPhoneNumberResponse._();
  factory SettingsPhoneNumberResponse([void Function(SettingsPhoneNumberResponseBuilder) updates]) = _$SettingsPhoneNumberResponse;

  static Serializer<SettingsPhoneNumberResponse> get serializer => _$settingsPhoneNumberResponseSerializer;
}