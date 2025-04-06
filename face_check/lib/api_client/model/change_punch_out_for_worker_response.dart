//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element

import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';
import 'local_time.dart';

part 'change_punch_out_for_worker_response.g.dart';

/// ChangePunchOutForWorkerResponse
///
/// Properties:
/// * [workerId] 
/// * [dateWhenWorkerDidntMakePunchOut] 
/// * [newPunchOutDate] 
/// * [newPunchOutTime] 
@BuiltValue()
abstract class ChangePunchOutForWorkerResponse implements Built<ChangePunchOutForWorkerResponse, ChangePunchOutForWorkerResponseBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'dateWhenWorkerDidntMakePunchOut')
  DateTime? get dateWhenWorkerDidntMakePunchOut;

  @BuiltValueField(wireName: r'newPunchOutDate')
  Date? get newPunchOutDate;

  @BuiltValueField(wireName: r'newPunchOutTime')
  LocalTime? get newPunchOutTime;

  ChangePunchOutForWorkerResponse._();

  factory ChangePunchOutForWorkerResponse([void updates(ChangePunchOutForWorkerResponseBuilder b)]) = _$ChangePunchOutForWorkerResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ChangePunchOutForWorkerResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ChangePunchOutForWorkerResponse> get serializer => _$ChangePunchOutForWorkerResponseSerializer();
}

class _$ChangePunchOutForWorkerResponseSerializer implements PrimitiveSerializer<ChangePunchOutForWorkerResponse> {
  @override
  final Iterable<Type> types = const [ChangePunchOutForWorkerResponse, _$ChangePunchOutForWorkerResponse];

  @override
  final String wireName = r'ChangePunchOutForWorkerResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ChangePunchOutForWorkerResponse object, {
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
    ChangePunchOutForWorkerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ChangePunchOutForWorkerResponseBuilder result,
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
  ChangePunchOutForWorkerResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ChangePunchOutForWorkerResponseBuilder();
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

