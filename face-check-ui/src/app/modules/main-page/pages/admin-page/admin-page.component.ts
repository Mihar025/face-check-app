import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../additionalServices/auth-service';
import { UserServiceControllerService } from "../../../../services/services/user-service-controller.service";
import { AdminControllerService } from "../../../../services/services/admin-controller.service";
import { NotificationControllerService } from "../../../../services/services/notification-controller.service";
import { NotificationResponse } from "../../../../services/models/notification-response";
import { PageResponseNotificationResponse } from "../../../../services/models/page-response-notification-response";

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {
  // User data - from your original code
  userName: string = 'John Doe';
  companyName: string = '';
  totalEmployees: number = 0;
  totalWorksites: number = 0;
  userPhotoUrl: string = '';


  notificationsPage: number = 0;
  notificationsSize: number = 5;
  notificationsTotalPages: number = 0;
  notificationsTotalElements: number = 0;

  // Additional simple data
  currentDate: Date = new Date();

  // New properties for notifications
  notifications: NotificationResponse[] = [];
  isLoadingNotifications: boolean = false;
  companyId: number = 0;

  // NEW PROPERTY: Track if user has a company
  hasCompany: boolean = false;

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private adminService: AdminControllerService,
    private notificationService: NotificationControllerService
  ) { }

  ngOnInit(): void {
    // Your existing authentication check
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

    // Load your existing data
    this.loadUserFullName();
    this.checkIfCompanyExists(); // NEW: Check if company exists
    this.getUserPhoto();
  }

  // NEW METHOD: Check if admin has a company
  checkIfCompanyExists(): void {
    // First try to load company ID
    this.userService.findWorkerCompanyIdByAuthentication().subscribe(
      response => {
        if (response && response.companyId && response.companyId > 0) {
          this.companyId = response.companyId;
          this.hasCompany = true;

          // Only load company-related data if company exists
          this.loadCompanyName();
          this.loadTotalEmployees();
          this.loadTotalWorksites();
          this.loadTodaysNotifications();
        } else {
          this.hasCompany = false;
          this.companyName = '';
        }
      },
      error => {
        console.error('Error checking company existence:', error);
        // Fallback - try to load company name to double-check
        this.loadCompanyName();
      }
    );
  }

  // Your existing methods - unchanged
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
        if (response && response.companyName && response.companyName.trim() !== '') {
          this.companyName = response.companyName;
          this.hasCompany = true;

          // If we found company name but don't have ID yet, load it
          if (!this.companyId) {
            this.loadAdminsCompanyId().then(id => {
              if (id > 0) {
                this.companyId = id;
                this.loadTodaysNotifications();
              }
            });
          }
        } else {
          this.hasCompany = false;
          this.companyName = '';
        }
      },
      error => {
        console.error('Error loading company name:', error);
        this.hasCompany = false;
      }
    );
  }

  loadTotalEmployees(): void {
    if (!this.hasCompany) return;

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
    if (!this.hasCompany) return;

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

  // Your existing method for loading company ID
  private async loadAdminsCompanyId(): Promise<number> {
    try {
      const response = await this.userService.findWorkerCompanyIdByAuthentication().toPromise();
      if (response && response.companyId) {
        this.companyId = response.companyId;
        return response.companyId;
      }
      return 0;
    } catch (error) {
      console.error('Error loading company Id', error);
      return 0;
    }
  }
// Load today's notifications from backend
  loadTodaysNotifications(): void {
    if (!this.companyId || !this.hasCompany) {
      console.log('Company not available, skipping notifications');
      return;
    }

    this.isLoadingNotifications = true;

    this.notificationService.getTodaysNotifications({
      companyId: this.companyId,
      page: this.notificationsPage,  // ← ИСПОЛЬЗУЙ ПЕРЕМЕННУЮ
      size: this.notificationsSize   // ← ИСПОЛЬЗУЙ ПЕРЕМЕННУЮ
    }).subscribe({
      next: (response: PageResponseNotificationResponse) => {
        this.notifications = response.content || [];
        this.notificationsTotalPages = response.totalPages || 0;     // ← ДОБАВЬ
        this.notificationsTotalElements = response.totalElement || 0; // ← ДОБАВЬ
        this.isLoadingNotifications = false;
        console.log('Loaded notifications:', this.notifications);
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.isLoadingNotifications = false;
        this.notifications = [];
      }
    });
  }

  // Helper method to get notification icon based on type/activity
  getNotificationIcon(notification: NotificationResponse): string {
    const message = notification.message?.toLowerCase() || '';

    // Punch in/out notifications
    if (message.includes('punch in') || message.includes('made punch in')) {
      return 'fas fa-sign-in-alt';
    } else if (message.includes('punch out') || message.includes('made punch out')) {
      return 'fas fa-sign-out-alt';
    }
    // Employee registration
    else if (message.includes('was successfully registered') && !message.includes('worksite') && !message.includes('company')) {
      return 'fas fa-user-plus';
    }
    // Worksite creation
    else if (message.includes('worksite') && message.includes('was successfully registered')) {
      return 'fas fa-map-marked-alt';
    }
    // Company registration
    else if (message.includes('company') && message.includes('was successfully registered')) {
      return 'fas fa-building';
    }
    // Paystub generation
    else if (message.includes('paystub') && message.includes('was successfully generated')) {
      return 'fas fa-file-invoice-dollar';
    }
    // Default for other notifications
    else {
      return 'fas fa-bell';
    }
  }

  // Helper method to get notification CSS class
  getNotificationClass(notification: NotificationResponse): string {
    const message = notification.message?.toLowerCase() || '';

    if (message.includes('punch in') || message.includes('made punch in')) {
      return 'check-in';
    } else if (message.includes('punch out') || message.includes('made punch out')) {
      return 'check-out';
    } else if (message.includes('paystub') && message.includes('was successfully generated')) {
      return 'paystub';
    } else if (message.includes('was successfully registered') || message.includes('was successfully generated')) {
      return 'new-user'; // Green for successful operations
    } else {
      return 'update'; // Purple for other updates
    }
  }

  // Helper method to format notification time
  getNotificationTime(notification: NotificationResponse): string {
    if (!notification.createdAt) return 'Unknown time';

    const notificationDate = new Date(notification.createdAt);
    const now = new Date();
    const diffMs = now.getTime() - notificationDate.getTime();
    const diffMins = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));

    if (diffMins < 1) {
      return 'Just now';
    } else if (diffMins < 60) {
      return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
    } else if (diffHours < 24) {
      return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    } else {
      return notificationDate.toLocaleDateString();
    }
  }

  trackNotification(index: number, notification: NotificationResponse): any {
    return notification.notificationId || index;
  }

  // Pagination methods
  changeNotificationsPage(newPage: number): void {
    if (newPage >= 0 && newPage < this.notificationsTotalPages) {
      this.notificationsPage = newPage;
      this.loadTodaysNotifications();
    }
  }

  nextNotificationsPage(): void {
    if (this.notificationsPage < this.notificationsTotalPages - 1) {
      this.notificationsPage++;
      this.loadTodaysNotifications();
    }
  }

  previousNotificationsPage(): void {
    if (this.notificationsPage > 0) {
      this.notificationsPage--;
      this.loadTodaysNotifications();
    }
  }

// Method to refresh notifications manually
  refreshNotifications(): void {
    if (this.hasCompany) {
      this.notificationsPage = 0; // ← ДОБАВЬ СБРОС СТРАНИЦЫ
      this.loadTodaysNotifications();
    }
  }


  // Method to delete a notification
  deleteNotification(notificationId: number): void {
    if (!notificationId) return;

    this.notificationService.deleteNotification({
      id: notificationId
    }).subscribe({
      next: () => {
        // Remove from local array
        this.notifications = this.notifications.filter(n => n.notificationId !== notificationId);
        console.log('Notification deleted successfully');
      },
      error: (error) => {
        console.error('Error deleting notification:', error);
      }
    });
  }

}
