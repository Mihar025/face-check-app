//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_import

import 'package:one_of_serializer/any_of_serializer.dart';
import 'package:one_of_serializer/one_of_serializer.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/json_object.dart';
import 'package:built_value/serializer.dart';
import 'package:built_value/standard_json_plugin.dart';
import 'package:built_value/iso_8601_date_time_serializer.dart';


import 'date_serializer.dart';
import 'model/authentication_request.dart';
import 'model/authentication_response.dart';
import 'model/can_punch_out_request_work_site.dart';
import 'model/change_punch_in_for_worker_response.dart';
import 'model/change_punch_in_request.dart';
import 'model/change_punch_out_for_worker_response.dart';
import 'model/change_punch_out_request.dart';
import 'model/company_income_per_month_request.dart';
import 'model/company_income_per_month_response.dart';
import 'model/company_registration_request.dart';
import 'model/company_tax_calculation_request.dart';
import 'model/company_tax_calculation_response.dart';
import 'model/company_updating_request.dart';
import 'model/company_updating_response.dart';
import 'model/daily_schedule_response.dart';
import 'model/date.dart';
import 'model/employee_raise_hour_rate_request.dart';
import 'model/employee_salary_response.dart';
import 'model/is_within_radius_request.dart';
import 'model/is_within_radius_response.dart';
import 'model/local_time.dart';
import 'model/page_response_related_user_in_company_response.dart';
import 'model/page_response_work_site_closed_days_response.dart';
import 'model/page_response_work_site_response.dart';
import 'model/page_response_worker_currently_working_in_work_site.dart';
import 'model/payment_request.dart';
import 'model/punch_in_request.dart';
import 'model/punch_in_response.dart';
import 'model/punch_out_request.dart';
import 'model/punch_out_response.dart';
import 'model/registration_admin_request.dart';
import 'model/registration_request.dart';
import 'model/related_user_in_company_response.dart';
import 'model/schedule_inactive_day_request.dart';
import 'model/schedule_inactive_day_response.dart';
import 'model/set_new_custom_radius_for_worker_in_special_work_site_response.dart';
import 'model/set_new_custom_radius_request.dart';
import 'model/set_new_custom_radius_response.dart';
import 'model/settings_email_request.dart';
import 'model/settings_email_response.dart';
import 'model/settings_password_request.dart';
import 'model/settings_phone_number_request.dart';
import 'model/settings_phone_number_response.dart';
import 'model/update_name_request.dart';
import 'model/update_status_work_site_request.dart';
import 'model/update_work_site_address.dart';
import 'model/user_email_response.dart';
import 'model/user_full_contact_information.dart';
import 'model/user_full_name_response.dart';
import 'model/user_home_address_response.dart';
import 'model/user_phone_number_response.dart';
import 'model/weekly_schedule_response.dart';
import 'model/work_scheduler_response.dart';
import 'model/work_site_closed_days_response.dart';
import 'model/work_site_request.dart';
import 'model/work_site_response.dart';
import 'model/work_site_update_address_response.dart';
import 'model/work_site_update_location_request.dart';
import 'model/work_site_update_location_response.dart';
import 'model/work_site_update_name_response.dart';
import 'model/work_site_update_working_hours_request.dart';
import 'model/work_site_update_working_hours_response.dart';
import 'model/worker_currently_working_in_work_site.dart';
import 'model/worker_hour_response.dart';
import 'model/worker_set_schedule_request.dart';
part 'serializers.g.dart';

@SerializersFor([
  AuthenticationRequest,
  AuthenticationResponse,
  CanPunchOutRequestWorkSite,
  ChangePunchInForWorkerResponse,
  ChangePunchInRequest,
  ChangePunchOutForWorkerResponse,
  ChangePunchOutRequest,
  CompanyIncomePerMonthRequest,
  CompanyIncomePerMonthResponse,
  CompanyRegistrationRequest,
  CompanyTaxCalculationRequest,
  CompanyTaxCalculationResponse,
  CompanyUpdatingRequest,
  CompanyUpdatingResponse,
  DailyScheduleResponse,
  EmployeeRaiseHourRateRequest,
  EmployeeSalaryResponse,
  IsWithinRadiusRequest,
  IsWithinRadiusResponse,
  LocalTime,
  PageResponseRelatedUserInCompanyResponse,
  PageResponseWorkSiteClosedDaysResponse,
  PageResponseWorkSiteResponse,
  PageResponseWorkerCurrentlyWorkingInWorkSite,
  PaymentRequest,
  PunchInRequest,
  PunchInResponse,
  PunchOutRequest,
  PunchOutResponse,
  RegistrationAdminRequest,
  RegistrationRequest,
  RelatedUserInCompanyResponse,
  ScheduleInactiveDayRequest,
  ScheduleInactiveDayResponse,
  SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse,
  SetNewCustomRadiusRequest,
  SetNewCustomRadiusResponse,
  UpdateNameRequest,
  UpdateStatusWorkSiteRequest,
  UpdateWorkSiteAddress,
  UserEmailResponse,
  UserFullContactInformation,
  UserFullNameResponse,
  UserHomeAddressResponse,
  UserPhoneNumberResponse,
  WeeklyScheduleResponse,
  WorkSchedulerResponse,
  WorkSiteClosedDaysResponse,
  WorkSiteRequest,
  WorkSiteResponse,
  WorkSiteUpdateAddressResponse,
  WorkSiteUpdateLocationRequest,
  WorkSiteUpdateLocationResponse,
  WorkSiteUpdateNameResponse,
  WorkSiteUpdateWorkingHoursRequest,
  WorkSiteUpdateWorkingHoursResponse,
  WorkerCurrentlyWorkingInWorkSite,
  WorkerHourResponse,
  WorkerSetScheduleRequest,
  SettingsEmailRequest,
  SettingsEmailResponse,
  SettingsPasswordRequest,
  SettingsPhoneNumberRequest,
  SettingsPhoneNumberResponse,
])
Serializers serializers = (_$serializers.toBuilder()
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(EmployeeSalaryResponse)]),
        () => ListBuilder<EmployeeSalaryResponse>(),
      )
      ..addBuilderFactory(
        const FullType(BuiltList, [FullType(String)]),
        () => ListBuilder<String>(),
      )
      ..add(const OneOfSerializer())
      ..add(const AnyOfSerializer())
      ..add(const DateSerializer())
      ..add(Iso8601DateTimeSerializer()))
    .build();

Serializers standardSerializers =
    (serializers.toBuilder()..addPlugin(StandardJsonPlugin())).build();
