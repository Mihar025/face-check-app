//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'user_home_address_response.g.dart';

/// UserHomeAddressResponse
///
/// Properties:
/// * [homeAddress] 
@BuiltValue()
abstract class UserHomeAddressResponse implements Built<UserHomeAddressResponse, UserHomeAddressResponseBuilder> {
  @BuiltValueField(wireName: r'homeAddress')
  String? get homeAddress;

  UserHomeAddressResponse._();

  factory UserHomeAddressResponse([void updates(UserHomeAddressResponseBuilder b)]) = _$UserHomeAddressResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UserHomeAddressResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UserHomeAddressResponse> get serializer => _$UserHomeAddressResponseSerializer();
}

class _$UserHomeAddressResponseSerializer implements PrimitiveSerializer<UserHomeAddressResponse> {
  @override
  final Iterable<Type> types = const [UserHomeAddressResponse, _$UserHomeAddressResponse];

  @override
  final String wireName = r'UserHomeAddressResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UserHomeAddressResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.homeAddress != null) {
      yield r'homeAddress';
      yield serializers.serialize(
        object.homeAddress,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    UserHomeAddressResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UserHomeAddressResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'homeAddress':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.homeAddress = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UserHomeAddressResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UserHomeAddressResponseBuilder();
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

