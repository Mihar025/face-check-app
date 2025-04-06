//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'punch_in_response.g.dart';

/// PunchInResponse
///
/// Properties:
/// * [workerId] 
/// * [workSiteId] 
/// * [workSiteName] 
/// * [workerFullName] 
/// * [checkInTime] 
/// * [formattedCheckInTime] 
/// * [checkInPhotoUrl] 
/// * [checkInLatitude] 
/// * [checkInLongitude] 
/// * [workSiteAddress] 
/// * [isSuccessful] 
/// * [message] 
@BuiltValue()
abstract class PunchInResponse implements Built<PunchInResponse, PunchInResponseBuilder> {
  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'workSiteName')
  String? get workSiteName;

  @BuiltValueField(wireName: r'workerFullName')
  String? get workerFullName;

  @BuiltValueField(wireName: r'checkInTime')
  DateTime? get checkInTime;

  @BuiltValueField(wireName: r'formattedCheckInTime')
  String? get formattedCheckInTime;

  @BuiltValueField(wireName: r'checkInPhotoUrl')
  String? get checkInPhotoUrl;

  @BuiltValueField(wireName: r'checkInLatitude')
  double? get checkInLatitude;

  @BuiltValueField(wireName: r'checkInLongitude')
  double? get checkInLongitude;

  @BuiltValueField(wireName: r'workSiteAddress')
  String? get workSiteAddress;

  @BuiltValueField(wireName: r'isSuccessful')
  bool? get isSuccessful;

  @BuiltValueField(wireName: r'message')
  String? get message;

  PunchInResponse._();

  factory PunchInResponse([void updates(PunchInResponseBuilder b)]) = _$PunchInResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PunchInResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PunchInResponse> get serializer => _$PunchInResponseSerializer();
}

class _$PunchInResponseSerializer implements PrimitiveSerializer<PunchInResponse> {
  @override
  final Iterable<Type> types = const [PunchInResponse, _$PunchInResponse];

  @override
  final String wireName = r'PunchInResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PunchInResponse object, {
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
    if (object.workSiteName != null) {
      yield r'workSiteName';
      yield serializers.serialize(
        object.workSiteName,
        specifiedType: const FullType(String),
      );
    }
    if (object.workerFullName != null) {
      yield r'workerFullName';
      yield serializers.serialize(
        object.workerFullName,
        specifiedType: const FullType(String),
      );
    }
    if (object.checkInTime != null) {
      yield r'checkInTime';
      yield serializers.serialize(
        object.checkInTime,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.formattedCheckInTime != null) {
      yield r'formattedCheckInTime';
      yield serializers.serialize(
        object.formattedCheckInTime,
        specifiedType: const FullType(String),
      );
    }
    if (object.checkInPhotoUrl != null) {
      yield r'checkInPhotoUrl';
      yield serializers.serialize(
        object.checkInPhotoUrl,
        specifiedType: const FullType(String),
      );
    }
    if (object.checkInLatitude != null) {
      yield r'checkInLatitude';
      yield serializers.serialize(
        object.checkInLatitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.checkInLongitude != null) {
      yield r'checkInLongitude';
      yield serializers.serialize(
        object.checkInLongitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.workSiteAddress != null) {
      yield r'workSiteAddress';
      yield serializers.serialize(
        object.workSiteAddress,
        specifiedType: const FullType(String),
      );
    }
    if (object.isSuccessful != null) {
      yield r'isSuccessful';
      yield serializers.serialize(
        object.isSuccessful,
        specifiedType: const FullType(bool),
      );
    }
    if (object.message != null) {
      yield r'message';
      yield serializers.serialize(
        object.message,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PunchInResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PunchInResponseBuilder result,
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
        case r'workSiteName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workSiteName = valueDes;
          break;
        case r'workerFullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workerFullName = valueDes;
          break;
        case r'checkInTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.checkInTime = valueDes;
          break;
        case r'formattedCheckInTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.formattedCheckInTime = valueDes;
          break;
        case r'checkInPhotoUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.checkInPhotoUrl = valueDes;
          break;
        case r'checkInLatitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.checkInLatitude = valueDes;
          break;
        case r'checkInLongitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.checkInLongitude = valueDes;
          break;
        case r'workSiteAddress':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.workSiteAddress = valueDes;
          break;
        case r'isSuccessful':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.isSuccessful = valueDes;
          break;
        case r'message':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.message = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PunchInResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PunchInResponseBuilder();
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

