// src/app/modules/shared/shared.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AdminSidebarComponent } from '../components/admin-sidebar/admin-sidebar.component';
import { AppOwnerSidebarComponent } from '../components/app-owner-sidebar/app-owner-sidebar.component';

@NgModule({
  declarations: [
    AdminSidebarComponent,
    AppOwnerSidebarComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    AdminSidebarComponent,
    AppOwnerSidebarComponent
  ]
})
export class SharedModule {}
