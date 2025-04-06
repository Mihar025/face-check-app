// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'punch_out_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PunchOutResponse extends PunchOutResponse {
  @override
  final int? workerId;
  @override
  final int? workSiteId;
  @override
  final String? workSiteName;
  @override
  final String? workerFullName;
  @override
  final DateTime? checkInTime;
  @override
  final String? formattedCheckInTime;
  @override
  final DateTime? checkOutTime;
  @override
  final String? formattedCheckOutTime;
  @override
  final String? checkOutPhotoUrl;
  @override
  final double? checkOutLatitude;
  @override
  final double? checkOutLongitude;
  @override
  final double? hoursWorked;
  @override
  final double? overtimeHours;
  @override
  final String? workSiteAddress;
  @override
  final bool? isSuccessful;
  @override
  final String? message;

  factory _$PunchOutResponse(
          [void Function(PunchOutResponseBuilder)? updates]) =>
      (new PunchOutResponseBuilder()..update(updates))._build();

  _$PunchOutResponse._(
      {this.workerId,
      this.workSiteId,
      this.workSiteName,
      this.workerFullName,
      this.checkInTime,
      this.formattedCheckInTime,
      this.checkOutTime,
      this.formattedCheckOutTime,
      this.checkOutPhotoUrl,
      this.checkOutLatitude,
      this.checkOutLongitude,
      this.hoursWorked,
      this.overtimeHours,
      this.workSiteAddress,
      this.isSuccessful,
      this.message})
      : super._();

  @override
  PunchOutResponse rebuild(void Function(PunchOutResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PunchOutResponseBuilder toBuilder() =>
      new PunchOutResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PunchOutResponse &&
        workerId == other.workerId &&
        workSiteId == other.workSiteId &&
        workSiteName == other.workSiteName &&
        workerFullName == other.workerFullName &&
        checkInTime == other.checkInTime &&
        formattedCheckInTime == other.formattedCheckInTime &&
        checkOutTime == other.checkOutTime &&
        formattedCheckOutTime == other.formattedCheckOutTime &&
        checkOutPhotoUrl == other.checkOutPhotoUrl &&
        checkOutLatitude == other.checkOutLatitude &&
        checkOutLongitude == other.checkOutLongitude &&
        hoursWorked == other.hoursWorked &&
        overtimeHours == other.overtimeHours &&
        workSiteAddress == other.workSiteAddress &&
        isSuccessful == other.isSuccessful &&
        message == other.message;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, workerId.hashCode);
    _$hash = $jc(_$hash, workSiteId.hashCode);
    _$hash = $jc(_$hash, workSiteName.hashCode);
    _$hash = $jc(_$hash, workerFullName.hashCode);
    _$hash = $jc(_$hash, checkInTime.hashCode);
    _$hash = $jc(_$hash, formattedCheckInTime.hashCode);
    _$hash = $jc(_$hash, checkOutTime.hashCode);
    _$hash = $jc(_$hash, formattedCheckOutTime.hashCode);
    _$hash = $jc(_$hash, checkOutPhotoUrl.hashCode);
    _$hash = $jc(_$hash, checkOutLatitude.hashCode);
    _$hash = $jc(_$hash, checkOutLongitude.hashCode);
    _$hash = $jc(_$hash, hoursWorked.hashCode);
    _$hash = $jc(_$hash, overtimeHours.hashCode);
    _$hash = $jc(_$hash, workSiteAddress.hashCode);
    _$hash = $jc(_$hash, isSuccessful.hashCode);
    _$hash = $jc(_$hash, message.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PunchOutResponse')
          ..add('workerId', workerId)
          ..add('workSiteId', workSiteId)
          ..add('workSiteName', workSiteName)
          ..add('workerFullName', workerFullName)
          ..add('checkInTime', checkInTime)
          ..add('formattedCheckInTime', formattedCheckInTime)
          ..add('checkOutTime', checkOutTime)
          ..add('formattedCheckOutTime', formattedCheckOutTime)
          ..add('checkOutPhotoUrl', checkOutPhotoUrl)
          ..add('checkOutLatitude', checkOutLatitude)
          ..add('checkOutLongitude', checkOutLongitude)
          ..add('hoursWorked', hoursWorked)
          ..add('overtimeHours', overtimeHours)
          ..add('workSiteAddress', workSiteAddress)
          ..add('isSuccessful', isSuccessful)
          ..add('message', message))
        .toString();
  }
}

class PunchOutResponseBuilder
    implements Builder<PunchOutResponse, PunchOutResponseBuilder> {
  _$PunchOutResponse? _$v;

  int? _workerId;
  int? get workerId => _$this._workerId;
  set workerId(int? workerId) => _$this._workerId = workerId;

  int? _workSiteId;
  int? get workSiteId => _$this._workSiteId;
  set workSiteId(int? workSiteId) => _$this._workSiteId = workSiteId;

  String? _workSiteName;
  String? get workSiteName => _$this._workSiteName;
  set workSiteName(String? workSiteName) => _$this._workSiteName = workSiteName;

  String? _workerFullName;
  String? get workerFullName => _$this._workerFullName;
  set workerFullName(String? workerFullName) =>
      _$this._workerFullName = workerFullName;

  DateTime? _checkInTime;
  DateTime? get checkInTime => _$this._checkInTime;
  set checkInTime(DateTime? checkInTime) => _$this._checkInTime = checkInTime;

  String? _formattedCheckInTime;
  String? get formattedCheckInTime => _$this._formattedCheckInTime;
  set formattedCheckInTime(String? formattedCheckInTime) =>
      _$this._formattedCheckInTime = formattedCheckInTime;

  DateTime? _checkOutTime;
  DateTime? get checkOutTime => _$this._checkOutTime;
  set checkOutTime(DateTime? checkOutTime) =>
      _$this._checkOutTime = checkOutTime;

  String? _formattedCheckOutTime;
  String? get formattedCheckOutTime => _$this._formattedCheckOutTime;
  set formattedCheckOutTime(String? formattedCheckOutTime) =>
      _$this._formattedCheckOutTime = formattedCheckOutTime;

  String? _checkOutPhotoUrl;
  String? get checkOutPhotoUrl => _$this._checkOutPhotoUrl;
  set checkOutPhotoUrl(String? checkOutPhotoUrl) =>
      _$this._checkOutPhotoUrl = checkOutPhotoUrl;

  double? _checkOutLatitude;
  double? get checkOutLatitude => _$this._checkOutLatitude;
  set checkOutLatitude(double? checkOutLatitude) =>
      _$this._checkOutLatitude = checkOutLatitude;

  double? _checkOutLongitude;
  double? get checkOutLongitude => _$this._checkOutLongitude;
  set checkOutLongitude(double? checkOutLongitude) =>
      _$this._checkOutLongitude = checkOutLongitude;

  double? _hoursWorked;
  double? get hoursWorked => _$this._hoursWorked;
  set hoursWorked(double? hoursWorked) => _$this._hoursWorked = hoursWorked;

  double? _overtimeHours;
  double? get overtimeHours => _$this._overtimeHours;
  set overtimeHours(double? overtimeHours) =>
      _$this._overtimeHours = overtimeHours;

  String? _workSiteAddress;
  String? get workSiteAddress => _$this._workSiteAddress;
  set workSiteAddress(String? workSiteAddress) =>
      _$this._workSiteAddress = workSiteAddress;

  bool? _isSuccessful;
  bool? get isSuccessful => _$this._isSuccessful;
  set isSuccessful(bool? isSuccessful) => _$this._isSuccessful = isSuccessful;

  String? _message;
  String? get message => _$this._message;
  set message(String? message) => _$this._message = message;

  PunchOutResponseBuilder() {
    PunchOutResponse._defaults(this);
  }

  PunchOutResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workerId = $v.workerId;
      _workSiteId = $v.workSiteId;
      _workSiteName = $v.workSiteName;
      _workerFullName = $v.workerFullName;
      _checkInTime = $v.checkInTime;
      _formattedCheckInTime = $v.formattedCheckInTime;
      _checkOutTime = $v.checkOutTime;
      _formattedCheckOutTime = $v.formattedCheckOutTime;
      _checkOutPhotoUrl = $v.checkOutPhotoUrl;
      _checkOutLatitude = $v.checkOutLatitude;
      _checkOutLongitude = $v.checkOutLongitude;
      _hoursWorked = $v.hoursWorked;
      _overtimeHours = $v.overtimeHours;
      _workSiteAddress = $v.workSiteAddress;
      _isSuccessful = $v.isSuccessful;
      _message = $v.message;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PunchOutResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$PunchOutResponse;
  }

  @override
  void update(void Function(PunchOutResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PunchOutResponse build() => _build();

  _$PunchOutResponse _build() {
    final _$result = _$v ??
        new _$PunchOutResponse._(
            workerId: workerId,
            workSiteId: workSiteId,
            workSiteName: workSiteName,
            workerFullName: workerFullName,
            checkInTime: checkInTime,
            formattedCheckInTime: formattedCheckInTime,
            checkOutTime: checkOutTime,
            formattedCheckOutTime: formattedCheckOutTime,
            checkOutPhotoUrl: checkOutPhotoUrl,
            checkOutLatitude: checkOutLatitude,
            checkOutLongitude: checkOutLongitude,
            hoursWorked: hoursWorked,
            overtimeHours: overtimeHours,
            workSiteAddress: workSiteAddress,
            isSuccessful: isSuccessful,
            message: message);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
