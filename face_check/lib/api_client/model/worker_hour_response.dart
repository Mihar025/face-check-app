//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'worker_hour_response.g.dart';

/// WorkerHourResponse
///
/// Properties:
/// * [regularHours] 
/// * [overtimeHours] 
/// * [totalHours] 
@BuiltValue()
abstract class WorkerHourResponse implements Built<WorkerHourResponse, WorkerHourResponseBuilder> {
  @BuiltValueField(wireName: r'regularHours')
  double? get regularHours;

  @BuiltValueField(wireName: r'overtimeHours')
  double? get overtimeHours;

  @BuiltValueField(wireName: r'totalHours')
  double? get totalHours;

  WorkerHourResponse._();

  factory WorkerHourResponse([void updates(WorkerHourResponseBuilder b)]) = _$WorkerHourResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkerHourResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkerHourResponse> get serializer => _$WorkerHourResponseSerializer();
}

class _$WorkerHourResponseSerializer implements PrimitiveSerializer<WorkerHourResponse> {
  @override
  final Iterable<Type> types = const [WorkerHourResponse, _$WorkerHourResponse];

  @override
  final String wireName = r'WorkerHourResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkerHourResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.regularHours != null) {
      yield r'regularHours';
      yield serializers.serialize(
        object.regularHours,
        specifiedType: const FullType(double),
      );
    }
    if (object.overtimeHours != null) {
      yield r'overtimeHours';
      yield serializers.serialize(
        object.overtimeHours,
        specifiedType: const FullType(double),
      );
    }
    if (object.totalHours != null) {
      yield r'totalHours';
      yield serializers.serialize(
        object.totalHours,
        specifiedType: const FullType(double),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkerHourResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkerHourResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'regularHours':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.regularHours = valueDes;
          break;
        case r'overtimeHours':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.overtimeHours = valueDes;
          break;
        case r'totalHours':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.totalHours = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkerHourResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkerHourResponseBuilder();
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

