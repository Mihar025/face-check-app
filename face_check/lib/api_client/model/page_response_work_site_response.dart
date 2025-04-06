//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'work_site_response.dart';

part 'page_response_work_site_response.g.dart';

/// PageResponseWorkSiteResponse
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
abstract class PageResponseWorkSiteResponse implements Built<PageResponseWorkSiteResponse, PageResponseWorkSiteResponseBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<WorkSiteResponse>? get content;

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

  PageResponseWorkSiteResponse._();

  factory PageResponseWorkSiteResponse([void updates(PageResponseWorkSiteResponseBuilder b)]) = _$PageResponseWorkSiteResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseWorkSiteResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseWorkSiteResponse> get serializer => _$PageResponseWorkSiteResponseSerializer();
}

class _$PageResponseWorkSiteResponseSerializer implements StructuredSerializer<PageResponseWorkSiteResponse> {
  @override
  final Iterable<Type> types = const [PageResponseWorkSiteResponse, _$PageResponseWorkSiteResponse];

  @override
  final String wireName = r'PageResponseWorkSiteResponse';

  @override
  Iterable<Object?> serialize(Serializers serializers, PageResponseWorkSiteResponse object,
      {FullType specifiedType = FullType.unspecified}) {
    final result = <Object?>[];
    if (object.content != null) {
      result
        ..add(r'content')
        ..add(serializers.serialize(object.content,
            specifiedType: const FullType(BuiltList, [FullType(WorkSiteResponse)])));
    }
    if (object.number != null) {
      result
        ..add(r'number')
        ..add(serializers.serialize(object.number, specifiedType: const FullType(int)));
    }
    if (object.size != null) {
      result
        ..add(r'size')
        ..add(serializers.serialize(object.size, specifiedType: const FullType(int)));
    }
    if (object.totalElement != null) {
      result
        ..add(r'totalElement')
        ..add(serializers.serialize(object.totalElement, specifiedType: const FullType(int)));
    }
    if (object.totalPages != null) {
      result
        ..add(r'totalPages')
        ..add(serializers.serialize(object.totalPages, specifiedType: const FullType(int)));
    }
    if (object.first != null) {
      result
        ..add(r'first')
        ..add(serializers.serialize(object.first, specifiedType: const FullType(bool)));
    }
    if (object.last != null) {
      result
        ..add(r'last')
        ..add(serializers.serialize(object.last, specifiedType: const FullType(bool)));
    }
    return result;
  }

  @override
  PageResponseWorkSiteResponse deserialize(Serializers serializers, Iterable<Object?> serialized,
      {FullType specifiedType = FullType.unspecified}) {
    final result = PageResponseWorkSiteResponseBuilder();

    final iterator = serialized.iterator;
    while (iterator.moveNext()) {
      final key = iterator.current as String;
      iterator.moveNext();
      final Object? value = iterator.current;
      switch (key) {
        case r'content':
          if (value != null) {
            result.content.replace(serializers.deserialize(value,
                specifiedType: const FullType(BuiltList, [FullType(WorkSiteResponse)])) as BuiltList<WorkSiteResponse>);
          }
          break;
        case r'number':
          if (value != null) {
            result.number = serializers.deserialize(value, specifiedType: const FullType(int)) as int;
          }
          break;
        case r'size':
          if (value != null) {
            result.size = serializers.deserialize(value, specifiedType: const FullType(int)) as int;
          }
          break;
        case r'totalElement':
          if (value != null) {
            result.totalElement = serializers.deserialize(value, specifiedType: const FullType(int)) as int;
          }
          break;
        case r'totalPages':
          if (value != null) {
            result.totalPages = serializers.deserialize(value, specifiedType: const FullType(int)) as int;
          }
          break;
        case r'first':
          if (value != null) {
            result.first = serializers.deserialize(value, specifiedType: const FullType(bool)) as bool;
          }
          break;
        case r'last':
          if (value != null) {
            result.last = serializers.deserialize(value, specifiedType: const FullType(bool)) as bool;
          }
          break;
      }
    }

    return result.build();
  }
}