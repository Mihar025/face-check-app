//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'local_time.dart';

part 'work_site_update_working_hours_response.g.dart';

/// WorkSiteUpdateWorkingHoursResponse
///
/// Properties:
/// * [workSiteId] 
/// * [newStart] 
/// * [newEnd] 
@BuiltValue()
abstract class WorkSiteUpdateWorkingHoursResponse implements Built<WorkSiteUpdateWorkingHoursResponse, WorkSiteUpdateWorkingHoursResponseBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'newStart')
  LocalTime? get newStart;

  @BuiltValueField(wireName: r'newEnd')
  LocalTime? get newEnd;

  WorkSiteUpdateWorkingHoursResponse._();

  factory WorkSiteUpdateWorkingHoursResponse([void updates(WorkSiteUpdateWorkingHoursResponseBuilder b)]) = _$WorkSiteUpdateWorkingHoursResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteUpdateWorkingHoursResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteUpdateWorkingHoursResponse> get serializer => _$WorkSiteUpdateWorkingHoursResponseSerializer();
}

class _$WorkSiteUpdateWorkingHoursResponseSerializer implements PrimitiveSerializer<WorkSiteUpdateWorkingHoursResponse> {
  @override
  final Iterable<Type> types = const [WorkSiteUpdateWorkingHoursResponse, _$WorkSiteUpdateWorkingHoursResponse];

  @override
  final String wireName = r'WorkSiteUpdateWorkingHoursResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteUpdateWorkingHoursResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.newStart != null) {
      yield r'newStart';
      yield serializers.serialize(
        object.newStart,
        specifiedType: const FullType(LocalTime),
      );
    }
    if (object.newEnd != null) {
      yield r'newEnd';
      yield serializers.serialize(
        object.newEnd,
        specifiedType: const FullType(LocalTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteUpdateWorkingHoursResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteUpdateWorkingHoursResponseBuilder result,
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
        case r'newStart':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.newStart.replace(valueDes);
          break;
        case r'newEnd':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.newEnd.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkSiteUpdateWorkingHoursResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteUpdateWorkingHoursResponseBuilder();
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

