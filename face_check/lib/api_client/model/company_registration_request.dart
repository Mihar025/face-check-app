//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'company_registration_request.g.dart';

/// CompanyRegistrationRequest
///
/// Properties:
/// * [companyName] 
/// * [companyAddress] 
/// * [companyPhone] 
/// * [companyEmail] 
@BuiltValue()
abstract class CompanyRegistrationRequest implements Built<CompanyRegistrationRequest, CompanyRegistrationRequestBuilder> {
  @BuiltValueField(wireName: r'companyName')
  String get companyName;

  @BuiltValueField(wireName: r'companyAddress')
  String get companyAddress;

  @BuiltValueField(wireName: r'companyPhone')
  String get companyPhone;

  @BuiltValueField(wireName: r'companyEmail')
  String get companyEmail;

  CompanyRegistrationRequest._();

  factory CompanyRegistrationRequest([void updates(CompanyRegistrationRequestBuilder b)]) = _$CompanyRegistrationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CompanyRegistrationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CompanyRegistrationRequest> get serializer => _$CompanyRegistrationRequestSerializer();
}

class _$CompanyRegistrationRequestSerializer implements PrimitiveSerializer<CompanyRegistrationRequest> {
  @override
  final Iterable<Type> types = const [CompanyRegistrationRequest, _$CompanyRegistrationRequest];

  @override
  final String wireName = r'CompanyRegistrationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CompanyRegistrationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    yield r'companyPhone';
    yield serializers.serialize(
      object.companyPhone,
      specifiedType: const FullType(String),
    );
    yield r'companyEmail';
    yield serializers.serialize(
      object.companyEmail,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CompanyRegistrationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CompanyRegistrationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
        case r'companyPhone':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.companyPhone = valueDes;
          break;
        case r'companyEmail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.companyEmail = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CompanyRegistrationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CompanyRegistrationRequestBuilder();
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

