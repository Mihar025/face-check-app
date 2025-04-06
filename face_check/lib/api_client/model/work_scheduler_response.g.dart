// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_scheduler_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSchedulerResponse extends WorkSchedulerResponse {
  @override
  final int? workerId;
  @override
  final LocalTime? startTime;
  @override
  final LocalTime? endTime;

  factory _$WorkSchedulerResponse(
          [void Function(WorkSchedulerResponseBuilder)? updates]) =>
      (new WorkSchedulerResponseBuilder()..update(updates))._build();

  _$WorkSchedulerResponse._({this.workerId, this.startTime, this.endTime})
      : super._();

  @override
  WorkSchedulerResponse rebuild(
          void Function(WorkSchedulerResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSchedulerResponseBuilder toBuilder() =>
      new WorkSchedulerResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSchedulerResponse &&
        workerId == other.workerId &&
        startTime == other.startTime &&
        endTime == other.endTime;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workerId.hashCode);
    _$hash = $jc(_$hash, startTime.hashCode);
    _$hash = $jc(_$hash, endTime.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSchedulerResponse')
          ..add('workerId', workerId)
          ..add('startTime', startTime)
          ..add('endTime', endTime))
        .toString();
  }
}

class WorkSchedulerResponseBuilder
    implements Builder<WorkSchedulerResponse, WorkSchedulerResponseBuilder> {
  _$WorkSchedulerResponse? _$v;

  int? _workerId;
  int? get workerId => _$this._workerId;
  set workerId(int? workerId) => _$this._workerId = workerId;

  LocalTimeBuilder? _startTime;
  LocalTimeBuilder get startTime =>
      _$this._startTime ??= new LocalTimeBuilder();
  set startTime(LocalTimeBuilder? startTime) => _$this._startTime = startTime;

  LocalTimeBuilder? _endTime;
  LocalTimeBuilder get endTime => _$this._endTime ??= new LocalTimeBuilder();
  set endTime(LocalTimeBuilder? endTime) => _$this._endTime = endTime;

  WorkSchedulerResponseBuilder() {
    WorkSchedulerResponse._defaults(this);
  }

  WorkSchedulerResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workerId = $v.workerId;
      _startTime = $v.startTime?.toBuilder();
      _endTime = $v.endTime?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSchedulerResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSchedulerResponse;
  }

  @override
  void update(void Function(WorkSchedulerResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSchedulerResponse build() => _build();

  _$WorkSchedulerResponse _build() {
    _$WorkSchedulerResponse _$result;
    try {
      _$result = _$v ??
          new _$WorkSchedulerResponse._(
              workerId: workerId,
              startTime: _startTime?.build(),
              endTime: _endTime?.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'startTime';
        _startTime?.build();
        _$failedField = 'endTime';
        _endTime?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WorkSchedulerResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
