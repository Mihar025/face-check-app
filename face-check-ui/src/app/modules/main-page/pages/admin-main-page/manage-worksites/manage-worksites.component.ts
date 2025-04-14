import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../../additionalServices/auth-service";
import { UserServiceControllerService } from "../../../../../services/services/user-service-controller.service";
import { WorkSiteControllerService } from "../../../../../services/services/work-site-controller.service";
import { FindAllWorkSites$Params } from "../../../../../services/fn/work-site-controller/find-all-work-sites";
import { PageResponseWorkSiteResponse } from "../../../../../services/models/page-response-work-site-response";
import { WorkSiteResponse } from "../../../../../services/models/work-site-response";
import { CreateWorkSite$Params } from "../../../../../services/fn/work-site-controller/create-work-site";
import { WorkSiteRequest } from "../../../../../services/models/work-site-request";
import { LocalTime } from "../../../../../services/models/local-time";
import { UpdateNameRequest } from "../../../../../services/models/update-name-request";
import { UpdateName$Params } from "../../../../../services/fn/work-site-controller/update-name";
import { UpdateWorkSiteAddress } from "../../../../../services/models/update-work-site-address";
import { UpdateAddress$Params } from "../../../../../services/fn/work-site-controller/update-address";
import { WorkSiteUpdateLocationRequest } from "../../../../../services/models/work-site-update-location-request";
import { WorkSiteUpdateWorkingHoursResponse } from "../../../../../services/models/work-site-update-working-hours-response";
import { UpdateLocation$Params } from "../../../../../services/fn/work-site-controller/update-location";
import { WorkSiteUpdateLocationResponse } from "../../../../../services/models/work-site-update-location-response";
import { UpdateWorkingHours$Params } from "../../../../../services/fn/work-site-controller/update-working-hours";
import { WorkSiteUpdateWorkingHoursRequest } from "../../../../../services/models/work-site-update-working-hours-request";
import { DeleteWorkSiteById$Params } from "../../../../../services/fn/work-site-controller/delete-work-site-by-id";
import { FindWorkSiteAllInformation$Params } from "../../../../../services/fn/work-site-controller/find-work-site-all-information";
import { WorkSiteAllInformationResponse } from "../../../../../services/models/work-site-all-information-response";
import { SetNewCustomRadiusRequest } from "../../../../../services/models/set-new-custom-radius-request";
import { SetCustomRadius$Params } from "../../../../../services/fn/work-site-controller/set-custom-radius";
import { FindWorkSiteById$Params } from "../../../../../services/fn/work-site-controller/find-work-site-by-id";

@Component({
  selector: 'app-manage-worksites',
  templateUrl: './manage-worksites.component.html',
  styleUrl: './manage-worksites.component.scss'
})
export class ManageWorksitesComponent implements OnInit {
  // Основные данные
  userName: string = '';
  companyName: string = '';
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Пагинация
  page: number = 0;
  size: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;

  // Рабочие объекты
  worksites: Array<WorkSiteResponse> = [];
  selectedWorksite: WorkSiteResponse | null = null;
  selectedWorksiteId: number = 0;

  // Модальные окна
  showWorksiteInfoModal: boolean = false;
  showEditWorksiteModal: boolean = false;
  showAddWorksiteModal: boolean = false;
  showDeleteWorksiteModal: boolean = false;

  // Формы в модальных окнах
  showWorkingHoursForm: boolean = false;
  showLocationForm: boolean = false;
  showRadiusForm: boolean = false;
  activeEditTab: string = 'name'; // Для вкладок в редактировании

  // Поля для создания нового рабочего объекта
  address: string = '';
  allowedRadius: number = 0;
  latitude: number = 0;
  longitude: number = 0;
  workDayStart: LocalTime = { hour: 0, minute: 0 };
  workDayEnd: LocalTime = { hour: 0, minute: 0 };
  workSiteName: string = '';

  // Поля для обновления рабочего объекта
  updatedWorkSiteName: string = '';
  updatedWorkSiteAddress: string = '';
  updatedNewLatitude: number = 0;
  updatedNewLongitude: number = 0;
  updatedNewRadius: number = 0;
  newEnd: LocalTime = {};
  newStart: LocalTime = {};
  newCustomRadius: number = 0;

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private workSiteService: WorkSiteControllerService
  ) {
  }

  ngOnInit() {
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
    this.loadAllWorksites();
  }

  logout() {
    this.authService.logout();
  }

  // Загрузка данных профиля
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

  // Загрузка списка рабочих объектов
  loadAllWorksites(): void {
    this.loading = true;
    this.errorMessage = '';

    const params: FindAllWorkSites$Params = {
      page: this.page,
      size: this.size
    };

    this.workSiteService.findAllWorkSites(params).subscribe(
      (response: PageResponseWorkSiteResponse) => {
        this.worksites = response.content || [];
        console.log("Worksites data:", this.worksites);
        this.totalElements = response.totalElement || 0;
        this.totalPages = response.totalPages || 0;
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Error loading worksites: ' + (error.message || 'Unknown Error');
        this.loading = false;
        console.error('Error loading worksites:', error);
      }
    );
  }

  // Пагинация
  changePage(newPage: number): void {
    this.page = newPage;
    this.loadAllWorksites();
  }

  createNewWorkSite() {
    // Преобразуем часы и минуты в строку "HH:mm:ss"
    const formattedWorkDayStart = `${this.padNumber(this.workDayStart?.hour ?? 0)}:${this.padNumber(this.workDayStart?.minute ?? 0)}:00`;
    const formattedWorkDayEnd = `${this.padNumber(this.workDayEnd?.hour ?? 0)}:${this.padNumber(this.workDayEnd?.minute ?? 0)}:00`;

    const newWorksite: any = {
      workSiteName: this.workSiteName,
      address: this.address,
      latitude: this.latitude,
      longitude: this.longitude,
      allowedRadius: this.allowedRadius,
      workDayStart: formattedWorkDayStart,
      workDayEnd: formattedWorkDayEnd
    };

    this.loading = true;
    this.workSiteService.createWorkSite({ body: newWorksite }).subscribe({
      next: (response) => {
        this.successMessage = "Worksite successfully created!";
        this.errorMessage = '';
        this.loading = false;
        this.closeAddWorksiteModal();
        this.loadAllWorksites();
      },
      error: (error) => {
        console.error("Error creating worksite", error);
        this.errorMessage = "Failed to create worksite.";
        this.successMessage = '';
        this.loading = false;
      }
    });
  }

// Утилита для добавления нуля перед числом
  private padNumber(num: number): string {
    return num.toString().padStart(2, '0');
  }


  // Обновление имени рабочего объекта
  updateWorkSiteName() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const data: UpdateNameRequest = {
      name: this.updatedWorkSiteName
    };

    const params: UpdateName$Params = {
      workSiteId: this.selectedWorksiteId,
      body: data
    };

    this.workSiteService.updateName(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = 'Worksite name updated successfully!';
        this.updatedWorkSiteName = '';
        this.closeEditWorksiteModal();
        this.loadAllWorksites();
      },
      error => {
        this.errorMessage = 'Cannot update worksite name: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Cannot update worksite name!', error);
      }
    );
  }

  // Обновление адреса рабочего объекта
  updateWorkSiteAddress() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const data: UpdateWorkSiteAddress = {
      address: this.updatedWorkSiteAddress
    };

    const params: UpdateAddress$Params = {
      workSiteId: this.selectedWorksiteId,
      body: data
    };

    this.workSiteService.updateAddress(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = 'Worksite address updated successfully!';
        this.updatedWorkSiteAddress = '';
        this.closeEditWorksiteModal();
        this.loadAllWorksites();
      },
      error => {
        this.errorMessage = 'Cannot update worksite address: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Cannot update worksite address!', error);
      }
    );
  }

  // Обновление расположения рабочего объекта
  updateWorkSiteLocation() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const data: WorkSiteUpdateLocationRequest = {
      newLatitude: this.updatedNewLatitude,
      newLongitude: this.updatedNewLongitude,
      newRadius: this.updatedNewRadius
    };

    const params: UpdateLocation$Params = {
      workSiteId: this.selectedWorksiteId,
      body: data
    };

    this.workSiteService.updateLocation(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = 'Worksite location updated successfully!';
        this.toggleLocationForm();
        this.loadWorkSiteById(this.selectedWorksiteId);
      },
      error => {
        this.errorMessage = 'Cannot update worksite location: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Cannot update worksite location!', error);
      }
    );
  }

  // Обновление радиуса рабочего объекта
  updateWorkSiteRadius() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const data: SetNewCustomRadiusRequest = {
      customRadius: this.newCustomRadius
    };

    const params: SetCustomRadius$Params = {
      workSiteId: this.selectedWorksiteId,
      body: data
    };

    this.workSiteService.setCustomRadius(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = 'Worksite radius updated successfully!';
        this.toggleRadiusForm();
        this.loadWorkSiteById(this.selectedWorksiteId);
      },
      error => {
        this.errorMessage = 'Cannot update worksite radius: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Cannot update worksite radius!', error);
      }
    );
  }

  // Обновление рабочих часов рабочего объекта
  updateWorkSiteWorkingHours() {
    if (this.newStart.hour === undefined || this.newEnd.hour === undefined) {
      this.errorMessage = "Please enter valid start and end times";
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const newStartHour = this.newStart.hour ?? 0;
    const newStartMinute = this.newStart.minute ?? 0;
    const newEndHour = this.newEnd.hour ?? 0;
    const newEndMinute = this.newEnd.minute ?? 0;

    const newStartTimeStr = `${newStartHour.toString().padStart(2, '0')}:${newStartMinute.toString().padStart(2, '0')}:00`;
    const newEndTimeStr = `${newEndHour.toString().padStart(2, '0')}:${newEndMinute.toString().padStart(2, '0')}:00`;


    const data = {
      newEnd: newEndTimeStr,
      newStart: newStartTimeStr
    };

    const params: UpdateWorkingHours$Params = {
      workSiteId: this.selectedWorksiteId,
      body: data as any
    };

    this.workSiteService.updateWorkingHours(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = 'Working hours updated successfully!';
        this.toggleWorkingHoursForm();
        this.loadWorkSiteById(this.selectedWorksiteId);
      },
      error => {
        this.errorMessage = 'Cannot update worksite working hours: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Cannot update worksite working hours!', error);
      }
    );
  }

  // Удаление рабочего объекта
  deleteWorkSite() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const params: DeleteWorkSiteById$Params = {
      workSiteId: this.selectedWorksiteId
    };

    this.workSiteService.deleteWorkSiteById(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = 'Worksite deleted successfully!';
        this.closeDeleteWorksiteModal();
        this.loadAllWorksites();
      },
      error => {
        this.errorMessage = 'Cannot delete worksite: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Cannot delete worksite!', error);
      }
    );
  }

  // Загрузка информации о рабочем объекте
  loadWorkSiteAllInformation() {
    this.loading = true;
    this.errorMessage = '';

    const params: FindWorkSiteAllInformation$Params = {
      workSiteId: this.selectedWorksiteId
    };

    this.workSiteService.findWorkSiteAllInformation(params).subscribe(
      (response: WorkSiteAllInformationResponse) => {
        console.log('Found Worksite Information!', response);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Error loading worksite information: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Error loading worksite information', error);
      }
    );
  }

  // Загрузка информации о рабочем объекте по ID
  loadWorkSiteById(workSiteId: number) {
    this.loading = true;
    this.errorMessage = '';

    const params: FindWorkSiteById$Params = {
      id: workSiteId
    };

    this.workSiteService.findWorkSiteById(params).subscribe(
      (response: WorkSiteResponse) => {
        this.selectedWorksite = response;
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Error loading worksite: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Error loading worksite', error);
      }
    );
  }

  // Управление модальными окнами
  openWorksiteInfoModal(workSiteId: number) {
    this.selectedWorksiteId = workSiteId;
    this.loadWorkSiteById(workSiteId);
    this.showWorkingHoursForm = false;
    this.showLocationForm = false;
    this.showRadiusForm = false;
    this.showWorksiteInfoModal = true;
  }

  closeWorksiteInfoModal() {
    this.showWorksiteInfoModal = false;
    this.selectedWorksite = null;
    this.resetUpdateForms();
  }

  openEditWorksiteModal(workSiteId: number) {
    this.selectedWorksiteId = workSiteId;
    const worksite = this.worksites.find(ws => ws.workSiteId === workSiteId);
    if (worksite) {
      this.selectedWorksite = worksite;
      this.updatedWorkSiteName = worksite.workSiteName || '';
      this.updatedWorkSiteAddress = worksite.address || '';
    }
    this.setActiveEditTab('name');
    this.showEditWorksiteModal = true;
  }

  closeEditWorksiteModal() {
    this.showEditWorksiteModal = false;
    this.selectedWorksite = null;
    this.resetUpdateForms();
  }

  openAddWorksiteModal() {
    this.resetForm();
    this.showAddWorksiteModal = true;
  }

  closeAddWorksiteModal() {
    this.showAddWorksiteModal = false;
    this.resetForm();
  }

  openDeleteWorksiteModal(workSiteId: number) {
    this.selectedWorksiteId = workSiteId;
    const worksite = this.worksites.find(ws => ws.workSiteId === workSiteId);
    if (worksite) {
      this.selectedWorksite = worksite;
    }
    this.showDeleteWorksiteModal = true;
  }

  closeDeleteWorksiteModal() {
    this.showDeleteWorksiteModal = false;
    this.selectedWorksite = null;
  }

  // Управление вкладками и формами
  setActiveEditTab(tab: string) {
    this.activeEditTab = tab;
  }

  toggleWorkingHoursForm() {
    this.showWorkingHoursForm = !this.showWorkingHoursForm;
    if (this.showWorkingHoursForm) {
      this.showLocationForm = false;
      this.showRadiusForm = false;
      // Предзаполняем текущими значениями
      if (this.selectedWorksite) {
        this.newStart = {...this.selectedWorksite.workDayStart};
        this.newEnd = {...this.selectedWorksite.workDayEnd};
      }
    }
  }

  toggleLocationForm() {
    this.showLocationForm = !this.showLocationForm;
    if (this.showLocationForm) {
      this.showWorkingHoursForm = false;
      this.showRadiusForm = false;
      // Предзаполняем текущими значениями
      if (this.selectedWorksite) {
        this.updatedNewLatitude = this.selectedWorksite.latitude || 0;
        this.updatedNewLongitude = this.selectedWorksite.longitude || 0;
        this.updatedNewRadius = this.selectedWorksite.allowedRadius || 0;
      }
    }
  }

  toggleRadiusForm() {
    this.showRadiusForm = !this.showRadiusForm;
    if (this.showRadiusForm) {
      this.showWorkingHoursForm = false;
      this.showLocationForm = false;
      // Предзаполняем текущим значением
      if (this.selectedWorksite) {
        this.newCustomRadius = this.selectedWorksite.allowedRadius || 0;
      }
    }
  }

  resetUpdateForms() {
    this.showWorkingHoursForm = false;
    this.showLocationForm = false;
    this.showRadiusForm = false;
    this.newStart = {};
    this.newEnd = {};
    this.updatedNewLatitude = 0;
    this.updatedNewLongitude = 0;
    this.updatedNewRadius = 0;
    this.newCustomRadius = 0;
    this.updatedWorkSiteName = '';
    this.updatedWorkSiteAddress = '';
  }

  resetForm() {
    this.address = '';
    this.allowedRadius = 0;
    this.latitude = 0;
    this.longitude = 0;
    this.workDayEnd = {};
    this.workDayStart = {};
    this.workSiteName = '';
  }


  formatWorkingHours(start: any, end: any): string {
    const formatTime = (time: any) => {
      if (!time) return '--:--';

      // Если время приходит как строка
      if (typeof time === 'string') {
        const parts = time.split(':');
        if (parts.length >= 2) {
          return `${parts[0]}:${parts[1]}`;
        }
        return time;
      }

      // Если время - объект
      const hour = time.hour !== undefined ? this.padNumber(time.hour) : '00';
      const minute = time.minute !== undefined ? this.padNumber(time.minute) : '00';
      return `${hour}:${minute}`;
    };

    return `${formatTime(start)} - ${formatTime(end)}`;
  }
}
