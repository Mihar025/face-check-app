import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {
  AuthenticationService,
  CompanyControllerService,
  UserServiceControllerService, WorkScheduleControllerService
} from "../../../../../services/services";
import {RelatedUserInCompanyResponse} from "../../../../../services/models/related-user-in-company-response";
import {GetAllEmployees$Params} from "../../../../../services/fn/company-controller/get-all-employees";
import {
  PageResponseRelatedUserInCompanyResponse
} from "../../../../../services/models/page-response-related-user-in-company-response";
import {RegistrationRequest} from "../../../../../services/models/registration-request";
import {Register$Params} from "../../../../../services/fn/authentication/register";
import {FireEmployee$Params} from "../../../../../services/fn/company-controller/fire-employee";
import {Router} from "@angular/router";
import {UpdateEmployeeRate$Params} from "../../../../../services/fn/company-controller/update-employee-rate";
import {GetEmployeeRate$Params} from "../../../../../services/fn/company-controller/get-employee-rate";
import {EmployeeSalaryResponse} from "../../../../../services/models/employee-salary-response";
import {UserFullContactInformation} from "../../../../../services/models/user-full-contact-information";
import {LocalTime} from "../../../../../services/models/local-time";
import {WorkerSetScheduleRequest} from "../../../../../services/models/worker-set-schedule-request";
import {SetWorkerSchedule$Params} from "../../../../../services/fn/work-schedule-controller/set-worker-schedule";
import {WorkSchedulerResponse} from "../../../../../services/models/work-scheduler-response";
import {GetUserEmployees$Params} from "../../../../../services/fn/company-controller/get-user-employees";
import {
  GetWorkerPersonalInformation$Params
} from "../../../../../services/fn/user-service-controller/get-worker-personal-information";
import {WorkerPersonalInformationResponse} from "../../../../../services/models/worker-personal-information-response";

@Component({
  selector: 'app-manage-employees',
  templateUrl: './manage-employees.component.html',
  styleUrl: './manage-employees.component.scss'
})
export class ManageEmployeesComponent implements OnInit {
  // Основные данные
  userName = '';
  companyName = '';
  employeeId: number = 0;

  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  page: number = 0;
  size: number = 10;

  companyId: number = 0;
  employees: Array<RelatedUserInCompanyResponse> = [];
  totalElements: number = 0;
  totalPages: number = 0;



  showFireModal: boolean = false;
  showRateModal: boolean = false;
  showAddEmployeeModal: boolean = false;
  showEmployeeInfoModal: boolean = false;
  selectedEmployeeId: number = 0;

  newHourlyRate: number = 0;

  selectedEmployee: RelatedUserInCompanyResponse | null = null;
  employeeRateInfo: EmployeeSalaryResponse | null = null;
  employeePersonalInfo: WorkerPersonalInformationResponse | null = null;
  employeeGeneratedSchedule: WorkSchedulerResponse | null = null;

  companyAddress: string = '';
  companyName2: string = '';
  dateOfBirth?: string = '';
  email: string = '';
  firstName: string = '';
  gender: 'MALE' | 'FEMALE' | 'OTHER' | '' = '';
  homeAddress: string = '';
  lastName: string = '';
  password: string = '';
  phoneNumber: string = '';
  ssn_WORKER?: string = '';
  userPhotoUrl: string = '';


  endTime: LocalTime = {};
  startTime: LocalTime = {};

  showScheduleForm: boolean = false;

  constructor(
    private authService: AuthService,
    private authenticationControllerService: AuthenticationService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private workScheduleController: WorkScheduleControllerService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN') {
      let targetURL = '/';

      if (userRole === 'USER') {
        targetURL = '/main-page/user';
      }
      window.location.href = targetURL;
      return;
    }

    this.loadAdminName();
    this.loadAdminCompany();
    this.loadAllEmployeesRelatedToCertainCompany();
    this.getUserPhoto();

  }

  logout() {
    this.authService.logout();
  }


  loadAdminName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response && response.fullName) {
          this.userName = response.fullName;
        }
      },
      error => {
        console.error('Cannot load username', error);
      }
    );
  }

  loadAdminCompany(): void {
    this.userService.findWorkerCompanyName().subscribe(
      response => {
        if (response && response.companyName) {
          this.companyName = response.companyName;
          this.companyName2 = response.companyName; // Устанавливаем имя компании для формы регистрации
        }
      },
      error => {
        console.error('Cannot load company name', error);
      }
    );
  }

  async loadAllEmployeesRelatedToCertainCompany(): Promise<void> {
    this.loading = true;
    this.errorMessage = '';

    const loadedCompanyId = await this.loadAdminsCompanyId();

    const params: GetAllEmployees$Params = {
      companyId: loadedCompanyId,
      page: this.page,
      size: this.size
    };

    this.companyService.getAllEmployees(params).subscribe(
      (response: PageResponseRelatedUserInCompanyResponse) => {
        this.employees = response.content || [];
        this.totalElements = response.totalElement || 0;
        this.totalPages = response.totalPages || 0;
        this.loading = false;
        },
      error => {
        this.errorMessage = 'Error loading employees: ' + (error.message || 'Unknown Error');
        this.loading = false;
      }
    );
  }

  changePage(newPage: number): void {
    this.page = newPage;
    this.loadAllEmployeesRelatedToCertainCompany();
  }

  // Модальное окно информации о сотруднике
  openEmployeeInfoModal(workerId: number | undefined) {
    if (workerId === undefined) {
      this.errorMessage = "Cannot show employee info: Employee ID is not defined";
      return;
    }

    this.selectedEmployeeId = workerId;
    this.selectedEmployee = this.employees.find(emp => emp.workerId === workerId) || null;
    this.showEmployeeInfoModal = true;
    this.showScheduleForm = false;

    // Загружаем информацию о сотруднике
    this.findWorkerPersonalInformation(workerId);
  }

  closeEmployeeInfoModal() {
    this.showEmployeeInfoModal = false;
    this.employeePersonalInfo = null;
    this.employeeGeneratedSchedule = null;
  }

  toggleScheduleForm() {
    this.showScheduleForm = !this.showScheduleForm;
    // Reset schedule form
    this.startTime = {};
    this.endTime = {};
  }

  // Модальное окно добавления сотрудника
  openAddEmployeeModal() {
    this.resetForm();
    this.showAddEmployeeModal = true;
  }

  closeAddEmployeeModal() {
    this.showAddEmployeeModal = false;
  }

  createNewEmployee() {
    this.loading = true;
    this.errorMessage = '';

    const data: RegistrationRequest = {
      companyAddress: this.companyAddress,
      companyName: this.companyName2,
      dateOfBirth: this.dateOfBirth,
      email: this.email,
      firstName: this.firstName,
      gender: this.gender || 'MALE',
      homeAddress: this.homeAddress,
      lastName: this.lastName,
      password: this.password,
      phoneNumber: this.phoneNumber,
      ssn_WORKER: this.ssn_WORKER
    };

    const params: Register$Params = {
      body: data
    };

    this.authenticationControllerService.register(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = "Employee registered successfully!";
        this.closeAddEmployeeModal(); // Закрываем модальное окно после успеха
        this.resetForm();
        this.loadAllEmployeesRelatedToCertainCompany();
      },
      error => {
        this.errorMessage = 'Cannot register new employee: ' + (error.message || 'Unknown error');
        this.loading = false;
      }
    );
  }

  // Увольнение сотрудника
  openFireModal(workerId: number | undefined) {
    if (workerId === undefined) {
      this.errorMessage = "Cannot fire employee: Employee ID is not defined";
      return;
    }

    this.selectedEmployeeId = workerId;
    this.selectedEmployee = this.employees.find(emp => emp.workerId === workerId) || null;
    this.showFireModal = true;
  }

  cancelFireModal() {
    this.showFireModal = false;
  }

  fireEmployee(workerId: number) {
    this.loading = true;
    this.errorMessage = '';

    const params: FireEmployee$Params = {
      employeeId: workerId
    };

    this.companyService.fireEmployee(params).subscribe(
      () => {
        this.loading = false;
        this.showFireModal = false;
        this.successMessage = "Employee fired successfully.";
        this.loadAllEmployeesRelatedToCertainCompany(); // Обновляем список сотрудников
      },
      error => {
        this.errorMessage = 'Cannot fire employee: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  // Изменение почасовой ставки
  openChangeRateModal(workerId: number | undefined, currentRate: number | undefined) {
    if (workerId === undefined) {
      this.errorMessage = "Cannot change rate: Employee ID is not defined";
      return;
    }

    this.selectedEmployeeId = workerId;
    this.newHourlyRate = currentRate ?? 0;
    this.showRateModal = true;
  }

  cancelRateModal() {
    this.showRateModal = false;
  }

  changeHourlyRate() {
    this.loading = true;
    this.errorMessage = '';
    this.loadAdminsCompanyId().then(companyId => {
      const params: UpdateEmployeeRate$Params = {
        companyId: companyId,
        employeeId: this.selectedEmployeeId,
        body: {
          baseHourlyRate: this.newHourlyRate
        }
      };

      this.companyService.updateEmployeeRate(params).subscribe(
        () => {
          this.loading = false;
          this.showRateModal = false;
          this.successMessage = "Hourly rate updated successfully.";
          this.loadAllEmployeesRelatedToCertainCompany();
        },
        error => {
          this.errorMessage = 'Cannot update hourly rate: ' + (error.message || 'Unknown problem');
          this.loading = false;
        }
      );
    });
  }

  loadEmployeeRateInfo(workerId: number | undefined) {
    if (workerId === undefined) {
      this.errorMessage = "Cannot load employee rate: Employee ID is not defined";
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Получаем ID компании
    this.loadAdminsCompanyId().then(companyId => {
      const params: GetEmployeeRate$Params = {
        companyId: companyId,
        employeeId: workerId
      };

      this.companyService.getEmployeeRate(params).subscribe(
        (response: EmployeeSalaryResponse) => {
          this.employeeRateInfo = response;
          this.loading = false;
        },
        error => {
          this.errorMessage = 'Cannot load employee rate information: ' + (error.message || 'Unknown problem');
          this.loading = false;
        }
      );
    });
  }

  findWorkerPersonalInformation(workerId: number | undefined) {
    if (workerId === undefined) {
      this.errorMessage = "Cannot load employee information: Employee ID is not defined";
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const params: GetWorkerPersonalInformation$Params = {
      employeeId: workerId
    }

    this.userService.getWorkerPersonalInformation(params).subscribe(
      (response: WorkerPersonalInformationResponse) => {
        this.employeePersonalInfo = response;
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot load Employee personal information: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  generateScheduleForWorker() {
    if (!this.selectedEmployeeId) {
      this.errorMessage = "Cannot generate schedule: No employee selected";
      return;
    }

    if (this.startTime.hour === undefined || this.endTime.hour === undefined) {
      this.errorMessage = "Please enter valid start and end times";
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Преобразуем время в строковый формат ISO-8601, который ожидает Jackson для LocalTime
    const startTimeStr = `${this.startTime.hour.toString().padStart(2, '0')}:${(this.startTime.minute || 0).toString().padStart(2, '0')}:00`;
    const endTimeStr = `${this.endTime.hour.toString().padStart(2, '0')}:${(this.endTime.minute || 0).toString().padStart(2, '0')}:00`;

    const data = {
      startTime: startTimeStr,
      endTime: endTimeStr
    };

    const params: SetWorkerSchedule$Params = {
      workerId: this.selectedEmployeeId,
      body: data as any
    };

    this.workScheduleController.setWorkerSchedule(params).subscribe(
      (response: WorkSchedulerResponse) => {
        this.employeeGeneratedSchedule = response;
        this.loading = false;
        this.successMessage = "Schedule generated successfully";
        this.showScheduleForm = false;
      },
      error => {
        this.errorMessage = 'Cannot generate Schedule for Employee: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
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

  private resetForm() {
    this.companyAddress = '';
    this.companyName2 = this.companyName;
    this.dateOfBirth = '';
    this.email = '';
    this.firstName = '';
    this.gender = '';
    this.homeAddress = '';
    this.lastName = '';
    this.password = '';
    this.phoneNumber = '';
    this.ssn_WORKER = '';
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

  moveToVerificationPage(workerId: number | undefined){
    if (!workerId) return;
    this.router.navigate(['verification/admin'], {
      queryParams: { id: workerId }
    });
  }

}
