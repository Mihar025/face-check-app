import {Component, OnInit} from '@angular/core';
import {RemoteVerificationAdminResponse} from "../../../../../services/models/remote-verification-admin-response";
import {RemoteVerificationStatsResponse} from "../../../../../services/models/remote-verification-stats-response";
import {
  PageResponseRemoteVerificationAdminResponse
} from "../../../../../services/models/page-response-remote-verification-admin-response";
import {AuthService} from "../../../additionalServices/auth-service";
import {RemoteWorkerControllerService} from "../../../../../services/services/remote-worker-controller.service";

@Component({
  selector: 'app-remote-workers-admin-component',
  templateUrl: './remote-workers-admin-component.component.html',
  styleUrl: './remote-workers-admin-component.component.scss'
})
export class RemoteWorkersAdminComponentComponent implements OnInit {



  userName: string = 'Admin User';
  companyName: string = 'My Company';
  userPhotoUrl: string = '';

  // Data
  verifications: RemoteVerificationAdminResponse[] = [];
  stats: RemoteVerificationStatsResponse = {
    completedToday: 0,
    missedToday: 0,
    pendingToday: 0,
    totalToday: 0,
    complianceRate: 0
  };

  // Loading
  isLoading: boolean = false;
  statsLoading: boolean = false;
  errorMessage: string = '';

  // Filters
  statusFilter: string = '';
  dateFrom: string = '';
  dateTo: string = '';

  // Pagination
  page: number = 0;
  size: number = 20;
  totalPages: number = 0;
  totalElements: number = 0;
  isFirst: boolean = true;
  isLast: boolean = true;

  // Photo modal
  showPhotoModal: boolean = false;
  selectedVerification: RemoteVerificationAdminResponse | null = null;
  fullscreenImage: string = '';

  Math = Math;

  constructor(
    private authService: AuthService,
    private remoteWorkerService: RemoteWorkerControllerService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const today = new Date().toISOString().split('T')[0];
    this.dateFrom = today;
    this.dateTo = today;

    this.loadStats();
    this.loadVerifications();
  }

  // ==================== DATA LOADING ====================

  loadVerifications(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.remoteWorkerService.getAllVerifications({
      page: this.page,
      size: this.size,
      status: this.statusFilter || undefined,
      dateFrom: this.dateFrom || undefined,
      dateTo: this.dateTo || undefined
    }).subscribe({
      next: (response: PageResponseRemoteVerificationAdminResponse) => {
        this.verifications = response.content || [];
        this.totalPages = response.totalPages || 0;
        this.totalElements = response.totalElement || 0;
        this.isFirst = response.first ?? true;
        this.isLast = response.last ?? true;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading verifications:', error);
        this.errorMessage = 'Failed to load verification data. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadStats(): void {
    this.statsLoading = true;

    this.remoteWorkerService.getVerificationStats({}).subscribe({
      next: (stats: RemoteVerificationStatsResponse) => {
        this.stats = stats;
        this.statsLoading = false;
      },
      error: (error) => {
        console.error('Error loading stats:', error);
        this.statsLoading = false;
      }
    });
  }

  // ==================== FILTERS ====================

  applyFilters(): void {
    this.page = 0;
    this.loadVerifications();
    this.loadStats();
  }

  resetFilters(): void {
    this.statusFilter = '';
    const today = new Date().toISOString().split('T')[0];
    this.dateFrom = today;
    this.dateTo = today;
    this.page = 0;
    this.loadVerifications();
    this.loadStats();
  }

  setDateRange(range: string): void {
    const today = new Date();
    this.dateTo = today.toISOString().split('T')[0];

    switch (range) {
      case 'today':
        this.dateFrom = this.dateTo;
        break;
      case 'week':
        const weekAgo = new Date(today);
        weekAgo.setDate(weekAgo.getDate() - 7);
        this.dateFrom = weekAgo.toISOString().split('T')[0];
        break;
      case 'month':
        const monthAgo = new Date(today);
        monthAgo.setMonth(monthAgo.getMonth() - 1);
        this.dateFrom = monthAgo.toISOString().split('T')[0];
        break;
      case 'all':
        this.dateFrom = '';
        this.dateTo = '';
        break;
    }
    this.applyFilters();
  }

  // ==================== PAGINATION ====================

  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.page = pageNumber;
      this.loadVerifications();
    }
  }

  nextPage(): void {
    if (!this.isLast) {
      this.page++;
      this.loadVerifications();
    }
  }

  previousPage(): void {
    if (!this.isFirst) {
      this.page--;
      this.loadVerifications();
    }
  }

  changePageSize(newSize: number): void {
    this.size = newSize;
    this.page = 0;
    this.loadVerifications();
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

  // ==================== MODALS ====================

  openPhotoModal(v: RemoteVerificationAdminResponse): void {
    this.selectedVerification = v;
    this.showPhotoModal = true;
  }

  closePhotoModal(): void {
    this.showPhotoModal = false;
    this.selectedVerification = null;
  }

  openFullscreen(url: string): void {
    this.fullscreenImage = url;
  }

  closeFullscreen(): void {
    this.fullscreenImage = '';
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/no-photo-placeholder.png';
  }

  preventClose(event: MouseEvent): void {
    event.stopPropagation();
  }

  openGoogleMaps(lat: number | undefined, lng: number | undefined): void {
    if (lat && lng) {
      window.open(`https://www.google.com/maps?q=${lat},${lng}`, '_blank');
    }
  }

  // ==================== FORMATTING ====================

  getStatusClass(status: string | undefined): string {
    switch (status) {
      case 'COMPLETED': return 'status-completed';
      case 'MISSED': return 'status-missed';
      case 'PENDING': return 'status-pending';
      default: return '';
    }
  }

  getStatusIcon(status: string | undefined): string {
    switch (status) {
      case 'COMPLETED': return 'fa-check-circle';
      case 'MISSED': return 'fa-times-circle';
      case 'PENDING': return 'fa-clock';
      default: return 'fa-question-circle';
    }
  }

  getStatusRowClass(status: string | undefined): string {
    switch (status) {
      case 'MISSED': return 'row-missed';
      case 'PENDING': return 'row-pending';
      default: return '';
    }
  }

  formatDateTime(dateTime: string | undefined): string {
    if (!dateTime) return 'N/A';
    return new Date(dateTime).toLocaleString('en-US', {
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

  formatCoords(lat: number | undefined, lng: number | undefined): string {
    if (!lat || !lng) return 'N/A';
    return `${lat.toFixed(4)}, ${lng.toFixed(4)}`;
  }

  getCurrentDate(): string {
    return new Date().toISOString().split('T')[0];
  }
}
