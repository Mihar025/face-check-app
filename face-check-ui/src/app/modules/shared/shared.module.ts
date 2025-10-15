import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminSidebarComponent } from '../components/admin-sidebar/admin-sidebar.component';
import {AppOwnerSidebarComponent} from "../components/app-owner-sidebar/app-owner-sidebar.component";

@NgModule({
  declarations: [
    AdminSidebarComponent,
    AppOwnerSidebarComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    AdminSidebarComponent,
    AppOwnerSidebarComponent
  ]
})
export class SharedModule { }
