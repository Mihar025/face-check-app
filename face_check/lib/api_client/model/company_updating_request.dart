//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'company_updating_request.g.dart';

/// CompanyUpdatingRequest
///
/// Properties:
/// * [companyName] 
/// * [companyAddress] 
/// * [companyPhone] 
/// * [companyEmail] 
/// * [workersQuantity] 
@BuiltValue()
abstract class CompanyUpdatingRequest implements Built<CompanyUpdatingRequest, CompanyUpdatingRequestBuilder> {
  @BuiltValueField(wireName: r'companyName')
  String get companyName;

  @BuiltValueField(wireName: r'companyAddress')
  String get companyAddress;

  @BuiltValueField(wireName: r'companyPhone')
  String get companyPhone;

  @BuiltValueField(wireName: r'companyEmail')
  String get companyEmail;

  @BuiltValueField(wireName: r'workersQuantity')
  int get workersQuantity;

  CompanyUpdatingRequest._();

  factory CompanyUpdatingRequest([void updates(CompanyUpdatingRequestBuilder b)]) = _$CompanyUpdatingRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CompanyUpdatingRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CompanyUpdatingRequest> get serializer => _$CompanyUpdatingRequestSerializer();
}

class _$CompanyUpdatingRequestSerializer implements PrimitiveSerializer<CompanyUpdatingRequest> {
  @override
  final Iterable<Type> types = const [CompanyUpdatingRequest, _$CompanyUpdatingRequest];

  @override
  final String wireName = r'CompanyUpdatingRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CompanyUpdatingRequest object, {
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
    yield r'workersQuantity';
    yield serializers.serialize(
      object.workersQuantity,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CompanyUpdatingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CompanyUpdatingRequestBuilder result,
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
        case r'workersQuantity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.workersQuantity = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CompanyUpdatingRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CompanyUpdatingRequestBuilder();
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

