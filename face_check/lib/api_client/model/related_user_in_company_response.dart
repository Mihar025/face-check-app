//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'related_user_in_company_response.g.dart';

/// RelatedUserInCompanyResponse
///
/// Properties:
/// * [workerId] 
/// * [companyId] 
/// * [firstName] 
/// * [lastName] 
/// * [email] 
/// * [baseHourlyRate] 
@BuiltValue()
abstract class RelatedUserInCompanyResponse implements Built<RelatedUserInCompanyResponse, RelatedUserInCompanyResponseBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'companyId')
  int? get companyId;

  @BuiltValueField(wireName: r'firstName')
  String? get firstName;

  @BuiltValueField(wireName: r'lastName')
  String? get lastName;

  @BuiltValueField(wireName: r'email')
  String? get email;

  @BuiltValueField(wireName: r'baseHourlyRate')
  num? get baseHourlyRate;

  RelatedUserInCompanyResponse._();

  factory RelatedUserInCompanyResponse([void updates(RelatedUserInCompanyResponseBuilder b)]) = _$RelatedUserInCompanyResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RelatedUserInCompanyResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RelatedUserInCompanyResponse> get serializer => _$RelatedUserInCompanyResponseSerializer();
}

class _$RelatedUserInCompanyResponseSerializer implements PrimitiveSerializer<RelatedUserInCompanyResponse> {
  @override
  final Iterable<Type> types = const [RelatedUserInCompanyResponse, _$RelatedUserInCompanyResponse];

  @override
  final String wireName = r'RelatedUserInCompanyResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RelatedUserInCompanyResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workerId != null) {
      yield r'workerId';
      yield serializers.serialize(
        object.workerId,
        specifiedType: const FullType(int),
      );
    }
    if (object.companyId != null) {
      yield r'companyId';
      yield serializers.serialize(
        object.companyId,
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
    RelatedUserInCompanyResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RelatedUserInCompanyResponseBuilder result,
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
        case r'companyId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.companyId = valueDes;
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
  RelatedUserInCompanyResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RelatedUserInCompanyResponseBuilder();
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

