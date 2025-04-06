//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'set_new_custom_radius_for_worker_in_special_work_site_response.g.dart';

/// SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse
///
/// Properties:
/// * [workSiteId] 
/// * [workerId] 
/// * [firstName] 
/// * [lastName] 
/// * [companyName] 
/// * [newRadius] 
@BuiltValue()
abstract class SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse implements Built<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse, SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder> {
  @BuiltValueField(wireName: r'workSiteId')
  int? get workSiteId;

  @BuiltValueField(wireName: r'workerId')
  int? get workerId;

  @BuiltValueField(wireName: r'firstName')
  String? get firstName;

  @BuiltValueField(wireName: r'lastName')
  String? get lastName;

  @BuiltValueField(wireName: r'companyName')
  String? get companyName;

  @BuiltValueField(wireName: r'newRadius')
  double? get newRadius;

  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse._();

  factory SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse([void updates(SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder b)]) = _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse> get serializer => _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseSerializer();
}

class _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseSerializer implements PrimitiveSerializer<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse> {
  @override
  final Iterable<Type> types = const [SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse, _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse];

  @override
  final String wireName = r'SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.workSiteId != null) {
      yield r'workSiteId';
      yield serializers.serialize(
        object.workSiteId,
        specifiedType: const FullType(int),
      );
    }
    if (object.workerId != null) {
      yield r'workerId';
      yield serializers.serialize(
        object.workerId,
        specifiedType: const FullType(int),
      );
    }
    if (object.firstName != null) {
      yield r'firstName';
      yield serializers.serialize(
        object.firstName,
        specifiedType: const FullType(String),
      );
    }
    if (object.lastName != null) {
      yield r'lastName';
      yield serializers.serialize(
        object.lastName,
        specifiedType: const FullType(String),
      );
    }
    if (object.companyName != null) {
      yield r'companyName';
      yield serializers.serialize(
        object.companyName,
        specifiedType: const FullType(String),
      );
    }
    if (object.newRadius != null) {
      yield r'newRadius';
      yield serializers.serialize(
        object.newRadius,
        specifiedType: const FullType(double),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder result,
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
        case r'workerId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.workerId = valueDes;
          break;
        case r'firstName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.firstName = valueDes;
          break;
        case r'lastName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.lastName = valueDes;
          break;
        case r'companyName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.companyName = valueDes;
          break;
        case r'newRadius':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(double),
          ) as double;
          result.newRadius = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder();
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

