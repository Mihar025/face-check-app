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

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Получаем токен из сервиса
    const token = this.authService.getToken();

    // Проверяем, является ли запрос запросом аутентификации
    const isAuthenticateRequest = request.url.includes('/auth/authenticate');

    if (token && !isAuthenticateRequest) {
      // Клонируем запрос и добавляем заголовок авторизации
      const authReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
      });

      // Обрабатываем запрос с токеном
      return next.handle(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
          // Если получаем 401 (Unauthorized), выходим из системы
          if (error.status === 401) {
            this.authService.logout();
          }
          return throwError(() => error);
        })
      );
    }

    // Если нет токена или это запрос аутентификации, просто обрабатываем запрос без изменений
    return next.handle(request);
  }
}
