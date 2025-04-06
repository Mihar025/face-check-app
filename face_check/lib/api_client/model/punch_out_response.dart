//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'punch_out_response.g.dart';

/// PunchOutResponse
///
/// Properties:
/// * [workerId] 
/// * [workSiteId] 
/// * [workSiteName] 
/// * [workerFullName] 
/// * [checkInTime] 
/// * [formattedCheckInTime] 
/// * [checkOutTime] 
/// * [formattedCheckOutTime] 
/// * [checkOutPhotoUrl] 
/// * [checkOutLatitude] 
/// * [checkOutLongitude] 
/// * [hoursWorked] 
/// * [overtimeHours] 
/// * [workSiteAddress] 
/// * [isSuccessful] 
/// * [message] 
@BuiltValue()
abstract class PunchOutResponse implements Built<PunchOutResponse, PunchOutResponseBuilder> {
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

  @BuiltValueField(wireName: r'checkOutTime')
  DateTime? get checkOutTime;

  @BuiltValueField(wireName: r'formattedCheckOutTime')
  String? get formattedCheckOutTime;

  @BuiltValueField(wireName: r'checkOutPhotoUrl')
  String? get checkOutPhotoUrl;

  @BuiltValueField(wireName: r'checkOutLatitude')
  double? get checkOutLatitude;

  @BuiltValueField(wireName: r'checkOutLongitude')
  double? get checkOutLongitude;

  @BuiltValueField(wireName: r'hoursWorked')
  double? get hoursWorked;

  @BuiltValueField(wireName: r'overtimeHours')
  double? get overtimeHours;

  @BuiltValueField(wireName: r'workSiteAddress')
  String? get workSiteAddress;

  @BuiltValueField(wireName: r'isSuccessful')
  bool? get isSuccessful;

  @BuiltValueField(wireName: r'message')
  String? get message;

  PunchOutResponse._();

  factory PunchOutResponse([void updates(PunchOutResponseBuilder b)]) = _$PunchOutResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PunchOutResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PunchOutResponse> get serializer => _$PunchOutResponseSerializer();
}

class _$PunchOutResponseSerializer implements PrimitiveSerializer<PunchOutResponse> {
  @override
  final Iterable<Type> types = const [PunchOutResponse, _$PunchOutResponse];

  @override
  final String wireName = r'PunchOutResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PunchOutResponse object, {
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
    if (object.checkOutTime != null) {
      yield r'checkOutTime';
      yield serializers.serialize(
        object.checkOutTime,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.formattedCheckOutTime != null) {
      yield r'formattedCheckOutTime';
      yield serializers.serialize(
        object.formattedCheckOutTime,
        specifiedType: const FullType(String),
      );
    }
    if (object.checkOutPhotoUrl != null) {
      yield r'checkOutPhotoUrl';
      yield serializers.serialize(
        object.checkOutPhotoUrl,
        specifiedType: const FullType(String),
      );
    }
    if (object.checkOutLatitude != null) {
      yield r'checkOutLatitude';
      yield serializers.serialize(
        object.checkOutLatitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.checkOutLongitude != null) {
      yield r'checkOutLongitude';
      yield serializers.serialize(
        object.checkOutLongitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.hoursWorked != null) {
      yield r'hoursWorked';
      yield serializers.serialize(
        object.hoursWorked,
        specifiedType: const FullType(double),
      );
    }
    if (object.overtimeHours != null) {
      yield r'overtimeHours';
      yield serializers.serialize(
        object.overtimeHours,
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
    PunchOutResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PunchOutResponseBuilder result,
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
        case r'checkOutTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.checkOutTime = valueDes;
          break;
        case r'formattedCheckOutTime':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.formattedCheckOutTime = valueDes;
          break;
        case r'checkOutPhotoUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.checkOutPhotoUrl = valueDes;
          break;
        case r'checkOutLatitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.checkOutLatitude = valueDes;
          break;
        case r'checkOutLongitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.checkOutLongitude = valueDes;
          break;
        case r'hoursWorked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.hoursWorked = valueDes;
          break;
        case r'overtimeHours':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.overtimeHours = valueDes;
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
  PunchOutResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PunchOutResponseBuilder();
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

