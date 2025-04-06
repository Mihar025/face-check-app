//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'punch_in_request.g.dart';

/// PunchInRequest
///
/// Properties:
/// * [workSiteId] 
/// * [photoBase64] 
/// * [latitude] 
/// * [longitude] 
@BuiltValue()
abstract class PunchInRequest implements Built<PunchInRequest, PunchInRequestBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'photoBase64')
  String? get photoBase64;

  @BuiltValueField(wireName: r'latitude')
  double? get latitude;

  @BuiltValueField(wireName: r'longitude')
  double? get longitude;

  PunchInRequest._();

  factory PunchInRequest([void updates(PunchInRequestBuilder b)]) = _$PunchInRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PunchInRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PunchInRequest> get serializer => _$PunchInRequestSerializer();
}

class _$PunchInRequestSerializer implements PrimitiveSerializer<PunchInRequest> {
  @override
  final Iterable<Type> types = const [PunchInRequest, _$PunchInRequest];

  @override
  final String wireName = r'PunchInRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PunchInRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.photoBase64 != null) {
      yield r'photoBase64';
      yield serializers.serialize(
        object.photoBase64,
        specifiedType: const FullType(String),
      );
    }
    if (object.latitude != null) {
      yield r'latitude';
      yield serializers.serialize(
        object.latitude,
        specifiedType: const FullType(double),
      );
    }
    if (object.longitude != null) {
      yield r'longitude';
      yield serializers.serialize(
        object.longitude,
        specifiedType: const FullType(double),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PunchInRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PunchInRequestBuilder result,
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
        case r'photoBase64':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.photoBase64 = valueDes;
          break;
        case r'latitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.latitude = valueDes;
          break;
        case r'longitude':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.longitude = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PunchInRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PunchInRequestBuilder();
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

