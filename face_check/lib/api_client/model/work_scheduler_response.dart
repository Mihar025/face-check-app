//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'local_time.dart';

part 'work_scheduler_response.g.dart';

/// WorkSchedulerResponse
///
/// Properties:
/// * [workerId] 
/// * [startTime] 
/// * [endTime] 
@BuiltValue()
abstract class WorkSchedulerResponse implements Built<WorkSchedulerResponse, WorkSchedulerResponseBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'startTime')
  LocalTime? get startTime;

  @BuiltValueField(wireName: r'endTime')
  LocalTime? get endTime;

  WorkSchedulerResponse._();

  factory WorkSchedulerResponse([void updates(WorkSchedulerResponseBuilder b)]) = _$WorkSchedulerResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSchedulerResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSchedulerResponse> get serializer => _$WorkSchedulerResponseSerializer();
}

class _$WorkSchedulerResponseSerializer implements PrimitiveSerializer<WorkSchedulerResponse> {
  @override
  final Iterable<Type> types = const [WorkSchedulerResponse, _$WorkSchedulerResponse];

  @override
  final String wireName = r'WorkSchedulerResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSchedulerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workerId != null) {
      yield r'workerId';
      yield serializers.serialize(
        object.workerId,
        specifiedType: const FullType(int),
      );
    }
    if (object.startTime != null) {
      yield r'startTime';
      yield serializers.serialize(
        object.startTime,
        specifiedType: const FullType(LocalTime),
      );
    }
    if (object.endTime != null) {
      yield r'endTime';
      yield serializers.serialize(
        object.endTime,
        specifiedType: const FullType(LocalTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSchedulerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSchedulerResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'workerId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.workerId = valueDes;
          break;
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
  WorkSchedulerResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSchedulerResponseBuilder();
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

