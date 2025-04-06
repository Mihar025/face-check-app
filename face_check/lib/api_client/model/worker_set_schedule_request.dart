//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'local_time.dart';

part 'worker_set_schedule_request.g.dart';

/// WorkerSetScheduleRequest
///
/// Properties:
/// * [startTime] 
/// * [endTime] 
@BuiltValue()
abstract class WorkerSetScheduleRequest implements Built<WorkerSetScheduleRequest, WorkerSetScheduleRequestBuilder> {
  @BuiltValueField(wireName: r'startTime')
  LocalTime get startTime;

  @BuiltValueField(wireName: r'endTime')
  LocalTime get endTime;

  WorkerSetScheduleRequest._();

  factory WorkerSetScheduleRequest([void updates(WorkerSetScheduleRequestBuilder b)]) = _$WorkerSetScheduleRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkerSetScheduleRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkerSetScheduleRequest> get serializer => _$WorkerSetScheduleRequestSerializer();
}

class _$WorkerSetScheduleRequestSerializer implements PrimitiveSerializer<WorkerSetScheduleRequest> {
  @override
  final Iterable<Type> types = const [WorkerSetScheduleRequest, _$WorkerSetScheduleRequest];

  @override
  final String wireName = r'WorkerSetScheduleRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkerSetScheduleRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'startTime';
    yield serializers.serialize(
      object.startTime,
      specifiedType: const FullType(LocalTime),
    );
    yield r'endTime';
    yield serializers.serialize(
      object.endTime,
      specifiedType: const FullType(LocalTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkerSetScheduleRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkerSetScheduleRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'startTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.startTime.replace(valueDes);
          break;
        case r'endTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.endTime.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkerSetScheduleRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkerSetScheduleRequestBuilder();
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

