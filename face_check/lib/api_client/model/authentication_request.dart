//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'authentication_request.g.dart';

/// AuthenticationRequest
///
/// Properties:
/// * [email] 
/// * [password] 
@BuiltValue()
abstract class AuthenticationRequest implements Built<AuthenticationRequest, AuthenticationRequestBuilder> {
  @BuiltValueField(wireName: r'email')
  String? get email;

  @BuiltValueField(wireName: r'password')
  String? get password;

  AuthenticationRequest._();

  factory AuthenticationRequest([void updates(AuthenticationRequestBuilder b)]) = _$AuthenticationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AuthenticationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AuthenticationRequest> get serializer => _$AuthenticationRequestSerializer();
}

class _$AuthenticationRequestSerializer implements PrimitiveSerializer<AuthenticationRequest> {
  @override
  final Iterable<Type> types = const [AuthenticationRequest, _$AuthenticationRequest];

  @override
  final String wireName = r'AuthenticationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AuthenticationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.email != null) {
      yield r'email';
      yield serializers.serialize(
        object.email,
        specifiedType: const FullType(String),
      );
    }
    if (object.password != null) {
      yield r'password';
      yield serializers.serialize(
        object.password,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    AuthenticationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AuthenticationRequestBuilder result,
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
        case r'password':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.password = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AuthenticationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AuthenticationRequestBuilder();
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

