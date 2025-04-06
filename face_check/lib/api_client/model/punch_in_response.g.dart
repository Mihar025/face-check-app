// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'punch_in_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PunchInResponse extends PunchInResponse {
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
  final String? checkInPhotoUrl;
  @override
  final double? checkInLatitude;
  @override
  final double? checkInLongitude;
  @override
  final String? workSiteAddress;
  @override
  final bool? isSuccessful;
  @override
  final String? message;

  factory _$PunchInResponse([void Function(PunchInResponseBuilder)? updates]) =>
      (new PunchInResponseBuilder()..update(updates))._build();

  _$PunchInResponse._(
      {this.workerId,
      this.workSiteId,
      this.workSiteName,
      this.workerFullName,
      this.checkInTime,
      this.formattedCheckInTime,
      this.checkInPhotoUrl,
      this.checkInLatitude,
      this.checkInLongitude,
      this.workSiteAddress,
      this.isSuccessful,
      this.message})
      : super._();

  @override
  PunchInResponse rebuild(void Function(PunchInResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PunchInResponseBuilder toBuilder() =>
      new PunchInResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PunchInResponse &&
        workerId == other.workerId &&
        workSiteId == other.workSiteId &&
        workSiteName == other.workSiteName &&
        workerFullName == other.workerFullName &&
        checkInTime == other.checkInTime &&
        formattedCheckInTime == other.formattedCheckInTime &&
        checkInPhotoUrl == other.checkInPhotoUrl &&
        checkInLatitude == other.checkInLatitude &&
        checkInLongitude == other.checkInLongitude &&
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
    _$hash = $jc(_$hash, checkInPhotoUrl.hashCode);
    _$hash = $jc(_$hash, checkInLatitude.hashCode);
    _$hash = $jc(_$hash, checkInLongitude.hashCode);
    _$hash = $jc(_$hash, workSiteAddress.hashCode);
    _$hash = $jc(_$hash, isSuccessful.hashCode);
    _$hash = $jc(_$hash, message.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PunchInResponse')
          ..add('workerId', workerId)
          ..add('workSiteId', workSiteId)
          ..add('workSiteName', workSiteName)
          ..add('workerFullName', workerFullName)
          ..add('checkInTime', checkInTime)
          ..add('formattedCheckInTime', formattedCheckInTime)
          ..add('checkInPhotoUrl', checkInPhotoUrl)
          ..add('checkInLatitude', checkInLatitude)
          ..add('checkInLongitude', checkInLongitude)
          ..add('workSiteAddress', workSiteAddress)
          ..add('isSuccessful', isSuccessful)
          ..add('message', message))
        .toString();
  }
}

class PunchInResponseBuilder
    implements Builder<PunchInResponse, PunchInResponseBuilder> {
  _$PunchInResponse? _$v;

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

  String? _checkInPhotoUrl;
  String? get checkInPhotoUrl => _$this._checkInPhotoUrl;
  set checkInPhotoUrl(String? checkInPhotoUrl) =>
      _$this._checkInPhotoUrl = checkInPhotoUrl;

  double? _checkInLatitude;
  double? get checkInLatitude => _$this._checkInLatitude;
  set checkInLatitude(double? checkInLatitude) =>
      _$this._checkInLatitude = checkInLatitude;

  double? _checkInLongitude;
  double? get checkInLongitude => _$this._checkInLongitude;
  set checkInLongitude(double? checkInLongitude) =>
      _$this._checkInLongitude = checkInLongitude;

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

  PunchInResponseBuilder() {
    PunchInResponse._defaults(this);
  }

  PunchInResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _workerId = $v.workerId;
      _workSiteId = $v.workSiteId;
      _workSiteName = $v.workSiteName;
      _workerFullName = $v.workerFullName;
      _checkInTime = $v.checkInTime;
      _formattedCheckInTime = $v.formattedCheckInTime;
      _checkInPhotoUrl = $v.checkInPhotoUrl;
      _checkInLatitude = $v.checkInLatitude;
      _checkInLongitude = $v.checkInLongitude;
      _workSiteAddress = $v.workSiteAddress;
      _isSuccessful = $v.isSuccessful;
      _message = $v.message;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PunchInResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$PunchInResponse;
  }

  @override
  void update(void Function(PunchInResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PunchInResponse build() => _build();

  _$PunchInResponse _build() {
    final _$result = _$v ??
        new _$PunchInResponse._(
            workerId: workerId,
            workSiteId: workSiteId,
            workSiteName: workSiteName,
            workerFullName: workerFullName,
            checkInTime: checkInTime,
            formattedCheckInTime: formattedCheckInTime,
            checkInPhotoUrl: checkInPhotoUrl,
            checkInLatitude: checkInLatitude,
            checkInLongitude: checkInLongitude,
            workSiteAddress: workSiteAddress,
            isSuccessful: isSuccessful,
            message: message);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
