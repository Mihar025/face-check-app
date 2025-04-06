//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'company_income_per_month_request.g.dart';

/// CompanyIncomePerMonthRequest
///
/// Properties:
/// * [companyIncomePerMonth] 
@BuiltValue()
abstract class CompanyIncomePerMonthRequest implements Built<CompanyIncomePerMonthRequest, CompanyIncomePerMonthRequestBuilder> {
  @BuiltValueField(wireName: r'companyIncomePerMonth')
  num? get companyIncomePerMonth;

  CompanyIncomePerMonthRequest._();

  factory CompanyIncomePerMonthRequest([void updates(CompanyIncomePerMonthRequestBuilder b)]) = _$CompanyIncomePerMonthRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CompanyIncomePerMonthRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CompanyIncomePerMonthRequest> get serializer => _$CompanyIncomePerMonthRequestSerializer();
}

class _$CompanyIncomePerMonthRequestSerializer implements PrimitiveSerializer<CompanyIncomePerMonthRequest> {
  @override
  final Iterable<Type> types = const [CompanyIncomePerMonthRequest, _$CompanyIncomePerMonthRequest];

  @override
  final String wireName = r'CompanyIncomePerMonthRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CompanyIncomePerMonthRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.companyIncomePerMonth != null) {
      yield r'companyIncomePerMonth';
      yield serializers.serialize(
        object.companyIncomePerMonth,
        specifiedType: const FullType(num),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CompanyIncomePerMonthRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CompanyIncomePerMonthRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'companyIncomePerMonth':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.companyIncomePerMonth = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CompanyIncomePerMonthRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CompanyIncomePerMonthRequestBuilder();
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

