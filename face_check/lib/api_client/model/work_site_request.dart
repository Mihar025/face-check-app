//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'local_time.dart';

part 'work_site_request.g.dart';

/// WorkSiteRequest
///
/// Properties:
/// * [workSiteName] 
/// * [address] 
/// * [latitude] 
/// * [longitude] 
/// * [allowedRadius] 
/// * [workDayStart] 
/// * [workDayEnd] 
@BuiltValue()
abstract class WorkSiteRequest implements Built<WorkSiteRequest, WorkSiteRequestBuilder> {
  @BuiltValueField(wireName: r'workSiteName')
  String get workSiteName;

  @BuiltValueField(wireName: r'address')
  String get address;

  @BuiltValueField(wireName: r'latitude')
  double get latitude;

  @BuiltValueField(wireName: r'longitude')
  double get longitude;

  @BuiltValueField(wireName: r'allowedRadius')
  double get allowedRadius;

  @BuiltValueField(wireName: r'workDayStart')
  LocalTime get workDayStart;

  @BuiltValueField(wireName: r'workDayEnd')
  LocalTime get workDayEnd;

  WorkSiteRequest._();

  factory WorkSiteRequest([void updates(WorkSiteRequestBuilder b)]) = _$WorkSiteRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteRequest> get serializer => _$WorkSiteRequestSerializer();
}

class _$WorkSiteRequestSerializer implements PrimitiveSerializer<WorkSiteRequest> {
  @override
  final Iterable<Type> types = const [WorkSiteRequest, _$WorkSiteRequest];

  @override
  final String wireName = r'WorkSiteRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'workSiteName';
    yield serializers.serialize(
      object.workSiteName,
      specifiedType: const FullType(String),
    );
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
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
    yield r'allowedRadius';
    yield serializers.serialize(
      object.allowedRadius,
      specifiedType: const FullType(double),
    );
    yield r'workDayStart';
    yield serializers.serialize(
      object.workDayStart,
      specifiedType: const FullType(LocalTime),
    );
    yield r'workDayEnd';
    yield serializers.serialize(
      object.workDayEnd,
      specifiedType: const FullType(LocalTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'workSiteName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workSiteName = valueDes;
          break;
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
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
        case r'allowedRadius':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.allowedRadius = valueDes;
          break;
        case r'workDayStart':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.workDayStart.replace(valueDes);
          break;
        case r'workDayEnd':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.workDayEnd.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkSiteRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteRequestBuilder();
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

