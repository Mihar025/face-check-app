import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";
import {map, catchError, of, switchMap, Subscription} from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import {UserDataService} from "../../../../components/user-data-service/user-data-service";

@Component({
  selector: 'app-company-information',
  templateUrl: './company-information.component.html',
  styleUrls: ['./company-information.component.scss']
})
export class CompanyInformationComponent implements OnInit, OnDestroy {
  userName: string = '';
  companyName: string = '';
  companyEmail: string = '';
  companyPhone: string = '';
  companyAddress: string = '';
  userPhotoUrl: string = '';
  employeesCount: number = 0;
  companyId: number | null = null;

  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    public userDataService: UserDataService
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

    // Подписываемся на данные из сервиса
    this.subscriptions.add(
      this.userDataService.userName$.subscribe(name => {
        this.userName = name;
        console.log('CompanyInfo - userName updated:', name);
      })
    );

    this.subscriptions.add(
      this.userDataService.companyName$.subscribe(name => {
        this.companyName = name;
        console.log('CompanyInfo - companyName updated:', name);
      })
    );

    this.subscriptions.add(
      this.userDataService.userPhoto$.subscribe(photo => {
        this.userPhotoUrl = photo;
        console.log('CompanyInfo - userPhoto updated:', photo);
      })
    );


    if (!this.userDataService.userName && !this.userDataService.companyName) {
      console.log('Data not loaded, refreshing...');
      this.userDataService.refreshUserData();
    }


      // Эти данные загружаем отдельно, так как их нет в UserDataService
    this.loadCompanyEmail();
    this.loadCompanyIdAndEmployees();
    this.loadCompanyPhone();
    this.loadCompanyAddress();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadCompanyEmail(): void {
    this.userService.findWorkerCompanyEmail().subscribe(
      response => {
        this.companyEmail = response.email || '';
      },
      error => {
        console.error('Error loading email:', error);
        this.companyEmail = 'Не удалось загрузить';
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
        this.companyPhone = 'Не удалось загрузить';
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
        this.companyAddress = 'Не удалось загрузить';
      }
    );
  }

  loadCompanyIdAndEmployees(): void {
    this.companyService.getCompanyId()
      .pipe(
        catchError(err => {
          console.error('Error loading companyId:', err);
          return of(0);
        }),
        switchMap((id: number) => {
          this.companyId = id;
          return this.companyService.count({ companyId: id });
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
