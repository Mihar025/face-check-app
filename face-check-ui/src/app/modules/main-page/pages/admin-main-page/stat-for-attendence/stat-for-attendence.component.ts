import {Component, OnInit} from '@angular/core';
import {AttendanceResponse} from "../../../../../services/models/attendance-response";
import {AuthService} from "../../../additionalServices/auth-service";
import {WorkerAttendanceControllerService} from "../../../../../services/services/worker-attendance-controller.service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {PageResponseAttendanceResponse} from "../../../../../services/models/page-response-attendance-response";
import {catchError, of} from "rxjs";

@Component({
  selector: 'app-stat-for-attendence',
  templateUrl: './stat-for-attendence.component.html',
  styleUrl: './stat-for-attendence.component.scss'
})
export class StatForAttendenceComponent implements OnInit{

  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';

  // Данные посещаемости
  attendanceList: AttendanceResponse[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  // Пагинация
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  isFirst: boolean = true;
  isLast: boolean = true;

  // Modal для фотографий
  showPhotoModal: boolean = false;
  selectedAttendance: AttendanceResponse | null = null;
  photos: Array<{url: string, type: string, time: string}> = [];
  selectedDate: string = '';
  fullscreenImage: string = '';
  loadingPhotos: boolean = false;

  constructor(
    private authService: AuthService,
    private attendanceService: WorkerAttendanceControllerService,
    private userService: UserServiceControllerService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }
    this.loadAttendance();
    this.getUserPhoto();
    this.loadUserFullName();
    this.loadCompanyName();
  }

  loadAttendance(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.attendanceService.getAllAttendanceForAdmin({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response: PageResponseAttendanceResponse) => {
        this.attendanceList = response.content || [];
        this.totalPages = response.totalPages || 0;
        this.totalElements = response.totalElement || 0;
        this.isFirst = response.first || true;
        this.isLast = response.last || true;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading attendance:', error);
        this.errorMessage = 'Failed to load attendance data. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadUserFullName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response && response.fullName) {
          this.userName = response.fullName;
        }
      },
      error => {
        console.error('Error loading user full name', error);
      }
    );
  }

  loadCompanyName() {
    this.userService.findWorkerCompanyName().subscribe(
      response => {
        if (response && response.companyName) {
          this.companyName = response.companyName;
        }
      },
      error => {
        console.error('Error loading company name', error);
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

  // Методы пагинации
  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.page = pageNumber;
      this.loadAttendance();
    }
  }

  nextPage(): void {
    if (!this.isLast) {
      this.page++;
      this.loadAttendance();
    }
  }

  previousPage(): void {
    if (!this.isFirst) {
      this.page--;
      this.loadAttendance();
    }
  }

  changePageSize(newSize: number): void {
    this.size = newSize;
    this.page = 0;
    this.loadAttendance();
  }

  // ✅ ИСПРАВЛЕННЫЙ метод - используем attendanceId!
  openPhotoModal(attendance: AttendanceResponse): void {
    this.selectedAttendance = attendance;
    this.showPhotoModal = true;

    // Устанавливаем дату из checkInTime
    if (attendance.checkInTime) {
      this.selectedDate = attendance.checkInTime.split('T')[0];
    } else {
      this.selectedDate = new Date().toISOString().split('T')[0];
    }

    // ✅ СРАЗУ загружаем фото для ЭТОЙ КОНКРЕТНОЙ ЗАПИСИ
    this.loadPhotosForAttendance();
  }

  // ✅ НОВЫЙ метод - грузим фото по attendanceId
  loadPhotosForAttendance(): void {
    if (!this.selectedAttendance || !this.selectedAttendance.attendanceId) {
      console.error('No attendance ID found!');
      return;
    }

    this.loadingPhotos = true;
    this.photos = [];

    // ✅ Используем НОВЫЙ метод с attendanceId
    this.attendanceService.getPhotosByAttendanceId({
      attendanceId: this.selectedAttendance.attendanceId
    }).pipe(
      catchError(error => {
        console.error('Error loading photos:', error);
        return of(null);
      })
    ).subscribe({
      next: (response) => {
        console.log('Photos response:', response);

        if (response) {
          // ✅ Добавляем check-in фото
          if (response.checkInPhotoUrl) {
            this.photos.push({
              url: response.checkInPhotoUrl,
              type: 'punch-in',
              time: response.checkInTime || ''
            });
          }

          // ✅ Добавляем check-out фото
          if (response.checkOutPhotoUrl) {
            this.photos.push({
              url: response.checkOutPhotoUrl,
              type: 'punch-out',
              time: response.checkOutTime || ''
            });
          }
        }

        console.log('Formatted photos:', this.photos);
        this.loadingPhotos = false;
      },
      error: (error) => {
        console.error('Error loading photos:', error);
        this.loadingPhotos = false;
      }
    });
  }

  // ✅ Форматирование времени из строки "HH:mm:ss"
  formatTimeOnly(timeString: string | undefined): string {
    if (!timeString) return '';

    try {
      const parts = timeString.split(':');
      if (parts.length >= 2) {
        const hours = parseInt(parts[0]);
        const minutes = parts[1];
        const ampm = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;
        return `${displayHours}:${minutes} ${ampm}`;
      }
      return timeString;
    } catch (e) {
      console.error('Error formatting time:', e);
      return timeString;
    }
  }

  openFullscreen(url: string): void {
    this.fullscreenImage = url;
  }

  closeFullscreen(): void {
    this.fullscreenImage = '';
  }

  closeModal(): void {
    this.showPhotoModal = false;
    this.selectedAttendance = null;
    this.photos = [];
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/no-photo-placeholder.png';
  }

  // Вспомогательные методы
  formatDateTime(dateTime: string | undefined): string {
    if (!dateTime) return 'N/A';
    return new Date(dateTime).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  formatCurrency(amount: number | undefined): string {
    if (!amount) return '$0.00';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatHours(hours: number | undefined): string {
    if (!hours) return '0h';
    return `${hours.toFixed(2)}h`;
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, this.page - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  preventClose(event: MouseEvent): void {
    event.stopPropagation();
  }

  Math = Math;
}
