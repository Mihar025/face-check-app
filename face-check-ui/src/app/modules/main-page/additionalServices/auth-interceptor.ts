import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { AuthService } from './auth-service';
import { catchError } from 'rxjs/operators';
import { Router } from "@angular/router";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

    const isAuthenticateRequest = request.url.includes('/auth/authenticate');

    if (token && !isAuthenticateRequest) {
      const authReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
      });

      return next.handle(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            // ДОБАВЛЕНО: Очистка токена
            localStorage.removeItem('auth_token');

            // ДОБАВЛЕНО: Уведомление пользователю
            alert('Your session is expired. Please, sign in again!.');
            this.router.navigate(['/face-check']);

          }
          return throwError(() => error);
        })
      );
    }

    return next.handle(request);
  }
}
