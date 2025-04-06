//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'worker_currently_working_in_work_site.dart';

part 'page_response_worker_currently_working_in_work_site.g.dart';

/// PageResponseWorkerCurrentlyWorkingInWorkSite
///
/// Properties:
/// * [content] 
/// * [number] 
/// * [size] 
/// * [totalElement] 
/// * [totalPages] 
/// * [first] 
/// * [last] 
@BuiltValue()
abstract class PageResponseWorkerCurrentlyWorkingInWorkSite implements Built<PageResponseWorkerCurrentlyWorkingInWorkSite, PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<WorkerCurrentlyWorkingInWorkSite>? get content;

  @BuiltValueField(wireName: r'number')
  int? get number;

  @BuiltValueField(wireName: r'size')
  int? get size;

  @BuiltValueField(wireName: r'totalElement')
  int? get totalElement;

  @BuiltValueField(wireName: r'totalPages')
  int? get totalPages;

  @BuiltValueField(wireName: r'first')
  bool? get first;

  @BuiltValueField(wireName: r'last')
  bool? get last;

  PageResponseWorkerCurrentlyWorkingInWorkSite._();

  factory PageResponseWorkerCurrentlyWorkingInWorkSite([void updates(PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder b)]) = _$PageResponseWorkerCurrentlyWorkingInWorkSite;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseWorkerCurrentlyWorkingInWorkSite> get serializer => _$PageResponseWorkerCurrentlyWorkingInWorkSiteSerializer();
}

class _$PageResponseWorkerCurrentlyWorkingInWorkSiteSerializer implements PrimitiveSerializer<PageResponseWorkerCurrentlyWorkingInWorkSite> {
  @override
  final Iterable<Type> types = const [PageResponseWorkerCurrentlyWorkingInWorkSite, _$PageResponseWorkerCurrentlyWorkingInWorkSite];

  @override
  final String wireName = r'PageResponseWorkerCurrentlyWorkingInWorkSite';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponseWorkerCurrentlyWorkingInWorkSite object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.content != null) {
      yield r'content';
      yield serializers.serialize(
        object.content,
        specifiedType: const FullType(BuiltList, [FullType(WorkerCurrentlyWorkingInWorkSite)]),
      );
    }
    if (object.number != null) {
      yield r'number';
      yield serializers.serialize(
        object.number,
        specifiedType: const FullType(int),
      );
    }
    if (object.size != null) {
      yield r'size';
      yield serializers.serialize(
        object.size,
        specifiedType: const FullType(int),
      );
    }
    if (object.totalElement != null) {
      yield r'totalElement';
      yield serializers.serialize(
        object.totalElement,
        specifiedType: const FullType(int),
      );
    }
    if (object.totalPages != null) {
      yield r'totalPages';
      yield serializers.serialize(
        object.totalPages,
        specifiedType: const FullType(int),
      );
    }
    if (object.first != null) {
      yield r'first';
      yield serializers.serialize(
        object.first,
        specifiedType: const FullType(bool),
      );
    }
    if (object.last != null) {
      yield r'last';
      yield serializers.serialize(
        object.last,
        specifiedType: const FullType(bool),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PageResponseWorkerCurrentlyWorkingInWorkSite object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(WorkerCurrentlyWorkingInWorkSite)]),
          ) as BuiltList<WorkerCurrentlyWorkingInWorkSite>;
          result.content.replace(valueDes);
          break;
        case r'number':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.number = valueDes;
          break;
        case r'size':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.size = valueDes;
          break;
        case r'totalElement':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.totalElement = valueDes;
          break;
        case r'totalPages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.totalPages = valueDes;
          break;
        case r'first':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.first = valueDes;
          break;
        case r'last':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.last = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PageResponseWorkerCurrentlyWorkingInWorkSite deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder();
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

