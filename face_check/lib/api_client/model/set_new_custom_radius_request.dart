//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'set_new_custom_radius_request.g.dart';

/// SetNewCustomRadiusRequest
///
/// Properties:
/// * [customRadius] 
@BuiltValue()
abstract class SetNewCustomRadiusRequest implements Built<SetNewCustomRadiusRequest, SetNewCustomRadiusRequestBuilder> {
  @BuiltValueField(wireName: r'customRadius')
  double get customRadius;

  SetNewCustomRadiusRequest._();

  factory SetNewCustomRadiusRequest([void updates(SetNewCustomRadiusRequestBuilder b)]) = _$SetNewCustomRadiusRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SetNewCustomRadiusRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SetNewCustomRadiusRequest> get serializer => _$SetNewCustomRadiusRequestSerializer();
}

class _$SetNewCustomRadiusRequestSerializer implements PrimitiveSerializer<SetNewCustomRadiusRequest> {
  @override
  final Iterable<Type> types = const [SetNewCustomRadiusRequest, _$SetNewCustomRadiusRequest];

  @override
  final String wireName = r'SetNewCustomRadiusRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SetNewCustomRadiusRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'customRadius';
    yield serializers.serialize(
      object.customRadius,
      specifiedType: const FullType(double),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SetNewCustomRadiusRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SetNewCustomRadiusRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'customRadius':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.customRadius = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SetNewCustomRadiusRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SetNewCustomRadiusRequestBuilder();
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

