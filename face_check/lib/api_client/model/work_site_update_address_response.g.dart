// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_update_address_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteUpdateAddressResponse extends WorkSiteUpdateAddressResponse {
  @override
  final int? worksiteId;
  @override
  final String? workSiteAddress;

  factory _$WorkSiteUpdateAddressResponse(
          [void Function(WorkSiteUpdateAddressResponseBuilder)? updates]) =>
      (new WorkSiteUpdateAddressResponseBuilder()..update(updates))._build();

  _$WorkSiteUpdateAddressResponse._({this.worksiteId, this.workSiteAddress})
      : super._();

  @override
  WorkSiteUpdateAddressResponse rebuild(
          void Function(WorkSiteUpdateAddressResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteUpdateAddressResponseBuilder toBuilder() =>
      new WorkSiteUpdateAddressResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteUpdateAddressResponse &&
        worksiteId == other.worksiteId &&
        workSiteAddress == other.workSiteAddress;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, worksiteId.hashCode);
    _$hash = $jc(_$hash, workSiteAddress.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteUpdateAddressResponse')
          ..add('worksiteId', worksiteId)
          ..add('workSiteAddress', workSiteAddress))
        .toString();
  }
}

class WorkSiteUpdateAddressResponseBuilder
    implements
        Builder<WorkSiteUpdateAddressResponse,
            WorkSiteUpdateAddressResponseBuilder> {
  _$WorkSiteUpdateAddressResponse? _$v;

  int? _worksiteId;
  int? get worksiteId => _$this._worksiteId;
  set worksiteId(int? worksiteId) => _$this._worksiteId = worksiteId;

  String? _workSiteAddress;
  String? get workSiteAddress => _$this._workSiteAddress;
  set workSiteAddress(String? workSiteAddress) =>
      _$this._workSiteAddress = workSiteAddress;

  WorkSiteUpdateAddressResponseBuilder() {
    WorkSiteUpdateAddressResponse._defaults(this);
  }

  WorkSiteUpdateAddressResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _worksiteId = $v.worksiteId;
      _workSiteAddress = $v.workSiteAddress;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteUpdateAddressResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteUpdateAddressResponse;
  }

  @override
  void update(void Function(WorkSiteUpdateAddressResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteUpdateAddressResponse build() => _build();

  _$WorkSiteUpdateAddressResponse _build() {
    final _$result = _$v ??
        new _$WorkSiteUpdateAddressResponse._(
            worksiteId: worksiteId, workSiteAddress: workSiteAddress);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
