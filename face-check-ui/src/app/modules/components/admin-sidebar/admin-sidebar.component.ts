import { Component, Input, OnInit, OnDestroy, HostListener } from '@angular/core';
import { AuthService } from "../../main-page/additionalServices/auth-service";
import { Router } from "@angular/router";
import { UserDataService } from "../user-data-service/user-data-service";
import { Subscription } from 'rxjs';

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

  isSidebarOpen = false;
  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router,
    public userDataService: UserDataService
  ) {}

  ngOnInit(): void {

    this.subscriptions.add(
      this.userDataService.userName$.subscribe(name => {
        if (name) this.userName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.companyName$.subscribe(name => {
        if (name) this.companyName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.userPhoto$.subscribe(photo => {
        if (photo) this.userPhotoUrl = photo;
      })
    );

    this.currentRoute = this.router.url;
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    document.body.classList.remove('sidebar-open');
  }

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
    this.toggleBodyScroll();
  }

  closeSidebar(): void {
    if (this.isSidebarOpen) {
      this.isSidebarOpen = false;
      this.toggleBodyScroll();
    }
  }

  onNavItemClick(): void {
    if (window.innerWidth <= 992) {
      this.closeSidebar();
    }
  }

  private toggleBodyScroll(): void {
    if (window.innerWidth <= 992) {
      if (this.isSidebarOpen) {
        document.body.classList.add('sidebar-open');
      } else {
        document.body.classList.remove('sidebar-open');
      }
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    const target = event.target as Window;
    if (target.innerWidth > 992 && this.isSidebarOpen) {
      this.closeSidebar();
    }
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKey(event: KeyboardEvent): void {
    if (window.innerWidth <= 992 && this.isSidebarOpen) {
      this.closeSidebar();
    }
  }

  logout(): void {
    this.closeSidebar();
    this.authService.logout();
  }

  isActive(route: string): boolean {
    return this.currentRoute.includes(route);
  }
}
