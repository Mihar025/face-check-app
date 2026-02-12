import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";
import {LocalTime} from "../../../../../services/models/local-time";
import {FileControllerService} from "../../../../../services/services/file-controller.service";
import {forkJoin, of, Subscription} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {UserDataService} from "../../../../components/user-data-service/user-data-service";
import {WorkerAttendanceControllerService} from "../../../../../services/services/worker-attendance-controller.service";

@Component({
  selector: 'app-employee-attendance',
  templateUrl: './employee-attendance.component.html',
  styleUrl: './employee-attendance.component.scss'
})
export class EmployeeAttendanceComponent implements OnInit,OnDestroy {

  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';

  photo: string = '';
  photos: Array<{url: string, type: string, time: string}> = [];
  companyId: number = 0;


  employees: Array<any> = [];
  selectedEmployee: any = null;
  showPhotoModal: boolean = false;
  selectedDate: string = new Date().toISOString().split('T')[0];
  fullscreenImage: string = '';

  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  // Property for storing all photos of all employees
  allEmployeesPhotos: Map<number, Array<{url: string, type: string}>> = new Map();
  loading: boolean = false;

  private subscriptions = new Subscription();


  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private awsService: FileControllerService,
    private companyService: CompanyControllerService,
    public userDataService: UserDataService,
    public attendanceService: WorkerAttendanceControllerService,
  ) {}

  async ngOnInit(): Promise<void> {
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
    // First load company ID, then everything else
    try {
      this.companyId = await this.loadAdminsCompanyId();
      await this.loadAllEmployeesAndPhotos();
    } catch (error) {
      console.error('Error during initialization:', error);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

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


  private async loadAllEmployeesAndPhotos(): Promise<void> {
    this.loading = true;

    // Step 1: Load all company employees с пагинацией
    this.companyService.getAllEmployees({
      companyId: this.companyId,
      page: this.page,        // ИЗМЕНЕНО
      size: this.size         // ИЗМЕНЕНО
    }).subscribe(
      response => {
        this.employees = response.content || [];
        this.totalPages = response.totalPages || 0;      // ДОБАВЛЕНО
        this.totalElements = response.totalElement || 0; // ДОБАВЛЕНО

        // Step 2: Load photos for each employee
        this.loadAllPhotosForEmployees();
      },
      error => {
        console.error('Error loading employees:', error);
        this.loading = false;
      }
    );
  }

  changePage(newPage: number): void {
    this.page = newPage;
    this.loadAllEmployeesAndPhotos();
  }

  // Load photos for all employees
  private loadAllPhotosForEmployees() {
    if (this.employees.length === 0) {
      this.loading = false;
      return;
    }

    // Create Observable array for loading each employee's photos
    const photoRequests = this.employees.map(employee =>
      this.awsService.getWorkersAttendancePhoto({
        workerId: employee.workerId
      }).pipe(
        catchError(error => {
          console.error(`Error loading photos for employee ${employee.workerId}:`, error);
          return of([]); // Return empty array in case of error
        })
      )
    );

    // Execute all requests in parallel
    forkJoin(photoRequests).subscribe(
      (allPhotosResponses) => {
        // Process responses and save photos for each employee
        this.employees.forEach((employee, index) => {
          const photosResponse = allPhotosResponses[index];
          if (photosResponse && photosResponse.length > 0) {
            const employeePhotos = photosResponse.map(item => ({
              url: item.photoUrl || '',
              type: this.extractType(item.photoUrl || '')
            }));

            // Save photos in Map by workerId
            this.allEmployeesPhotos.set(employee.workerId, employeePhotos);

            // Add photo count information to employee
            employee.photosCount = employeePhotos.length;
            employee.hasPhotos = employeePhotos.length > 0;
          } else {
            this.allEmployeesPhotos.set(employee.workerId, []);
            employee.photosCount = 0;
            employee.hasPhotos = false;
          }
        });

        this.loading = false;
        console.log('All photos loaded successfully', this.allEmployeesPhotos);
      },
      error => {
        console.error('Error loading photos:', error);
        this.loading = false;
      }
    );
  }

  openPhotoModal(employee: any) {
    this.selectedEmployee = employee;
    this.showPhotoModal = true;
    this.loadPhotosForDate();
  }
/*
  loadPhotosForDate() {
    if (!this.selectedEmployee) return;

    // Get photos from loaded cache
    const employeePhotos =
        this.allEmployeesPhotos.get(this.selectedEmployee.workerId) || [];

    // Filter by selected date - Fixed to match exact date
    this.photos = employeePhotos.filter(photo => this.matchesDate(photo.url));
  }
*/

  loadPhotosForDate(): void {
    if (!this.selectedEmployee) return;

    this.photos = [];

    this.attendanceService.getWorkerPhotosByDateList({
      workerId: this.selectedEmployee.workerId,
      date: this.selectedDate
    }).pipe(
      catchError(error => {
        console.error('Error loading photos:', error);
        return of([]);
      })
    ).subscribe(responses => {
      if (responses) {
        for (const r of responses) {
          if (r.checkInPhotoUrl) {
            this.photos.push({
              url: r.checkInPhotoUrl,
              type: 'Check In',
              time: r.checkInTime || ''
            });
          }
          if (r.checkOutPhotoUrl) {
            this.photos.push({
              url: r.checkOutPhotoUrl,
              type: 'Check Out',
              time: r.checkOutTime || ''
            });
          }
        }
      }
    });
  }


  matchesDate(url: string): boolean {
    const dateMatch = url.match(/(\d{8})/);
    if (dateMatch) {
      // Extract date from URL (format: YYYYMMDD)
      const year = dateMatch[1].slice(0, 4);
      const month = dateMatch[1].slice(4, 6);
      const day = dateMatch[1].slice(6, 8);
      const photoDate = `${year}-${month}-${day}`;

      // Compare with selected date directly
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

  openFullscreen(url: string) {
    this.fullscreenImage = url;
  }

  closeFullscreen() {
    this.fullscreenImage = '';
  }

  closeModal() {
    this.showPhotoModal = false;
    this.selectedEmployee = null;
  }

  handleImageError(event: any) {
    event.target.style.display = 'none';
    const placeholder = event.target.parentElement.querySelector('.avatar-placeholder');
    if (placeholder) {
      placeholder.style.display = 'flex';
    }
  }

  onImageError(event: any) {
    event.target.src = 'assets/images/no-photo-placeholder.png';
  }

  // Helper methods for improved UI
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

    const day = date.getDate();
    const month = months[date.getMonth()];
    const year = date.getFullYear();

    // American format: Month Day, Year
    return `${month} ${day}, ${year}`;
  }

  setToday(): void {
    this.selectedDate = this.getCurrentDate();
    this.loadPhotosForDate();
  }

  getTotalPhotosCount(): number {
    let total = 0;
    this.allEmployeesPhotos.forEach(photos => {
      total += photos.length;
    });
    return total;
  }

  getRoleLabel(role: string): string {
    const roleLabels: {[key: string]: string} = {
      'ADMIN': 'Administrator',
      'USER': 'Employee',
      'FOREMAN': 'Foreman',
      'MANAGER': 'Manager'
    };
    return roleLabels[role] || role;
  }

  // Additional method to format date for display in American format (MM/DD/YYYY)
  formatDateForInput(dateString: string): string {
    if (!dateString) return '';

    const date = new Date(dateString + 'T00:00:00');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const year = date.getFullYear();

    return `${month}/${day}/${year}`;
  }

  // Method to parse American date input (MM/DD/YYYY) to ISO format
  parseAmericanDate(dateString: string): string {
    if (!dateString) return '';

    const parts = dateString.split('/');
    if (parts.length === 3) {
      const month = parts[0].padStart(2, '0');
      const day = parts[1].padStart(2, '0');
      const year = parts[2];
      return `${year}-${month}-${day}`;
    }
    return dateString;
  }
}
