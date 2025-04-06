//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'company_tax_calculation_request.g.dart';

/// CompanyTaxCalculationRequest
///
/// Properties:
/// * [year] 
/// * [month] 
@BuiltValue()
abstract class CompanyTaxCalculationRequest implements Built<CompanyTaxCalculationRequest, CompanyTaxCalculationRequestBuilder> {
  @BuiltValueField(wireName: r'year')
  int get year;

  @BuiltValueField(wireName: r'month')
  int get month;

  CompanyTaxCalculationRequest._();

  factory CompanyTaxCalculationRequest([void updates(CompanyTaxCalculationRequestBuilder b)]) = _$CompanyTaxCalculationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CompanyTaxCalculationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CompanyTaxCalculationRequest> get serializer => _$CompanyTaxCalculationRequestSerializer();
}

class _$CompanyTaxCalculationRequestSerializer implements PrimitiveSerializer<CompanyTaxCalculationRequest> {
  @override
  final Iterable<Type> types = const [CompanyTaxCalculationRequest, _$CompanyTaxCalculationRequest];

  @override
  final String wireName = r'CompanyTaxCalculationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CompanyTaxCalculationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'year';
    yield serializers.serialize(
      object.year,
      specifiedType: const FullType(int),
    );
    yield r'month';
    yield serializers.serialize(
      object.month,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CompanyTaxCalculationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CompanyTaxCalculationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'year':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.year = valueDes;
          break;
        case r'month':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.month = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CompanyTaxCalculationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CompanyTaxCalculationRequestBuilder();
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

