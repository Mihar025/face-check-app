import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import {AuthService} from "../../additionalServices/auth-service";

@Injectable({
  providedIn: 'root'
})
export class TokenExpiryGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/face-check']);
      return false;
    }

    // Проверяем истечение токена
    if (this.authService.isTokenExpired()) {
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user_role');
      alert('Your session is expired. Please, sign in again!.');
      this.router.navigate(['/face-check']);
      return false;
    }

    return true;
  }
}
