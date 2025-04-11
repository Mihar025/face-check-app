import { Injectable } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { AuthenticationService } from "../../../services/services/authentication.service";
import { Authenticate$Params } from "../../../services/fn/authentication/authenticate";
import {RegisterCompany$Params} from "../../../services/fn/authentication/register-company";

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

// Более простая и надежная функция декодирования токена
  private decodeToken(token: string): any {
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

        const targetUrl = '/register/admin';
        console.log('Redirecting to: ', targetUrl);

        setTimeout(() => {
          window.location.href = targetUrl;
        }, 100);
      }),

      catchError(error => {
        console.error('Auth error:', error);
        return throwError(() => error);
      })
    );
  }
}


