// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'set_new_custom_radius_for_worker_in_special_work_site_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse
    extends SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse {
  @override
  final int? workSiteId;
  @override
  final int? workerId;
  @override
  final String? firstName;
  @override
  final String? lastName;
  @override
  final String? companyName;
  @override
  final double? newRadius;

  factory _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse(
          [void Function(
                  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder)?
              updates]) =>
      (new SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder()
            ..update(updates))
          ._build();

  _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse._(
      {this.workSiteId,
      this.workerId,
      this.firstName,
      this.lastName,
      this.companyName,
      this.newRadius})
      : super._();

  @override
  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse rebuild(
          void Function(
                  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder toBuilder() =>
      new SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder()
        ..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse &&
        workSiteId == other.workSiteId &&
        workerId == other.workerId &&
        firstName == other.firstName &&
        lastName == other.lastName &&
        companyName == other.companyName &&
        newRadius == other.newRadius;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, workerId.hashCode);
    _$hash = $jc(_$hash, firstName.hashCode);
    _$hash = $jc(_$hash, lastName.hashCode);
    _$hash = $jc(_$hash, companyName.hashCode);
    _$hash = $jc(_$hash, newRadius.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(
            r'SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse')
          ..add('workSiteId', workSiteId)
          ..add('workerId', workerId)
          ..add('firstName', firstName)
          ..add('lastName', lastName)
          ..add('companyName', companyName)
          ..add('newRadius', newRadius))
        .toString();
  }
}

class SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder
    implements
        Builder<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse,
            SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder> {
  _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse? _$v;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  int? _workerId;
  int? get workerId => _$this._workerId;
  set workerId(int? workerId) => _$this._workerId = workerId;

  String? _firstName;
  String? get firstName => _$this._firstName;
  set firstName(String? firstName) => _$this._firstName = firstName;

  String? _lastName;
  String? get lastName => _$this._lastName;
  set lastName(String? lastName) => _$this._lastName = lastName;

  String? _companyName;
  String? get companyName => _$this._companyName;
  set companyName(String? companyName) => _$this._companyName = companyName;

  double? _newRadius;
  double? get newRadius => _$this._newRadius;
  set newRadius(double? newRadius) => _$this._newRadius = newRadius;

  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder() {
    SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse._defaults(this);
  }

  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workSiteId = $v.workSiteId;
      _workerId = $v.workerId;
      _firstName = $v.firstName;
      _lastName = $v.lastName;
      _companyName = $v.companyName;
      _newRadius = $v.newRadius;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse;
  }

  @override
  void update(
      void Function(
              SetNewCustomRadiusForWorkerInSpecialWorkSiteResponseBuilder)?
          updates) {
    if (updates != null) updates(this);
  }

  @override
  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse build() => _build();

  _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse _build() {
    final _$result = _$v ??
        new _$SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse._(
            workSiteId: workSiteId,
            workerId: workerId,
            firstName: firstName,
            lastName: lastName,
            companyName: companyName,
            newRadius: newRadius);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
