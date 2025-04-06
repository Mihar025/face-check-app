//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'punch_out_request.g.dart';

/// PunchOutRequest
///
/// Properties:
/// * [workSiteId] 
/// * [photoBase64] 
/// * [latitude] 
/// * [longitude] 
@BuiltValue()
abstract class PunchOutRequest implements Built<PunchOutRequest, PunchOutRequestBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'photoBase64')
  String? get photoBase64;

  @BuiltValueField(wireName: r'latitude')
  double? get latitude;

  @BuiltValueField(wireName: r'longitude')
  double? get longitude;

  PunchOutRequest._();

  factory PunchOutRequest([void updates(PunchOutRequestBuilder b)]) = _$PunchOutRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PunchOutRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PunchOutRequest> get serializer => _$PunchOutRequestSerializer();
}

class _$PunchOutRequestSerializer implements PrimitiveSerializer<PunchOutRequest> {
  @override
  final Iterable<Type> types = const [PunchOutRequest, _$PunchOutRequest];

  @override
  final String wireName = r'PunchOutRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PunchOutRequest object, {
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
    PunchOutRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PunchOutRequestBuilder result,
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
  PunchOutRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PunchOutRequestBuilder();
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

