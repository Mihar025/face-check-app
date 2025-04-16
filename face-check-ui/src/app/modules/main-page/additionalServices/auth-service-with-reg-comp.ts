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
}


