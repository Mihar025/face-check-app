//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'work_site_update_location_request.g.dart';

/// WorkSiteUpdateLocationRequest
///
/// Properties:
/// * [newLatitude] 
/// * [newLongitude] 
/// * [newRadius] 
@BuiltValue()
abstract class WorkSiteUpdateLocationRequest implements Built<WorkSiteUpdateLocationRequest, WorkSiteUpdateLocationRequestBuilder> {
  @BuiltValueField(wireName: r'newLatitude')
  double get newLatitude;

  @BuiltValueField(wireName: r'newLongitude')
  double get newLongitude;

  @BuiltValueField(wireName: r'newRadius')
  double get newRadius;

  WorkSiteUpdateLocationRequest._();

  factory WorkSiteUpdateLocationRequest([void updates(WorkSiteUpdateLocationRequestBuilder b)]) = _$WorkSiteUpdateLocationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteUpdateLocationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteUpdateLocationRequest> get serializer => _$WorkSiteUpdateLocationRequestSerializer();
}

class _$WorkSiteUpdateLocationRequestSerializer implements PrimitiveSerializer<WorkSiteUpdateLocationRequest> {
  @override
  final Iterable<Type> types = const [WorkSiteUpdateLocationRequest, _$WorkSiteUpdateLocationRequest];

  @override
  final String wireName = r'WorkSiteUpdateLocationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteUpdateLocationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'newLatitude';
    yield serializers.serialize(
      object.newLatitude,
      specifiedType: const FullType(double),
    );
    yield r'newLongitude';
    yield serializers.serialize(
      object.newLongitude,
      specifiedType: const FullType(double),
    );
    yield r'newRadius';
    yield serializers.serialize(
      object.newRadius,
      specifiedType: const FullType(double),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteUpdateLocationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteUpdateLocationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'newLatitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.newLatitude = valueDes;
          break;
        case r'newLongitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.newLongitude = valueDes;
          break;
        case r'newRadius':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.newRadius = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkSiteUpdateLocationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteUpdateLocationRequestBuilder();
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

