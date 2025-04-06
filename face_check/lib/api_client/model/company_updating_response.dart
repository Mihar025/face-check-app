//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'company_updating_response.g.dart';

/// CompanyUpdatingResponse
///
/// Properties:
/// * [companyId] 
/// * [companyName] 
/// * [companyAddress] 
/// * [companyPhone] 
/// * [companyEmail] 
/// * [workersQuantity] 
@BuiltValue()
abstract class CompanyUpdatingResponse implements Built<CompanyUpdatingResponse, CompanyUpdatingResponseBuilder> {
  @BuiltValueField(wireName: r'companyId')
  int? get companyId;

  @BuiltValueField(wireName: r'companyName')
  String? get companyName;

  @BuiltValueField(wireName: r'companyAddress')
  String? get companyAddress;

  @BuiltValueField(wireName: r'companyPhone')
  String? get companyPhone;

  @BuiltValueField(wireName: r'companyEmail')
  String? get companyEmail;

  @BuiltValueField(wireName: r'workersQuantity')
  int? get workersQuantity;

  CompanyUpdatingResponse._();

  factory CompanyUpdatingResponse([void updates(CompanyUpdatingResponseBuilder b)]) = _$CompanyUpdatingResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CompanyUpdatingResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CompanyUpdatingResponse> get serializer => _$CompanyUpdatingResponseSerializer();
}

class _$CompanyUpdatingResponseSerializer implements PrimitiveSerializer<CompanyUpdatingResponse> {
  @override
  final Iterable<Type> types = const [CompanyUpdatingResponse, _$CompanyUpdatingResponse];

  @override
  final String wireName = r'CompanyUpdatingResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CompanyUpdatingResponse object, {
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
    if (object.companyAddress != null) {
      yield r'companyAddress';
      yield serializers.serialize(
        object.companyAddress,
        specifiedType: const FullType(String),
      );
    }
    if (object.companyPhone != null) {
      yield r'companyPhone';
      yield serializers.serialize(
        object.companyPhone,
        specifiedType: const FullType(String),
      );
    }
    if (object.companyEmail != null) {
      yield r'companyEmail';
      yield serializers.serialize(
        object.companyEmail,
        specifiedType: const FullType(String),
      );
    }
    if (object.workersQuantity != null) {
      yield r'workersQuantity';
      yield serializers.serialize(
        object.workersQuantity,
        specifiedType: const FullType(int),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    CompanyUpdatingResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CompanyUpdatingResponseBuilder result,
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
  CompanyUpdatingResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CompanyUpdatingResponseBuilder();
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

