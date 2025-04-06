//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'daily_schedule_response.dart';

part 'weekly_schedule_response.g.dart';

/// WeeklyScheduleResponse
///
/// Properties:
/// * [dailySchedules] 
/// * [totalWeekHours] 
/// * [regularHours] 
/// * [overtimeHours] 
@BuiltValue()
abstract class WeeklyScheduleResponse implements Built<WeeklyScheduleResponse, WeeklyScheduleResponseBuilder> {
  @BuiltValueField(wireName: r'dailySchedules')
  BuiltList<DailyScheduleResponse>? get dailySchedules;

  @BuiltValueField(wireName: r'totalWeekHours')
  double? get totalWeekHours;

  @BuiltValueField(wireName: r'regularHours')
  double? get regularHours;

  @BuiltValueField(wireName: r'overtimeHours')
  double? get overtimeHours;

  WeeklyScheduleResponse._();

  factory WeeklyScheduleResponse([void updates(WeeklyScheduleResponseBuilder b)]) = _$WeeklyScheduleResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WeeklyScheduleResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WeeklyScheduleResponse> get serializer => _$WeeklyScheduleResponseSerializer();
}

class _$WeeklyScheduleResponseSerializer implements PrimitiveSerializer<WeeklyScheduleResponse> {
  @override
  final Iterable<Type> types = const [WeeklyScheduleResponse, _$WeeklyScheduleResponse];

  @override
  final String wireName = r'WeeklyScheduleResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WeeklyScheduleResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.dailySchedules != null) {
      yield r'dailySchedules';
      yield serializers.serialize(
        object.dailySchedules,
        specifiedType: const FullType(BuiltList, [FullType(DailyScheduleResponse)]),
      );
    }
    if (object.totalWeekHours != null) {
      yield r'totalWeekHours';
      yield serializers.serialize(
        object.totalWeekHours,
        specifiedType: const FullType(double),
      );
    }
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
  }

  @override
  Object serialize(
    Serializers serializers,
    WeeklyScheduleResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WeeklyScheduleResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'dailySchedules':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(DailyScheduleResponse)]),
          ) as BuiltList<DailyScheduleResponse>;
          result.dailySchedules.replace(valueDes);
          break;
        case r'totalWeekHours':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.totalWeekHours = valueDes;
          break;
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WeeklyScheduleResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WeeklyScheduleResponseBuilder();
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

