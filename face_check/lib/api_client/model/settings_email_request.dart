import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'settings_email_request.g.dart';

abstract class SettingsEmailRequest implements Built<SettingsEmailRequest, SettingsEmailRequestBuilder> {
  @BuiltValueField(wireName: 'currentEmail')
  String get currentEmail;

  @BuiltValueField(wireName: 'newEmail')
  String get newEmail;

  @BuiltValueField(wireName: 'confirmNewEmail')
  String get confirmNewEmail;

  SettingsEmailRequest._();
  factory SettingsEmailRequest([void Function(SettingsEmailRequestBuilder) updates]) = _$SettingsEmailRequest;

  static Serializer<SettingsEmailRequest> get serializer => _$settingsEmailRequestSerializer;
}