import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from "../../main-page/additionalServices/auth-service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-app-owner-sidebar',
  templateUrl: './app-owner-sidebar.component.html',
  styleUrl: './app-owner-sidebar.component.scss'
})
export class AppOwnerSidebarComponent implements OnInit{

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
