import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../additionalServices/auth-service';
import { UserServiceControllerService } from "../../../../services/services/user-service-controller.service";
import { AdminControllerService } from "../../../../services/services/admin-controller.service";

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {
  userName: string = 'John Doe';
  companyName: string = 'FaceCheck Inc.';
  totalEmployees: number = 0;
  totalWorksites: number = 0;
  userPhotoUrl: string = '';


  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private adminService: AdminControllerService

  ) { }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();

      return;
    }

    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN') {
      let targetUrl = '/';
      if (userRole === 'USER') {
        targetUrl = '/main-page/user';
      }
      window.location.href = targetUrl;
      return;
    }

    this.loadUserFullName();
    this.loadCompanyName();
    this.loadTotalEmployees();
    this.loadTotalWorksites();
    this.getUserPhoto();

  }

  logout(): void {
    this.authService.logout();
  }

  loadUserFullName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response && response.fullName) {
          this.userName = response.fullName;
        }
      },
      error => {
        console.error('Error loading user full name:', error);
      }
    );
  }

  loadCompanyName(): void {
    this.userService.findWorkerCompanyName().subscribe(
      response => {
        if (response && response.companyName) {
          this.companyName = response.companyName;
        }
      },
      error => {
        console.error('Error loading company name:', error);
      }
    );
  }

  loadTotalEmployees(): void {
    this.adminService.getTotalEmployeesCount().subscribe(
      count => {
        this.totalEmployees = count;
      },
      error => {
        console.error('Error loading total employees count:', error);
      }
    );
  }

  loadTotalWorksites(): void {
    this.adminService.getTotalWorksitesCount().subscribe(
      count => {
        this.totalWorksites = count;
      },
      error => {
        console.error('Error loading total worksites count:', error);
      }
    );
  }

  getUserPhoto(): void {
    this.userService.findWorkerFullContactInformation().subscribe(
      response => {
        if (response && response.photoUrl) {
          this.userPhotoUrl = response.photoUrl;
        }
      },
      error => {
        console.error('Error loading user photo:', error);
      }
    );
  }
}
