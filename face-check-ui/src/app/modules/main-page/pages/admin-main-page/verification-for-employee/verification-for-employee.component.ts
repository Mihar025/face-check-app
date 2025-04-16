import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../../../../services/services/authentication.service";

@Component({
  selector: 'app-verification-for-employee',
  templateUrl: './verification-for-employee.component.html',
  styleUrl: './verification-for-employee.component.scss'
})
export class VerificationForEmployeeComponent {
  message = '';
  isOkay = true;
  submitted = false;
  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) {}

  private confirmAccount(token: string) {
    this.authService.confirm({
      token
    }).subscribe({
      next: () => {
        this.message = 'This account has been successfully activated.\nNow you can proceed to Manage-Employee-Page';
        this.submitted = true;
      },
      error: () => {
        this.message = 'Token has been expired or invalid';
        this.submitted = true;
        this.isOkay = false;
      }
    });
  }

  redirectToManageEmployee() {
    this.router.navigate(['main-page/admin/manage-employees']);
  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }


}
