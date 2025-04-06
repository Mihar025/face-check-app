//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'user_full_name_response.g.dart';

/// UserFullNameResponse
///
/// Properties:
/// * [fullName] 
@BuiltValue()
abstract class UserFullNameResponse implements Built<UserFullNameResponse, UserFullNameResponseBuilder> {
  @BuiltValueField(wireName: r'fullName')
  String? get fullName;

  UserFullNameResponse._();

  factory UserFullNameResponse([void updates(UserFullNameResponseBuilder b)]) = _$UserFullNameResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UserFullNameResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UserFullNameResponse> get serializer => _$UserFullNameResponseSerializer();
}

class _$UserFullNameResponseSerializer implements PrimitiveSerializer<UserFullNameResponse> {
  @override
  final Iterable<Type> types = const [UserFullNameResponse, _$UserFullNameResponse];

  @override
  final String wireName = r'UserFullNameResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UserFullNameResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.fullName != null) {
      yield r'fullName';
      yield serializers.serialize(
        object.fullName,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    UserFullNameResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UserFullNameResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UserFullNameResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UserFullNameResponseBuilder();
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

