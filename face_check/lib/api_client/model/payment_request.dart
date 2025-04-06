//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_request.g.dart';

/// PaymentRequest
///
/// Properties:
/// * [hourRate] 
/// * [overtimeRate] 
@BuiltValue()
abstract class PaymentRequest implements Built<PaymentRequest, PaymentRequestBuilder> {
  @BuiltValueField(wireName: r'hourRate')
  num get hourRate;

  @BuiltValueField(wireName: r'overtimeRate')
  num get overtimeRate;

  PaymentRequest._();

  factory PaymentRequest([void updates(PaymentRequestBuilder b)]) = _$PaymentRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentRequest> get serializer => _$PaymentRequestSerializer();
}

class _$PaymentRequestSerializer implements PrimitiveSerializer<PaymentRequest> {
  @override
  final Iterable<Type> types = const [PaymentRequest, _$PaymentRequest];

  @override
  final String wireName = r'PaymentRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'hourRate';
    yield serializers.serialize(
      object.hourRate,
      specifiedType: const FullType(num),
    );
    yield r'overtimeRate';
    yield serializers.serialize(
      object.overtimeRate,
      specifiedType: const FullType(num),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'hourRate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.hourRate = valueDes;
          break;
        case r'overtimeRate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.overtimeRate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentRequestBuilder();
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

