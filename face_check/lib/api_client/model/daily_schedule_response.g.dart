// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'daily_schedule_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$DailyScheduleResponse extends DailyScheduleResponse {
  @override
  final String? dayOfWeek;
  @override
  final Date? date;
  @override
  final double? hoursWorked;
  @override
  final LocalTime? startTime;
  @override
  final LocalTime? endTime;
  @override
  final String? workSiteName;
  @override
  final bool? isOnDuty;

  factory _$DailyScheduleResponse(
          [void Function(DailyScheduleResponseBuilder)? updates]) =>
      (new DailyScheduleResponseBuilder()..update(updates))._build();

  _$DailyScheduleResponse._(
      {this.dayOfWeek,
      this.date,
      this.hoursWorked,
      this.startTime,
      this.endTime,
      this.workSiteName,
      this.isOnDuty})
      : super._();

  @override
  DailyScheduleResponse rebuild(
          void Function(DailyScheduleResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  DailyScheduleResponseBuilder toBuilder() =>
      new DailyScheduleResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is DailyScheduleResponse &&
        dayOfWeek == other.dayOfWeek &&
        date == other.date &&
        hoursWorked == other.hoursWorked &&
        startTime == other.startTime &&
        endTime == other.endTime &&
        workSiteName == other.workSiteName &&
        isOnDuty == other.isOnDuty;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, dayOfWeek.hashCode);
    _$hash = $jc(_$hash, date.hashCode);
    _$hash = $jc(_$hash, hoursWorked.hashCode);
    _$hash = $jc(_$hash, startTime.hashCode);
    _$hash = $jc(_$hash, endTime.hashCode);
    _$hash = $jc(_$hash, workSiteName.hashCode);
    _$hash = $jc(_$hash, isOnDuty.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'DailyScheduleResponse')
          ..add('dayOfWeek', dayOfWeek)
          ..add('date', date)
          ..add('hoursWorked', hoursWorked)
          ..add('startTime', startTime)
          ..add('endTime', endTime)
          ..add('workSiteName', workSiteName)
          ..add('isOnDuty', isOnDuty))
        .toString();
  }
}

class DailyScheduleResponseBuilder
    implements Builder<DailyScheduleResponse, DailyScheduleResponseBuilder> {
  _$DailyScheduleResponse? _$v;

  String? _dayOfWeek;
  String? get dayOfWeek => _$this._dayOfWeek;
  set dayOfWeek(String? dayOfWeek) => _$this._dayOfWeek = dayOfWeek;

  Date? _date;
  Date? get date => _$this._date;
  set date(Date? date) => _$this._date = date;

  double? _hoursWorked;
  double? get hoursWorked => _$this._hoursWorked;
  set hoursWorked(double? hoursWorked) => _$this._hoursWorked = hoursWorked;

  LocalTimeBuilder? _startTime;
  LocalTimeBuilder get startTime =>
      _$this._startTime ??= new LocalTimeBuilder();
  set startTime(LocalTimeBuilder? startTime) => _$this._startTime = startTime;

  LocalTimeBuilder? _endTime;
  LocalTimeBuilder get endTime => _$this._endTime ??= new LocalTimeBuilder();
  set endTime(LocalTimeBuilder? endTime) => _$this._endTime = endTime;

  String? _workSiteName;
  String? get workSiteName => _$this._workSiteName;
  set workSiteName(String? workSiteName) => _$this._workSiteName = workSiteName;

  bool? _isOnDuty;
  bool? get isOnDuty => _$this._isOnDuty;
  set isOnDuty(bool? isOnDuty) => _$this._isOnDuty = isOnDuty;

  DailyScheduleResponseBuilder() {
    DailyScheduleResponse._defaults(this);
  }

  DailyScheduleResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _dayOfWeek = $v.dayOfWeek;
      _date = $v.date;
      _hoursWorked = $v.hoursWorked;
      _startTime = $v.startTime?.toBuilder();
      _endTime = $v.endTime?.toBuilder();
      _workSiteName = $v.workSiteName;
      _isOnDuty = $v.isOnDuty;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(DailyScheduleResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$DailyScheduleResponse;
  }

  @override
  void update(void Function(DailyScheduleResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  DailyScheduleResponse build() => _build();

  _$DailyScheduleResponse _build() {
    _$DailyScheduleResponse _$result;
    try {
      _$result = _$v ??
          new _$DailyScheduleResponse._(
              dayOfWeek: dayOfWeek,
              date: date,
              hoursWorked: hoursWorked,
              startTime: _startTime?.build(),
              endTime: _endTime?.build(),
              workSiteName: workSiteName,
              isOnDuty: isOnDuty);
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'startTime';
        _startTime?.build();
        _$failedField = 'endTime';
        _endTime?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'DailyScheduleResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
