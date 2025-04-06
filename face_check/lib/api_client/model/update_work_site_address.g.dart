// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_work_site_address.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateWorkSiteAddress extends UpdateWorkSiteAddress {
  @override
  final String address;

  factory _$UpdateWorkSiteAddress(
          [void Function(UpdateWorkSiteAddressBuilder)? updates]) =>
      (new UpdateWorkSiteAddressBuilder()..update(updates))._build();

  _$UpdateWorkSiteAddress._({required this.address}) : super._() {
    BuiltValueNullFieldError.checkNotNull(
        address, r'UpdateWorkSiteAddress', 'address');
  }

  @override
  UpdateWorkSiteAddress rebuild(
          void Function(UpdateWorkSiteAddressBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateWorkSiteAddressBuilder toBuilder() =>
      new UpdateWorkSiteAddressBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateWorkSiteAddress && address == other.address;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, address.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateWorkSiteAddress')
          ..add('address', address))
        .toString();
  }
}

class UpdateWorkSiteAddressBuilder
    implements Builder<UpdateWorkSiteAddress, UpdateWorkSiteAddressBuilder> {
  _$UpdateWorkSiteAddress? _$v;

  String? _address;
  String? get address => _$this._address;
  set address(String? address) => _$this._address = address;

  UpdateWorkSiteAddressBuilder() {
    UpdateWorkSiteAddress._defaults(this);
  }

  UpdateWorkSiteAddressBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _address = $v.address;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateWorkSiteAddress other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$UpdateWorkSiteAddress;
  }

  @override
  void update(void Function(UpdateWorkSiteAddressBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateWorkSiteAddress build() => _build();

  _$UpdateWorkSiteAddress _build() {
    final _$result = _$v ??
        new _$UpdateWorkSiteAddress._(
            address: BuiltValueNullFieldError.checkNotNull(
                address, r'UpdateWorkSiteAddress', 'address'));
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
