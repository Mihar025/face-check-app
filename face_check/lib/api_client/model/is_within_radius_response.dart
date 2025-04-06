//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'is_within_radius_response.g.dart';

/// IsWithinRadiusResponse
///
/// Properties:
/// * [worksiteId] 
/// * [providedLatitude] 
/// * [providedLongitude] 
/// * [actualLatitude] 
/// * [actualLongitude] 
/// * [allowedRadius] 
/// * [withinRadius] 
@BuiltValue()
abstract class IsWithinRadiusResponse implements Built<IsWithinRadiusResponse, IsWithinRadiusResponseBuilder> {
  @BuiltValueField(wireName: r'worksiteId')
  int? get worksiteId;

  @BuiltValueField(wireName: r'providedLatitude')
  double? get providedLatitude;

  @BuiltValueField(wireName: r'providedLongitude')
  double? get providedLongitude;

  @BuiltValueField(wireName: r'actualLatitude')
  double? get actualLatitude;

  @BuiltValueField(wireName: r'actualLongitude')
  double? get actualLongitude;

  @BuiltValueField(wireName: r'allowedRadius')
  double? get allowedRadius;

  @BuiltValueField(wireName: r'withinRadius')
  bool? get withinRadius;

  IsWithinRadiusResponse._();

  factory IsWithinRadiusResponse([void updates(IsWithinRadiusResponseBuilder b)]) = _$IsWithinRadiusResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(IsWithinRadiusResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<IsWithinRadiusResponse> get serializer => _$IsWithinRadiusResponseSerializer();
}

class _$IsWithinRadiusResponseSerializer implements PrimitiveSerializer<IsWithinRadiusResponse> {
  @override
  final Iterable<Type> types = const [IsWithinRadiusResponse, _$IsWithinRadiusResponse];

  @override
  final String wireName = r'IsWithinRadiusResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    IsWithinRadiusResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.worksiteId != null) {
      yield r'worksiteId';
      yield serializers.serialize(
        object.worksiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.providedLatitude != null) {
      yield r'providedLatitude';
      yield serializers.serialize(
        object.providedLatitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.providedLongitude != null) {
      yield r'providedLongitude';
      yield serializers.serialize(
        object.providedLongitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.actualLatitude != null) {
      yield r'actualLatitude';
      yield serializers.serialize(
        object.actualLatitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.actualLongitude != null) {
      yield r'actualLongitude';
      yield serializers.serialize(
        object.actualLongitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.allowedRadius != null) {
      yield r'allowedRadius';
      yield serializers.serialize(
        object.allowedRadius,
        specifiedType: const FullType(double),
      );
    }
    if (object.withinRadius != null) {
      yield r'withinRadius';
      yield serializers.serialize(
        object.withinRadius,
        specifiedType: const FullType(bool),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    IsWithinRadiusResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required IsWithinRadiusResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'worksiteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.worksiteId = valueDes;
          break;
        case r'providedLatitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.providedLatitude = valueDes;
          break;
        case r'providedLongitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.providedLongitude = valueDes;
          break;
        case r'actualLatitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.actualLatitude = valueDes;
          break;
        case r'actualLongitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.actualLongitude = valueDes;
          break;
        case r'allowedRadius':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.allowedRadius = valueDes;
          break;
        case r'withinRadius':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.withinRadius = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  IsWithinRadiusResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = IsWithinRadiusResponseBuilder();
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

