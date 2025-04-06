import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'settings_password_request.g.dart';

abstract class SettingsPasswordRequest implements Built<SettingsPasswordRequest, SettingsPasswordRequestBuilder> {
  @BuiltValueField(wireName: 'currentPassword')
  String get currentPassword;

  @BuiltValueField(wireName: 'newPassword')
  String get newPassword;

  @BuiltValueField(wireName: 'confirmPassword')
  String get confirmPassword;

  SettingsPasswordRequest._();
  factory SettingsPasswordRequest([void Function(SettingsPasswordRequestBuilder) updates]) = _$SettingsPasswordRequest;

  static Serializer<SettingsPasswordRequest> get serializer => _$settingsPasswordRequestSerializer;
}
