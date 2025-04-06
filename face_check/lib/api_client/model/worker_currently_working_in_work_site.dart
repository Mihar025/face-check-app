//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'worker_currently_working_in_work_site.g.dart';

/// WorkerCurrentlyWorkingInWorkSite
///
/// Properties:
/// * [workerId] 
/// * [workSiteId] 
/// * [punchedIn] 
/// * [workerFullName] 
/// * [workerPhoneNumber] 
/// * [workSiteName] 
/// * [workSiteAddress] 
@BuiltValue()
abstract class WorkerCurrentlyWorkingInWorkSite implements Built<WorkerCurrentlyWorkingInWorkSite, WorkerCurrentlyWorkingInWorkSiteBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'punchedIn')
  DateTime? get punchedIn;

  @BuiltValueField(wireName: r'workerFullName')
  String? get workerFullName;

  @BuiltValueField(wireName: r'workerPhoneNumber')
  String? get workerPhoneNumber;

  @BuiltValueField(wireName: r'workSiteName')
  String? get workSiteName;

  @BuiltValueField(wireName: r'workSiteAddress')
  String? get workSiteAddress;

  WorkerCurrentlyWorkingInWorkSite._();

  factory WorkerCurrentlyWorkingInWorkSite([void updates(WorkerCurrentlyWorkingInWorkSiteBuilder b)]) = _$WorkerCurrentlyWorkingInWorkSite;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(WorkerCurrentlyWorkingInWorkSiteBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<WorkerCurrentlyWorkingInWorkSite> get serializer => _$WorkerCurrentlyWorkingInWorkSiteSerializer();
}

class _$WorkerCurrentlyWorkingInWorkSiteSerializer implements PrimitiveSerializer<WorkerCurrentlyWorkingInWorkSite> {
  @override
  final Iterable<Type> types = const [WorkerCurrentlyWorkingInWorkSite, _$WorkerCurrentlyWorkingInWorkSite];

  @override
  final String wireName = r'WorkerCurrentlyWorkingInWorkSite';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    WorkerCurrentlyWorkingInWorkSite object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workerId != null) {
      yield r'workerId';
      yield serializers.serialize(
        object.workerId,
        specifiedType: const FullType(int),
      );
    }
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.punchedIn != null) {
      yield r'punchedIn';
      yield serializers.serialize(
        object.punchedIn,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.workerFullName != null) {
      yield r'workerFullName';
      yield serializers.serialize(
        object.workerFullName,
        specifiedType: const FullType(String),
      );
    }
    if (object.workerPhoneNumber != null) {
      yield r'workerPhoneNumber';
      yield serializers.serialize(
        object.workerPhoneNumber,
        specifiedType: const FullType(String),
      );
    }
    if (object.workSiteName != null) {
      yield r'workSiteName';
      yield serializers.serialize(
        object.workSiteName,
        specifiedType: const FullType(String),
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
    WorkerCurrentlyWorkingInWorkSite object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required WorkerCurrentlyWorkingInWorkSiteBuilder result,
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
        case r'workSiteId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.workSiteId = valueDes;
          break;
        case r'punchedIn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.punchedIn = valueDes;
          break;
        case r'workerFullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workerFullName = valueDes;
          break;
        case r'workerPhoneNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workerPhoneNumber = valueDes;
          break;
        case r'workSiteName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workSiteName = valueDes;
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
  WorkerCurrentlyWorkingInWorkSite deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = WorkerCurrentlyWorkingInWorkSiteBuilder();
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

