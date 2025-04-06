// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'can_punch_out_request_work_site.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializer<CanPunchOutRequestWorkSite> _$canPunchOutRequestWorkSiteSerializer =
    new _$CanPunchOutRequestWorkSiteSerializer();

class _$CanPunchOutRequestWorkSiteSerializer
    implements StructuredSerializer<CanPunchOutRequestWorkSite> {
  @override
  final Iterable<Type> types = const [
    CanPunchOutRequestWorkSite,
    _$CanPunchOutRequestWorkSite
  ];
  @override
  final String wireName = 'CanPunchOutRequestWorkSite';

  @override
  Iterable<Object?> serialize(
      Serializers serializers, CanPunchOutRequestWorkSite object,
      {FullType specifiedType = FullType.unspecified}) {
    final result = <Object?>[
      'canPunchOut',
      serializers.serialize(object.canPunchOut,
          specifiedType: const FullType(LocalTime)),
    ];

    return result;
  }

  @override
  CanPunchOutRequestWorkSite deserialize(
      Serializers serializers, Iterable<Object?> serialized,
      {FullType specifiedType = FullType.unspecified}) {
    final result = new CanPunchOutRequestWorkSiteBuilder();

    final iterator = serialized.iterator;
    while (iterator.moveNext()) {
      final key = iterator.current! as String;
      iterator.moveNext();
      final Object? value = iterator.current;
      switch (key) {
        case 'canPunchOut':
          result.canPunchOut.replace(serializers.deserialize(value,
              specifiedType: const FullType(LocalTime))! as LocalTime);
          break;
      }
    }

    return result.build();
  }
}

class _$CanPunchOutRequestWorkSite extends CanPunchOutRequestWorkSite {
  @override
  final LocalTime canPunchOut;

  factory _$CanPunchOutRequestWorkSite(
          [void Function(CanPunchOutRequestWorkSiteBuilder)? updates]) =>
      (new CanPunchOutRequestWorkSiteBuilder()..update(updates))._build();

  _$CanPunchOutRequestWorkSite._({required this.canPunchOut}) : super._() {
    BuiltValueNullFieldError.checkNotNull(
        canPunchOut, r'CanPunchOutRequestWorkSite', 'canPunchOut');
  }

  @override
  CanPunchOutRequestWorkSite rebuild(
          void Function(CanPunchOutRequestWorkSiteBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CanPunchOutRequestWorkSiteBuilder toBuilder() =>
      new CanPunchOutRequestWorkSiteBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CanPunchOutRequestWorkSite &&
        canPunchOut == other.canPunchOut;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, canPunchOut.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CanPunchOutRequestWorkSite')
          ..add('canPunchOut', canPunchOut))
        .toString();
  }
}

class CanPunchOutRequestWorkSiteBuilder
    implements
        Builder<CanPunchOutRequestWorkSite, CanPunchOutRequestWorkSiteBuilder> {
  _$CanPunchOutRequestWorkSite? _$v;

  LocalTimeBuilder? _canPunchOut;
  LocalTimeBuilder get canPunchOut =>
      _$this._canPunchOut ??= new LocalTimeBuilder();
  set canPunchOut(LocalTimeBuilder? canPunchOut) =>
      _$this._canPunchOut = canPunchOut;

  CanPunchOutRequestWorkSiteBuilder() {
    CanPunchOutRequestWorkSite._defaults(this);
  }

  CanPunchOutRequestWorkSiteBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _canPunchOut = $v.canPunchOut.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CanPunchOutRequestWorkSite other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$CanPunchOutRequestWorkSite;
  }

  @override
  void update(void Function(CanPunchOutRequestWorkSiteBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CanPunchOutRequestWorkSite build() => _build();

  _$CanPunchOutRequestWorkSite _build() {
    _$CanPunchOutRequestWorkSite _$result;
    try {
      _$result = _$v ??
          new _$CanPunchOutRequestWorkSite._(canPunchOut: canPunchOut.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'canPunchOut';
        canPunchOut.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'CanPunchOutRequestWorkSite', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
