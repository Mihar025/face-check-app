//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_status_work_site_request.g.dart';

/// UpdateStatusWorkSiteRequest
///
/// Properties:
/// * [active] 
@BuiltValue()
abstract class UpdateStatusWorkSiteRequest implements Built<UpdateStatusWorkSiteRequest, UpdateStatusWorkSiteRequestBuilder> {
  @BuiltValueField(wireName: r'active')
  bool? get active;

  UpdateStatusWorkSiteRequest._();

  factory UpdateStatusWorkSiteRequest([void updates(UpdateStatusWorkSiteRequestBuilder b)]) = _$UpdateStatusWorkSiteRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateStatusWorkSiteRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateStatusWorkSiteRequest> get serializer => _$UpdateStatusWorkSiteRequestSerializer();
}

class _$UpdateStatusWorkSiteRequestSerializer implements PrimitiveSerializer<UpdateStatusWorkSiteRequest> {
  @override
  final Iterable<Type> types = const [UpdateStatusWorkSiteRequest, _$UpdateStatusWorkSiteRequest];

  @override
  final String wireName = r'UpdateStatusWorkSiteRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateStatusWorkSiteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.active != null) {
      yield r'active';
      yield serializers.serialize(
        object.active,
        specifiedType: const FullType(bool),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateStatusWorkSiteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateStatusWorkSiteRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'active':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.active = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UpdateStatusWorkSiteRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateStatusWorkSiteRequestBuilder();
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

