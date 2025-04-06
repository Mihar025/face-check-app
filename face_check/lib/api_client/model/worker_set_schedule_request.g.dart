// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'worker_set_schedule_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkerSetScheduleRequest extends WorkerSetScheduleRequest {
  @override
  final LocalTime startTime;
  @override
  final LocalTime endTime;

  factory _$WorkerSetScheduleRequest(
          [void Function(WorkerSetScheduleRequestBuilder)? updates]) =>
      (new WorkerSetScheduleRequestBuilder()..update(updates))._build();

  _$WorkerSetScheduleRequest._({required this.startTime, required this.endTime})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        startTime, r'WorkerSetScheduleRequest', 'startTime');
    BuiltValueNullFieldError.checkNotNull(
        endTime, r'WorkerSetScheduleRequest', 'endTime');
  }

  @override
  WorkerSetScheduleRequest rebuild(
          void Function(WorkerSetScheduleRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkerSetScheduleRequestBuilder toBuilder() =>
      new WorkerSetScheduleRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkerSetScheduleRequest &&
        startTime == other.startTime &&
        endTime == other.endTime;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, startTime.hashCode);
    _$hash = $jc(_$hash, endTime.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkerSetScheduleRequest')
          ..add('startTime', startTime)
          ..add('endTime', endTime))
        .toString();
  }
}

class WorkerSetScheduleRequestBuilder
    implements
        Builder<WorkerSetScheduleRequest, WorkerSetScheduleRequestBuilder> {
  _$WorkerSetScheduleRequest? _$v;

  LocalTimeBuilder? _startTime;
  LocalTimeBuilder get startTime =>
      _$this._startTime ??= new LocalTimeBuilder();
  set startTime(LocalTimeBuilder? startTime) => _$this._startTime = startTime;

  LocalTimeBuilder? _endTime;
  LocalTimeBuilder get endTime => _$this._endTime ??= new LocalTimeBuilder();
  set endTime(LocalTimeBuilder? endTime) => _$this._endTime = endTime;

  WorkerSetScheduleRequestBuilder() {
    WorkerSetScheduleRequest._defaults(this);
  }

  WorkerSetScheduleRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _startTime = $v.startTime.toBuilder();
      _endTime = $v.endTime.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkerSetScheduleRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkerSetScheduleRequest;
  }

  @override
  void update(void Function(WorkerSetScheduleRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkerSetScheduleRequest build() => _build();

  _$WorkerSetScheduleRequest _build() {
    _$WorkerSetScheduleRequest _$result;
    try {
      _$result = _$v ??
          new _$WorkerSetScheduleRequest._(
              startTime: startTime.build(), endTime: endTime.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'startTime';
        startTime.build();
        _$failedField = 'endTime';
        endTime.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WorkerSetScheduleRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
