//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'set_new_custom_radius_response.g.dart';

/// SetNewCustomRadiusResponse
///
/// Properties:
/// * [workSiteId] 
/// * [customRadius] 
@BuiltValue()
abstract class SetNewCustomRadiusResponse implements Built<SetNewCustomRadiusResponse, SetNewCustomRadiusResponseBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'customRadius')
  double? get customRadius;

  SetNewCustomRadiusResponse._();

  factory SetNewCustomRadiusResponse([void updates(SetNewCustomRadiusResponseBuilder b)]) = _$SetNewCustomRadiusResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SetNewCustomRadiusResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SetNewCustomRadiusResponse> get serializer => _$SetNewCustomRadiusResponseSerializer();
}

class _$SetNewCustomRadiusResponseSerializer implements PrimitiveSerializer<SetNewCustomRadiusResponse> {
  @override
  final Iterable<Type> types = const [SetNewCustomRadiusResponse, _$SetNewCustomRadiusResponse];

  @override
  final String wireName = r'SetNewCustomRadiusResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SetNewCustomRadiusResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.customRadius != null) {
      yield r'customRadius';
      yield serializers.serialize(
        object.customRadius,
        specifiedType: const FullType(double),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    SetNewCustomRadiusResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SetNewCustomRadiusResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'workSiteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.workSiteId = valueDes;
          break;
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
  SetNewCustomRadiusResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SetNewCustomRadiusResponseBuilder();
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

