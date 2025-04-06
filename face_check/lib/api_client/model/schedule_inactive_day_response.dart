//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';

part 'schedule_inactive_day_response.g.dart';

/// ScheduleInactiveDayResponse
///
/// Properties:
/// * [workSiteId] 
/// * [inactiveDate] 
@BuiltValue()
abstract class ScheduleInactiveDayResponse implements Built<ScheduleInactiveDayResponse, ScheduleInactiveDayResponseBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'inactiveDate')
  Date? get inactiveDate;

  ScheduleInactiveDayResponse._();

  factory ScheduleInactiveDayResponse([void updates(ScheduleInactiveDayResponseBuilder b)]) = _$ScheduleInactiveDayResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ScheduleInactiveDayResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ScheduleInactiveDayResponse> get serializer => _$ScheduleInactiveDayResponseSerializer();
}

class _$ScheduleInactiveDayResponseSerializer implements PrimitiveSerializer<ScheduleInactiveDayResponse> {
  @override
  final Iterable<Type> types = const [ScheduleInactiveDayResponse, _$ScheduleInactiveDayResponse];

  @override
  final String wireName = r'ScheduleInactiveDayResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ScheduleInactiveDayResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.inactiveDate != null) {
      yield r'inactiveDate';
      yield serializers.serialize(
        object.inactiveDate,
        specifiedType: const FullType(Date),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    ScheduleInactiveDayResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ScheduleInactiveDayResponseBuilder result,
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
        case r'inactiveDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.inactiveDate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ScheduleInactiveDayResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ScheduleInactiveDayResponseBuilder();
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

