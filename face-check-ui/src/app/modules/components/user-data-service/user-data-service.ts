import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import {UserServiceControllerService} from "../../../services/services/user-service-controller.service";

@Injectable({
  providedIn: 'root'
})
export class UserDataService {
  // Кэшированные данные
  private userNameSubject = new BehaviorSubject<string>('');
  private companyNameSubject = new BehaviorSubject<string>('');
  private userPhotoSubject = new BehaviorSubject<string>('');
  private companyIdSubject = new BehaviorSubject<number>(0);

  // Observable для подписки
  userName$ = this.userNameSubject.asObservable();
  companyName$ = this.companyNameSubject.asObservable();
  userPhoto$ = this.userPhotoSubject.asObservable();
  companyId$ = this.companyIdSubject.asObservable();

  // Флаг загрузки
  private isLoaded = false;

  constructor(private userService: UserServiceControllerService) {}

  // Загружаем ВСЕ данные один раз
  loadUserData(): void {
    if (this.isLoaded) {
      return;
    }

    this.loadUserFullName();
    this.loadCompanyName();
    this.loadUserPhoto();
    this.loadCompanyId();

    this.isLoaded = true;
  }

  // Принудительное обновление
  refreshUserData(): void {
    this.isLoaded = false;
    this.loadUserData();
  }

  clearUserData(): void {
    this.userNameSubject.next('');
    this.companyNameSubject.next('');
    this.userPhotoSubject.next('');
    this.companyIdSubject.next(0);
    this.isLoaded = false;
    localStorage.removeItem('company_id');
  }

  private loadUserFullName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response?.fullName) {
          this.userNameSubject.next(response.fullName);
        }
      },
      error => console.error('Error loading user name:', error)
    );
  }

  private loadCompanyName(): void {
    this.userService.findWorkerCompanyName().subscribe(
      response => {
        if (response?.companyName) {
          this.companyNameSubject.next(response.companyName);
        }
      },
      error => console.error('Error loading company name:', error)
    );
  }

  private loadUserPhoto(): void {
    this.userService.findWorkerFullContactInformation().subscribe(
      response => {
        if (response?.photoUrl) {
          this.userPhotoSubject.next(response.photoUrl);
        }
      },
      error => console.error('Error loading user photo:', error)
    );
  }

  private loadCompanyId(): void {
    this.userService.findWorkerCompanyIdByAuthentication().subscribe(
      response => {
        if (response?.companyId) {
          this.companyIdSubject.next(response.companyId);
          localStorage.setItem('company_id', response.companyId.toString());
        }
      },
      error => console.error('Error loading company ID:', error)
    );
  }


  // Геттеры для синхронного доступа
  get userName(): string {
    return this.userNameSubject.value;
  }

  get companyName(): string {
    return this.companyNameSubject.value;
  }

  get userPhoto(): string {
    return this.userPhotoSubject.value;
  }

  get companyId(): number {
    return this.companyIdSubject.value;
  }
}
