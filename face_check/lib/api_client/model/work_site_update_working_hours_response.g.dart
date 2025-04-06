// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_update_working_hours_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteUpdateWorkingHoursResponse
    extends WorkSiteUpdateWorkingHoursResponse {
  @override
  final int? workSiteId;
  @override
  final LocalTime? newStart;
  @override
  final LocalTime? newEnd;

  factory _$WorkSiteUpdateWorkingHoursResponse(
          [void Function(WorkSiteUpdateWorkingHoursResponseBuilder)?
              updates]) =>
      (new WorkSiteUpdateWorkingHoursResponseBuilder()..update(updates))
          ._build();

  _$WorkSiteUpdateWorkingHoursResponse._(
      {this.workSiteId, this.newStart, this.newEnd})
      : super._();

  @override
  WorkSiteUpdateWorkingHoursResponse rebuild(
          void Function(WorkSiteUpdateWorkingHoursResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteUpdateWorkingHoursResponseBuilder toBuilder() =>
      new WorkSiteUpdateWorkingHoursResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteUpdateWorkingHoursResponse &&
        workSiteId == other.workSiteId &&
        newStart == other.newStart &&
        newEnd == other.newEnd;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, newStart.hashCode);
    _$hash = $jc(_$hash, newEnd.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteUpdateWorkingHoursResponse')
          ..add('workSiteId', workSiteId)
          ..add('newStart', newStart)
          ..add('newEnd', newEnd))
        .toString();
  }
}

class WorkSiteUpdateWorkingHoursResponseBuilder
    implements
        Builder<WorkSiteUpdateWorkingHoursResponse,
            WorkSiteUpdateWorkingHoursResponseBuilder> {
  _$WorkSiteUpdateWorkingHoursResponse? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  LocalTimeBuilder? _newStart;
  LocalTimeBuilder get newStart => _$this._newStart ??= new LocalTimeBuilder();
  set newStart(LocalTimeBuilder? newStart) => _$this._newStart = newStart;

  LocalTimeBuilder? _newEnd;
  LocalTimeBuilder get newEnd => _$this._newEnd ??= new LocalTimeBuilder();
  set newEnd(LocalTimeBuilder? newEnd) => _$this._newEnd = newEnd;

  WorkSiteUpdateWorkingHoursResponseBuilder() {
    WorkSiteUpdateWorkingHoursResponse._defaults(this);
  }

  WorkSiteUpdateWorkingHoursResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _newStart = $v.newStart?.toBuilder();
      _newEnd = $v.newEnd?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteUpdateWorkingHoursResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteUpdateWorkingHoursResponse;
  }

  @override
  void update(
      void Function(WorkSiteUpdateWorkingHoursResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteUpdateWorkingHoursResponse build() => _build();

  _$WorkSiteUpdateWorkingHoursResponse _build() {
    _$WorkSiteUpdateWorkingHoursResponse _$result;
    try {
      _$result = _$v ??
          new _$WorkSiteUpdateWorkingHoursResponse._(
              workSiteId: workSiteId,
              newStart: _newStart?.build(),
              newEnd: _newEnd?.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'newStart';
        _newStart?.build();
        _$failedField = 'newEnd';
        _newEnd?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WorkSiteUpdateWorkingHoursResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
