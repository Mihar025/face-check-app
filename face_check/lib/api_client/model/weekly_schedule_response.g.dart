// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'weekly_schedule_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WeeklyScheduleResponse extends WeeklyScheduleResponse {
  @override
  final BuiltList<DailyScheduleResponse>? dailySchedules;
  @override
  final double? totalWeekHours;
  @override
  final double? regularHours;
  @override
  final double? overtimeHours;

  factory _$WeeklyScheduleResponse(
          [void Function(WeeklyScheduleResponseBuilder)? updates]) =>
      (new WeeklyScheduleResponseBuilder()..update(updates))._build();

  _$WeeklyScheduleResponse._(
      {this.dailySchedules,
      this.totalWeekHours,
      this.regularHours,
      this.overtimeHours})
      : super._();

  @override
  WeeklyScheduleResponse rebuild(
          void Function(WeeklyScheduleResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WeeklyScheduleResponseBuilder toBuilder() =>
      new WeeklyScheduleResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WeeklyScheduleResponse &&
        dailySchedules == other.dailySchedules &&
        totalWeekHours == other.totalWeekHours &&
        regularHours == other.regularHours &&
        overtimeHours == other.overtimeHours;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, dailySchedules.hashCode);
    _$hash = $jc(_$hash, totalWeekHours.hashCode);
    _$hash = $jc(_$hash, regularHours.hashCode);
    _$hash = $jc(_$hash, overtimeHours.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WeeklyScheduleResponse')
          ..add('dailySchedules', dailySchedules)
          ..add('totalWeekHours', totalWeekHours)
          ..add('regularHours', regularHours)
          ..add('overtimeHours', overtimeHours))
        .toString();
  }
}

class WeeklyScheduleResponseBuilder
    implements Builder<WeeklyScheduleResponse, WeeklyScheduleResponseBuilder> {
  _$WeeklyScheduleResponse? _$v;

  ListBuilder<DailyScheduleResponse>? _dailySchedules;
  ListBuilder<DailyScheduleResponse> get dailySchedules =>
      _$this._dailySchedules ??= new ListBuilder<DailyScheduleResponse>();
  set dailySchedules(ListBuilder<DailyScheduleResponse>? dailySchedules) =>
      _$this._dailySchedules = dailySchedules;

  double? _totalWeekHours;
  double? get totalWeekHours => _$this._totalWeekHours;
  set totalWeekHours(double? totalWeekHours) =>
      _$this._totalWeekHours = totalWeekHours;

  double? _regularHours;
  double? get regularHours => _$this._regularHours;
  set regularHours(double? regularHours) => _$this._regularHours = regularHours;

  double? _overtimeHours;
  double? get overtimeHours => _$this._overtimeHours;
  set overtimeHours(double? overtimeHours) =>
      _$this._overtimeHours = overtimeHours;

  WeeklyScheduleResponseBuilder() {
    WeeklyScheduleResponse._defaults(this);
  }

  WeeklyScheduleResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _dailySchedules = $v.dailySchedules?.toBuilder();
      _totalWeekHours = $v.totalWeekHours;
      _regularHours = $v.regularHours;
      _overtimeHours = $v.overtimeHours;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WeeklyScheduleResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WeeklyScheduleResponse;
  }

  @override
  void update(void Function(WeeklyScheduleResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WeeklyScheduleResponse build() => _build();

  _$WeeklyScheduleResponse _build() {
    _$WeeklyScheduleResponse _$result;
    try {
      _$result = _$v ??
          new _$WeeklyScheduleResponse._(
              dailySchedules: _dailySchedules?.build(),
              totalWeekHours: totalWeekHours,
              regularHours: regularHours,
              overtimeHours: overtimeHours);
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'dailySchedules';
        _dailySchedules?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WeeklyScheduleResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
