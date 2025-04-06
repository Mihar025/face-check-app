// user-page.component.ts
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../additionalServices/auth-service';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss']
})
export class UserPageComponent implements OnInit {
  // Добавьте флаг для предотвращения повторных проверок
  private authChecked = false;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    // Проверяем только один раз
    if (this.authChecked) {
      return;
    }

    this.authChecked = true;

    // Простая проверка аутентификации
    if (!this.authService.isUserAuthenticated()) {
      console.log('User not authenticated, redirecting to login');
      this.authService.logout();
      return;
    }

    // Проверка роли
    const role = this.authService.getUserRole();
    if (role !== 'USER') {
      console.log('User has wrong role, redirecting');
      if (role === 'ADMIN') {
        window.location.href = '/main-page/admin';
      } else {
        window.location.href = '/sign-in';
      }
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
