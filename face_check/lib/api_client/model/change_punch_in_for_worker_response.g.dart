// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'change_punch_in_for_worker_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ChangePunchInForWorkerResponse extends ChangePunchInForWorkerResponse {
  @override
  final int? workerId;
  @override
  final DateTime? dateWhenWorkerDidntMakePunchIn;
  @override
  final Date? newPunchInDate;
  @override
  final LocalTime? newPunchInTime;

  factory _$ChangePunchInForWorkerResponse(
          [void Function(ChangePunchInForWorkerResponseBuilder)? updates]) =>
      (new ChangePunchInForWorkerResponseBuilder()..update(updates))._build();

  _$ChangePunchInForWorkerResponse._(
      {this.workerId,
      this.dateWhenWorkerDidntMakePunchIn,
      this.newPunchInDate,
      this.newPunchInTime})
      : super._();

  @override
  ChangePunchInForWorkerResponse rebuild(
          void Function(ChangePunchInForWorkerResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ChangePunchInForWorkerResponseBuilder toBuilder() =>
      new ChangePunchInForWorkerResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ChangePunchInForWorkerResponse &&
        workerId == other.workerId &&
        dateWhenWorkerDidntMakePunchIn ==
            other.dateWhenWorkerDidntMakePunchIn &&
        newPunchInDate == other.newPunchInDate &&
        newPunchInTime == other.newPunchInTime;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workerId.hashCode);
    _$hash = $jc(_$hash, dateWhenWorkerDidntMakePunchIn.hashCode);
    _$hash = $jc(_$hash, newPunchInDate.hashCode);
    _$hash = $jc(_$hash, newPunchInTime.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ChangePunchInForWorkerResponse')
          ..add('workerId', workerId)
          ..add(
              'dateWhenWorkerDidntMakePunchIn', dateWhenWorkerDidntMakePunchIn)
          ..add('newPunchInDate', newPunchInDate)
          ..add('newPunchInTime', newPunchInTime))
        .toString();
  }
}

class ChangePunchInForWorkerResponseBuilder
    implements
        Builder<ChangePunchInForWorkerResponse,
            ChangePunchInForWorkerResponseBuilder> {
  _$ChangePunchInForWorkerResponse? _$v;

  int? _workerId;
  int? get workerId => _$this._workerId;
  set workerId(int? workerId) => _$this._workerId = workerId;

  DateTime? _dateWhenWorkerDidntMakePunchIn;
  DateTime? get dateWhenWorkerDidntMakePunchIn =>
      _$this._dateWhenWorkerDidntMakePunchIn;
  set dateWhenWorkerDidntMakePunchIn(
          DateTime? dateWhenWorkerDidntMakePunchIn) =>
      _$this._dateWhenWorkerDidntMakePunchIn = dateWhenWorkerDidntMakePunchIn;

  Date? _newPunchInDate;
  Date? get newPunchInDate => _$this._newPunchInDate;
  set newPunchInDate(Date? newPunchInDate) =>
      _$this._newPunchInDate = newPunchInDate;

  LocalTimeBuilder? _newPunchInTime;
  LocalTimeBuilder get newPunchInTime =>
      _$this._newPunchInTime ??= new LocalTimeBuilder();
  set newPunchInTime(LocalTimeBuilder? newPunchInTime) =>
      _$this._newPunchInTime = newPunchInTime;

  ChangePunchInForWorkerResponseBuilder() {
    ChangePunchInForWorkerResponse._defaults(this);
  }

  ChangePunchInForWorkerResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workerId = $v.workerId;
      _dateWhenWorkerDidntMakePunchIn = $v.dateWhenWorkerDidntMakePunchIn;
      _newPunchInDate = $v.newPunchInDate;
      _newPunchInTime = $v.newPunchInTime?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ChangePunchInForWorkerResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$ChangePunchInForWorkerResponse;
  }

  @override
  void update(void Function(ChangePunchInForWorkerResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ChangePunchInForWorkerResponse build() => _build();

  _$ChangePunchInForWorkerResponse _build() {
    _$ChangePunchInForWorkerResponse _$result;
    try {
      _$result = _$v ??
          new _$ChangePunchInForWorkerResponse._(
              workerId: workerId,
              dateWhenWorkerDidntMakePunchIn: dateWhenWorkerDidntMakePunchIn,
              newPunchInDate: newPunchInDate,
              newPunchInTime: _newPunchInTime?.build());
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'newPunchInTime';
        _newPunchInTime?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'ChangePunchInForWorkerResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
