import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from "../../main-page/additionalServices/auth-service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-admin-sidebar',
  templateUrl: './admin-sidebar.component.html',
  styleUrl: './admin-sidebar.component.scss'
})
export class AdminSidebarComponent implements OnInit{
        @Input() userName: string = '';
        @Input() companyName: string = '';
        @Input() userPhotoUrl: string = '';
        @Input() currentRoute: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

    ngOnInit(): void {
       this.currentRoute = this.router.url;
    }

    logout(): void {
      this.authService.logout();
    }

    isActive(route: string): boolean{
      return this.currentRoute.includes(route)
    }

}
