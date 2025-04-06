//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'date.dart';

part 'work_site_closed_days_response.g.dart';

/// WorkSiteClosedDaysResponse
///
/// Properties:
/// * [workSiteId] 
/// * [siteName] 
/// * [siteAddress] 
/// * [closedDays] 
@BuiltValue()
abstract class WorkSiteClosedDaysResponse implements Built<WorkSiteClosedDaysResponse, WorkSiteClosedDaysResponseBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'siteName')
  String? get siteName;

  @BuiltValueField(wireName: r'siteAddress')
  String? get siteAddress;

  @BuiltValueField(wireName: r'closedDays')
  BuiltList<Date>? get closedDays;

  WorkSiteClosedDaysResponse._();

  factory WorkSiteClosedDaysResponse([void updates(WorkSiteClosedDaysResponseBuilder b)]) = _$WorkSiteClosedDaysResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkSiteClosedDaysResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkSiteClosedDaysResponse> get serializer => _$WorkSiteClosedDaysResponseSerializer();
}

class _$WorkSiteClosedDaysResponseSerializer implements PrimitiveSerializer<WorkSiteClosedDaysResponse> {
  @override
  final Iterable<Type> types = const [WorkSiteClosedDaysResponse, _$WorkSiteClosedDaysResponse];

  @override
  final String wireName = r'WorkSiteClosedDaysResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkSiteClosedDaysResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.siteName != null) {
      yield r'siteName';
      yield serializers.serialize(
        object.siteName,
        specifiedType: const FullType(String),
      );
    }
    if (object.siteAddress != null) {
      yield r'siteAddress';
      yield serializers.serialize(
        object.siteAddress,
        specifiedType: const FullType(String),
      );
    }
    if (object.closedDays != null) {
      yield r'closedDays';
      yield serializers.serialize(
        object.closedDays,
        specifiedType: const FullType(BuiltList, [FullType(Date)]),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    WorkSiteClosedDaysResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkSiteClosedDaysResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'workSiteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.workSiteId = valueDes;
          break;
        case r'siteName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.siteName = valueDes;
          break;
        case r'siteAddress':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.siteAddress = valueDes;
          break;
        case r'closedDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(Date)]),
          ) as BuiltList<Date>;
          result.closedDays.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  WorkSiteClosedDaysResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkSiteClosedDaysResponseBuilder();
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

