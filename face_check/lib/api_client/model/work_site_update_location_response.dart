//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'work_site_update_location_response.g.dart';

/// WorkSiteUpdateLocationResponse
///
/// Properties:
/// * [workSiteId] 
/// * [newLatitude] 
/// * [newLongitude] 
/// * [newRadius] 
@BuiltValue()
abstract class WorkSiteUpdateLocationResponse implements Built<WorkSiteUpdateLocationResponse, WorkSiteUpdateLocationResponseBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'newLatitude')
  double? get newLatitude;

  @BuiltValueField(wireName: r'newLongitude')
  double? get newLongitude;

  @BuiltValueField(wireName: r'newRadius')
  double? get newRadius;

  WorkSiteUpdateLocationResponse._();

  factory WorkSiteUpdateLocationResponse([void updates(WorkSiteUpdateLocationResponseBuilder b)]) = _$WorkSiteUpdateLocationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteUpdateLocationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteUpdateLocationResponse> get serializer => _$WorkSiteUpdateLocationResponseSerializer();
}

class _$WorkSiteUpdateLocationResponseSerializer implements PrimitiveSerializer<WorkSiteUpdateLocationResponse> {
  @override
  final Iterable<Type> types = const [WorkSiteUpdateLocationResponse, _$WorkSiteUpdateLocationResponse];

  @override
  final String wireName = r'WorkSiteUpdateLocationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteUpdateLocationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.newLatitude != null) {
      yield r'newLatitude';
      yield serializers.serialize(
        object.newLatitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.newLongitude != null) {
      yield r'newLongitude';
      yield serializers.serialize(
        object.newLongitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.newRadius != null) {
      yield r'newRadius';
      yield serializers.serialize(
        object.newRadius,
        specifiedType: const FullType(double),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteUpdateLocationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteUpdateLocationResponseBuilder result,
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
  WorkSiteUpdateLocationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteUpdateLocationResponseBuilder();
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

