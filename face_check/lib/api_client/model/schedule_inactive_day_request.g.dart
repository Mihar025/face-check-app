// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'schedule_inactive_day_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ScheduleInactiveDayRequest extends ScheduleInactiveDayRequest {
  @override
  final Date inactiveDate;

  factory _$ScheduleInactiveDayRequest(
          [void Function(ScheduleInactiveDayRequestBuilder)? updates]) =>
      (new ScheduleInactiveDayRequestBuilder()..update(updates))._build();

  _$ScheduleInactiveDayRequest._({required this.inactiveDate}) : super._() {
    BuiltValueNullFieldError.checkNotNull(
        inactiveDate, r'ScheduleInactiveDayRequest', 'inactiveDate');
  }

  @override
  ScheduleInactiveDayRequest rebuild(
          void Function(ScheduleInactiveDayRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ScheduleInactiveDayRequestBuilder toBuilder() =>
      new ScheduleInactiveDayRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ScheduleInactiveDayRequest &&
        inactiveDate == other.inactiveDate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, inactiveDate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ScheduleInactiveDayRequest')
          ..add('inactiveDate', inactiveDate))
        .toString();
  }
}

class ScheduleInactiveDayRequestBuilder
    implements
        Builder<ScheduleInactiveDayRequest, ScheduleInactiveDayRequestBuilder> {
  _$ScheduleInactiveDayRequest? _$v;

  Date? _inactiveDate;
  Date? get inactiveDate => _$this._inactiveDate;
  set inactiveDate(Date? inactiveDate) => _$this._inactiveDate = inactiveDate;

  ScheduleInactiveDayRequestBuilder() {
    ScheduleInactiveDayRequest._defaults(this);
  }

  ScheduleInactiveDayRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _inactiveDate = $v.inactiveDate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ScheduleInactiveDayRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$ScheduleInactiveDayRequest;
  }

  @override
  void update(void Function(ScheduleInactiveDayRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ScheduleInactiveDayRequest build() => _build();

  _$ScheduleInactiveDayRequest _build() {
    final _$result = _$v ??
        new _$ScheduleInactiveDayRequest._(
            inactiveDate: BuiltValueNullFieldError.checkNotNull(
                inactiveDate, r'ScheduleInactiveDayRequest', 'inactiveDate'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
