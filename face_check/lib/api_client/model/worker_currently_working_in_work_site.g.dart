// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'worker_currently_working_in_work_site.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$WorkerCurrentlyWorkingInWorkSite
    extends WorkerCurrentlyWorkingInWorkSite {
  @override
  final int? workerId;
  @override
  final int? workSiteId;
  @override
  final DateTime? punchedIn;
  @override
  final String? workerFullName;
  @override
  final String? workerPhoneNumber;
  @override
  final String? workSiteName;
  @override
  final String? workSiteAddress;

  factory _$WorkerCurrentlyWorkingInWorkSite(
          [void Function(WorkerCurrentlyWorkingInWorkSiteBuilder)? updates]) =>
      (new WorkerCurrentlyWorkingInWorkSiteBuilder()..update(updates))._build();

  _$WorkerCurrentlyWorkingInWorkSite._(
      {this.workerId,
      this.workSiteId,
      this.punchedIn,
      this.workerFullName,
      this.workerPhoneNumber,
      this.workSiteName,
      this.workSiteAddress})
      : super._();

  @override
  WorkerCurrentlyWorkingInWorkSite rebuild(
          void Function(WorkerCurrentlyWorkingInWorkSiteBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  WorkerCurrentlyWorkingInWorkSiteBuilder toBuilder() =>
      new WorkerCurrentlyWorkingInWorkSiteBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is WorkerCurrentlyWorkingInWorkSite &&
        workerId == other.workerId &&
        workSiteId == other.workSiteId &&
        punchedIn == other.punchedIn &&
        workerFullName == other.workerFullName &&
        workerPhoneNumber == other.workerPhoneNumber &&
        workSiteName == other.workSiteName &&
        workSiteAddress == other.workSiteAddress;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workerId.hashCode);
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, punchedIn.hashCode);
    _$hash = $jc(_$hash, workerFullName.hashCode);
    _$hash = $jc(_$hash, workerPhoneNumber.hashCode);
    _$hash = $jc(_$hash, workSiteName.hashCode);
    _$hash = $jc(_$hash, workSiteAddress.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'WorkerCurrentlyWorkingInWorkSite')
          ..add('workerId', workerId)
          ..add('workSiteId', workSiteId)
          ..add('punchedIn', punchedIn)
          ..add('workerFullName', workerFullName)
          ..add('workerPhoneNumber', workerPhoneNumber)
          ..add('workSiteName', workSiteName)
          ..add('workSiteAddress', workSiteAddress))
        .toString();
  }
}

class WorkerCurrentlyWorkingInWorkSiteBuilder
    implements
        Builder<WorkerCurrentlyWorkingInWorkSite,
            WorkerCurrentlyWorkingInWorkSiteBuilder> {
  _$WorkerCurrentlyWorkingInWorkSite? _$v;

  int? _workerId;
  int? get workerId => _$this._workerId;
  set workerId(int? workerId) => _$this._workerId = workerId;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  DateTime? _punchedIn;
  DateTime? get punchedIn => _$this._punchedIn;
  set punchedIn(DateTime? punchedIn) => _$this._punchedIn = punchedIn;

  String? _workerFullName;
  String? get workerFullName => _$this._workerFullName;
  set workerFullName(String? workerFullName) =>
      _$this._workerFullName = workerFullName;

  String? _workerPhoneNumber;
  String? get workerPhoneNumber => _$this._workerPhoneNumber;
  set workerPhoneNumber(String? workerPhoneNumber) =>
      _$this._workerPhoneNumber = workerPhoneNumber;

  String? _workSiteName;
  String? get workSiteName => _$this._workSiteName;
  set workSiteName(String? workSiteName) => _$this._workSiteName = workSiteName;

  String? _workSiteAddress;
  String? get workSiteAddress => _$this._workSiteAddress;
  set workSiteAddress(String? workSiteAddress) =>
      _$this._workSiteAddress = workSiteAddress;

  WorkerCurrentlyWorkingInWorkSiteBuilder() {
    WorkerCurrentlyWorkingInWorkSite._defaults(this);
  }

  WorkerCurrentlyWorkingInWorkSiteBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workerId = $v.workerId;
      _workSiteId = $v.workSiteId;
      _punchedIn = $v.punchedIn;
      _workerFullName = $v.workerFullName;
      _workerPhoneNumber = $v.workerPhoneNumber;
      _workSiteName = $v.workSiteName;
      _workSiteAddress = $v.workSiteAddress;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(WorkerCurrentlyWorkingInWorkSite other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$WorkerCurrentlyWorkingInWorkSite;
  }

  @override
  void update(void Function(WorkerCurrentlyWorkingInWorkSiteBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  WorkerCurrentlyWorkingInWorkSite build() => _build();

  _$WorkerCurrentlyWorkingInWorkSite _build() {
    final _$result = _$v ??
        new _$WorkerCurrentlyWorkingInWorkSite._(
            workerId: workerId,
            workSiteId: workSiteId,
            punchedIn: punchedIn,
            workerFullName: workerFullName,
            workerPhoneNumber: workerPhoneNumber,
            workSiteName: workSiteName,
            workSiteAddress: workSiteAddress);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
