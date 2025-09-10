import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainPageRoutingModule } from './main-page-routing.module';
import {HomeComponent} from "./pages/home/home.component";
import { SignInComponent } from './authentication/sign-in/sign-in.component';
import { AboutUsComponent } from './pages/about-us/about-us.component';
import { PricingComponent } from './pages/pricing/pricing.component';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { VerificationCodeComponent } from './authentication/verification-code/verification-code.component';
import {CodeInputModule} from "angular-code-input";
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RegisterComponent} from "./authentication/register/register.component";
import {RegisterCompanyComponent} from "./authentication/register-company/register-company.component";
import { CompanyInformationComponent } from './pages/admin-main-page/company-information/company-information.component';
import { ManageWorksitesComponent } from './pages/admin-main-page/manage-worksites/manage-worksites.component';
import { SettingsComponent } from './pages/admin-main-page/settings/settings.component';
import { ManageEmployeesComponent } from './pages/admin-main-page/manage-employees/manage-employees.component';
import { VerificationForEmployeeComponent } from './pages/admin-main-page/verification-for-employee/verification-for-employee.component';
import {SharedModule} from "../shared/shared.module";
import { ForgotPasswordComponent } from './authentication/forgot-password/forgot-password.component';
import { PrivacyPolicyComponent } from './pages/privacy-policy/privacy-policy.component';
import { TermsOfServiceComponent } from './pages/terms-of-service/terms-of-service.component';
import { ScheduleConsultationComponent } from './pages/schedule-consultation/schedule-consultation.component';
import { ViewDemoComponent } from './pages/view-demo/view-demo.component';
import { RefundPolicyComponent } from './pages/refund-policy/refund-policy.component';
import { FinancePageComponent } from './pages/admin-main-page/finance-page/finance-page.component';
import { LocTrackComponent } from './pages/admin-main-page/loc-track/loc-track.component';
import { EmployeeAttendanceComponent } from './pages/admin-main-page/employee-attendance/employee-attendance.component';


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
      EmployeeAttendanceComponent

  ],
  imports: [
    CommonModule,
    MainPageRoutingModule,
    ReactiveFormsModule,
    CodeInputModule,
    FormsModule,
    SharedModule

  ]
})
export class MainPageModule { }
