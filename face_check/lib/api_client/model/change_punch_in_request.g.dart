// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'change_punch_in_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ChangePunchInRequest extends ChangePunchInRequest {
  @override
  final int? workerId;
  @override
  final DateTime? dateWhenWorkerDidntMakePunchIn;
  @override
  final Date? newPunchInDate;
  @override
  final LocalTime? newPunchInTime;

  factory _$ChangePunchInRequest(
          [void Function(ChangePunchInRequestBuilder)? updates]) =>
      (new ChangePunchInRequestBuilder()..update(updates))._build();

  _$ChangePunchInRequest._(
      {this.workerId,
      this.dateWhenWorkerDidntMakePunchIn,
      this.newPunchInDate,
      this.newPunchInTime})
      : super._();

  @override
  ChangePunchInRequest rebuild(
          void Function(ChangePunchInRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ChangePunchInRequestBuilder toBuilder() =>
      new ChangePunchInRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ChangePunchInRequest &&
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
    return (newBuiltValueToStringHelper(r'ChangePunchInRequest')
          ..add('workerId', workerId)
          ..add(
              'dateWhenWorkerDidntMakePunchIn', dateWhenWorkerDidntMakePunchIn)
          ..add('newPunchInDate', newPunchInDate)
          ..add('newPunchInTime', newPunchInTime))
        .toString();
  }
}

class ChangePunchInRequestBuilder
    implements Builder<ChangePunchInRequest, ChangePunchInRequestBuilder> {
  _$ChangePunchInRequest? _$v;

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

  ChangePunchInRequestBuilder() {
    ChangePunchInRequest._defaults(this);
  }

  ChangePunchInRequestBuilder get _$this {
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
  void replace(ChangePunchInRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$ChangePunchInRequest;
  }

  @override
  void update(void Function(ChangePunchInRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ChangePunchInRequest build() => _build();

  _$ChangePunchInRequest _build() {
    _$ChangePunchInRequest _$result;
    try {
      _$result = _$v ??
          new _$ChangePunchInRequest._(
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
            r'ChangePunchInRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
