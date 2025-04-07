import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainPageRoutingModule } from './main-page-routing.module';
import {HomeComponent} from "./pages/home/home.component";
import { SignInComponent } from './pages/sign-in/sign-in.component';
import { AboutUsComponent } from './pages/about-us/about-us.component';
import { PricingComponent } from './pages/pricing/pricing.component';
import { RegisterComponent } from './pages/register/register.component';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { UserPageComponent } from './pages/user-page/user-page.component';
import {ReactiveFormsModule} from "@angular/forms";
import { RegisterAdminComponent } from './pages/register-admin/register-admin.component';


@NgModule({
  declarations: [
      HomeComponent,
      SignInComponent,
      AboutUsComponent,
      PricingComponent,
      RegisterComponent,
      AdminPageComponent,
      UserPageComponent,
      RegisterAdminComponent
  ],
  imports: [
    CommonModule,
    MainPageRoutingModule,
    ReactiveFormsModule,
  ]
})
export class MainPageModule { }
