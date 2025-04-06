//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'user_full_contact_information.g.dart';

/// UserFullContactInformation
///
/// Properties:
/// * [fullName] 
/// * [phoneNumber] 
/// * [email] 
/// * [address]
/// * [photoFileName]
/// * [photoUrl]
@BuiltValue()
abstract class UserFullContactInformation implements Built<UserFullContactInformation, UserFullContactInformationBuilder> {
  @BuiltValueField(wireName: r'fullName')
  String? get fullName;

  @BuiltValueField(wireName: r'phoneNumber')
  String? get phoneNumber;

  @BuiltValueField(wireName: r'email')
  String? get email;

  @BuiltValueField(wireName: r'address')
  String? get address;

  @BuiltValueField(wireName: r'photoFileName')
  String? get photoFileName;

  @BuiltValueField(wireName: r'photoUrl')
  String? get photoUrl;

  UserFullContactInformation._();

  factory UserFullContactInformation([void updates(UserFullContactInformationBuilder b)]) = _$UserFullContactInformation;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UserFullContactInformationBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UserFullContactInformation> get serializer => _$UserFullContactInformationSerializer();
}

class _$UserFullContactInformationSerializer implements PrimitiveSerializer<UserFullContactInformation> {
  @override
  final Iterable<Type> types = const [UserFullContactInformation, _$UserFullContactInformation];

  @override
  final String wireName = r'UserFullContactInformation';

  Iterable<Object?> _serializeProperties(
      Serializers serializers,
      UserFullContactInformation object, {
        FullType specifiedType = FullType.unspecified,
      }) sync* {
    if (object.fullName != null) {
      yield r'fullName';
      yield serializers.serialize(
        object.fullName,
        specifiedType: const FullType(String),
      );
    }
    if (object.phoneNumber != null) {
      yield r'phoneNumber';
      yield serializers.serialize(
        object.phoneNumber,
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
    if (object.address != null) {
      yield r'address';
      yield serializers.serialize(
        object.address,
        specifiedType: const FullType(String),
      );
    }
    if (object.photoFileName != null) {
      yield r'photoFileName';
      yield serializers.serialize(
        object.photoFileName,
        specifiedType: const FullType(String),
      );
    }
    if (object.photoUrl != null) {
      yield r'photoUrl';
      yield serializers.serialize(
        object.photoUrl,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
      Serializers serializers,
      UserFullContactInformation object, {
        FullType specifiedType = FullType.unspecified,
      }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
      Serializers serializers,
      Object serialized, {
        FullType specifiedType = FullType.unspecified,
        required List<Object?> serializedList,
        required UserFullContactInformationBuilder result,
        required List<Object?> unhandled,
      }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
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
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        case r'photoFileName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.photoFileName = valueDes;
          break;
        case r'photoUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.photoUrl = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UserFullContactInformation deserialize(
      Serializers serializers,
      Object serialized, {
        FullType specifiedType = FullType.unspecified,
      }) {
    final result = UserFullContactInformationBuilder();
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