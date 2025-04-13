import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainPageRoutingModule } from './main-page-routing.module';
import {HomeComponent} from "./pages/home/home.component";
import { SignInComponent } from './authentication/sign-in/sign-in.component';
import { AboutUsComponent } from './pages/about-us/about-us.component';
import { PricingComponent } from './pages/pricing/pricing.component';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { UserPageComponent } from './pages/user-page/user-page.component';
import { VerificationCodeComponent } from './authentication/verification-code/verification-code.component';
import {CodeInputModule} from "angular-code-input";
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RegisterComponent} from "./authentication/register/register.component";
import {RegisterCompanyComponent} from "./authentication/register-company/register-company.component";
import { CompanyInformationComponent } from './pages/admin-main-page/company-information/company-information.component';
import { ManageWorksitesComponent } from './pages/admin-main-page/manage-worksites/manage-worksites.component';
import { SettingsComponent } from './pages/admin-main-page/settings/settings.component';
import { ManageEmployeesComponent } from './pages/admin-main-page/manage-employees/manage-employees.component';


@NgModule({
  declarations: [
      HomeComponent,
      SignInComponent,
      AboutUsComponent,
      PricingComponent,
      RegisterComponent,
      AdminPageComponent,
      UserPageComponent,
      VerificationCodeComponent,
      RegisterCompanyComponent,
      CompanyInformationComponent,
      ManageWorksitesComponent,
      SettingsComponent,
      ManageEmployeesComponent
  ],
  imports: [
    CommonModule,
    MainPageRoutingModule,
    ReactiveFormsModule,
    CodeInputModule,
    FormsModule,
  ]
})
export class MainPageModule { }
