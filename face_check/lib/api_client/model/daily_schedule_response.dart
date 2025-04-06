//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element

import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';
import 'local_time.dart';

part 'daily_schedule_response.g.dart';

/// DailyScheduleResponse
///
/// Properties:
/// * [dayOfWeek] 
/// * [date] 
/// * [hoursWorked] 
/// * [startTime] 
/// * [endTime] 
/// * [workSiteName] 
/// * [isOnDuty] 
@BuiltValue()
abstract class DailyScheduleResponse implements Built<DailyScheduleResponse, DailyScheduleResponseBuilder> {
  @BuiltValueField(wireName: r'dayOfWeek')
  String? get dayOfWeek;

  @BuiltValueField(wireName: r'date')
  Date? get date;

  @BuiltValueField(wireName: r'hoursWorked')
  double? get hoursWorked;

  @BuiltValueField(wireName: r'startTime')
  LocalTime? get startTime;

  @BuiltValueField(wireName: r'endTime')
  LocalTime? get endTime;

  @BuiltValueField(wireName: r'workSiteName')
  String? get workSiteName;

  @BuiltValueField(wireName: r'isOnDuty')
  bool? get isOnDuty;

  DailyScheduleResponse._();

  factory DailyScheduleResponse([void updates(DailyScheduleResponseBuilder b)]) = _$DailyScheduleResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(DailyScheduleResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<DailyScheduleResponse> get serializer => _$DailyScheduleResponseSerializer();
}

class _$DailyScheduleResponseSerializer implements PrimitiveSerializer<DailyScheduleResponse> {
  @override
  final Iterable<Type> types = const [DailyScheduleResponse, _$DailyScheduleResponse];

  @override
  final String wireName = r'DailyScheduleResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    DailyScheduleResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.dayOfWeek != null) {
      yield r'dayOfWeek';
      yield serializers.serialize(
        object.dayOfWeek,
        specifiedType: const FullType(String),
      );
    }
    if (object.date != null) {
      yield r'date';
      yield serializers.serialize(
        object.date,
        specifiedType: const FullType(Date),
      );
    }
    if (object.hoursWorked != null) {
      yield r'hoursWorked';
      yield serializers.serialize(
        object.hoursWorked,
        specifiedType: const FullType(double),
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
    if (object.workSiteName != null) {
      yield r'workSiteName';
      yield serializers.serialize(
        object.workSiteName,
        specifiedType: const FullType(String),
      );
    }
    if (object.isOnDuty != null) {
      yield r'isOnDuty';
      yield serializers.serialize(
        object.isOnDuty,
        specifiedType: const FullType(bool),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    DailyScheduleResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required DailyScheduleResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'dayOfWeek':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.dayOfWeek = valueDes;
          break;
        case r'date':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.date = valueDes;
          break;
        case r'hoursWorked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.hoursWorked = valueDes;
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
        case r'workSiteName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workSiteName = valueDes;
          break;
        case r'isOnDuty':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.isOnDuty = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  DailyScheduleResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = DailyScheduleResponseBuilder();
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

