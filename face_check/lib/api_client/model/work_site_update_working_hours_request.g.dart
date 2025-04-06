// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'work_site_update_working_hours_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkSiteUpdateWorkingHoursRequest
    extends WorkSiteUpdateWorkingHoursRequest {
  @override
  final LocalTime newStart;
  @override
  final LocalTime newEnd;

  factory _$WorkSiteUpdateWorkingHoursRequest(
          [void Function(WorkSiteUpdateWorkingHoursRequestBuilder)? updates]) =>
      (new WorkSiteUpdateWorkingHoursRequestBuilder()..update(updates))
          ._build();

  _$WorkSiteUpdateWorkingHoursRequest._(
      {required this.newStart, required this.newEnd})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        newStart, r'WorkSiteUpdateWorkingHoursRequest', 'newStart');
    BuiltValueNullFieldError.checkNotNull(
        newEnd, r'WorkSiteUpdateWorkingHoursRequest', 'newEnd');
  }

  @override
  WorkSiteUpdateWorkingHoursRequest rebuild(
          void Function(WorkSiteUpdateWorkingHoursRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkSiteUpdateWorkingHoursRequestBuilder toBuilder() =>
      new WorkSiteUpdateWorkingHoursRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkSiteUpdateWorkingHoursRequest &&
        newStart == other.newStart &&
        newEnd == other.newEnd;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, newStart.hashCode);
    _$hash = $jc(_$hash, newEnd.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkSiteUpdateWorkingHoursRequest')
          ..add('newStart', newStart)
          ..add('newEnd', newEnd))
        .toString();
  }
}

class WorkSiteUpdateWorkingHoursRequestBuilder
    implements
        Builder<WorkSiteUpdateWorkingHoursRequest,
            WorkSiteUpdateWorkingHoursRequestBuilder> {
  _$WorkSiteUpdateWorkingHoursRequest? _$v;

  LocalTimeBuilder? _newStart;
  LocalTimeBuilder get newStart => _$this._newStart ??= new LocalTimeBuilder();
  set newStart(LocalTimeBuilder? newStart) => _$this._newStart = newStart;

  LocalTimeBuilder? _newEnd;
  LocalTimeBuilder get newEnd => _$this._newEnd ??= new LocalTimeBuilder();
  set newEnd(LocalTimeBuilder? newEnd) => _$this._newEnd = newEnd;

  WorkSiteUpdateWorkingHoursRequestBuilder() {
    WorkSiteUpdateWorkingHoursRequest._defaults(this);
  }

  WorkSiteUpdateWorkingHoursRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _newStart = $v.newStart.toBuilder();
      _newEnd = $v.newEnd.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkSiteUpdateWorkingHoursRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkSiteUpdateWorkingHoursRequest;
  }

  @override
  void update(
      void Function(WorkSiteUpdateWorkingHoursRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkSiteUpdateWorkingHoursRequest build() => _build();

  _$WorkSiteUpdateWorkingHoursRequest _build() {
    _$WorkSiteUpdateWorkingHoursRequest _$result;
    try {
      _$result = _$v ??
          new _$WorkSiteUpdateWorkingHoursRequest._(
              newStart: newStart.build(), newEnd: newEnd.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'newStart';
        newStart.build();
        _$failedField = 'newEnd';
        newEnd.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'WorkSiteUpdateWorkingHoursRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
