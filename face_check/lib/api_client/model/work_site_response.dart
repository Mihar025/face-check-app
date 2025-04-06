//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'local_time.dart';

part 'work_site_response.g.dart';

/// WorkSiteResponse
///
/// Properties:
/// * [workSiteId] 
/// * [workSiteName] 
/// * [address] 
/// * [latitude] 
/// * [longitude] 
/// * [allowedRadius] 
/// * [workDayStart] 
/// * [workDayEnd] 
@BuiltValue()
abstract class WorkSiteResponse implements Built<WorkSiteResponse, WorkSiteResponseBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'workSiteName')
  String? get workSiteName;

  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'latitude')
  double? get latitude;

  @BuiltValueField(wireName: r'longitude')
  double? get longitude;

  @BuiltValueField(wireName: r'allowedRadius')
  double? get allowedRadius;

  @BuiltValueField(wireName: r'workDayStart')
  LocalTime? get workDayStart;

  @BuiltValueField(wireName: r'workDayEnd')
  LocalTime? get workDayEnd;

  WorkSiteResponse._();

  factory WorkSiteResponse([void updates(WorkSiteResponseBuilder b)]) = _$WorkSiteResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteResponse> get serializer => _$WorkSiteResponseSerializer();
}

class _$WorkSiteResponseSerializer implements PrimitiveSerializer<WorkSiteResponse> {
  @override
  final Iterable<Type> types = const [WorkSiteResponse, _$WorkSiteResponse];

  @override
  final String wireName = r'WorkSiteResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.workSiteName != null) {
      yield r'workSiteName';
      yield serializers.serialize(
        object.workSiteName,
        specifiedType: const FullType(String),
      );
    }
    if (object.address != null) {
      yield r'address';
      yield serializers.serialize(
        object.address,
        specifiedType: const FullType(String),
      );
    }
    if (object.latitude != null) {
      yield r'latitude';
      yield serializers.serialize(
        object.latitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.longitude != null) {
      yield r'longitude';
      yield serializers.serialize(
        object.longitude,
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
    if (object.workDayStart != null) {
      yield r'workDayStart';
      yield serializers.serialize(
        object.workDayStart,
        specifiedType: const FullType(LocalTime),
      );
    }
    if (object.workDayEnd != null) {
      yield r'workDayEnd';
      yield serializers.serialize(
        object.workDayEnd,
        specifiedType: const FullType(LocalTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteResponseBuilder result,
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
  WorkSiteResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteResponseBuilder();
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

