//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_work_site_address.g.dart';

/// UpdateWorkSiteAddress
///
/// Properties:
/// * [address] 
@BuiltValue()
abstract class UpdateWorkSiteAddress implements Built<UpdateWorkSiteAddress, UpdateWorkSiteAddressBuilder> {
  @BuiltValueField(wireName: r'address')
  String get address;

  UpdateWorkSiteAddress._();

  factory UpdateWorkSiteAddress([void updates(UpdateWorkSiteAddressBuilder b)]) = _$UpdateWorkSiteAddress;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateWorkSiteAddressBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateWorkSiteAddress> get serializer => _$UpdateWorkSiteAddressSerializer();
}

class _$UpdateWorkSiteAddressSerializer implements PrimitiveSerializer<UpdateWorkSiteAddress> {
  @override
  final Iterable<Type> types = const [UpdateWorkSiteAddress, _$UpdateWorkSiteAddress];

  @override
  final String wireName = r'UpdateWorkSiteAddress';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateWorkSiteAddress object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'address';
    yield serializers.serialize(
      object.address,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateWorkSiteAddress object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateWorkSiteAddressBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'address':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.address = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UpdateWorkSiteAddress deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateWorkSiteAddressBuilder();
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

