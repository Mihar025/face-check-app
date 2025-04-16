import { Injectable } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { AuthenticationService } from "../../../services/services/authentication.service";
import { Authenticate$Params } from "../../../services/fn/authentication/authenticate";
import { RegisterCompany$Params } from "../../../services/fn/authentication/register-company";
import { RegisterAdmin$Params } from "../../../services/fn/authentication/register-admin";
import { VerifyCode$Params } from "../../../services/fn/authentication/verify-code";
import {HttpHeaders} from "@angular/common/http";

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
  registerCompany(companyName: string,
                  companyAddress: string,
                  CompanyPhone: string,
                  CompanyEmail: string): Observable<any> {
    const params: RegisterCompany$Params = {
      body: {
        companyName: companyName,
        companyAddress: companyAddress,
        companyPhone: CompanyPhone,
        companyEmail: CompanyEmail
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

  registerAdmin(
    firstName: string,
    lastName: string,
    email: string,
    password: string,
    phoneNumber: string,
    homeAddress: string,
    dateOfBirth: string,
    gender: 'MALE' | 'FEMALE' | 'OTHER',
    ssn_WORKER: string
  ): Observable<any> {
    const params: RegisterAdmin$Params = {
      body: {
        firstName,
        lastName,
        email,
        password,
        phoneNumber,
        homeAddress,
        dateOfBirth,
        gender,
        ssn_WORKER
      }
    };

    console.log('Registering admin:', params);

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
