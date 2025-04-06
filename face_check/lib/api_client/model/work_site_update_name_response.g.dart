// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_update_name_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteUpdateNameResponse extends WorkSiteUpdateNameResponse {
  @override
  final int? worksiteId;
  @override
  final String? workSiteName;

  factory _$WorkSiteUpdateNameResponse(
          [void Function(WorkSiteUpdateNameResponseBuilder)? updates]) =>
      (new WorkSiteUpdateNameResponseBuilder()..update(updates))._build();

  _$WorkSiteUpdateNameResponse._({this.worksiteId, this.workSiteName})
      : super._();

  @override
  WorkSiteUpdateNameResponse rebuild(
          void Function(WorkSiteUpdateNameResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteUpdateNameResponseBuilder toBuilder() =>
      new WorkSiteUpdateNameResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteUpdateNameResponse &&
        worksiteId == other.worksiteId &&
        workSiteName == other.workSiteName;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, worksiteId.hashCode);
    _$hash = $jc(_$hash, workSiteName.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteUpdateNameResponse')
          ..add('worksiteId', worksiteId)
          ..add('workSiteName', workSiteName))
        .toString();
  }
}

class WorkSiteUpdateNameResponseBuilder
    implements
        Builder<WorkSiteUpdateNameResponse, WorkSiteUpdateNameResponseBuilder> {
  _$WorkSiteUpdateNameResponse? _$v;

  int? _worksiteId;
  int? get worksiteId => _$this._worksiteId;
  set worksiteId(int? worksiteId) => _$this._worksiteId = worksiteId;

  String? _workSiteName;
  String? get workSiteName => _$this._workSiteName;
  set workSiteName(String? workSiteName) => _$this._workSiteName = workSiteName;

  WorkSiteUpdateNameResponseBuilder() {
    WorkSiteUpdateNameResponse._defaults(this);
  }

  WorkSiteUpdateNameResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _worksiteId = $v.worksiteId;
      _workSiteName = $v.workSiteName;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteUpdateNameResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteUpdateNameResponse;
  }

  @override
  void update(void Function(WorkSiteUpdateNameResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteUpdateNameResponse build() => _build();

  _$WorkSiteUpdateNameResponse _build() {
    final _$result = _$v ??
        new _$WorkSiteUpdateNameResponse._(
            worksiteId: worksiteId, workSiteName: workSiteName);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
