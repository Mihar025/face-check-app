// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'schedule_inactive_day_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ScheduleInactiveDayResponse extends ScheduleInactiveDayResponse {
  @override
  final int? workSiteId;
  @override
  final Date? inactiveDate;

  factory _$ScheduleInactiveDayResponse(
          [void Function(ScheduleInactiveDayResponseBuilder)? updates]) =>
      (new ScheduleInactiveDayResponseBuilder()..update(updates))._build();

  _$ScheduleInactiveDayResponse._({this.workSiteId, this.inactiveDate})
      : super._();

  @override
  ScheduleInactiveDayResponse rebuild(
          void Function(ScheduleInactiveDayResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ScheduleInactiveDayResponseBuilder toBuilder() =>
      new ScheduleInactiveDayResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ScheduleInactiveDayResponse &&
        workSiteId == other.workSiteId &&
        inactiveDate == other.inactiveDate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, inactiveDate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ScheduleInactiveDayResponse')
          ..add('workSiteId', workSiteId)
          ..add('inactiveDate', inactiveDate))
        .toString();
  }
}

class ScheduleInactiveDayResponseBuilder
    implements
        Builder<ScheduleInactiveDayResponse,
            ScheduleInactiveDayResponseBuilder> {
  _$ScheduleInactiveDayResponse? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  Date? _inactiveDate;
  Date? get inactiveDate => _$this._inactiveDate;
  set inactiveDate(Date? inactiveDate) => _$this._inactiveDate = inactiveDate;

  ScheduleInactiveDayResponseBuilder() {
    ScheduleInactiveDayResponse._defaults(this);
  }

  ScheduleInactiveDayResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _inactiveDate = $v.inactiveDate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ScheduleInactiveDayResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$ScheduleInactiveDayResponse;
  }

  @override
  void update(void Function(ScheduleInactiveDayResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ScheduleInactiveDayResponse build() => _build();

  _$ScheduleInactiveDayResponse _build() {
    final _$result = _$v ??
        new _$ScheduleInactiveDayResponse._(
            workSiteId: workSiteId, inactiveDate: inactiveDate);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
