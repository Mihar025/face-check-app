// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'registration_admin_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RegistrationAdminRequestGenderEnum
    _$registrationAdminRequestGenderEnum_MALE =
    const RegistrationAdminRequestGenderEnum._('MALE');
const RegistrationAdminRequestGenderEnum
    _$registrationAdminRequestGenderEnum_FEMALE =
    const RegistrationAdminRequestGenderEnum._('FEMALE');
const RegistrationAdminRequestGenderEnum
    _$registrationAdminRequestGenderEnum_OTHER =
    const RegistrationAdminRequestGenderEnum._('OTHER');

RegistrationAdminRequestGenderEnum _$registrationAdminRequestGenderEnumValueOf(
    String name) {
  switch (name) {
    case 'MALE':
      return _$registrationAdminRequestGenderEnum_MALE;
    case 'FEMALE':
      return _$registrationAdminRequestGenderEnum_FEMALE;
    case 'OTHER':
      return _$registrationAdminRequestGenderEnum_OTHER;
    default:
      throw new ArgumentError(name);
  }
}

final BuiltSet<RegistrationAdminRequestGenderEnum>
    _$registrationAdminRequestGenderEnumValues = new BuiltSet<
        RegistrationAdminRequestGenderEnum>(const <RegistrationAdminRequestGenderEnum>[
  _$registrationAdminRequestGenderEnum_MALE,
  _$registrationAdminRequestGenderEnum_FEMALE,
  _$registrationAdminRequestGenderEnum_OTHER,
]);

Serializer<RegistrationAdminRequestGenderEnum>
    _$registrationAdminRequestGenderEnumSerializer =
    new _$RegistrationAdminRequestGenderEnumSerializer();

class _$RegistrationAdminRequestGenderEnumSerializer
    implements PrimitiveSerializer<RegistrationAdminRequestGenderEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MALE': 'MALE',
    'FEMALE': 'FEMALE',
    'OTHER': 'OTHER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MALE': 'MALE',
    'FEMALE': 'FEMALE',
    'OTHER': 'OTHER',
  };

  @override
  final Iterable<Type> types = const <Type>[RegistrationAdminRequestGenderEnum];
  @override
  final String wireName = 'RegistrationAdminRequestGenderEnum';

  @override
  Object serialize(
          Serializers serializers, RegistrationAdminRequestGenderEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RegistrationAdminRequestGenderEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RegistrationAdminRequestGenderEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RegistrationAdminRequest extends RegistrationAdminRequest {
  @override
  final String firstName;
  @override
  final String lastName;
  @override
  final String homeAddress;
  @override
  final Date? dateOfBirth;
  @override
  final RegistrationAdminRequestGenderEnum gender;
  @override
  final String phoneNumber;
  @override
  final String email;
  @override
  final String password;
  @override
  final num? ssnWORKER;

  factory _$RegistrationAdminRequest(
          [void Function(RegistrationAdminRequestBuilder)? updates]) =>
      (new RegistrationAdminRequestBuilder()..update(updates))._build();

  _$RegistrationAdminRequest._(
      {required this.firstName,
      required this.lastName,
      required this.homeAddress,
      this.dateOfBirth,
      required this.gender,
      required this.phoneNumber,
      required this.email,
      required this.password,
      this.ssnWORKER})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        firstName, r'RegistrationAdminRequest', 'firstName');
    BuiltValueNullFieldError.checkNotNull(
        lastName, r'RegistrationAdminRequest', 'lastName');
    BuiltValueNullFieldError.checkNotNull(
        homeAddress, r'RegistrationAdminRequest', 'homeAddress');
    BuiltValueNullFieldError.checkNotNull(
        gender, r'RegistrationAdminRequest', 'gender');
    BuiltValueNullFieldError.checkNotNull(
        phoneNumber, r'RegistrationAdminRequest', 'phoneNumber');
    BuiltValueNullFieldError.checkNotNull(
        email, r'RegistrationAdminRequest', 'email');
    BuiltValueNullFieldError.checkNotNull(
        password, r'RegistrationAdminRequest', 'password');
  }

  @override
  RegistrationAdminRequest rebuild(
          void Function(RegistrationAdminRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RegistrationAdminRequestBuilder toBuilder() =>
      new RegistrationAdminRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RegistrationAdminRequest &&
        firstName == other.firstName &&
        lastName == other.lastName &&
        homeAddress == other.homeAddress &&
        dateOfBirth == other.dateOfBirth &&
        gender == other.gender &&
        phoneNumber == other.phoneNumber &&
        email == other.email &&
        password == other.password &&
        ssnWORKER == other.ssnWORKER;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, firstName.hashCode);
    _$hash = $jc(_$hash, lastName.hashCode);
    _$hash = $jc(_$hash, homeAddress.hashCode);
    _$hash = $jc(_$hash, dateOfBirth.hashCode);
    _$hash = $jc(_$hash, gender.hashCode);
    _$hash = $jc(_$hash, phoneNumber.hashCode);
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, password.hashCode);
    _$hash = $jc(_$hash, ssnWORKER.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RegistrationAdminRequest')
          ..add('firstName', firstName)
          ..add('lastName', lastName)
          ..add('homeAddress', homeAddress)
          ..add('dateOfBirth', dateOfBirth)
          ..add('gender', gender)
          ..add('phoneNumber', phoneNumber)
          ..add('email', email)
          ..add('password', password)
          ..add('ssnWORKER', ssnWORKER))
        .toString();
  }
}

class RegistrationAdminRequestBuilder
    implements
        Builder<RegistrationAdminRequest, RegistrationAdminRequestBuilder> {
  _$RegistrationAdminRequest? _$v;

  String? _firstName;
  String? get firstName => _$this._firstName;
  set firstName(String? firstName) => _$this._firstName = firstName;

  String? _lastName;
  String? get lastName => _$this._lastName;
  set lastName(String? lastName) => _$this._lastName = lastName;

  String? _homeAddress;
  String? get homeAddress => _$this._homeAddress;
  set homeAddress(String? homeAddress) => _$this._homeAddress = homeAddress;

  Date? _dateOfBirth;
  Date? get dateOfBirth => _$this._dateOfBirth;
  set dateOfBirth(Date? dateOfBirth) => _$this._dateOfBirth = dateOfBirth;

  RegistrationAdminRequestGenderEnum? _gender;
  RegistrationAdminRequestGenderEnum? get gender => _$this._gender;
  set gender(RegistrationAdminRequestGenderEnum? gender) =>
      _$this._gender = gender;

  String? _phoneNumber;
  String? get phoneNumber => _$this._phoneNumber;
  set phoneNumber(String? phoneNumber) => _$this._phoneNumber = phoneNumber;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  String? _password;
  String? get password => _$this._password;
  set password(String? password) => _$this._password = password;

  num? _ssnWORKER;
  num? get ssnWORKER => _$this._ssnWORKER;
  set ssnWORKER(num? ssnWORKER) => _$this._ssnWORKER = ssnWORKER;

  RegistrationAdminRequestBuilder() {
    RegistrationAdminRequest._defaults(this);
  }

  RegistrationAdminRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _firstName = $v.firstName;
      _lastName = $v.lastName;
      _homeAddress = $v.homeAddress;
      _dateOfBirth = $v.dateOfBirth;
      _gender = $v.gender;
      _phoneNumber = $v.phoneNumber;
      _email = $v.email;
      _password = $v.password;
      _ssnWORKER = $v.ssnWORKER;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RegistrationAdminRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$RegistrationAdminRequest;
  }

  @override
  void update(void Function(RegistrationAdminRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RegistrationAdminRequest build() => _build();

  _$RegistrationAdminRequest _build() {
    final _$result = _$v ??
        new _$RegistrationAdminRequest._(
            firstName: BuiltValueNullFieldError.checkNotNull(
                firstName, r'RegistrationAdminRequest', 'firstName'),
            lastName: BuiltValueNullFieldError.checkNotNull(
                lastName, r'RegistrationAdminRequest', 'lastName'),
            homeAddress: BuiltValueNullFieldError.checkNotNull(
                homeAddress, r'RegistrationAdminRequest', 'homeAddress'),
            dateOfBirth: dateOfBirth,
            gender: BuiltValueNullFieldError.checkNotNull(
                gender, r'RegistrationAdminRequest', 'gender'),
            phoneNumber: BuiltValueNullFieldError.checkNotNull(
                phoneNumber, r'RegistrationAdminRequest', 'phoneNumber'),
            email: BuiltValueNullFieldError.checkNotNull(
                email, r'RegistrationAdminRequest', 'email'),
            password: BuiltValueNullFieldError.checkNotNull(
                password, r'RegistrationAdminRequest', 'password'),
            ssnWORKER: ssnWORKER);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
