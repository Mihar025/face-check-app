//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'employee_salary_response.g.dart';

/// EmployeeSalaryResponse
///
/// Properties:
/// * [employeeId] 
/// * [firstName] 
/// * [lastName] 
/// * [email] 
/// * [baseHourlyRate] 
@BuiltValue()
abstract class EmployeeSalaryResponse implements Built<EmployeeSalaryResponse, EmployeeSalaryResponseBuilder> {
  @BuiltValueField(wireName: r'employeeId')
  int? get employeeId;

  @BuiltValueField(wireName: r'firstName')
  String? get firstName;

  @BuiltValueField(wireName: r'lastName')
  String? get lastName;

  @BuiltValueField(wireName: r'email')
  String? get email;

  @BuiltValueField(wireName: r'baseHourlyRate')
  num? get baseHourlyRate;

  EmployeeSalaryResponse._();

  factory EmployeeSalaryResponse([void updates(EmployeeSalaryResponseBuilder b)]) = _$EmployeeSalaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(EmployeeSalaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<EmployeeSalaryResponse> get serializer => _$EmployeeSalaryResponseSerializer();
}

class _$EmployeeSalaryResponseSerializer implements PrimitiveSerializer<EmployeeSalaryResponse> {
  @override
  final Iterable<Type> types = const [EmployeeSalaryResponse, _$EmployeeSalaryResponse];

  @override
  final String wireName = r'EmployeeSalaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    EmployeeSalaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.employeeId != null) {
      yield r'employeeId';
      yield serializers.serialize(
        object.employeeId,
        specifiedType: const FullType(int),
      );
    }
    if (object.firstName != null) {
      yield r'firstName';
      yield serializers.serialize(
        object.firstName,
        specifiedType: const FullType(String),
      );
    }
    if (object.lastName != null) {
      yield r'lastName';
      yield serializers.serialize(
        object.lastName,
        specifiedType: const FullType(String),
      );
    }
    if (object.email != null) {
      yield r'email';
      yield serializers.serialize(
        object.email,
        specifiedType: const FullType(String),
      );
    }
    if (object.baseHourlyRate != null) {
      yield r'baseHourlyRate';
      yield serializers.serialize(
        object.baseHourlyRate,
        specifiedType: const FullType(num),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    EmployeeSalaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required EmployeeSalaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'employeeId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.employeeId = valueDes;
          break;
        case r'firstName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.firstName = valueDes;
          break;
        case r'lastName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.lastName = valueDes;
          break;
        case r'email':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.email = valueDes;
          break;
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
  EmployeeSalaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = EmployeeSalaryResponseBuilder();
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

