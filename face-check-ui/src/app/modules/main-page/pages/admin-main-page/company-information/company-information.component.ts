import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";
import {map, catchError, of, switchMap} from 'rxjs';
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
  employeesCount: number = 0;
  companyId: number | null = null;



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
    this.loadCompanyIdAndEmployees();

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
    this.userService.findWorkerCompanyName().subscribe(
      response => {
        this.companyName = response.companyName || '';
      },
      error => {
        console.error('Error loading company name:', error);
        this.companyName = 'Cant load company name';
      }
    );
  }

  loadCompanyEmail(): void {
    this.userService.findWorkerCompanyEmail().subscribe(
      response => {
        this.companyEmail = response.email || '';
      },
      error => {
        console.error('Error loading email:', error);
        this.companyName = 'Не удалось загрузить';
      }
    );
  }

  loadCompanyPhone(): void {
    this.userService.findWorkerCompanyPhoneNumber().subscribe(
      response => {
        this.companyPhone = response.phoneNumber || '';
      },
      error => {
        console.error('Error loading phoneNumber:', error);
        this.companyName = 'Не удалось загрузить';
      }
    );
  }

  loadCompanyAddress(): void {
    this.userService.findWorkerCompanyAddress().subscribe(
      response => {
        this.companyAddress = response.companyAddress || '';
      },
      error => {
        console.error('Error loading companyAddress:', error);
        this.companyName = 'Не удалось загрузить';
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

  loadCompanyIdAndEmployees(): void {
    this.companyService.getCompanyId()
      .pipe(
        // сервер отдаёт JSON-число; getCompanyId уже парсит его в number
        catchError(err => {
          console.error('Error loading companyId:', err);
          return of(0); // без companyId покажем 0 сотрудников
        }),
        switchMap((id: number) => {
          this.companyId = id;
          return this.companyService.count({ companyId: id }); // text/plain
        }),
        map((v: string) => Number(v) || 0),
        catchError(err => {
          console.error('Error loading employees count:', err);
          return of(0);
        })
      )
      .subscribe(n => this.employeesCount = n);
  }


}
