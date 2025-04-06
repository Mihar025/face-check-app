//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'work_site_update_address_response.g.dart';

/// WorkSiteUpdateAddressResponse
///
/// Properties:
/// * [worksiteId] 
/// * [workSiteAddress] 
@BuiltValue()
abstract class WorkSiteUpdateAddressResponse implements Built<WorkSiteUpdateAddressResponse, WorkSiteUpdateAddressResponseBuilder> {
  @BuiltValueField(wireName: r'worksiteId')
  int? get worksiteId;

  @BuiltValueField(wireName: r'workSiteAddress')
  String? get workSiteAddress;

  WorkSiteUpdateAddressResponse._();

  factory WorkSiteUpdateAddressResponse([void updates(WorkSiteUpdateAddressResponseBuilder b)]) = _$WorkSiteUpdateAddressResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteUpdateAddressResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteUpdateAddressResponse> get serializer => _$WorkSiteUpdateAddressResponseSerializer();
}

class _$WorkSiteUpdateAddressResponseSerializer implements PrimitiveSerializer<WorkSiteUpdateAddressResponse> {
  @override
  final Iterable<Type> types = const [WorkSiteUpdateAddressResponse, _$WorkSiteUpdateAddressResponse];

  @override
  final String wireName = r'WorkSiteUpdateAddressResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteUpdateAddressResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.worksiteId != null) {
      yield r'worksiteId';
      yield serializers.serialize(
        object.worksiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.workSiteAddress != null) {
      yield r'workSiteAddress';
      yield serializers.serialize(
        object.workSiteAddress,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteUpdateAddressResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteUpdateAddressResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'worksiteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.worksiteId = valueDes;
          break;
        case r'workSiteAddress':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workSiteAddress = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkSiteUpdateAddressResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteUpdateAddressResponseBuilder();
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

