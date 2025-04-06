//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';

part 'schedule_inactive_day_request.g.dart';

/// ScheduleInactiveDayRequest
///
/// Properties:
/// * [inactiveDate] 
@BuiltValue()
abstract class ScheduleInactiveDayRequest implements Built<ScheduleInactiveDayRequest, ScheduleInactiveDayRequestBuilder> {
  @BuiltValueField(wireName: r'inactiveDate')
  Date get inactiveDate;

  ScheduleInactiveDayRequest._();

  factory ScheduleInactiveDayRequest([void updates(ScheduleInactiveDayRequestBuilder b)]) = _$ScheduleInactiveDayRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ScheduleInactiveDayRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ScheduleInactiveDayRequest> get serializer => _$ScheduleInactiveDayRequestSerializer();
}

class _$ScheduleInactiveDayRequestSerializer implements PrimitiveSerializer<ScheduleInactiveDayRequest> {
  @override
  final Iterable<Type> types = const [ScheduleInactiveDayRequest, _$ScheduleInactiveDayRequest];

  @override
  final String wireName = r'ScheduleInactiveDayRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ScheduleInactiveDayRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'inactiveDate';
    yield serializers.serialize(
      object.inactiveDate,
      specifiedType: const FullType(Date),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ScheduleInactiveDayRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ScheduleInactiveDayRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
  ScheduleInactiveDayRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ScheduleInactiveDayRequestBuilder();
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

