import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../additionalServices/auth-service';

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    // Проверяем аутентификацию пользователя
    if (!this.authService.isUserAuthenticated()) {
      // Если пользователь не аутентифицирован, перенаправляем его на страницу входа
      this.authService.logout();
    }

    // Проверяем, соответствует ли роль пользователя этой странице
    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN') {
      // Если роль пользователя не соответствует, перенаправляем его на соответствующую страницу
      let targetUrl = '/';
      if (userRole === 'USER') {
        targetUrl = '/main-page/user';
      }
      window.location.href = targetUrl;
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
