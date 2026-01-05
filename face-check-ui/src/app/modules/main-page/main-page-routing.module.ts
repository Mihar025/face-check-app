import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from "./pages/home/home.component";
import { AboutUsComponent } from "./pages/about-us/about-us.component";
import { PricingComponent } from "./pages/pricing/pricing.component";
import { AdminPageComponent } from "./pages/admin-page/admin-page.component";
import {SignInComponent} from "./authentication/sign-in/sign-in.component";
import {RegisterComponent} from "./authentication/register/register.component";
import {VerificationCodeComponent} from "./authentication/verification-code/verification-code.component";
import {RegisterCompanyComponent} from "./authentication/register-company/register-company.component";
import {CompanyInformationComponent} from "./pages/admin-main-page/company-information/company-information.component";
import {ManageWorksitesComponent} from "./pages/admin-main-page/manage-worksites/manage-worksites.component";
import {SettingsComponent} from "./pages/admin-main-page/settings/settings.component";
import {ManageEmployeesComponent} from "./pages/admin-main-page/manage-employees/manage-employees.component";
import {
  VerificationForEmployeeComponent
} from "./pages/admin-main-page/verification-for-employee/verification-for-employee.component";
import {ForgotPasswordComponent} from "./authentication/forgot-password/forgot-password.component";
import {PrivacyPolicyComponent} from "./pages/privacy-policy/privacy-policy.component";
import {TermsOfServiceComponent} from "./pages/terms-of-service/terms-of-service.component";
import {ScheduleConsultationComponent} from "./pages/schedule-consultation/schedule-consultation.component";
import {ViewDemoComponent} from "./pages/view-demo/view-demo.component";
import {RefundPolicyComponent} from "./pages/refund-policy/refund-policy.component";
import {FinancePageComponent} from "./pages/admin-main-page/finance-page/finance-page.component";
import {LocTrackComponent} from "./pages/admin-main-page/loc-track/loc-track.component";
import {EmployeeAttendanceComponent} from "./pages/admin-main-page/employee-attendance/employee-attendance.component";
import {MainPageAppOwnerComponent} from "./pages/appOwner/main-page-app-owner/main-page-app-owner.component";
import {
  ManageWorksitesAppOwnerComponent
} from "./pages/appOwner/manage-worksites-app-owner/manage-worksites-app-owner.component";
import {
  LocationTrackingAppOwnerComponent
} from "./pages/appOwner/location-tracking-app-owner/location-tracking-app-owner.component";
import {
  EmployeesAttendanceAppOwnerComponent
} from "./pages/appOwner/employees-attendance-app-owner/employees-attendance-app-owner.component";
import {
  CompaniesInfoAppOwnerComponent
} from "./pages/appOwner/companies-info-app-owner/companies-info-app-owner.component";
import {SettingsAppOwnerComponent} from "./pages/appOwner/settings-app-owner/settings-app-owner.component";
import {
  ManageEmployeesAppOwnerComponent
} from "./pages/appOwner/manage-employees-app-owner/manage-employees-app-owner.component";
import {
  CallToCustomersAppOwnerComponent
} from "./pages/appOwner/call-to-customers-app-owner/call-to-customers-app-owner.component";
import {TokenExpiryGuard} from "./authentication/guard/token-expiry.guard";
import {PayrollsAppOwnerComponent} from "./pages/appOwner/payrolls-app-owner/payrolls-app-owner.component";
import {
  AttendenceTrackEmployeeAdminComponent
} from "./pages/admin-main-page/attendence-track-employee-admin/attendence-track-employee-admin.component";
import {
  AttendenceTrackEmployeeAppOwnerComponent
} from "./pages/appOwner/attendence-track-employee-app-owner/attendence-track-employee-app-owner.component";
import {StatForAttendenceComponent} from "./pages/admin-main-page/stat-for-attendence/stat-for-attendence.component";
import {
  NotificationAdminPageComponent
} from "./pages/admin-main-page/notification-admin-page/notification-admin-page.component";
import {BillingSuccessComponent} from "./pages/billing-success/billing-success.component";
import {BillingCanceledComponent} from "./pages/billing-canceled/billing-canceled.component";
import {BillingPageComponent} from "./pages/billing-page/billing-page.component";
import {DeleteAccountComponent} from "./pages/delete-account/delete-account.component";

const routes: Routes = [
  //Registration all important endpoints
  {
    path: 'sign-in',
    component: SignInComponent
  },
  {
    path: 'sign-up',
    component: RegisterComponent
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent
  },
  {
    path: 'verification/admin',
    component: VerificationCodeComponent
  },
  {
    path: 'registration/company',
    component: RegisterCompanyComponent
  },
  {
    path: 'verification/employee',
    component: VerificationForEmployeeComponent
  },





  // Home Page
  {
    path: 'face-check',
    component: HomeComponent
  },
  {
    path: 'about-us',
    component: AboutUsComponent
  },
  {
    path: 'pricing',
    component: PricingComponent
  },
  {
    path: 'privacy-policy',
    component: PrivacyPolicyComponent
  },
  {
    path: 'terms-of-service',
    component: TermsOfServiceComponent
  },
  {
    path: 'schedule-consultation',
    component: ScheduleConsultationComponent
  },
  {
    path: 'view-demo',
    component: ViewDemoComponent
  },
  {
    path: 'refund-policy',
    component: RefundPolicyComponent
  },


  {
    path: 'delete-account',
    component: DeleteAccountComponent
  },


  {
    path: 'main-page/admin',
    component: AdminPageComponent
  },
  {
    path: 'main-page/about',
    component: AboutUsComponent
  },
  {
    path: 'main-page/pricing',
    component: PricingComponent
  },

  {
    path: 'main-page/admin/company-information',
    component: CompanyInformationComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/manage-worksites',
    component: ManageWorksitesComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/manage-employees',
    component: ManageEmployeesComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/settings',
    component: SettingsComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/finance',
    component: FinancePageComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/location-tracking',
    component: LocTrackComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/employee-attendance',
    component: EmployeeAttendanceComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/attendance-statistic',
    component: StatForAttendenceComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'main-page/admin/notifications',
    component: NotificationAdminPageComponent,
    canActivate: [TokenExpiryGuard]
  },

  {
    path: 'company/:companyId/billing',
    component: BillingPageComponent,
    canActivate: [TokenExpiryGuard]

  },
  {
    path: 'billing/success',
    component: BillingSuccessComponent
  },
  {
    path: 'billing/canceled',
    component: BillingCanceledComponent
  },




  /*
        Routes for App Owner!
   */
  {
    path: 'app-owner/main-page',
    component: MainPageAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'app-owner/manage-employees',
    component: ManageEmployeesAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'app-owner/manage-worksites',
    component: ManageWorksitesAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'app-owner/location-tracking',
    component: LocationTrackingAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },

  {
    path: 'app-owner/employee-attendance',
    component: EmployeesAttendanceAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'app-owner/companies-info',
    component: CompaniesInfoAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'app-owner/settings',
    component: SettingsAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'app-owner/payroll',
    component: PayrollsAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },
  {
    path: 'app-owner/call-to-customers',
    component: CallToCustomersAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },

  {
    path: 'app-owner/attendance-statistic',
    component: AttendenceTrackEmployeeAppOwnerComponent,
    canActivate: [TokenExpiryGuard]
  },











  {
    path: '',
    redirectTo: 'face-check',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'face-check'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MainPageRoutingModule { }
