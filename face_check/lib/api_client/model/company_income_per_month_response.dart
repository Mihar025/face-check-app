//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'company_income_per_month_response.g.dart';

/// CompanyIncomePerMonthResponse
///
/// Properties:
/// * [companyId] 
/// * [companyIncomePerMonth] 
@BuiltValue()
abstract class CompanyIncomePerMonthResponse implements Built<CompanyIncomePerMonthResponse, CompanyIncomePerMonthResponseBuilder> {
  @BuiltValueField(wireName: r'companyId')
  int? get companyId;

  @BuiltValueField(wireName: r'companyIncomePerMonth')
  num? get companyIncomePerMonth;

  CompanyIncomePerMonthResponse._();

  factory CompanyIncomePerMonthResponse([void updates(CompanyIncomePerMonthResponseBuilder b)]) = _$CompanyIncomePerMonthResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CompanyIncomePerMonthResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CompanyIncomePerMonthResponse> get serializer => _$CompanyIncomePerMonthResponseSerializer();
}

class _$CompanyIncomePerMonthResponseSerializer implements PrimitiveSerializer<CompanyIncomePerMonthResponse> {
  @override
  final Iterable<Type> types = const [CompanyIncomePerMonthResponse, _$CompanyIncomePerMonthResponse];

  @override
  final String wireName = r'CompanyIncomePerMonthResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CompanyIncomePerMonthResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.companyId != null) {
      yield r'companyId';
      yield serializers.serialize(
        object.companyId,
        specifiedType: const FullType(int),
      );
    }
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
    CompanyIncomePerMonthResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CompanyIncomePerMonthResponseBuilder result,
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
  CompanyIncomePerMonthResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CompanyIncomePerMonthResponseBuilder();
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

