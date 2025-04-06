//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';

part 'registration_request.g.dart';

/// RegistrationRequest
///
/// Properties:
/// * [firstName] 
/// * [lastName] 
/// * [homeAddress] 
/// * [dateOfBirth] 
/// * [companyName] 
/// * [companyAddress] 
/// * [gender] 
/// * [phoneNumber] 
/// * [email] 
/// * [password] 
/// * [ssnWORKER] 
@BuiltValue()
abstract class RegistrationRequest implements Built<RegistrationRequest, RegistrationRequestBuilder> {
  @BuiltValueField(wireName: r'firstName')
  String get firstName;

  @BuiltValueField(wireName: r'lastName')
  String get lastName;

  @BuiltValueField(wireName: r'homeAddress')
  String get homeAddress;

  @BuiltValueField(wireName: r'dateOfBirth')
  Date? get dateOfBirth;

  @BuiltValueField(wireName: r'companyName')
  String get companyName;

  @BuiltValueField(wireName: r'companyAddress')
  String get companyAddress;

  @BuiltValueField(wireName: r'gender')
  RegistrationRequestGenderEnum get gender;
  // enum genderEnum {  MALE,  FEMALE,  OTHER,  };

  @BuiltValueField(wireName: r'phoneNumber')
  String get phoneNumber;

  @BuiltValueField(wireName: r'email')
  String get email;

  @BuiltValueField(wireName: r'password')
  String get password;

  @BuiltValueField(wireName: r'ssn_WORKER')
  num? get ssnWORKER;

  RegistrationRequest._();

  factory RegistrationRequest([void updates(RegistrationRequestBuilder b)]) = _$RegistrationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RegistrationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RegistrationRequest> get serializer => _$RegistrationRequestSerializer();
}

class _$RegistrationRequestSerializer implements PrimitiveSerializer<RegistrationRequest> {
  @override
  final Iterable<Type> types = const [RegistrationRequest, _$RegistrationRequest];

  @override
  final String wireName = r'RegistrationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RegistrationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'firstName';
    yield serializers.serialize(
      object.firstName,
      specifiedType: const FullType(String),
    );
    yield r'lastName';
    yield serializers.serialize(
      object.lastName,
      specifiedType: const FullType(String),
    );
    yield r'homeAddress';
    yield serializers.serialize(
      object.homeAddress,
      specifiedType: const FullType(String),
    );
    if (object.dateOfBirth != null) {
      yield r'dateOfBirth';
      yield serializers.serialize(
        object.dateOfBirth,
        specifiedType: const FullType(Date),
      );
    }
    yield r'companyName';
    yield serializers.serialize(
      object.companyName,
      specifiedType: const FullType(String),
    );
    yield r'companyAddress';
    yield serializers.serialize(
      object.companyAddress,
      specifiedType: const FullType(String),
    );
    yield r'gender';
    yield serializers.serialize(
      object.gender,
      specifiedType: const FullType(RegistrationRequestGenderEnum),
    );
    yield r'phoneNumber';
    yield serializers.serialize(
      object.phoneNumber,
      specifiedType: const FullType(String),
    );
    yield r'email';
    yield serializers.serialize(
      object.email,
      specifiedType: const FullType(String),
    );
    yield r'password';
    yield serializers.serialize(
      object.password,
      specifiedType: const FullType(String),
    );
    if (object.ssnWORKER != null) {
      yield r'ssn_WORKER';
      yield serializers.serialize(
        object.ssnWORKER,
        specifiedType: const FullType(num),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    RegistrationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RegistrationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
        case r'homeAddress':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.homeAddress = valueDes;
          break;
        case r'dateOfBirth':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.dateOfBirth = valueDes;
          break;
        case r'companyName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.companyName = valueDes;
          break;
        case r'companyAddress':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.companyAddress = valueDes;
          break;
        case r'gender':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RegistrationRequestGenderEnum),
          ) as RegistrationRequestGenderEnum;
          result.gender = valueDes;
          break;
        case r'phoneNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.phoneNumber = valueDes;
          break;
        case r'email':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.email = valueDes;
          break;
        case r'password':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.password = valueDes;
          break;
        case r'ssn_WORKER':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(num),
          ) as num;
          result.ssnWORKER = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RegistrationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RegistrationRequestBuilder();
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

class RegistrationRequestGenderEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MALE')
  static const RegistrationRequestGenderEnum MALE = _$registrationRequestGenderEnum_MALE;
  @BuiltValueEnumConst(wireName: r'FEMALE')
  static const RegistrationRequestGenderEnum FEMALE = _$registrationRequestGenderEnum_FEMALE;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const RegistrationRequestGenderEnum OTHER = _$registrationRequestGenderEnum_OTHER;

  static Serializer<RegistrationRequestGenderEnum> get serializer => _$registrationRequestGenderEnumSerializer;

  const RegistrationRequestGenderEnum._(String name): super(name);

  static BuiltSet<RegistrationRequestGenderEnum> get values => _$registrationRequestGenderEnumValues;
  static RegistrationRequestGenderEnum valueOf(String name) => _$registrationRequestGenderEnumValueOf(name);
}

