//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element

import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';
import 'local_time.dart';

part 'change_punch_in_request.g.dart';

/// ChangePunchInRequest
///
/// Properties:
/// * [workerId] 
/// * [dateWhenWorkerDidntMakePunchIn] 
/// * [newPunchInDate] 
/// * [newPunchInTime] 
@BuiltValue()
abstract class ChangePunchInRequest implements Built<ChangePunchInRequest, ChangePunchInRequestBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'dateWhenWorkerDidntMakePunchIn')
  DateTime? get dateWhenWorkerDidntMakePunchIn;

  @BuiltValueField(wireName: r'newPunchInDate')
  Date? get newPunchInDate;

  @BuiltValueField(wireName: r'newPunchInTime')
  LocalTime? get newPunchInTime;

  ChangePunchInRequest._();

  factory ChangePunchInRequest([void updates(ChangePunchInRequestBuilder b)]) = _$ChangePunchInRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ChangePunchInRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ChangePunchInRequest> get serializer => _$ChangePunchInRequestSerializer();
}

class _$ChangePunchInRequestSerializer implements PrimitiveSerializer<ChangePunchInRequest> {
  @override
  final Iterable<Type> types = const [ChangePunchInRequest, _$ChangePunchInRequest];

  @override
  final String wireName = r'ChangePunchInRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ChangePunchInRequest object, {
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
    ChangePunchInRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ChangePunchInRequestBuilder result,
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
  ChangePunchInRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ChangePunchInRequestBuilder();
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

