//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'is_within_radius_request.g.dart';

/// IsWithinRadiusRequest
///
/// Properties:
/// * [latitude] 
/// * [longitude] 
@BuiltValue()
abstract class IsWithinRadiusRequest implements Built<IsWithinRadiusRequest, IsWithinRadiusRequestBuilder> {
  @BuiltValueField(wireName: r'latitude')
  double get latitude;

  @BuiltValueField(wireName: r'longitude')
  double get longitude;

  IsWithinRadiusRequest._();

  factory IsWithinRadiusRequest([void updates(IsWithinRadiusRequestBuilder b)]) = _$IsWithinRadiusRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(IsWithinRadiusRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<IsWithinRadiusRequest> get serializer => _$IsWithinRadiusRequestSerializer();
}

class _$IsWithinRadiusRequestSerializer implements PrimitiveSerializer<IsWithinRadiusRequest> {
  @override
  final Iterable<Type> types = const [IsWithinRadiusRequest, _$IsWithinRadiusRequest];

  @override
  final String wireName = r'IsWithinRadiusRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    IsWithinRadiusRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'latitude';
    yield serializers.serialize(
      object.latitude,
      specifiedType: const FullType(double),
    );
    yield r'longitude';
    yield serializers.serialize(
      object.longitude,
      specifiedType: const FullType(double),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    IsWithinRadiusRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required IsWithinRadiusRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'latitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.latitude = valueDes;
          break;
        case r'longitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.longitude = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  IsWithinRadiusRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = IsWithinRadiusRequestBuilder();
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

