//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'work_site_update_name_response.g.dart';

/// WorkSiteUpdateNameResponse
///
/// Properties:
/// * [worksiteId] 
/// * [workSiteName] 
@BuiltValue()
abstract class WorkSiteUpdateNameResponse implements Built<WorkSiteUpdateNameResponse, WorkSiteUpdateNameResponseBuilder> {
  @BuiltValueField(wireName: r'worksiteId')
  int? get worksiteId;

  @BuiltValueField(wireName: r'workSiteName')
  String? get workSiteName;

  WorkSiteUpdateNameResponse._();

  factory WorkSiteUpdateNameResponse([void updates(WorkSiteUpdateNameResponseBuilder b)]) = _$WorkSiteUpdateNameResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteUpdateNameResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteUpdateNameResponse> get serializer => _$WorkSiteUpdateNameResponseSerializer();
}

class _$WorkSiteUpdateNameResponseSerializer implements PrimitiveSerializer<WorkSiteUpdateNameResponse> {
  @override
  final Iterable<Type> types = const [WorkSiteUpdateNameResponse, _$WorkSiteUpdateNameResponse];

  @override
  final String wireName = r'WorkSiteUpdateNameResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteUpdateNameResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.worksiteId != null) {
      yield r'worksiteId';
      yield serializers.serialize(
        object.worksiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.workSiteName != null) {
      yield r'workSiteName';
      yield serializers.serialize(
        object.workSiteName,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteUpdateNameResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteUpdateNameResponseBuilder result,
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
        case r'workSiteName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workSiteName = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkSiteUpdateNameResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteUpdateNameResponseBuilder();
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

