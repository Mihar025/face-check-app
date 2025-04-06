// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_status_work_site_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateStatusWorkSiteRequest extends UpdateStatusWorkSiteRequest {
  @override
  final bool? active;

  factory _$UpdateStatusWorkSiteRequest(
          [void Function(UpdateStatusWorkSiteRequestBuilder)? updates]) =>
      (new UpdateStatusWorkSiteRequestBuilder()..update(updates))._build();

  _$UpdateStatusWorkSiteRequest._({this.active}) : super._();

  @override
  UpdateStatusWorkSiteRequest rebuild(
          void Function(UpdateStatusWorkSiteRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateStatusWorkSiteRequestBuilder toBuilder() =>
      new UpdateStatusWorkSiteRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateStatusWorkSiteRequest && active == other.active;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, active.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateStatusWorkSiteRequest')
          ..add('active', active))
        .toString();
  }
}

class UpdateStatusWorkSiteRequestBuilder
    implements
        Builder<UpdateStatusWorkSiteRequest,
            UpdateStatusWorkSiteRequestBuilder> {
  _$UpdateStatusWorkSiteRequest? _$v;

  bool? _active;
  bool? get active => _$this._active;
  set active(bool? active) => _$this._active = active;

  UpdateStatusWorkSiteRequestBuilder() {
    UpdateStatusWorkSiteRequest._defaults(this);
  }

  UpdateStatusWorkSiteRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _active = $v.active;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateStatusWorkSiteRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UpdateStatusWorkSiteRequest;
  }

  @override
  void update(void Function(UpdateStatusWorkSiteRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateStatusWorkSiteRequest build() => _build();

  _$UpdateStatusWorkSiteRequest _build() {
    final _$result = _$v ?? new _$UpdateStatusWorkSiteRequest._(active: active);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
