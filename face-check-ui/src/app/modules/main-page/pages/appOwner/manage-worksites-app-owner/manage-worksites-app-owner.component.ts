import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";
import {WorkSiteControllerService} from "../../../../../services/services/work-site-controller.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {WorkSiteResponse} from "../../../../../services/models/work-site-response";
import {WorkSiteRequest} from "../../../../../services/models/work-site-request";
import {PageResponseWorkSiteResponse} from "../../../../../services/models/page-response-work-site-response";

@Component({
  selector: 'app-manage-worksites-app-owner',
  templateUrl: './manage-worksites-app-owner.component.html',
  styleUrl: './manage-worksites-app-owner.component.scss'
})
export class ManageWorksitesAppOwnerComponent implements OnInit {

  worksites: WorkSiteResponse[] = [];
  totalElements: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  page: number = 0;
  size: number = 10;

  // User info
  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';

  // Modal controls
  isRegisterModalOpen: boolean = false;
  isEditModalOpen: boolean = false;
  isDetailsModalOpen: boolean = false;
  isActiveWorkersModalOpen: boolean = false;
  isClosedDaysModalOpen: boolean = false;

  // Messages
  registerErrorMessage = '';
  registerSuccessMessage = '';
  isRegisterLoading = false;
  isLoading = false;

  // Forms
  registerForm: FormGroup;
  editForm: FormGroup;

  // Selected worksite
  selectedWorksite: WorkSiteResponse | null = null;
  selectedWorksiteDetails: any = null;
  activeWorkers: any[] = [];
  closedDays: any[] = [];

  // Search and filter
  searchTerm: string = '';
  filterActive: string = 'all'; // all, active, inactive

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private workSiteService: WorkSiteControllerService
  ) {
    // Register form
    this.registerForm = this.fb.group({
      address: ['', [Validators.required]],
      allowedRadius: ['', [Validators.required, Validators.min(1)]],
      companyId: ['', [Validators.required]],
      latitude: ['', [Validators.required, Validators.min(-90), Validators.max(90)]],
      longitude: ['', [Validators.required, Validators.min(-180), Validators.max(180)]],
      workDayEnd: ['', [Validators.required]],
      workDayStart: ['', [Validators.required]],
      workSiteName: ['', [Validators.required]],
    });

    // Edit form
    this.editForm = this.fb.group({
      address: ['', [Validators.required]],
      allowedRadius: ['', [Validators.required, Validators.min(1)]],
      latitude: ['', [Validators.required, Validators.min(-90), Validators.max(90)]],
      longitude: ['', [Validators.required, Validators.min(-180), Validators.max(180)]],
      workDayEnd: ['', [Validators.required]],
      workDayStart: ['', [Validators.required]],
      workSiteName: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }
    this.loadAllWorkSites();
  }

  loadAllWorkSites(): void {
    this.isLoading = true;
    this.workSiteService.findAllWorkSitesForAppOwner({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response: PageResponseWorkSiteResponse) => {
        console.log('Response from server:', response);

        // Обработка worksites с преобразованием времени
        this.worksites = (response.content || []).map(worksite => {
          // Если время приходит как строка "HH:MM:SS", преобразуем в объект
          if (typeof worksite.workDayStart === 'string') {
            const startParts = (worksite.workDayStart as any).split(':');
            worksite.workDayStart = {
              hour: parseInt(startParts[0]) || 0,
              minute: parseInt(startParts[1]) || 0,
              second: parseInt(startParts[2]) || 0,
              nano: 0
            };
          }

          if (typeof worksite.workDayEnd === 'string') {
            const endParts = (worksite.workDayEnd as any).split(':');
            worksite.workDayEnd = {
              hour: parseInt(endParts[0]) || 0,
              minute: parseInt(endParts[1]) || 0,
              second: parseInt(endParts[2]) || 0,
              nano: 0
            };
          }

          console.log('Processed worksite:', worksite);
          return worksite;
        });

        this.totalElements = response.totalElement || 0;
        this.totalPages = response.totalPages || 0;
        this.currentPage = response.number || 0;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading worksites:', error);
        this.isLoading = false;
      }
    });
  }

  openRegisterModal(): void {
    this.isRegisterModalOpen = true;
    this.registerErrorMessage = '';
    this.registerSuccessMessage = '';
    this.registerForm.reset();
  }

  closeRegisterModal(): void {
    this.isRegisterModalOpen = false;
    this.registerForm.reset();
    this.registerErrorMessage = '';
    this.registerSuccessMessage = '';
  }

  onRegisterSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.isRegisterLoading = true;
    this.registerErrorMessage = '';
    this.registerSuccessMessage = '';

    const workDayStartValue = this.registerForm.get('workDayStart')?.value;
    const workDayEndValue = this.registerForm.get('workDayEnd')?.value;

    const workSiteRequest: WorkSiteRequest = {
      address: this.registerForm.get('address')?.value,
      allowedRadius: Number(this.registerForm.get('allowedRadius')?.value),
      companyId: Number(this.registerForm.get('companyId')?.value),
      latitude: Number(this.registerForm.get('latitude')?.value),
      longitude: Number(this.registerForm.get('longitude')?.value),
      workDayEnd: `${workDayEndValue}:00` as any,
      workDayStart: `${workDayStartValue}:00` as any,
      workSiteName: this.registerForm.get('workSiteName')?.value
    };

    this.workSiteService.createWorkSite({body: workSiteRequest}).subscribe({
      next: (response) => {
        this.registerSuccessMessage = 'Worksite created successfully!';
        this.isRegisterLoading = false;
        setTimeout(() => {
          this.closeRegisterModal();
          this.loadAllWorkSites();
        }, 1500);
      },
      error: (error) => {
        this.registerErrorMessage = error.error?.message || 'Error creating worksite';
        this.isRegisterLoading = false;
      }
    });
  }

  openEditModal(worksite: WorkSiteResponse): void {
    this.selectedWorksite = worksite;

    // Безопасное получение часов и минут
    const startHour = worksite.workDayStart?.hour || 0;
    const startMinute = worksite.workDayStart?.minute || 0;
    const endHour = worksite.workDayEnd?.hour || 0;
    const endMinute = worksite.workDayEnd?.minute || 0;

    this.editForm.patchValue({
      address: worksite.address,
      allowedRadius: worksite.allowedRadius,
      latitude: worksite.latitude,
      longitude: worksite.longitude,
      workDayEnd: `${endHour.toString().padStart(2, '0')}:${endMinute.toString().padStart(2, '0')}`,
      workDayStart: `${startHour.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}`,
      workSiteName: worksite.workSiteName
    });
    this.isEditModalOpen = true;
  }

  closeEditModal(): void {
    this.isEditModalOpen = false;
    this.selectedWorksite = null;
    this.editForm.reset();
  }

  onEditSubmit(): void {
    if (this.editForm.invalid || !this.selectedWorksite) {
      return;
    }

    // Update name
    if (this.editForm.get('workSiteName')?.value !== this.selectedWorksite.workSiteName) {
      this.workSiteService.updateName({
        workSiteId: this.selectedWorksite.workSiteId!,
        body: { name: this.editForm.get('workSiteName')?.value }
      }).subscribe();
    }

    // Update address
    if (this.editForm.get('address')?.value !== this.selectedWorksite.address) {
      this.workSiteService.updateAddress({
        workSiteId: this.selectedWorksite.workSiteId!,
        body: { address: this.editForm.get('address')?.value }
      }).subscribe();
    }

    // Update location
    this.workSiteService.updateLocation1({
      workSiteId: this.selectedWorksite.workSiteId!,
      body: {
        newLatitude: Number(this.editForm.get('latitude')?.value),
        newLongitude: Number(this.editForm.get('longitude')?.value),
        newRadius: Number(this.editForm.get('allowedRadius')?.value)
      }
    }).subscribe();

    // Update working hours
    const workDayStartValue = this.editForm.get('workDayStart')?.value;
    const workDayEndValue = this.editForm.get('workDayEnd')?.value;

    this.workSiteService.updateWorkingHours({
      workSiteId: this.selectedWorksite.workSiteId!,
      body: {
        newStart: `${workDayStartValue}:00` as any,
        newEnd: `${workDayEndValue}:00` as any
      }
    }).subscribe({
      next: () => {
        this.closeEditModal();
        this.loadAllWorkSites();
      }
    });
  }

  deleteWorksite(worksite: WorkSiteResponse): void {
    if (confirm(`Are you sure you want to delete worksite "${worksite.workSiteName}"?`)) {
      this.workSiteService.deleteWorkSiteById({workSiteId: worksite.workSiteId!}).subscribe({
        next: () => {
          this.loadAllWorkSites();
        },
        error: (error) => {
          console.error('Error deleting worksite:', error);
        }
      });
    }
  }

  toggleWorksiteStatus(worksite: WorkSiteResponse): void {
    this.workSiteService.setActive({
      workSiteId: worksite.workSiteId!,
      body: { active: !worksite.isActive }
    }).subscribe({
      next: () => {
        this.loadAllWorkSites();
      },
      error: (error) => {
        console.error('Error toggling status:', error);
      }
    });
  }

  viewDetails(worksite: WorkSiteResponse): void {
    this.workSiteService.findWorkSiteAllInformation({workSiteId: worksite.workSiteId!}).subscribe({
      next: (response) => {
        console.log('Details response:', response);

        // Преобразование времени если нужно
        if (typeof response.workDayStart === 'string') {
          const startParts = (response.workDayStart as any).split(':');
          response.workDayStart = {
            hour: parseInt(startParts[0]) || 0,
            minute: parseInt(startParts[1]) || 0,
            second: parseInt(startParts[2]) || 0,
            nano: 0
          };
        }

        if (typeof response.workDayEnd === 'string') {
          const endParts = (response.workDayEnd as any).split(':');
          response.workDayEnd = {
            hour: parseInt(endParts[0]) || 0,
            minute: parseInt(endParts[1]) || 0,
            second: parseInt(endParts[2]) || 0,
            nano: 0
          };
        }

        this.selectedWorksiteDetails = response;
        this.isDetailsModalOpen = true;
      },
      error: (error) => {
        console.error('Error loading details:', error);
      }
    });
  }

  closeDetailsModal(): void {
    this.isDetailsModalOpen = false;
    this.selectedWorksiteDetails = null;
  }

  viewActiveWorkers(worksite: WorkSiteResponse): void {
    this.workSiteService.getActiveWorkers({
      workSiteId: worksite.workSiteId!,
      page: 0,
      size: 100
    }).subscribe({
      next: (response) => {
        this.activeWorkers = response.content || [];
        this.selectedWorksite = worksite;
        this.isActiveWorkersModalOpen = true;
      },
      error: (error) => {
        console.error('Error loading active workers:', error);
      }
    });
  }

  closeActiveWorkersModal(): void {
    this.isActiveWorkersModalOpen = false;
    this.activeWorkers = [];
  }

  viewClosedDays(worksite: WorkSiteResponse): void {
    this.workSiteService.getWorkSiteClosedDays({
      workSiteId: worksite.workSiteId!,
      page: 0,
      size: 100
    }).subscribe({
      next: (response) => {
        this.closedDays = response.content || [];
        this.selectedWorksite = worksite;
        this.isClosedDaysModalOpen = true;
      },
      error: (error) => {
        console.error('Error loading closed days:', error);
      }
    });
  }

  closeClosedDaysModal(): void {
    this.isClosedDaysModalOpen = false;
    this.closedDays = [];
  }

  addClosedDay(): void {
    const date = prompt('Enter closed date (YYYY-MM-DD):');
    if (date && this.selectedWorksite) {
      this.workSiteService.scheduleInactiveDay({
        workSiteId: this.selectedWorksite.workSiteId!,
        body: { inactiveDate: date }
      }).subscribe({
        next: () => {
          this.viewClosedDays(this.selectedWorksite!);
        },
        error: (error) => {
          console.error('Error adding closed day:', error);
          alert('Error adding closed day');
        }
      });
    }
  }

  removeClosedDay(date: string): void {
    if (this.selectedWorksite && confirm('Remove this closed day?')) {
      this.workSiteService.removeInactiveDay({
        workSiteId: this.selectedWorksite.workSiteId!,
        body: { inactiveDate: date }
      }).subscribe({
        next: () => {
          this.viewClosedDays(this.selectedWorksite!);
        },
        error: (error) => {
          console.error('Error removing closed day:', error);
        }
      });
    }
  }

  setCustomRadius(worksite: WorkSiteResponse): void {
    const radius = prompt('Enter custom radius (in meters):');
    if (radius && !isNaN(Number(radius))) {
      this.workSiteService.setCustomRadius({
        workSiteId: worksite.workSiteId!,
        body: { customRadius: Number(radius) }
      }).subscribe({
        next: () => {
          this.loadAllWorkSites();
        },
        error: (error) => {
          console.error('Error setting custom radius:', error);
          alert('Error setting custom radius');
        }
      });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.page++;
      this.loadAllWorkSites();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.page--;
      this.loadAllWorkSites();
    }
  }

  goToPage(pageNumber: number): void {
    this.page = pageNumber;
    this.loadAllWorkSites();
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.loadAllWorkSites();
  }

  getFilteredWorksites(): WorkSiteResponse[] {
    let filtered = this.worksites;

    // Filter by search term
    if (this.searchTerm) {
      filtered = filtered.filter(w =>
        w.workSiteName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        w.address?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        w.companyName?.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }

    // Filter by active status
    if (this.filterActive === 'active') {
      filtered = filtered.filter(w => w.isActive === true);
    } else if (this.filterActive === 'inactive') {
      filtered = filtered.filter(w => w.isActive === false);
    }

    return filtered;
  }
}
