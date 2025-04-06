//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'local_time.dart';

part 'work_site_update_working_hours_request.g.dart';

/// WorkSiteUpdateWorkingHoursRequest
///
/// Properties:
/// * [newStart] 
/// * [newEnd] 
@BuiltValue()
abstract class WorkSiteUpdateWorkingHoursRequest implements Built<WorkSiteUpdateWorkingHoursRequest, WorkSiteUpdateWorkingHoursRequestBuilder> {
  @BuiltValueField(wireName: r'newStart')
  LocalTime get newStart;

  @BuiltValueField(wireName: r'newEnd')
  LocalTime get newEnd;

  WorkSiteUpdateWorkingHoursRequest._();

  factory WorkSiteUpdateWorkingHoursRequest([void updates(WorkSiteUpdateWorkingHoursRequestBuilder b)]) = _$WorkSiteUpdateWorkingHoursRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteUpdateWorkingHoursRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteUpdateWorkingHoursRequest> get serializer => _$WorkSiteUpdateWorkingHoursRequestSerializer();
}

class _$WorkSiteUpdateWorkingHoursRequestSerializer implements PrimitiveSerializer<WorkSiteUpdateWorkingHoursRequest> {
  @override
  final Iterable<Type> types = const [WorkSiteUpdateWorkingHoursRequest, _$WorkSiteUpdateWorkingHoursRequest];

  @override
  final String wireName = r'WorkSiteUpdateWorkingHoursRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteUpdateWorkingHoursRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'newStart';
    yield serializers.serialize(
      object.newStart,
      specifiedType: const FullType(LocalTime),
    );
    yield r'newEnd';
    yield serializers.serialize(
      object.newEnd,
      specifiedType: const FullType(LocalTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteUpdateWorkingHoursRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteUpdateWorkingHoursRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'newStart':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.newStart.replace(valueDes);
          break;
        case r'newEnd':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LocalTime),
          ) as LocalTime;
          result.newEnd.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkSiteUpdateWorkingHoursRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteUpdateWorkingHoursRequestBuilder();
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

