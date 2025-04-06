//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element

import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';
import 'local_time.dart';

part 'change_punch_in_for_worker_response.g.dart';

/// ChangePunchInForWorkerResponse
///
/// Properties:
/// * [workerId] 
/// * [dateWhenWorkerDidntMakePunchIn] 
/// * [newPunchInDate] 
/// * [newPunchInTime] 
@BuiltValue()
abstract class ChangePunchInForWorkerResponse implements Built<ChangePunchInForWorkerResponse, ChangePunchInForWorkerResponseBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'dateWhenWorkerDidntMakePunchIn')
  DateTime? get dateWhenWorkerDidntMakePunchIn;

  @BuiltValueField(wireName: r'newPunchInDate')
  Date? get newPunchInDate;

  @BuiltValueField(wireName: r'newPunchInTime')
  LocalTime? get newPunchInTime;

  ChangePunchInForWorkerResponse._();

  factory ChangePunchInForWorkerResponse([void updates(ChangePunchInForWorkerResponseBuilder b)]) = _$ChangePunchInForWorkerResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ChangePunchInForWorkerResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ChangePunchInForWorkerResponse> get serializer => _$ChangePunchInForWorkerResponseSerializer();
}

class _$ChangePunchInForWorkerResponseSerializer implements PrimitiveSerializer<ChangePunchInForWorkerResponse> {
  @override
  final Iterable<Type> types = const [ChangePunchInForWorkerResponse, _$ChangePunchInForWorkerResponse];

  @override
  final String wireName = r'ChangePunchInForWorkerResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ChangePunchInForWorkerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workerId != null) {
      yield r'workerId';
      yield serializers.serialize(
        object.workerId,
        specifiedType: const FullType(int),
      );
    }
    if (object.dateWhenWorkerDidntMakePunchIn != null) {
      yield r'dateWhenWorkerDidntMakePunchIn';
      yield serializers.serialize(
        object.dateWhenWorkerDidntMakePunchIn,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.newPunchInDate != null) {
      yield r'newPunchInDate';
      yield serializers.serialize(
        object.newPunchInDate,
        specifiedType: const FullType(Date),
      );
    }
    if (object.newPunchInTime != null) {
      yield r'newPunchInTime';
      yield serializers.serialize(
        object.newPunchInTime,
        specifiedType: const FullType(LocalTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    ChangePunchInForWorkerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ChangePunchInForWorkerResponseBuilder result,
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
        case r'dateWhenWorkerDidntMakePunchIn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.dateWhenWorkerDidntMakePunchIn = valueDes;
          break;
        case r'newPunchInDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.newPunchInDate = valueDes;
          break;
        case r'newPunchInTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.newPunchInTime.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ChangePunchInForWorkerResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ChangePunchInForWorkerResponseBuilder();
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

