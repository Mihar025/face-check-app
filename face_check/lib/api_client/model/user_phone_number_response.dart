//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'user_phone_number_response.g.dart';

/// UserPhoneNumberResponse
///
/// Properties:
/// * [phoneNumber] 
@BuiltValue()
abstract class UserPhoneNumberResponse implements Built<UserPhoneNumberResponse, UserPhoneNumberResponseBuilder> {
  @BuiltValueField(wireName: r'phoneNumber')
  String? get phoneNumber;

  UserPhoneNumberResponse._();

  factory UserPhoneNumberResponse([void updates(UserPhoneNumberResponseBuilder b)]) = _$UserPhoneNumberResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UserPhoneNumberResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UserPhoneNumberResponse> get serializer => _$UserPhoneNumberResponseSerializer();
}

class _$UserPhoneNumberResponseSerializer implements PrimitiveSerializer<UserPhoneNumberResponse> {
  @override
  final Iterable<Type> types = const [UserPhoneNumberResponse, _$UserPhoneNumberResponse];

  @override
  final String wireName = r'UserPhoneNumberResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UserPhoneNumberResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.phoneNumber != null) {
      yield r'phoneNumber';
      yield serializers.serialize(
        object.phoneNumber,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    UserPhoneNumberResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UserPhoneNumberResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'phoneNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.phoneNumber = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UserPhoneNumberResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UserPhoneNumberResponseBuilder();
    final serializedList = (serialized as Iterable<Object?>).toList();
    final unhandled = <Object?>[];
    _deserializeProperties(
      serializers,
      serialized,
      specifiedType: specifiedType,
      serializedList: serializedList,
      unhandled: unhandled,
      result: result,
    );
    return result.build();
  }
}

