//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';

part 'company_tax_calculation_response.g.dart';

/// CompanyTaxCalculationResponse
///
/// Properties:
/// * [companyId] 
/// * [companyName] 
/// * [monthlyIncome] 
/// * [totalTaxes] 
/// * [socialSecurityTax] 
/// * [medicareTax] 
/// * [federalUnemploymentTax] 
/// * [nyUnemploymentTax] 
/// * [nyDisabilityInsurance] 
/// * [workersCompensation] 
/// * [employeeCount] 
/// * [totalPayroll] 
/// * [calculationDate] 
@BuiltValue()
abstract class CompanyTaxCalculationResponse implements Built<CompanyTaxCalculationResponse, CompanyTaxCalculationResponseBuilder> {
  @BuiltValueField(wireName: r'companyId')
  int? get companyId;

  @BuiltValueField(wireName: r'companyName')
  String? get companyName;

  @BuiltValueField(wireName: r'monthlyIncome')
  num? get monthlyIncome;

  @BuiltValueField(wireName: r'totalTaxes')
  num? get totalTaxes;

  @BuiltValueField(wireName: r'socialSecurityTax')
  num? get socialSecurityTax;

  @BuiltValueField(wireName: r'medicareTax')
  num? get medicareTax;

  @BuiltValueField(wireName: r'federalUnemploymentTax')
  num? get federalUnemploymentTax;

  @BuiltValueField(wireName: r'nyUnemploymentTax')
  num? get nyUnemploymentTax;

  @BuiltValueField(wireName: r'nyDisabilityInsurance')
  num? get nyDisabilityInsurance;

  @BuiltValueField(wireName: r'workersCompensation')
  num? get workersCompensation;

  @BuiltValueField(wireName: r'employeeCount')
  int? get employeeCount;

  @BuiltValueField(wireName: r'totalPayroll')
  num? get totalPayroll;

  @BuiltValueField(wireName: r'calculationDate')
  Date? get calculationDate;

  CompanyTaxCalculationResponse._();

  factory CompanyTaxCalculationResponse([void updates(CompanyTaxCalculationResponseBuilder b)]) = _$CompanyTaxCalculationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CompanyTaxCalculationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CompanyTaxCalculationResponse> get serializer => _$CompanyTaxCalculationResponseSerializer();
}

class _$CompanyTaxCalculationResponseSerializer implements PrimitiveSerializer<CompanyTaxCalculationResponse> {
  @override
  final Iterable<Type> types = const [CompanyTaxCalculationResponse, _$CompanyTaxCalculationResponse];

  @override
  final String wireName = r'CompanyTaxCalculationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CompanyTaxCalculationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.companyId != null) {
      yield r'companyId';
      yield serializers.serialize(
        object.companyId,
        specifiedType: const FullType(int),
      );
    }
    if (object.companyName != null) {
      yield r'companyName';
      yield serializers.serialize(
        object.companyName,
        specifiedType: const FullType(String),
      );
    }
    if (object.monthlyIncome != null) {
      yield r'monthlyIncome';
      yield serializers.serialize(
        object.monthlyIncome,
        specifiedType: const FullType(num),
      );
    }
    if (object.totalTaxes != null) {
      yield r'totalTaxes';
      yield serializers.serialize(
        object.totalTaxes,
        specifiedType: const FullType(num),
      );
    }
    if (object.socialSecurityTax != null) {
      yield r'socialSecurityTax';
      yield serializers.serialize(
        object.socialSecurityTax,
        specifiedType: const FullType(num),
      );
    }
    if (object.medicareTax != null) {
      yield r'medicareTax';
      yield serializers.serialize(
        object.medicareTax,
        specifiedType: const FullType(num),
      );
    }
    if (object.federalUnemploymentTax != null) {
      yield r'federalUnemploymentTax';
      yield serializers.serialize(
        object.federalUnemploymentTax,
        specifiedType: const FullType(num),
      );
    }
    if (object.nyUnemploymentTax != null) {
      yield r'nyUnemploymentTax';
      yield serializers.serialize(
        object.nyUnemploymentTax,
        specifiedType: const FullType(num),
      );
    }
    if (object.nyDisabilityInsurance != null) {
      yield r'nyDisabilityInsurance';
      yield serializers.serialize(
        object.nyDisabilityInsurance,
        specifiedType: const FullType(num),
      );
    }
    if (object.workersCompensation != null) {
      yield r'workersCompensation';
      yield serializers.serialize(
        object.workersCompensation,
        specifiedType: const FullType(num),
      );
    }
    if (object.employeeCount != null) {
      yield r'employeeCount';
      yield serializers.serialize(
        object.employeeCount,
        specifiedType: const FullType(int),
      );
    }
    if (object.totalPayroll != null) {
      yield r'totalPayroll';
      yield serializers.serialize(
        object.totalPayroll,
        specifiedType: const FullType(num),
      );
    }
    if (object.calculationDate != null) {
      yield r'calculationDate';
      yield serializers.serialize(
        object.calculationDate,
        specifiedType: const FullType(Date),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CompanyTaxCalculationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CompanyTaxCalculationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'companyId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.companyId = valueDes;
          break;
        case r'companyName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.companyName = valueDes;
          break;
        case r'monthlyIncome':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.monthlyIncome = valueDes;
          break;
        case r'totalTaxes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.totalTaxes = valueDes;
          break;
        case r'socialSecurityTax':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.socialSecurityTax = valueDes;
          break;
        case r'medicareTax':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.medicareTax = valueDes;
          break;
        case r'federalUnemploymentTax':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.federalUnemploymentTax = valueDes;
          break;
        case r'nyUnemploymentTax':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.nyUnemploymentTax = valueDes;
          break;
        case r'nyDisabilityInsurance':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.nyDisabilityInsurance = valueDes;
          break;
        case r'workersCompensation':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.workersCompensation = valueDes;
          break;
        case r'employeeCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.employeeCount = valueDes;
          break;
        case r'totalPayroll':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.totalPayroll = valueDes;
          break;
        case r'calculationDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.calculationDate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CompanyTaxCalculationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CompanyTaxCalculationResponseBuilder();
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

