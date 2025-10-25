import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {WorkerAttendanceControllerService} from "../../../../../services/services/worker-attendance-controller.service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {NotificationControllerService} from "../../../../../services/services/notification-controller.service";
import {NotificationResponse} from "../../../../../services/models/notification-response";
import {PageResponseNotificationResponse} from "../../../../../services/models/page-response-notification-response";
import {
  GetTodaysNotifications$Params
} from "../../../../../services/fn/notification-controller/get-todays-notifications";
import {UserDataService} from "../../../../components/user-data-service/user-data-service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-notification-admin-page',
  templateUrl: './notification-admin-page.component.html',
  styleUrl: './notification-admin-page.component.scss'
})
export class NotificationAdminPageComponent implements OnInit, OnDestroy{



  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';
  message: string = '';
  successMessage: string = '';
  errorMessage: string =  '';
  loading: boolean = false;
  isMobileMenuOpen = false;

  notifications: NotificationResponse[] = [];
  notificationsPage: number = 0;
  notificationsSize: number = 10;
  notificationsTotalPages: number = 0;
  notificationsTotalElements: number = 0;
  notificationsLoading: boolean = false;
  showNotifications: boolean = false;

  private subscriptions = new Subscription();


  constructor(
    private authService: AuthService,
    private notificationService: NotificationControllerService,
    private userService: UserServiceControllerService,
    public userDataService: UserDataService
  ) {}



  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }
    this.subscriptions.add(
      this.userDataService.userName$.subscribe(name => {
        this.userName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.companyName$.subscribe(name => {
         this.companyName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.userPhoto$.subscribe(photo => {
         this.userPhotoUrl = photo;
      })
    );

    this.loadTodaysNotifications();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }





  createNotification(): void {
    if (!this.message || this.message.trim() === '') {
      this.errorMessage = 'Please enter a message';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.userService.findWorkerCompanyIdByAuthentication().subscribe({
      next: (response) => {
        if (!response || !response.companyId) {
          this.errorMessage = 'Failed to get company ID';
          this.loading = false;
          return;
        }

        const companyId = response.companyId;

        const params = {
          companyId: companyId,
          body: {
            message: this.message
          }
        };

        this.notificationService.createNotification(params).subscribe({
          next: () => {
            this.successMessage = 'Notification successfully sent to all employees!';
            this.errorMessage = '';
            this.message = '';
            this.loading = false;

            // Обновляем список уведомлений
            this.loadTodaysNotifications(); // ← ДОБАВЬ ЭТО!
          },
          error: (error) => {
            console.error('Error creating notification', error);
            this.errorMessage = 'Failed to send notification. Please try again.';
            this.successMessage = '';
            this.loading = false;
          }
        });
      },
      error: (error) => {
        console.error('Error getting company ID', error);
        this.errorMessage = 'Failed to get company information';
        this.loading = false;
      }
    });
  }

  formatNotificationDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }


  loadTodaysNotifications(): void {
    this.notificationsLoading = true;

    // Получаем companyId
    this.userService.findWorkerCompanyIdByAuthentication().subscribe({
      next: (response) => {
        if (!response || !response.companyId) {
          this.errorMessage = 'Failed to get company ID';
          this.notificationsLoading = false;
          return;
        }

        const companyId = response.companyId;

        // Загружаем уведомления
        const params: GetTodaysNotifications$Params = {
          companyId: companyId,
          page: this.notificationsPage,
          size: this.notificationsSize
        };

        this.notificationService.getTodaysNotifications(params).subscribe({
          next: (response: PageResponseNotificationResponse) => {
            this.notifications = response.content || [];
            this.notificationsTotalPages = response.totalPages || 0;
            this.notificationsTotalElements = response.totalElement || 0;
            this.notificationsLoading = false;
            this.showNotifications = true;
            console.log('Loaded notifications:', this.notifications);
          },
          error: (error) => {
            console.error('Error loading notifications:', error);
            this.errorMessage = 'Failed to load notifications';
            this.notificationsLoading = false;
          }
        });
      },
      error: (error) => {
        console.error('Error getting company ID:', error);
        this.errorMessage = 'Failed to get company information';
        this.notificationsLoading = false;
      }
    });
  }

  changeNotificationsPage(newPage: number): void {
    this.notificationsPage = newPage;
    this.loadTodaysNotifications();
  }

}
