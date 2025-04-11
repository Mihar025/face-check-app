import { Injectable } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { AuthenticationService } from "../../../services/services/authentication.service";
import { Authenticate$Params } from "../../../services/fn/authentication/authenticate";
import { RegisterCompany$Params } from "../../../services/fn/authentication/register-company";
import { RegisterAdmin$Params } from "../../../services/fn/authentication/register-admin";
import { VerifyCode$Params } from "../../../services/fn/authentication/verify-code";

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
          // Сохраняем токен
          localStorage.setItem('auth_token', response.token);

          try {
            // Декодируем токен для получения информации о пользователе
            const decodedToken = this.decodeToken(response.token);
            console.log('Decoded token:', decodedToken);

            // Проверяем наличие необходимых полей в декодированном токене
            if (decodedToken && decodedToken.authorities) {
              const role = Array.isArray(decodedToken.authorities)
                ? decodedToken.authorities[0]
                : decodedToken.authorities;

              localStorage.setItem('user_role', role);
              console.log('User role from token:', role);

              // Определяем целевую страницу на основе роли
              let targetUrl = '/';
              if (role === 'ADMIN') {
                targetUrl = '/main-page/admin';
              } else if (role === 'USER') {
                targetUrl = '/main-page/user';
              }

              console.log('Redirecting to:', targetUrl);

              // Используем Router вместо window.location
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

  // Функция декодирования токена (публичная для использования в других компонентах)
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
    // Очищаем localStorage
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_role');
    localStorage.removeItem('verification_email');
    localStorage.removeItem('temp_token');

    // Перенаправляем на страницу входа
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

    console.log('Registered company:', params);

    return this.apiAuthService.registerCompany(params).pipe(
      tap((response: any) => {
        console.log('Company Registration response:', response);

        // Используем Angular Router вместо window.location
      }),
      catchError(error => {
        console.error('Auth error:', error);
        return throwError(() => error);
      })
    );
  }

  // Метод для регистрации администратора
  registerAdmin(
    firstName: string,
    lastName: string,
    email: string,
    password: string,
    phoneNumber: string,
    homeAddress: string,
    dateOfBirth: string,
    gender: 'MALE' | 'FEMALE' | 'OTHER',
    ssn_WORKER: number
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

        // Сохраняем email в localStorage для использования при верификации
        localStorage.setItem('verification_email', email);

        // Сохраняем временный токен, если он есть в ответе
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

  // Метод для верификации кода администратора
  verifyAdminCode(code: string): Observable<any> {
    const email = localStorage.getItem('verification_email');

    if (!email) {
      return throwError(() => new Error('Email not found for verification'));
    }

    const params: VerifyCode$Params = {
      body: {
        email: email,
        code: code
      }
    };

    console.log('Verifying admin code:', params);

    return this.apiAuthService.verifyCode(params).pipe(
      tap((response: any) => {
        console.log('Verification response:', response);

        // Если в ответе есть токен, сохраняем его
        if (response && response.token) {
          localStorage.setItem('auth_token', response.token);

          // Очищаем временные данные
          localStorage.removeItem('verification_email');
          localStorage.removeItem('temp_token');
        }
      }),
      catchError(error => {
        console.error('Verification error:', error);
        return throwError(() => error);
      })
    );
  }

  // Метод для повторной отправки кода верификации
  resendVerificationCode(email: string): Observable<any> {
    // Используйте соответствующий API-метод из AuthenticationService
    // Это пример, нужно заменить на реальный метод из вашего API
    const params = {
      body: {
        email: email
      }
    };

    console.log('Resending verification code for:', email);

    // Замените sendResetCode на подходящий метод из вашего AuthenticationService
    return this.apiAuthService.sendResetCode(params).pipe(
      tap((response: any) => {
        console.log('Resend verification code response:', response);
      }),
      catchError(error => {
        console.error('Resend verification code error:', error);
        return throwError(() => error);
      })
    );
  }
}
