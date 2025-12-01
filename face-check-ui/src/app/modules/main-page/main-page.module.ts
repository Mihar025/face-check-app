// src/app/modules/main-page/main-page.module.ts
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MainPageRoutingModule } from './main-page-routing.module';
import { SharedModule } from '../shared/shared.module';
import { CodeInputModule } from 'angular-code-input';

// Pages / Auth
import { HomeComponent } from './pages/home/home.component';
import { SignInComponent } from './authentication/sign-in/sign-in.component';
import { AboutUsComponent } from './pages/about-us/about-us.component';
import { PricingComponent } from './pages/pricing/pricing.component';
import { RegisterComponent } from './authentication/register/register.component';
import { RegisterCompanyComponent } from './authentication/register-company/register-company.component';
import { VerificationCodeComponent } from './authentication/verification-code/verification-code.component';
import { ForgotPasswordComponent } from './authentication/forgot-password/forgot-password.component';

// Admin / Owner pages (ОСТАВЬ только те, что реально существуют)
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { CompanyInformationComponent } from './pages/admin-main-page/company-information/company-information.component';
import { ManageWorksitesComponent } from './pages/admin-main-page/manage-worksites/manage-worksites.component';
import { SettingsComponent } from './pages/admin-main-page/settings/settings.component';
import { ManageEmployeesComponent } from './pages/admin-main-page/manage-employees/manage-employees.component';
import { VerificationForEmployeeComponent } from './pages/admin-main-page/verification-for-employee/verification-for-employee.component';
import { FinancePageComponent } from './pages/admin-main-page/finance-page/finance-page.component';
import { LocTrackComponent } from './pages/admin-main-page/loc-track/loc-track.component';
import { EmployeeAttendanceComponent } from './pages/admin-main-page/employee-attendance/employee-attendance.component';
import { AttendenceTrackEmployeeAdminComponent } from './pages/admin-main-page/attendence-track-employee-admin/attendence-track-employee-admin.component';
import { StatForAttendenceComponent } from './pages/admin-main-page/stat-for-attendence/stat-for-attendence.component';

import { MainPageAppOwnerComponent } from './pages/appOwner/main-page-app-owner/main-page-app-owner.component';
import { CompaniesInfoAppOwnerComponent } from './pages/appOwner/companies-info-app-owner/companies-info-app-owner.component';
import { ManageWorksitesAppOwnerComponent } from './pages/appOwner/manage-worksites-app-owner/manage-worksites-app-owner.component';
import { ManageEmployeesAppOwnerComponent } from './pages/appOwner/manage-employees-app-owner/manage-employees-app-owner.component';
import { LocationTrackingAppOwnerComponent } from './pages/appOwner/location-tracking-app-owner/location-tracking-app-owner.component';
import { EmployeesAttendanceAppOwnerComponent } from './pages/appOwner/employees-attendance-app-owner/employees-attendance-app-owner.component';
import { SettingsAppOwnerComponent } from './pages/appOwner/settings-app-owner/settings-app-owner.component';
import { PayrollsAppOwnerComponent } from './pages/appOwner/payrolls-app-owner/payrolls-app-owner.component';
import { CallToCustomersAppOwnerComponent } from './pages/appOwner/call-to-customers-app-owner/call-to-customers-app-owner.component';

// Info pages (проверь, что файлы существуют)
import { PrivacyPolicyComponent } from './pages/privacy-policy/privacy-policy.component';
import { TermsOfServiceComponent } from './pages/terms-of-service/terms-of-service.component';
import { ScheduleConsultationComponent } from './pages/schedule-consultation/schedule-consultation.component';
import { ViewDemoComponent } from './pages/view-demo/view-demo.component';
import { RefundPolicyComponent } from './pages/refund-policy/refund-policy.component';
import {
  AttendenceTrackEmployeeAppOwnerComponent
} from "./pages/appOwner/attendence-track-employee-app-owner/attendence-track-employee-app-owner.component";
import { NotificationAdminPageComponent } from './pages/admin-main-page/notification-admin-page/notification-admin-page.component';
import { BillingPageComponent } from './pages/billing-page/billing-page.component';
import { BillingSuccessComponent } from './pages/billing-success/billing-success.component';
import { BillingCanceledComponent } from './pages/billing-canceled/billing-canceled.component';
import { BillingComponent } from './pages/billing/billing.component';

@NgModule({
  declarations: [
    HomeComponent,
    SignInComponent,
    AboutUsComponent,
    PricingComponent,
    RegisterComponent,
    AdminPageComponent,
    VerificationCodeComponent,
    RegisterCompanyComponent,
    CompanyInformationComponent,
    ManageWorksitesComponent,
    SettingsComponent,
    ManageEmployeesComponent,
    VerificationForEmployeeComponent,
    ForgotPasswordComponent,
    PrivacyPolicyComponent,
    TermsOfServiceComponent,
    ScheduleConsultationComponent,
    ViewDemoComponent,
    RefundPolicyComponent,
    FinancePageComponent,
    LocTrackComponent,
    EmployeeAttendanceComponent,
    MainPageAppOwnerComponent,
    CompaniesInfoAppOwnerComponent,
    ManageWorksitesAppOwnerComponent,
    ManageEmployeesAppOwnerComponent,
    LocationTrackingAppOwnerComponent,
    EmployeesAttendanceAppOwnerComponent,
    SettingsAppOwnerComponent,
    PayrollsAppOwnerComponent,
    CallToCustomersAppOwnerComponent,
    AttendenceTrackEmployeeAppOwnerComponent,
    AttendenceTrackEmployeeAdminComponent,
    StatForAttendenceComponent,
    NotificationAdminPageComponent,
    BillingPageComponent,
    BillingSuccessComponent,
    BillingCanceledComponent,
    BillingComponent
  ],
  imports: [
    CommonModule,
    MainPageRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    CodeInputModule,
    SharedModule
  ],
  providers: [
    DatePipe  // ← ДОБАВЬ ЭТО!
  ],
  // важное: чтобы Angular не падал на <code-input> и любые web components
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class MainPageModule {}
