import { Component, Input, OnInit, OnDestroy, HostListener } from '@angular/core';
import { AuthService } from "../../main-page/additionalServices/auth-service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-admin-sidebar',
  templateUrl: './admin-sidebar.component.html',
  styleUrl: './admin-sidebar.component.scss'
})
export class AdminSidebarComponent implements OnInit, OnDestroy {
  @Input() userName: string = '';
  @Input() companyName: string = '';
  @Input() userPhotoUrl: string = '';
  @Input() currentRoute: string = '';

  // Мобильное меню
  isSidebarOpen = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentRoute = this.router.url;
  }

  ngOnDestroy(): void {
    // Убедимся что body scroll восстановлен при уничтожении компонента
    document.body.classList.remove('sidebar-open');
  }

  /**
   * Открыть/закрыть sidebar (для мобильной версии)
   */
  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
    this.toggleBodyScroll();
  }

  /**
   * Закрыть sidebar (для мобильной версии)
   */
  closeSidebar(): void {
    if (this.isSidebarOpen) {
      this.isSidebarOpen = false;
      this.toggleBodyScroll();
    }
  }

  /**
   * Закрыть sidebar при клике на пункт меню
   * Только на мобильных устройствах (< 992px)
   */
  onNavItemClick(): void {
    if (window.innerWidth <= 992) {
      this.closeSidebar();
    }
  }

  /**
   * Блокировать/разблокировать скролл body
   * когда sidebar открыт на мобильных
   */
  private toggleBodyScroll(): void {
    if (window.innerWidth <= 992) {
      if (this.isSidebarOpen) {
        document.body.classList.add('sidebar-open');
      } else {
        document.body.classList.remove('sidebar-open');
      }
    }
  }

  /**
   * Закрыть sidebar при изменении размера экрана на desktop
   */
  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    const target = event.target as Window;
    if (target.innerWidth > 992 && this.isSidebarOpen) {
      this.closeSidebar();
    }
  }

  /**
   * Закрыть sidebar при нажатии Escape на мобильных
   */
  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKey(event: KeyboardEvent): void {
    if (window.innerWidth <= 992 && this.isSidebarOpen) {
      this.closeSidebar();
    }
  }

  /**
   * Выход из системы
   */
  logout(): void {
    // Закрыть sidebar если открыт
    this.closeSidebar();

    // Вызов вашего сервиса
    this.authService.logout();
  }

  /**
   * Проверка активного роута
   */
  isActive(route: string): boolean {
    return this.currentRoute.includes(route);
  }
}
