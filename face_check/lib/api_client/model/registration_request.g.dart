// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'registration_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RegistrationRequestGenderEnum _$registrationRequestGenderEnum_MALE =
    const RegistrationRequestGenderEnum._('MALE');
const RegistrationRequestGenderEnum _$registrationRequestGenderEnum_FEMALE =
    const RegistrationRequestGenderEnum._('FEMALE');
const RegistrationRequestGenderEnum _$registrationRequestGenderEnum_OTHER =
    const RegistrationRequestGenderEnum._('OTHER');

RegistrationRequestGenderEnum _$registrationRequestGenderEnumValueOf(
    String name) {
  switch (name) {
    case 'MALE':
      return _$registrationRequestGenderEnum_MALE;
    case 'FEMALE':
      return _$registrationRequestGenderEnum_FEMALE;
    case 'OTHER':
      return _$registrationRequestGenderEnum_OTHER;
    default:
      throw new ArgumentError(name);
  }
}

final BuiltSet<RegistrationRequestGenderEnum>
    _$registrationRequestGenderEnumValues = new BuiltSet<
        RegistrationRequestGenderEnum>(const <RegistrationRequestGenderEnum>[
  _$registrationRequestGenderEnum_MALE,
  _$registrationRequestGenderEnum_FEMALE,
  _$registrationRequestGenderEnum_OTHER,
]);

Serializer<RegistrationRequestGenderEnum>
    _$registrationRequestGenderEnumSerializer =
    new _$RegistrationRequestGenderEnumSerializer();

class _$RegistrationRequestGenderEnumSerializer
    implements PrimitiveSerializer<RegistrationRequestGenderEnum> {
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
  final Iterable<Type> types = const <Type>[RegistrationRequestGenderEnum];
  @override
  final String wireName = 'RegistrationRequestGenderEnum';

  @override
  Object serialize(
          Serializers serializers, RegistrationRequestGenderEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RegistrationRequestGenderEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RegistrationRequestGenderEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RegistrationRequest extends RegistrationRequest {
  @override
  final String firstName;
  @override
  final String lastName;
  @override
  final String homeAddress;
  @override
  final Date? dateOfBirth;
  @override
  final String companyName;
  @override
  final String companyAddress;
  @override
  final RegistrationRequestGenderEnum gender;
  @override
  final String phoneNumber;
  @override
  final String email;
  @override
  final String password;
  @override
  final num? ssnWORKER;

  factory _$RegistrationRequest(
          [void Function(RegistrationRequestBuilder)? updates]) =>
      (new RegistrationRequestBuilder()..update(updates))._build();

  _$RegistrationRequest._(
      {required this.firstName,
      required this.lastName,
      required this.homeAddress,
      this.dateOfBirth,
      required this.companyName,
      required this.companyAddress,
      required this.gender,
      required this.phoneNumber,
      required this.email,
      required this.password,
      this.ssnWORKER})
      : super._() {
    BuiltValueNullFieldError.checkNotNull(
        firstName, r'RegistrationRequest', 'firstName');
    BuiltValueNullFieldError.checkNotNull(
        lastName, r'RegistrationRequest', 'lastName');
    BuiltValueNullFieldError.checkNotNull(
        homeAddress, r'RegistrationRequest', 'homeAddress');
    BuiltValueNullFieldError.checkNotNull(
        companyName, r'RegistrationRequest', 'companyName');
    BuiltValueNullFieldError.checkNotNull(
        companyAddress, r'RegistrationRequest', 'companyAddress');
    BuiltValueNullFieldError.checkNotNull(
        gender, r'RegistrationRequest', 'gender');
    BuiltValueNullFieldError.checkNotNull(
        phoneNumber, r'RegistrationRequest', 'phoneNumber');
    BuiltValueNullFieldError.checkNotNull(
        email, r'RegistrationRequest', 'email');
    BuiltValueNullFieldError.checkNotNull(
        password, r'RegistrationRequest', 'password');
  }

  @override
  RegistrationRequest rebuild(
          void Function(RegistrationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RegistrationRequestBuilder toBuilder() =>
      new RegistrationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RegistrationRequest &&
        firstName == other.firstName &&
        lastName == other.lastName &&
        homeAddress == other.homeAddress &&
        dateOfBirth == other.dateOfBirth &&
        companyName == other.companyName &&
        companyAddress == other.companyAddress &&
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
    _$hash = $jc(_$hash, companyName.hashCode);
    _$hash = $jc(_$hash, companyAddress.hashCode);
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
    return (newBuiltValueToStringHelper(r'RegistrationRequest')
          ..add('firstName', firstName)
          ..add('lastName', lastName)
          ..add('homeAddress', homeAddress)
          ..add('dateOfBirth', dateOfBirth)
          ..add('companyName', companyName)
          ..add('companyAddress', companyAddress)
          ..add('gender', gender)
          ..add('phoneNumber', phoneNumber)
          ..add('email', email)
          ..add('password', password)
          ..add('ssnWORKER', ssnWORKER))
        .toString();
  }
}

class RegistrationRequestBuilder
    implements Builder<RegistrationRequest, RegistrationRequestBuilder> {
  _$RegistrationRequest? _$v;

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

  String? _companyName;
  String? get companyName => _$this._companyName;
  set companyName(String? companyName) => _$this._companyName = companyName;

  String? _companyAddress;
  String? get companyAddress => _$this._companyAddress;
  set companyAddress(String? companyAddress) =>
      _$this._companyAddress = companyAddress;

  RegistrationRequestGenderEnum? _gender;
  RegistrationRequestGenderEnum? get gender => _$this._gender;
  set gender(RegistrationRequestGenderEnum? gender) => _$this._gender = gender;

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

  RegistrationRequestBuilder() {
    RegistrationRequest._defaults(this);
  }

  RegistrationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _firstName = $v.firstName;
      _lastName = $v.lastName;
      _homeAddress = $v.homeAddress;
      _dateOfBirth = $v.dateOfBirth;
      _companyName = $v.companyName;
      _companyAddress = $v.companyAddress;
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
  void replace(RegistrationRequest other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$RegistrationRequest;
  }

  @override
  void update(void Function(RegistrationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RegistrationRequest build() => _build();

  _$RegistrationRequest _build() {
    final _$result = _$v ??
        new _$RegistrationRequest._(
            firstName: BuiltValueNullFieldError.checkNotNull(
                firstName, r'RegistrationRequest', 'firstName'),
            lastName: BuiltValueNullFieldError.checkNotNull(
                lastName, r'RegistrationRequest', 'lastName'),
            homeAddress: BuiltValueNullFieldError.checkNotNull(
                homeAddress, r'RegistrationRequest', 'homeAddress'),
            dateOfBirth: dateOfBirth,
            companyName: BuiltValueNullFieldError.checkNotNull(
                companyName, r'RegistrationRequest', 'companyName'),
            companyAddress: BuiltValueNullFieldError.checkNotNull(
                companyAddress, r'RegistrationRequest', 'companyAddress'),
            gender: BuiltValueNullFieldError.checkNotNull(
                gender, r'RegistrationRequest', 'gender'),
            phoneNumber: BuiltValueNullFieldError.checkNotNull(
                phoneNumber, r'RegistrationRequest', 'phoneNumber'),
            email: BuiltValueNullFieldError.checkNotNull(
                email, r'RegistrationRequest', 'email'),
            password:
                BuiltValueNullFieldError.checkNotNull(password, r'RegistrationRequest', 'password'),
            ssnWORKER: ssnWORKER);
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
