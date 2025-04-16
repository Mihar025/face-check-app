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
    component: CompanyInformationComponent
  },
  {
    path: 'main-page/admin/manage-worksites',
    component: ManageWorksitesComponent
  },
  {
    path: 'main-page/admin/manage-employees',
    component: ManageEmployeesComponent
  },
  {
    path: 'main-page/admin/settings',
    component: SettingsComponent
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
