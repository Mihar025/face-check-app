import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import { AuthService } from "./auth-service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    console.log('AuthGuard: Checking route access...');
    console.log('Current URL:', state.url);

    // Получаем токен и проверяем его наличие
    const token = this.authService.getToken();
    if (!token) {
      console.log('AuthGuard: No token found, redirecting to login');
      // Используем Angular Router вместо window.location
      this.router.navigate(['/sign-in']);
      return false;
    }

    // Проверка роли пользователя и требований маршрута
    if (route.data && route.data['roles']) {
      console.log('AuthGuard: Route requires roles:', route.data['roles']);

      const requiredRoles = route.data['roles'] as Array<string>;
      const userRole = this.authService.getUserRole();
      console.log('AuthGuard: User role:', userRole);

      // Проверка наличия роли
      if (!userRole) {
        console.log('AuthGuard: User has no role, redirecting to login');
        // Используем Angular Router вместо window.location
        this.router.navigate(['/sign-in']);
        return false;
      }

      // Проверка, имеет ли пользователь требуемую роль
      if (!requiredRoles.includes(userRole)) {
        console.log('AuthGuard: User role not authorized for this route');
        // Если нет доступа, перенаправляем на соответствующую страницу роли
        const targetUrl = userRole === 'ADMIN' ? '/main-page/admin' : '/main-page/user';
        // Используем Angular Router вместо window.location
        this.router.navigate([targetUrl]);
        return false;
      }
    }

    console.log('AuthGuard: Access granted to route');
    return true;
  }
}
