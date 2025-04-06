// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_closed_days_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteClosedDaysResponse extends WorkSiteClosedDaysResponse {
  @override
  final int? workSiteId;
  @override
  final String? siteName;
  @override
  final String? siteAddress;
  @override
  final BuiltList<Date>? closedDays;

  factory _$WorkSiteClosedDaysResponse(
          [void Function(WorkSiteClosedDaysResponseBuilder)? updates]) =>
      (new WorkSiteClosedDaysResponseBuilder()..update(updates))._build();

  _$WorkSiteClosedDaysResponse._(
      {this.workSiteId, this.siteName, this.siteAddress, this.closedDays})
      : super._();

  @override
  WorkSiteClosedDaysResponse rebuild(
          void Function(WorkSiteClosedDaysResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteClosedDaysResponseBuilder toBuilder() =>
      new WorkSiteClosedDaysResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteClosedDaysResponse &&
        workSiteId == other.workSiteId &&
        siteName == other.siteName &&
        siteAddress == other.siteAddress &&
        closedDays == other.closedDays;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, siteName.hashCode);
    _$hash = $jc(_$hash, siteAddress.hashCode);
    _$hash = $jc(_$hash, closedDays.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteClosedDaysResponse')
          ..add('workSiteId', workSiteId)
          ..add('siteName', siteName)
          ..add('siteAddress', siteAddress)
          ..add('closedDays', closedDays))
        .toString();
  }
}

class WorkSiteClosedDaysResponseBuilder
    implements
        Builder<WorkSiteClosedDaysResponse, WorkSiteClosedDaysResponseBuilder> {
  _$WorkSiteClosedDaysResponse? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  String? _siteName;
  String? get siteName => _$this._siteName;
  set siteName(String? siteName) => _$this._siteName = siteName;

  String? _siteAddress;
  String? get siteAddress => _$this._siteAddress;
  set siteAddress(String? siteAddress) => _$this._siteAddress = siteAddress;

  ListBuilder<Date>? _closedDays;
  ListBuilder<Date> get closedDays =>
      _$this._closedDays ??= new ListBuilder<Date>();
  set closedDays(ListBuilder<Date>? closedDays) =>
      _$this._closedDays = closedDays;

  WorkSiteClosedDaysResponseBuilder() {
    WorkSiteClosedDaysResponse._defaults(this);
  }

  WorkSiteClosedDaysResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _siteName = $v.siteName;
      _siteAddress = $v.siteAddress;
      _closedDays = $v.closedDays?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteClosedDaysResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteClosedDaysResponse;
  }

  @override
  void update(void Function(WorkSiteClosedDaysResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteClosedDaysResponse build() => _build();

  _$WorkSiteClosedDaysResponse _build() {
    _$WorkSiteClosedDaysResponse _$result;
    try {
      _$result = _$v ??
          new _$WorkSiteClosedDaysResponse._(
              workSiteId: workSiteId,
              siteName: siteName,
              siteAddress: siteAddress,
              closedDays: _closedDays?.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'closedDays';
        _closedDays?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WorkSiteClosedDaysResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
