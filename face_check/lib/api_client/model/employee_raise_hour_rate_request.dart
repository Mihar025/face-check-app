//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'employee_raise_hour_rate_request.g.dart';

/// EmployeeRaiseHourRateRequest
///
/// Properties:
/// * [baseHourlyRate] 
@BuiltValue()
abstract class EmployeeRaiseHourRateRequest implements Built<EmployeeRaiseHourRateRequest, EmployeeRaiseHourRateRequestBuilder> {
  @BuiltValueField(wireName: r'baseHourlyRate')
  num get baseHourlyRate;

  EmployeeRaiseHourRateRequest._();

  factory EmployeeRaiseHourRateRequest([void updates(EmployeeRaiseHourRateRequestBuilder b)]) = _$EmployeeRaiseHourRateRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(EmployeeRaiseHourRateRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<EmployeeRaiseHourRateRequest> get serializer => _$EmployeeRaiseHourRateRequestSerializer();
}

class _$EmployeeRaiseHourRateRequestSerializer implements PrimitiveSerializer<EmployeeRaiseHourRateRequest> {
  @override
  final Iterable<Type> types = const [EmployeeRaiseHourRateRequest, _$EmployeeRaiseHourRateRequest];

  @override
  final String wireName = r'EmployeeRaiseHourRateRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    EmployeeRaiseHourRateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'baseHourlyRate';
    yield serializers.serialize(
      object.baseHourlyRate,
      specifiedType: const FullType(num),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    EmployeeRaiseHourRateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required EmployeeRaiseHourRateRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'baseHourlyRate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.baseHourlyRate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  EmployeeRaiseHourRateRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = EmployeeRaiseHourRateRequestBuilder();
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

