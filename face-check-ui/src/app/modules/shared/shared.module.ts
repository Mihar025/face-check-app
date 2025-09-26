import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminSidebarComponent } from '../components/admin-sidebar/admin-sidebar.component';

@NgModule({
  declarations: [
    AdminSidebarComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    AdminSidebarComponent
  ]
})
export class SharedModule { }
