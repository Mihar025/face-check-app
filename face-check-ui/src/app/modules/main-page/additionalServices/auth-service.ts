import { Injectable } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { AuthenticationService } from "../../../services/services/authentication.service";
import { Authenticate$Params } from "../../../services/fn/authentication/authenticate";
import { RegisterCompany$Params } from "../../../services/fn/authentication/register-company";
import { RegisterAdmin$Params } from "../../../services/fn/authentication/register-admin";
import { VerifyCode$Params } from "../../../services/fn/authentication/verify-code";
import {HttpHeaders} from "@angular/common/http";
import {RegistrationAdminRequest} from "../../../services/models/registration-admin-request";
import {I9DocumentRequest} from "../../../services/models/i-9-document-request";
import {DependentsRequest} from "../../../services/models/dependents-request";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private apiAuthService: AuthenticationService
  ) {}

  login(email: string, password: string): Observable<any> {
    console.log('Login with:', email);

    const params: Authenticate$Params = {
      body: {
        email: email,
        password: password
      }
    };

    return this.apiAuthService.authenticate(params).pipe(
      tap((response: any) => {
        console.log('Auth response:', response);

        if (response && response.token) {
          localStorage.setItem('auth_token', response.token);

          try {
            const decodedToken = this.decodeToken(response.token);
            console.log('Decoded token:', decodedToken);

            if (decodedToken && decodedToken.authorities) {
              const role = Array.isArray(decodedToken.authorities)
                ? decodedToken.authorities[0]
                : decodedToken.authorities;

              localStorage.setItem('user_role', role);
              console.log('User role from token:', role);

              let targetUrl = '/';
              if (role === 'ADMIN') {
                targetUrl = '/main-page/admin';
              } else if (role === 'USER') {
                targetUrl = '/main-page/user';
              }

              console.log('Redirecting to:', targetUrl);

              setTimeout(() => {
                window.location.href = targetUrl;
              }, 100);
            } else {
              console.error('No authorities found in token');
            }
          } catch (error) {
            console.error('Error processing token:', error);
          }
        }
      }),
      catchError(error => {
        console.error('Auth error:', error);
        return throwError(() => error);
      })
    );
  }

  decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64));
    } catch (e) {
      console.error('Error decoding token:', e);
      return null;
    }
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_role');
    localStorage.removeItem('verification_email');
    localStorage.removeItem('temp_token');

    window.location.href = '/sign-in';
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  getUserRole(): string | null {
    return localStorage.getItem('user_role');
  }

  isUserAuthenticated(): boolean {
    return !!localStorage.getItem('auth_token');
  }



  registerCompany(
                  companyAddress: string,
                  companyCity: string,
                  CompanyEmail: string,
                  companyName: string,
                  CompanyPhone: string,
                  companyPaymentPosition: 'WEEKLY' | 'BIWEEKLY',
                  companyState: string,
                  companyStateIdNumber: string,
                  companyZipCode: string,
                  defaultMemo: string,
                  employerEIN: string,
                  experienceModRate: number,
                  fundingAccountNumber: string,
                  fundingBankName: string,
                  fundingRoutingNumber: string,
                  returnMailingAddress: string,
                  signatureName: string,
                  signatureTitle: string,
                  socialSecurityTaxForCompany: number,
                  specialTwoCharConditionCodeForMTA305: string,
                  wcInsuranceCarrier: string,
                  wcPolicyNumber: string,
  ): Observable<any> {
    const params: RegisterCompany$Params = {
      body: {
        companyAddress: companyAddress,
        companyCity: companyCity,
        companyEmail: CompanyEmail,
        companyName: companyName,
        companyPaymentPosition: companyPaymentPosition,
        companyPhone: CompanyPhone,
        companyState: companyState,
        companyStateIdNumber: companyStateIdNumber,
        companyZipCode: companyZipCode,
        defaultMemo: defaultMemo,
        employerEIN: employerEIN,
        experienceModRate: experienceModRate,
        fundingAccountNumber: fundingAccountNumber,
        fundingBankName: fundingBankName,
        fundingRoutingNumber: fundingRoutingNumber,
        returnMailingAddress: returnMailingAddress,
        signatureName: signatureName,
        signatureTitle: signatureTitle,
        socialSecurityTaxForCompany: socialSecurityTaxForCompany,
        specialTwoCharConditionCodeForMTA305: specialTwoCharConditionCodeForMTA305,
        wcInsuranceCarrier: wcInsuranceCarrier,
        wcPolicyNumber: wcPolicyNumber,
      }
    };

    console.log('Registering company:', params);
    const token = this.getToken();
    if (!token) {
      return throwError(() => new Error('Authentication token not found'));
    }

    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };

    return this.apiAuthService.registerCompany(params).pipe(
      tap((response: any) => {
        console.log('Company Registration response:', response);
      }),
      catchError(error => {
        console.error('Auth error:', error);
        if (error.status === 401 || error.status === 403) {
          console.warn('Authentication error. Token may be expired.');
        }
        return throwError(() => error);
      })
    );
  }


  registerAdminExtended(
    // Основная информация
    firstName: string,
    lastName: string,
    middleInitial: string | undefined,
    email: string,
    password: string,
    phoneNumber: string,

    // Адрес
    homeAddress: string,
    apt: string,
    city: string,
    state: string,
    zipcode: string,

    // Персональная информация
    dateOfBirth: string,
    gender: 'MALE' | 'FEMALE' | 'OTHER',
    ssn_WORKER: string | undefined,

    // Информация о трудоустройстве
    employmentType: 'W2' | 'CONTRACTOR_1099',
    payFrequency: 'WEEKLY' | 'BIWEEKLY' | 'MONTHLY',
    wcRiskClassCode: string | undefined,
    isRehired: boolean | undefined,
    dateWhenRehired: string | undefined,

    // Налоговая информация
    filingStatus: 'SINGLE' | 'HEAD_OF_HOUSEHOLD' | 'MARRIED_FILLING_SEPARATELY' | 'MARRIED_FILLING_JOINTLY',
    exemptFromWithholding: boolean,
    extraWithHoldings: number,
    multipleJobsOrSpouseWorks: boolean,
    twoJobsCheckBox: boolean,
    livesInNYC: boolean,

    // Зависимые лица
    dependents: number,
    dependentsList: Array<DependentsRequest>,
    dependentsUnder17: number | undefined,
    otherDependents: number | undefined,
    totalDependentsCredit: number | undefined,

    // Дополнительные налоговые поля
    multipleJobsWorksheetLine2a: number | undefined,
    multipleJobsWorksheetLine2b: number | undefined,
    multipleJobsAdditionalWithholding: number | undefined,
    adjustmentsSchedule1: number | undefined,
    deductions: number | undefined,
    estimatedItemizedDeductions: number | undefined,
    otherIncome: number | undefined,

    // Медицинское страхование
    enrolledInHealthPlan: boolean | undefined,
    monthlyHealthPremium: number | undefined,
    coverageStartDate: string | undefined,

    // Статус гражданства и документы I-9
    isCitizen: boolean | undefined,
    isNonCitizenNationalOfTheUS: boolean | undefined,
    isPermanentResident: boolean | undefined,
    isANonCitizen: boolean | undefined,
    uscisNumber: string | undefined,
    formI94AdmissionNumber: string | undefined,
    passportNumber: string | undefined,
    passportCountryOfIssuance: string | undefined,
    workAuthorizationExpiryDate: string | undefined,
    i9Documents: Array<I9DocumentRequest> | undefined
  ): Observable<any> {

    const registrationData: RegistrationAdminRequest = {
      // Обязательные поля
      firstName,
      lastName,
      email,
      password,
      phoneNumber,
      homeAddress,
      apt,
      city,
      state,
      zipcode,
      dateOfBirth,
      gender,
      employmentType,
      payFrequency,
      filingStatus,
      exemptFromWithholding,
      extraWithHoldings,
      multipleJobsOrSpouseWorks,
      twoJobsCheckBox,
      livesInNYC,
      dependents,
      dependentsList,

      // Опциональные поля
      ...(middleInitial && { middleInitial }),
      ...(ssn_WORKER && { ssn_WORKER }),
      ...(wcRiskClassCode && { wcRiskClassCode }),
      ...(isRehired !== undefined && { isRehired }),
      ...(dateWhenRehired && { dateWhenRehired }),
      ...(dependentsUnder17 !== undefined && { dependentsUnder17 }),
      ...(otherDependents !== undefined && { otherDependents }),
      ...(totalDependentsCredit !== undefined && { totalDependentsCredit }),
      ...(multipleJobsWorksheetLine2a !== undefined && { multipleJobsWorksheetLine2a }),
      ...(multipleJobsWorksheetLine2b !== undefined && { multipleJobsWorksheetLine2b }),
      ...(multipleJobsAdditionalWithholding !== undefined && { multipleJobsAdditionalWithholding }),
      ...(adjustmentsSchedule1 !== undefined && { adjustmentsSchedule1 }),
      ...(deductions !== undefined && { deductions }),
      ...(estimatedItemizedDeductions !== undefined && { estimatedItemizedDeductions }),
      ...(otherIncome !== undefined && { otherIncome }),
      ...(enrolledInHealthPlan !== undefined && { enrolledInHealthPlan }),
      ...(monthlyHealthPremium !== undefined && { monthlyHealthPremium }),
      ...(coverageStartDate && { coverageStartDate }),
      ...(isCitizen !== undefined && { isCitizen }),
      ...(isNonCitizenNationalOfTheUS !== undefined && { isNonCitizenNationalOfTheUS }),
      ...(isPermanentResident !== undefined && { isPermanentResident }),
      ...(isANonCitizen !== undefined && { isANonCitizen }),
      ...(uscisNumber && { uscisNumber }),
      ...(formI94AdmissionNumber && { formI94AdmissionNumber }),
      ...(passportNumber && { passportNumber }),
      ...(passportCountryOfIssuance && { passportCountryOfIssuance }),
      ...(workAuthorizationExpiryDate && { workAuthorizationExpiryDate }),
      ...(i9Documents && { i9Documents })
    };

    const params: RegisterAdmin$Params = {
      body: registrationData
    };

    console.log('Registering admin with extended data:', params);

    return this.apiAuthService.registerAdmin(params).pipe(
      tap((response: any) => {
        console.log('Admin Registration response:', response);
        localStorage.setItem('verification_email', email);

        if (response && response.token) {
          localStorage.setItem('temp_token', response.token);
        }
      }),
      catchError(error => {
        console.error('Admin registration error:', error);
        return throwError(() => error);
      })
    );
  }

}
