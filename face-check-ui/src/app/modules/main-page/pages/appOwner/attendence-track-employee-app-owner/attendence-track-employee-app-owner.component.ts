import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../../additionalServices/auth-service";
import { WorkerAttendanceControllerService } from "../../../../../services/services/worker-attendance-controller.service";
import { UserServiceControllerService } from "../../../../../services/services/user-service-controller.service";
import { PageResponseAttendanceResponse } from "../../../../../services/models/page-response-attendance-response";
import { AttendanceResponse } from "../../../../../services/models/attendance-response";
import { AddOvertimeRequest } from "../../../../../services/models/add-overtime-request";
import { OvertimeResponse } from "../../../../../services/models/overtime-response";
import { catchError, of } from "rxjs";

interface Photo {
  url: string;
  type: string;
  time: string;
}

@Component({
  selector: 'app-attendence-track-employee-app-owner',
  templateUrl: './attendence-track-employee-app-owner.component.html',
  styleUrl: './attendence-track-employee-app-owner.component.scss'
})
export class AttendenceTrackEmployeeAppOwnerComponent implements OnInit {

  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';

  // Attendance Data
  attendanceList: AttendanceResponse[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  showDeleteModal: boolean = false;
  selectedAttendanceId: number = 0;
  attendanceToDelete: AttendanceResponse | null = null;
  deleting: boolean = false;

  // Pagination
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  isFirst: boolean = true;
  isLast: boolean = true;

  // Overtime Modal
  showOvertimeModal: boolean = false;
  selectedAttendance: AttendanceResponse | null = null;
  isSubmitting: boolean = false;
  overtimeForm: {
    overtimeHours: number | null;
    reason: string;
  } = {
    overtimeHours: null,
    reason: ''
  };

  // Photo Modal
  showPhotoModal: boolean = false;
  photos: Photo[] = [];
  loadingPhotos: boolean = false;
  selectedDate: string = '';
  fullscreenImage: string = '';

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
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    this.userService.findWorkerFullName().subscribe({
      next: (response) => {
        if (response?.fullName) {
          this.userName = response.fullName;
        }
      },
      error: (error) => console.error('Error loading user name:', error)
    });

    this.userService.findWorkerCompanyName().subscribe({
      next: (response) => {
        if (response?.companyName) {
          this.companyName = response.companyName;
        }
      },
      error: (error) => console.error('Error loading company name:', error)
    });

    this.userService.findWorkerFullContactInformation().subscribe({
      next: (response) => {
        if (response?.photoUrl) {
          this.userPhotoUrl = response.photoUrl;
        }
      },
      error: (error) => console.error('Error loading user photo:', error)
    });
  }

  loadAttendance(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.attendanceService.getAllAttendanceForAppOwner({
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

  // Overtime Modal
  openOvertimeModal(attendance: AttendanceResponse): void {
    this.selectedAttendance = attendance;
    this.showOvertimeModal = true;
    this.resetOvertimeForm();
    this.clearMessages();
  }

  closeOvertimeModal(): void {
    this.showOvertimeModal = false;
    this.selectedAttendance = null;
    this.resetOvertimeForm();
  }

  resetOvertimeForm(): void {
    this.overtimeForm = {
      overtimeHours: null,
      reason: ''
    };
  }

  submitOvertime(): void {
    if (!this.selectedAttendance?.attendanceId ||
      !this.overtimeForm.overtimeHours ||
      !this.overtimeForm.reason) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    this.isSubmitting = true;
    this.clearMessages();

    const request: AddOvertimeRequest = {
      attendanceId: this.selectedAttendance.attendanceId,
      overtimeHours: this.overtimeForm.overtimeHours,
      reason: this.overtimeForm.reason.trim()
    };

    this.attendanceService.addManualOvertime({
      body: request
    }).subscribe({
      next: (response: OvertimeResponse) => {
        this.isSubmitting = false;

        if (response.isSuccessful) {
          this.successMessage = response.message || 'Overtime added successfully!';
          this.closeOvertimeModal();
          this.loadAttendance();

          setTimeout(() => {
            this.successMessage = '';
          }, 5000);
        } else {
          this.errorMessage = response.message || 'Failed to add overtime';
        }
      },
      error: (error) => {
        console.error('Error adding overtime:', error);
        this.errorMessage = error.error?.message || 'Failed to add overtime. Please try again.';
        this.isSubmitting = false;
      }
    });
  }

  openDeleteModal(attendance: AttendanceResponse): void {
    if (!attendance.attendanceId) {
      this.errorMessage = 'Cannot delete: Attendance ID is missing';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.selectedAttendanceId = attendance.attendanceId;
    this.attendanceToDelete = attendance;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedAttendanceId = 0;
    this.attendanceToDelete = null;
  }

  deleteAttendance(): void {
    if (!this.selectedAttendanceId) {
      this.errorMessage = 'No attendance selected for deletion';
      return;
    }

    this.deleting = true;
    this.errorMessage = '';

    this.attendanceService.deleteAttendanceRecord({
      attendanceId: this.selectedAttendanceId
    }).subscribe({
      next: () => {
        this.deleting = false;
        this.closeDeleteModal();

        // Показываем успешное сообщение
        this.errorMessage = ''; // Очищаем ошибки
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.innerHTML = '<i class="fas fa-check-circle"></i> Attendance record deleted successfully!';
        document.querySelector('.attendance-container')?.prepend(successDiv);

        setTimeout(() => {
          successDiv.remove();
        }, 3000);

        // Перезагружаем список
        this.loadAttendance();
      },
      error: (error) => {
        this.deleting = false;
        console.error('Delete error:', error);

        if (error.status === 404) {
          this.errorMessage = 'Attendance record not found';
        } else if (error.status === 403) {
          this.errorMessage = 'You do not have permission to delete this record';
        } else {
          this.errorMessage = `Failed to delete: ${error.error?.message || 'Unknown error'}`;
        }
      }
    });
  }

  // ✅ ОБНОВЛЕННЫЙ Photo Modal - используем attendanceId!
  openPhotoModal(attendance: AttendanceResponse): void {
    this.selectedAttendance = attendance;
    this.showPhotoModal = true;

    // Устанавливаем дату из checkInTime (read-only)
    if (attendance.checkInTime) {
      this.selectedDate = attendance.checkInTime.split('T')[0];
    } else {
      this.selectedDate = new Date().toISOString().split('T')[0];
    }

    // ✅ СРАЗУ загружаем фото для ЭТОЙ КОНКРЕТНОЙ ЗАПИСИ
    this.loadPhotosForAttendance();
  }

  closePhotoModal(): void {
    this.showPhotoModal = false;
    this.selectedAttendance = null;
    this.photos = [];
    this.selectedDate = '';
  }

  // ✅ НОВЫЙ метод - грузим фото по attendanceId
  loadPhotosForAttendance(): void {
    if (!this.selectedAttendance?.attendanceId) {
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

  // ❌ Старый метод - оставляем для совместимости
  extractTime(url: string): string {
    try {
      const match = url.match(/(\d{2})-(\d{2})-(\d{2})/);
      if (match) {
        return `${match[1]}:${match[2]}:${match[3]}`;
      }
      return '';
    } catch (e) {
      return '';
    }
  }

  onImageError(event: any): void {
    event.target.src = 'assets/placeholder.png';
  }

  // Fullscreen
  openFullscreen(imageUrl: string): void {
    this.fullscreenImage = imageUrl;
  }

  closeFullscreen(): void {
    this.fullscreenImage = '';
  }

  getCurrentDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  // Pagination
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

  // Helper Methods
  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  formatTime(dateTime: string | undefined): string {
    if (!dateTime) return 'N/A';
    return new Date(dateTime).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDateShort(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
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
    if (!amount && amount !== 0) return '$0.00';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatHours(hours: number | undefined): string {
    if (!hours && hours !== 0) return '0h';
    return `${hours.toFixed(1)}h`;
  }

  protected readonly Math = Math;
}
