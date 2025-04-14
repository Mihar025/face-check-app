import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";
import { map, catchError, of } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';


@Component({
  selector: 'app-company-information',
  templateUrl: './company-information.component.html',
  styleUrls: ['./company-information.component.scss']
})
export class CompanyInformationComponent implements OnInit {
  userName: string = '';
  companyName: string = '';
  companyEmail: string = '';
  companyPhone: string = '';
  companyAddress: string = '';
  userPhotoUrl: string = '';


  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService
  ) { }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN') {
      let targetUrl = '/';
      if (userRole === 'USER') {
        targetUrl = '/main-page/user';
      }
      window.location.href = targetUrl;
      return;
    }


    this.loadUserFullName();

    this.loadCompanyName();
    this.loadCompanyEmail();
    this.loadCompanyPhone();
    this.loadCompanyAddress();
    this.getUserPhoto();
  }

  logout(): void {
    this.authService.logout();
  }

  // Метод для загрузки полного имени пользователя
  loadUserFullName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response && response.fullName) {
          this.userName = response.fullName;
        }
      },
      error => {
        console.error('Error loading user full name:', error);
      }
    );
  }


  loadCompanyName(): void {
    this.companyService.getCompanyName().pipe(
      map((response: any) => {
        if (typeof response === 'string') {
          return response;
        }
        return '';
      }),
      catchError((error: any) => {
        if (error instanceof HttpErrorResponse && error.status === 200) {
          return of(error.error.text || '');
        }
        console.error('Error loading company name:', error);
        return of('');
      })
    ).subscribe(name => {
      this.companyName = name;
    });
  }

  loadCompanyEmail(): void {
    this.companyService.getCompanyEmail().pipe(
      email => {
        if (email) {
          this.companyEmail = email;
        }
      },
      error => {
        console.error('Error loading company email:', error);
      }
    );
  }

  loadCompanyPhone(): void {
    this.companyService.getCompanyPhone().subscribe(
      phone => {
        if (phone) {
          this.companyPhone = phone;
        }
      },
      error => {
        console.error('Error loading company phone:', error);
      }
    );
  }

  loadCompanyAddress(): void {
    this.companyService.getCompanyAddress().subscribe(
      address => {
        if (address) {
          this.companyAddress = address;
        }
      },
      error => {
        console.error('Error loading company address:', error);
      }
    );
  }

  getUserPhoto(): void {
    this.userService.findWorkerFullContactInformation().subscribe(
      response => {
        if (response && response.photoUrl) {
          this.userPhotoUrl = response.photoUrl;
        }
      },
      error => {
        console.error('Error loading user photo:', error);
      }
    );
  }



}
