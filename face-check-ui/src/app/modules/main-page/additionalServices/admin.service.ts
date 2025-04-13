import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

import { AuthService } from './auth-service';
import {AdminControllerService} from "../../../services/services/admin-controller.service";
import {CompanyControllerService} from "../../../services/services/company-controller.service";
import {WorkSiteControllerService} from "../../../services/services/work-site-controller.service";

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(
    private adminControllerService: AdminControllerService,
    private companyControllerService: CompanyControllerService,
    private workSiteControllerService: WorkSiteControllerService,
    private authService: AuthService
  ) { }

  /**
   * Получить список работников на рабочем участке
   * @param worksiteId ID рабочего участка
   * @param page Номер страницы (начиная с 0)
   * @param size Размер страницы
   */
  getWorkersInWorksite(worksiteId: number, page: number = 0, size: number = 10): Observable<any> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    return this.adminControllerService.getWorkersInWorksite({
      worksiteId: worksiteId,
      page: page,
      size: size
    }).pipe(
      catchError(error => {
        console.error('Error getting workers in worksite:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Получить всех сотрудников компании
   * @param companyId ID компании
   * @param page Номер страницы (начиная с 0)
   * @param size Размер страницы
   */
  getAllEmployees(companyId: number, page: number = 0, size: number = 10): Observable<any> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    return this.companyControllerService.getAllEmployees({
      companyId: companyId,
      page: page,
      size: size
    }).pipe(
      catchError(error => {
        console.error('Error getting all employees:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Получить все рабочие участки компании
   * @param page Номер страницы (начиная с 0)
   * @param size Размер страницы
   */
  getAllWorkSites(page: number = 0, size: number = 10): Observable<any> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    return this.workSiteControllerService.findAllWorkSites({
      page: page,
      size: size
    }).pipe(
      catchError(error => {
        console.error('Error getting all worksites:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Получить общую сумму зарплат компании
   */
  getTotalSalaries(): Observable<any> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    return this.companyControllerService.getTotalSalaries().pipe(
      catchError(error => {
        console.error('Error getting total salaries:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Получить данные о месячной выручке компании
   * @param companyId ID компании
   */
  getCompanyIncomePerMonth(companyId: number): Observable<any> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    return this.companyControllerService.findCompanyIncomePerMonth({
      companyId: companyId
    }).pipe(
      catchError(error => {
        console.error('Error getting company income:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Создать новый рабочий участок
   * @param workSiteData Данные нового рабочего участка
   */
  createWorkSite(workSiteData: any): Observable<any> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    return this.workSiteControllerService.createWorkSite({
      body: workSiteData
    }).pipe(
      catchError(error => {
        console.error('Error creating worksite:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Обновить ставку сотрудника
   * @param companyId ID компании
   * @param employeeId ID сотрудника
   * @param rateData Данные новой ставки
   */
  updateEmployeeRate(companyId: number, employeeId: number, rateData: any): Observable<any> {
    const token = this.authService.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    return this.companyControllerService.updateEmployeeRate({
      companyId: companyId,
      employeeId: employeeId,
      body: rateData
    }).pipe(
      catchError(error => {
        console.error('Error updating employee rate:', error);
        return throwError(() => error);
      })
    );
  }
}
