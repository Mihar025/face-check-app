import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../additionalServices/auth-service';
import {
  CompanyControllerService,
  FileControllerService,
  UserServiceControllerService
} from '../../../../../services/services';
import { RelatedUserInCompanyResponse } from '../../../../../services/models/related-user-in-company-response';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

type AttendancePhoto = { url: string; type: 'punch-in' | 'punch-out' | 'unknown' };

interface EmployeeWithExtras extends RelatedUserInCompanyResponse {
  photosCount?: number;
  hasPhotos?: boolean;
  // photoUrl?: string; // если в вашей модели уже есть, можно убрать комментарий
}

@Component({
  selector: 'app-employees-attendance-app-owner',
  templateUrl: './employees-attendance-app-owner.component.html',
  styleUrls: ['./employees-attendance-app-owner.component.scss']
})
export class EmployeesAttendanceAppOwnerComponent implements OnInit {
  // Header / user
  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';

  // Employees & photos
  employees: EmployeeWithExtras[] = [];
  allEmployeesPhotos: Map<number, AttendancePhoto[]> = new Map();

  // UI state
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Modal / viewer
  showPhotoModal: boolean = false;
  selectedEmployee: EmployeeWithExtras | null = null;
  selectedDate: string = new Date().toISOString().split('T')[0];
  photos: AttendancePhoto[] = [];
  fullscreenImage: string = '';

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private fileService: FileControllerService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const role = this.authService.getUserRole?.();
    // Разреши доступ нужным глобальным ролям
    if (role !== 'AppOwner' && role !== 'ADMIN' && role !== 'SUPER_ADMIN' && role !== 'OWNER') {
      // редирект, если не хватает прав
      window.location.href = '/main-page/user';
      return;
    }

    this.loadHeader();
    this.loadAllEmployeesGlobal();
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Header
  // ────────────────────────────────────────────────────────────────────────────────
  private loadHeader(): void {
    this.userService.findWorkerFullName().subscribe({
      next: (r) => (this.userName = r?.fullName || this.userName),
      error: () => {}
    });
    this.userService.findWorkerCompanyName().subscribe({
      next: (r) => (this.companyName = r?.companyName || this.companyName),
      error: () => {}
    });
    this.userService.findWorkerFullContactInformation().subscribe({
      next: (r) => (this.userPhotoUrl = r?.photoUrl || this.userPhotoUrl),
      error: () => {}
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Load employees globally via /company/find-all-employees
  // ────────────────────────────────────────────────────────────────────────────────
  private loadAllEmployeesGlobal(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const pageSizeBackend = 500; // подстрой под лимит бэка
    const acc: EmployeeWithExtras[] = [];

    const fetchPage = (page: number) => {
      this.companyService.findAllEmployees({ page, size: pageSizeBackend }).subscribe({
        next: (res: any) => {
          const content: EmployeeWithExtras[] = res?.content || res?.items || [];
          acc.push(...content);

          const totalElements =
            res?.totalElements ??
            res?.totalElement ??
            res?.total_items ??
            (typeof res?.total === 'number' ? res.total : undefined);

          const totalPages =
            res?.totalPages ??
            res?.total_pages ??
            (typeof totalElements === 'number'
              ? Math.ceil(totalElements / pageSizeBackend)
              : undefined);

          const hasMoreByPages =
            typeof totalPages === 'number' ? page + 1 < totalPages : false;
          const hasMoreByContent = content.length === pageSizeBackend;

          if (hasMoreByPages || hasMoreByContent) {
            fetchPage(page + 1);
            return;
          }

          // всё, собрали
          this.employees = acc;
          this.successMessage = `Loaded ${this.employees.length} employees`;

          // грузим фото пачками
          this.loadAllPhotosForEmployees();
        },
        error: (err) => {
          this.errorMessage = `Failed to load employees: ${err?.message || 'Unknown error'}`;
          this.loading = false;
        }
      });
    };

    fetchPage(0);
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Load photos for all employees
  // ────────────────────────────────────────────────────────────────────────────────
  private loadAllPhotosForEmployees(): void {
    if (!this.employees.length) {
      this.loading = false;
      return;
    }

    const requests = this.employees.map(emp =>
      this.fileService.getWorkersAttendancePhoto({ workerId: emp.workerId! }).pipe(
        catchError(err => {
          // не падаем — просто пустой массив для проблемных юзеров
          console.error(`Photos error for worker ${emp.workerId}:`, err);
          return of([]);
        })
      )
    );

    forkJoin(requests).subscribe({
      next: (responses: any[]) => {
        this.employees.forEach((emp, idx) => {
          const arr = Array.isArray(responses[idx]) ? responses[idx] : [];
          const mapped: AttendancePhoto[] = arr.map((item: any) => {
            const url = item?.photoUrl || '';
            return { url, type: this.extractType(url) };
          });

          this.allEmployeesPhotos.set(emp.workerId!, mapped);
          emp.photosCount = mapped.length;
          emp.hasPhotos = mapped.length > 0;
        });

        this.loading = false;
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: (err) => {
        console.error('forkJoin photos error:', err);
        this.loading = false;
      }
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Modal & date filtering
  // ────────────────────────────────────────────────────────────────────────────────
  openPhotoModal(employee: EmployeeWithExtras): void {
    this.selectedEmployee = employee;
    this.showPhotoModal = true;
    this.loadPhotosForDate();
  }

  closeModal(): void {
    this.showPhotoModal = false;
    this.selectedEmployee = null;
    this.photos = [];
  }

  loadPhotosForDate(): void {
    if (!this.selectedEmployee) return;
    const all = this.allEmployeesPhotos.get(this.selectedEmployee.workerId!) || [];
    this.photos = all.filter(p => this.matchesDate(p.url));
  }

  setToday(): void {
    this.selectedDate = this.getCurrentDate();
    this.loadPhotosForDate();
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Photo utils
  // ────────────────────────────────────────────────────────────────────────────────
  matchesDate(url: string): boolean {
    // ожидаем YYYYMMDD в url
    const dateMatch = url.match(/(\d{8})/);
    if (!dateMatch) return false;

    const raw = dateMatch[1]; // YYYYMMDD
    const y = raw.slice(0, 4);
    const m = raw.slice(4, 6);
    const d = raw.slice(6, 8);
    const asIso = `${y}-${m}-${d}`; // YYYY-MM-DD

    return asIso === this.selectedDate;
  }

  extractType(url: string): 'punch-in' | 'punch-out' | 'unknown' {
    if (!url) return 'unknown';
    if (url.includes('punch-in')) return 'punch-in';
    if (url.includes('punch-out')) return 'punch-out';
    return 'unknown';
  }

  extractTime(url: string): string {
    // ожидаем HHMMSS.jpg
    const m = url.match(/\b(\d{6})(?=\.jpg|\.jpeg|\.png|$)/i);
    if (!m) return '';
    const time = m[1]; // HHMMSS
    const HH = parseInt(time.slice(0, 2), 10);
    const MM = time.slice(2, 4);
    const ampm = HH >= 12 ? 'PM' : 'AM';
    const displayH = HH % 12 || 12;
    return `${displayH}:${MM} ${ampm}`;
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Images / fullscreen
  // ────────────────────────────────────────────────────────────────────────────────
  handleImageError(event: any): void {
    event.target.style.display = 'none';
    const placeholder = event.target.parentElement?.querySelector('.avatar-placeholder');
    if (placeholder) placeholder.style.display = 'flex';
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/no-photo-placeholder.png';
  }

  openFullscreen(url: string): void {
    this.fullscreenImage = url;
  }

  closeFullscreen(): void {
    this.fullscreenImage = '';
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Header helpers for template
  // ────────────────────────────────────────────────────────────────────────────────
  getCurrentDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString + 'T00:00:00');
    const months = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return `${months[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`;
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Misc
  // ────────────────────────────────────────────────────────────────────────────────
  getTotalPhotosCount(): number {
    let total = 0;
    this.allEmployeesPhotos.forEach(arr => (total += arr.length));
    return total;
  }

  getRoleLabel(role?: string): string {
    const map: Record<string, string> = {
      ADMIN: 'Administrator',
      USER: 'Employee',
      FOREMAN: 'Foreman',
    };
    return role ? (map[role] || role) : '';
  }




  trackByEmployee(index: number, e: any) {
    return e?.workerId ?? e?.email ?? index;
  }

}
