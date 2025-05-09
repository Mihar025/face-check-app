//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'authentication_response.g.dart';

/// AuthenticationResponse
///
/// Properties:
/// * [token] 
@BuiltValue()
abstract class AuthenticationResponse implements Built<AuthenticationResponse, AuthenticationResponseBuilder> {
  @BuiltValueField(wireName: r'token')
  String? get token;
  String? get refreshToken;

  AuthenticationResponse._();

  factory AuthenticationResponse([void updates(AuthenticationResponseBuilder b)]) = _$AuthenticationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AuthenticationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AuthenticationResponse> get serializer => _$AuthenticationResponseSerializer();
}

class _$AuthenticationResponseSerializer implements PrimitiveSerializer<AuthenticationResponse> {
  @override
  final Iterable<Type> types = const [AuthenticationResponse, _$AuthenticationResponse];

  @override
  final String wireName = r'AuthenticationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AuthenticationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.token != null) {
      yield r'token';
      yield serializers.serialize(
        object.token,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    AuthenticationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AuthenticationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'token':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.token = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AuthenticationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AuthenticationResponseBuilder();
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

