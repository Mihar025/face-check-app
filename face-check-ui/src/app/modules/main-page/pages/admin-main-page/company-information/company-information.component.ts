import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";


@Component({
  selector: 'app-company-information',
  templateUrl: './company-information.component.html',
  styleUrls: ['./company-information.component.scss']
})
export class CompanyInformationComponent implements OnInit {
  userName: string = 'John Doe'; // Значение по умолчанию
  companyName: string = 'FaceCheck Inc.'; // Значение по умолчанию
  companyEmail: string = 'info@facecheck.com'; // Значение по умолчанию
  companyPhone: string = '+1 (555) 123-4567'; // Значение по умолчанию
  companyAddress: string = '123 Tech Street, San Francisco, CA 94107'; // Значение по умолчанию

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

    // Загружаем имя пользователя
    this.loadUserFullName();

    // Загружаем информацию о компании
    this.loadCompanyName();
    this.loadCompanyEmail();
    this.loadCompanyPhone();
    this.loadCompanyAddress();
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

  // Методы для загрузки информации о компании
  loadCompanyName(): void {
    this.companyService.getCompanyName().subscribe(
      name => {
        if (name) {
          this.companyName = name;
        }
      },
      error => {
        console.error('Error loading company name:', error);
      }
    );
  }

  loadCompanyEmail(): void {
    this.companyService.getCompanyEmail().subscribe(
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


}
