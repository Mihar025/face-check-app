import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from "./pages/home/home.component";
import { SignInComponent } from "./pages/sign-in/sign-in.component";
import { AboutUsComponent } from "./pages/about-us/about-us.component";
import { PricingComponent } from "./pages/pricing/pricing.component";
import { RegisterComponent } from "./pages/register/register.component";
import { AdminPageComponent } from "./pages/admin-page/admin-page.component";
import { AuthGuard } from "./additionalServices/auth-guard";
import { UserPageComponent } from "./pages/user-page/user-page.component";

const routes: Routes = [
  {
    path: 'face-check',
    component: HomeComponent
  },
  {
    path: 'sign-in',
    component: SignInComponent
  },
  {
    path: 'sign-up',
    component: RegisterComponent
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
    path: 'main-page/user',
    component: UserPageComponent
  },
  {
    path: 'main-page/home',
    component: HomeComponent
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
