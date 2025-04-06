import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'settings_email_response.g.dart';

abstract class SettingsEmailResponse implements Built<SettingsEmailResponse, SettingsEmailResponseBuilder> {
  @BuiltValueField(wireName: 'email')
  String get email;

  SettingsEmailResponse._();
  factory SettingsEmailResponse([void Function(SettingsEmailResponseBuilder) updates]) = _$SettingsEmailResponse;

  static Serializer<SettingsEmailResponse> get serializer => _$settingsEmailResponseSerializer;
}