//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element

import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';
import 'local_time.dart';

part 'change_punch_out_request.g.dart';

/// ChangePunchOutRequest
///
/// Properties:
/// * [workerId] 
/// * [dateWhenWorkerDidntMakePunchOut] 
/// * [newPunchOutDate] 
/// * [newPunchOutTime] 
@BuiltValue()
abstract class ChangePunchOutRequest implements Built<ChangePunchOutRequest, ChangePunchOutRequestBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'dateWhenWorkerDidntMakePunchOut')
  DateTime? get dateWhenWorkerDidntMakePunchOut;

  @BuiltValueField(wireName: r'newPunchOutDate')
  Date? get newPunchOutDate;

  @BuiltValueField(wireName: r'newPunchOutTime')
  LocalTime? get newPunchOutTime;

  ChangePunchOutRequest._();

  factory ChangePunchOutRequest([void updates(ChangePunchOutRequestBuilder b)]) = _$ChangePunchOutRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ChangePunchOutRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ChangePunchOutRequest> get serializer => _$ChangePunchOutRequestSerializer();
}

class _$ChangePunchOutRequestSerializer implements PrimitiveSerializer<ChangePunchOutRequest> {
  @override
  final Iterable<Type> types = const [ChangePunchOutRequest, _$ChangePunchOutRequest];

  @override
  final String wireName = r'ChangePunchOutRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ChangePunchOutRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workerId != null) {
      yield r'workerId';
      yield serializers.serialize(
        object.workerId,
        specifiedType: const FullType(int),
      );
    }
    if (object.dateWhenWorkerDidntMakePunchOut != null) {
      yield r'dateWhenWorkerDidntMakePunchOut';
      yield serializers.serialize(
        object.dateWhenWorkerDidntMakePunchOut,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.newPunchOutDate != null) {
      yield r'newPunchOutDate';
      yield serializers.serialize(
        object.newPunchOutDate,
        specifiedType: const FullType(Date),
      );
    }
    if (object.newPunchOutTime != null) {
      yield r'newPunchOutTime';
      yield serializers.serialize(
        object.newPunchOutTime,
        specifiedType: const FullType(LocalTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    ChangePunchOutRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ChangePunchOutRequestBuilder result,
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
        case r'dateWhenWorkerDidntMakePunchOut':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.dateWhenWorkerDidntMakePunchOut = valueDes;
          break;
        case r'newPunchOutDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.newPunchOutDate = valueDes;
          break;
        case r'newPunchOutTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.newPunchOutTime.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ChangePunchOutRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ChangePunchOutRequestBuilder();
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

