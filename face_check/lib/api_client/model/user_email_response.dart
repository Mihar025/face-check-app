//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'user_email_response.g.dart';

/// UserEmailResponse
///
/// Properties:
/// * [email] 
@BuiltValue()
abstract class UserEmailResponse implements Built<UserEmailResponse, UserEmailResponseBuilder> {
  @BuiltValueField(wireName: r'email')
  String? get email;

  UserEmailResponse._();

  factory UserEmailResponse([void updates(UserEmailResponseBuilder b)]) = _$UserEmailResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UserEmailResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UserEmailResponse> get serializer => _$UserEmailResponseSerializer();
}

class _$UserEmailResponseSerializer implements PrimitiveSerializer<UserEmailResponse> {
  @override
  final Iterable<Type> types = const [UserEmailResponse, _$UserEmailResponse];

  @override
  final String wireName = r'UserEmailResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UserEmailResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.email != null) {
      yield r'email';
      yield serializers.serialize(
        object.email,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    UserEmailResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UserEmailResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'email':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.email = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UserEmailResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UserEmailResponseBuilder();
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

