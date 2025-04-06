// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_name_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateNameRequest extends UpdateNameRequest {
  @override
  final String name;

  factory _$UpdateNameRequest(
          [void Function(UpdateNameRequestBuilder)? updates]) =>
      (new UpdateNameRequestBuilder()..update(updates))._build();

  _$UpdateNameRequest._({required this.name}) : super._() {
    BuiltValueNullFieldError.checkNotNull(name, r'UpdateNameRequest', 'name');
  }

  @override
  UpdateNameRequest rebuild(void Function(UpdateNameRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateNameRequestBuilder toBuilder() =>
      new UpdateNameRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateNameRequest && name == other.name;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateNameRequest')
          ..add('name', name))
        .toString();
  }
}

class UpdateNameRequestBuilder
    implements Builder<UpdateNameRequest, UpdateNameRequestBuilder> {
  _$UpdateNameRequest? _$v;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  UpdateNameRequestBuilder() {
    UpdateNameRequest._defaults(this);
  }

  UpdateNameRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _name = $v.name;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateNameRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UpdateNameRequest;
  }

  @override
  void update(void Function(UpdateNameRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateNameRequest build() => _build();

  _$UpdateNameRequest _build() {
    final _$result = _$v ??
        new _$UpdateNameRequest._(
            name: BuiltValueNullFieldError.checkNotNull(
                name, r'UpdateNameRequest', 'name'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
