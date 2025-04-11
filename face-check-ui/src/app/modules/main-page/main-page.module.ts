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
import { ReactiveFormsModule } from '@angular/forms';
import {RegisterComponent} from "./authentication/register/register.component";
import { RegisterCompanyComponent } from './authentication/register-company/register-company.component';


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
      RegisterCompanyComponent
  ],
  imports: [
    CommonModule,
    MainPageRoutingModule,
    ReactiveFormsModule,
    CodeInputModule,
  ]
})
export class MainPageModule { }
