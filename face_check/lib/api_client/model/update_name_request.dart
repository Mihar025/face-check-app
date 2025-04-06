//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_name_request.g.dart';

/// UpdateNameRequest
///
/// Properties:
/// * [name] 
@BuiltValue()
abstract class UpdateNameRequest implements Built<UpdateNameRequest, UpdateNameRequestBuilder> {
  @BuiltValueField(wireName: r'name')
  String get name;

  UpdateNameRequest._();

  factory UpdateNameRequest([void updates(UpdateNameRequestBuilder b)]) = _$UpdateNameRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateNameRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateNameRequest> get serializer => _$UpdateNameRequestSerializer();
}

class _$UpdateNameRequestSerializer implements PrimitiveSerializer<UpdateNameRequest> {
  @override
  final Iterable<Type> types = const [UpdateNameRequest, _$UpdateNameRequest];

  @override
  final String wireName = r'UpdateNameRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateNameRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateNameRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateNameRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'name':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.name = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UpdateNameRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateNameRequestBuilder();
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

