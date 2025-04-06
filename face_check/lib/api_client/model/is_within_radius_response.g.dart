// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'is_within_radius_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$IsWithinRadiusResponse extends IsWithinRadiusResponse {
  @override
  final int? worksiteId;
  @override
  final double? providedLatitude;
  @override
  final double? providedLongitude;
  @override
  final double? actualLatitude;
  @override
  final double? actualLongitude;
  @override
  final double? allowedRadius;
  @override
  final bool? withinRadius;

  factory _$IsWithinRadiusResponse(
          [void Function(IsWithinRadiusResponseBuilder)? updates]) =>
      (new IsWithinRadiusResponseBuilder()..update(updates))._build();

  _$IsWithinRadiusResponse._(
      {this.worksiteId,
      this.providedLatitude,
      this.providedLongitude,
      this.actualLatitude,
      this.actualLongitude,
      this.allowedRadius,
      this.withinRadius})
      : super._();

  @override
  IsWithinRadiusResponse rebuild(
          void Function(IsWithinRadiusResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  IsWithinRadiusResponseBuilder toBuilder() =>
      new IsWithinRadiusResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is IsWithinRadiusResponse &&
        worksiteId == other.worksiteId &&
        providedLatitude == other.providedLatitude &&
        providedLongitude == other.providedLongitude &&
        actualLatitude == other.actualLatitude &&
        actualLongitude == other.actualLongitude &&
        allowedRadius == other.allowedRadius &&
        withinRadius == other.withinRadius;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, worksiteId.hashCode);
    _$hash = $jc(_$hash, providedLatitude.hashCode);
    _$hash = $jc(_$hash, providedLongitude.hashCode);
    _$hash = $jc(_$hash, actualLatitude.hashCode);
    _$hash = $jc(_$hash, actualLongitude.hashCode);
    _$hash = $jc(_$hash, allowedRadius.hashCode);
    _$hash = $jc(_$hash, withinRadius.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'IsWithinRadiusResponse')
          ..add('worksiteId', worksiteId)
          ..add('providedLatitude', providedLatitude)
          ..add('providedLongitude', providedLongitude)
          ..add('actualLatitude', actualLatitude)
          ..add('actualLongitude', actualLongitude)
          ..add('allowedRadius', allowedRadius)
          ..add('withinRadius', withinRadius))
        .toString();
  }
}

class IsWithinRadiusResponseBuilder
    implements Builder<IsWithinRadiusResponse, IsWithinRadiusResponseBuilder> {
  _$IsWithinRadiusResponse? _$v;

  int? _worksiteId;
  int? get worksiteId => _$this._worksiteId;
  set worksiteId(int? worksiteId) => _$this._worksiteId = worksiteId;

  double? _providedLatitude;
  double? get providedLatitude => _$this._providedLatitude;
  set providedLatitude(double? providedLatitude) =>
      _$this._providedLatitude = providedLatitude;

  double? _providedLongitude;
  double? get providedLongitude => _$this._providedLongitude;
  set providedLongitude(double? providedLongitude) =>
      _$this._providedLongitude = providedLongitude;

  double? _actualLatitude;
  double? get actualLatitude => _$this._actualLatitude;
  set actualLatitude(double? actualLatitude) =>
      _$this._actualLatitude = actualLatitude;

  double? _actualLongitude;
  double? get actualLongitude => _$this._actualLongitude;
  set actualLongitude(double? actualLongitude) =>
      _$this._actualLongitude = actualLongitude;

  double? _allowedRadius;
  double? get allowedRadius => _$this._allowedRadius;
  set allowedRadius(double? allowedRadius) =>
      _$this._allowedRadius = allowedRadius;

  bool? _withinRadius;
  bool? get withinRadius => _$this._withinRadius;
  set withinRadius(bool? withinRadius) => _$this._withinRadius = withinRadius;

  IsWithinRadiusResponseBuilder() {
    IsWithinRadiusResponse._defaults(this);
  }

  IsWithinRadiusResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _worksiteId = $v.worksiteId;
      _providedLatitude = $v.providedLatitude;
      _providedLongitude = $v.providedLongitude;
      _actualLatitude = $v.actualLatitude;
      _actualLongitude = $v.actualLongitude;
      _allowedRadius = $v.allowedRadius;
      _withinRadius = $v.withinRadius;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(IsWithinRadiusResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$IsWithinRadiusResponse;
  }

  @override
  void update(void Function(IsWithinRadiusResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  IsWithinRadiusResponse build() => _build();

  _$IsWithinRadiusResponse _build() {
    final _$result = _$v ??
        new _$IsWithinRadiusResponse._(
            worksiteId: worksiteId,
            providedLatitude: providedLatitude,
            providedLongitude: providedLongitude,
            actualLatitude: actualLatitude,
            actualLongitude: actualLongitude,
            allowedRadius: allowedRadius,
            withinRadius: withinRadius);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
