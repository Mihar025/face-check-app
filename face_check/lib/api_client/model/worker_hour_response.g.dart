// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'worker_hour_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkerHourResponse extends WorkerHourResponse {
  @override
  final double? regularHours;
  @override
  final double? overtimeHours;
  @override
  final double? totalHours;

  factory _$WorkerHourResponse(
          [void Function(WorkerHourResponseBuilder)? updates]) =>
      (new WorkerHourResponseBuilder()..update(updates))._build();

  _$WorkerHourResponse._(
      {this.regularHours, this.overtimeHours, this.totalHours})
      : super._();

  @override
  WorkerHourResponse rebuild(
          void Function(WorkerHourResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkerHourResponseBuilder toBuilder() =>
      new WorkerHourResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkerHourResponse &&
        regularHours == other.regularHours &&
        overtimeHours == other.overtimeHours &&
        totalHours == other.totalHours;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, regularHours.hashCode);
    _$hash = $jc(_$hash, overtimeHours.hashCode);
    _$hash = $jc(_$hash, totalHours.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkerHourResponse')
          ..add('regularHours', regularHours)
          ..add('overtimeHours', overtimeHours)
          ..add('totalHours', totalHours))
        .toString();
  }
}

class WorkerHourResponseBuilder
    implements Builder<WorkerHourResponse, WorkerHourResponseBuilder> {
  _$WorkerHourResponse? _$v;

  double? _regularHours;
  double? get regularHours => _$this._regularHours;
  set regularHours(double? regularHours) => _$this._regularHours = regularHours;

  double? _overtimeHours;
  double? get overtimeHours => _$this._overtimeHours;
  set overtimeHours(double? overtimeHours) =>
      _$this._overtimeHours = overtimeHours;

  double? _totalHours;
  double? get totalHours => _$this._totalHours;
  set totalHours(double? totalHours) => _$this._totalHours = totalHours;

  WorkerHourResponseBuilder() {
    WorkerHourResponse._defaults(this);
  }

  WorkerHourResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _regularHours = $v.regularHours;
      _overtimeHours = $v.overtimeHours;
      _totalHours = $v.totalHours;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkerHourResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkerHourResponse;
  }

  @override
  void update(void Function(WorkerHourResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkerHourResponse build() => _build();

  _$WorkerHourResponse _build() {
    final _$result = _$v ??
        new _$WorkerHourResponse._(
            regularHours: regularHours,
            overtimeHours: overtimeHours,
            totalHours: totalHours);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
