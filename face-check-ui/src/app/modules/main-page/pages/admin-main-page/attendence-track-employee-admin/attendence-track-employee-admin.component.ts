import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {WorkerAttendanceControllerService} from "../../../../../services/services/worker-attendance-controller.service";
import {PageResponseAttendanceResponse} from "../../../../../services/models/page-response-attendance-response";
import {AttendanceResponse} from "../../../../../services/models/attendance-response";
import {FileControllerService} from "../../../../../services/services/file-controller.service";
import {catchError, of} from "rxjs";

@Component({
  selector: 'app-attendence-track-employee-admin',
  templateUrl: './attendence-track-employee-admin.component.html',
  styleUrl: './attendence-track-employee-admin.component.scss'
})
export class AttendenceTrackEmployeeAdminComponent implements OnInit{

  userName: string = 'Admin User';
  companyName: string = 'My Company';
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
  photos: Array<{url: string, type: string}> = [];
  selectedDate: string = new Date().toISOString().split('T')[0];
  fullscreenImage: string = '';
  loadingPhotos: boolean = false;

  constructor(
    private authService: AuthService,
    private attendanceService: WorkerAttendanceControllerService,
    private fileService: FileControllerService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }
    this.loadAttendance();
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

  // Модальное окно с фотографиями
  openPhotoModal(attendance: AttendanceResponse): void {
    this.selectedAttendance = attendance;
    this.showPhotoModal = true;

    // Устанавливаем дату из checkInTime
    if (attendance.checkInTime) {
      this.selectedDate = attendance.checkInTime.split('T')[0];
    } else {
      this.selectedDate = new Date().toISOString().split('T')[0];
    }

    this.loadPhotosForWorker();
  }

  loadPhotosForWorker(): void {
    if (!this.selectedAttendance || !this.selectedAttendance.workerId) {
      return;
    }

    this.loadingPhotos = true;
    this.photos = [];

    this.fileService.getWorkersAttendancePhoto({
      workerId: this.selectedAttendance.workerId
    }).pipe(
      catchError(error => {
        console.error('Error loading photos:', error);
        return of([]);
      })
    ).subscribe({
      next: (response) => {
        if (response && response.length > 0) {
          const allPhotos = response.map(item => ({
            url: item.photoUrl || '',
            type: this.extractType(item.photoUrl || '')
          }));

          // Фильтруем по выбранной дате
          this.photos = allPhotos.filter(photo => this.matchesDate(photo.url));
        }
        this.loadingPhotos = false;
      },
      error: (error) => {
        console.error('Error loading photos:', error);
        this.loadingPhotos = false;
      }
    });
  }

  matchesDate(url: string): boolean {
    const dateMatch = url.match(/(\d{8})/);
    if (dateMatch) {
      const year = dateMatch[1].slice(0, 4);
      const month = dateMatch[1].slice(4, 6);
      const day = dateMatch[1].slice(6, 8);
      const photoDate = `${year}-${month}-${day}`;
      return photoDate === this.selectedDate;
    }
    return false;
  }

  extractType(url: string): string {
    return url.includes('punch-in') ? 'punch-in' : 'punch-out';
  }

  extractTime(url: string): string {
    const match = url.match(/\d{6}(?=\.jpg)/);
    if (match) {
      const time = match[0];
      const hours = parseInt(time.slice(0, 2));
      const minutes = time.slice(2, 4);
      const ampm = hours >= 12 ? 'PM' : 'AM';
      const displayHours = hours % 12 || 12;
      return `${displayHours}:${minutes} ${ampm}`;
    }
    return '';
  }

  setToday(): void {
    this.selectedDate = this.getCurrentDate();
    this.loadPhotosForWorker();
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

  getCurrentDate(): string {
    return new Date().toISOString().split('T')[0];
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

  // Для использования Math.min в шаблоне
  Math = Math;
}
