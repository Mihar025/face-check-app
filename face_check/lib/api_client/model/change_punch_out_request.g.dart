// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'change_punch_out_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ChangePunchOutRequest extends ChangePunchOutRequest {
  @override
  final int? workerId;
  @override
  final DateTime? dateWhenWorkerDidntMakePunchOut;
  @override
  final Date? newPunchOutDate;
  @override
  final LocalTime? newPunchOutTime;

  factory _$ChangePunchOutRequest(
          [void Function(ChangePunchOutRequestBuilder)? updates]) =>
      (new ChangePunchOutRequestBuilder()..update(updates))._build();

  _$ChangePunchOutRequest._(
      {this.workerId,
      this.dateWhenWorkerDidntMakePunchOut,
      this.newPunchOutDate,
      this.newPunchOutTime})
      : super._();

  @override
  ChangePunchOutRequest rebuild(
          void Function(ChangePunchOutRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ChangePunchOutRequestBuilder toBuilder() =>
      new ChangePunchOutRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ChangePunchOutRequest &&
        workerId == other.workerId &&
        dateWhenWorkerDidntMakePunchOut ==
            other.dateWhenWorkerDidntMakePunchOut &&
        newPunchOutDate == other.newPunchOutDate &&
        newPunchOutTime == other.newPunchOutTime;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workerId.hashCode);
    _$hash = $jc(_$hash, dateWhenWorkerDidntMakePunchOut.hashCode);
    _$hash = $jc(_$hash, newPunchOutDate.hashCode);
    _$hash = $jc(_$hash, newPunchOutTime.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ChangePunchOutRequest')
          ..add('workerId', workerId)
          ..add('dateWhenWorkerDidntMakePunchOut',
              dateWhenWorkerDidntMakePunchOut)
          ..add('newPunchOutDate', newPunchOutDate)
          ..add('newPunchOutTime', newPunchOutTime))
        .toString();
  }
}

class ChangePunchOutRequestBuilder
    implements Builder<ChangePunchOutRequest, ChangePunchOutRequestBuilder> {
  _$ChangePunchOutRequest? _$v;

  int? _workerId;
  int? get workerId => _$this._workerId;
  set workerId(int? workerId) => _$this._workerId = workerId;

  DateTime? _dateWhenWorkerDidntMakePunchOut;
  DateTime? get dateWhenWorkerDidntMakePunchOut =>
      _$this._dateWhenWorkerDidntMakePunchOut;
  set dateWhenWorkerDidntMakePunchOut(
          DateTime? dateWhenWorkerDidntMakePunchOut) =>
      _$this._dateWhenWorkerDidntMakePunchOut = dateWhenWorkerDidntMakePunchOut;

  Date? _newPunchOutDate;
  Date? get newPunchOutDate => _$this._newPunchOutDate;
  set newPunchOutDate(Date? newPunchOutDate) =>
      _$this._newPunchOutDate = newPunchOutDate;

  LocalTimeBuilder? _newPunchOutTime;
  LocalTimeBuilder get newPunchOutTime =>
      _$this._newPunchOutTime ??= new LocalTimeBuilder();
  set newPunchOutTime(LocalTimeBuilder? newPunchOutTime) =>
      _$this._newPunchOutTime = newPunchOutTime;

  ChangePunchOutRequestBuilder() {
    ChangePunchOutRequest._defaults(this);
  }

  ChangePunchOutRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workerId = $v.workerId;
      _dateWhenWorkerDidntMakePunchOut = $v.dateWhenWorkerDidntMakePunchOut;
      _newPunchOutDate = $v.newPunchOutDate;
      _newPunchOutTime = $v.newPunchOutTime?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ChangePunchOutRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$ChangePunchOutRequest;
  }

  @override
  void update(void Function(ChangePunchOutRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ChangePunchOutRequest build() => _build();

  _$ChangePunchOutRequest _build() {
    _$ChangePunchOutRequest _$result;
    try {
      _$result = _$v ??
          new _$ChangePunchOutRequest._(
              workerId: workerId,
              dateWhenWorkerDidntMakePunchOut: dateWhenWorkerDidntMakePunchOut,
              newPunchOutDate: newPunchOutDate,
              newPunchOutTime: _newPunchOutTime?.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'newPunchOutTime';
        _newPunchOutTime?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'ChangePunchOutRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
