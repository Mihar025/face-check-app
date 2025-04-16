import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {
  AdminControllerService,
  AuthenticationService,
  CompanyControllerService,
  UserServiceControllerService, WorkerAttendanceControllerService, WorkScheduleControllerService
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
import {LocalTime} from "../../../../../services/models/local-time";
import {SetWorkerSchedule$Params} from "../../../../../services/fn/work-schedule-controller/set-worker-schedule";
import {WorkSchedulerResponse} from "../../../../../services/models/work-scheduler-response";
import {
  GetWorkerPersonalInformation$Params
} from "../../../../../services/fn/user-service-controller/get-worker-personal-information";
import {WorkerPersonalInformationResponse} from "../../../../../services/models/worker-personal-information-response";
import {PunchInUpdateRequest} from "../../../../../services/models/punch-in-update-request";
import {UpdatePunchInTime$Params} from "../../../../../services/fn/admin-controller/update-punch-in-time";
import {ChangePunchInForWorker$Params} from "../../../../../services/fn/admin-controller/change-punch-in-for-worker";
import {ChangePunchOutForWorker$Params} from "../../../../../services/fn/admin-controller/change-punch-out-for-worker";
import {PromoteToForeman$Params} from "../../../../../services/fn/company-controller/promote-to-foreman";
import {
  DemoteFromForemanToUser$Params
} from "../../../../../services/fn/company-controller/demote-from-foreman-to-user";
import {PromoteToAdmin$Params} from "../../../../../services/fn/company-controller/promote-to-admin";
import {
  DemoteFromAdminToForeman$Params
} from "../../../../../services/fn/company-controller/demote-from-admin-to-foreman";

@Component({
  selector: 'app-manage-employees',
  templateUrl: './manage-employees.component.html',
  styleUrl: './manage-employees.component.scss'
})
export class ManageEmployeesComponent implements OnInit {
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
  // for update in
  newCheckInTime: string = '';
  // for change out
  dateWhenWorkerDidntMakePunchOut: string = '';
  newPunchOutDate: string = '';
  newPunchOutTime: LocalTime = {};
  // for change in
  dateWhenWorkerDidntMakePunchIn: string = '';
  newPunchInDate: string = '';
  newPunchInTime: LocalTime = {};



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
  activeTimeTab: 'updatePunchIn' | 'changeNewPunchIn' | 'changeNewPunchOut' = 'updatePunchIn';


  endTime: LocalTime = {};
  startTime: LocalTime = {};

  showScheduleForm: boolean = false;

  constructor(
    private authService: AuthService,
    private authenticationControllerService: AuthenticationService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private workScheduleController: WorkScheduleControllerService,
    private adminControllerService:AdminControllerService,
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

  updatePunchIn(){
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const punchInUpdateRequest: PunchInUpdateRequest = {
      newCheckInTIme: this.newCheckInTime
    }

    const params: UpdatePunchInTime$Params = {
      workerId: this.selectedEmployeeId,
      body: punchInUpdateRequest
    }

    this.adminControllerService.updatePunchInTime(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = "Punch time was updated successfully!";
      },
      error => {
        this.loading = false;
        this.errorMessage = "Failed to update punch time: " + (error.message || 'Unknown error');
        console.log('Error in method')
      }
    )
  }


  changeNewPunchIn() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.dateWhenWorkerDidntMakePunchIn || !this.newPunchInDate) {
      this.errorMessage = "Please fill in all required date fields";
      this.loading = false;
      return;
    }

    const formattedDateWhenMissed = `${this.dateWhenWorkerDidntMakePunchIn}T00:00:00`;

    const requestData = {
      dateWhenWorkerDidntMakePunchIn: formattedDateWhenMissed,
      newPunchInDate: this.newPunchInDate,
      newPunchInTime: `${(this.newPunchInTime.hour || 0).toString().padStart(2, '0')}:${(this.newPunchInTime.minute || 0).toString().padStart(2, '0')}:00`,
      workerId: this.selectedEmployeeId
    };

    const params: ChangePunchInForWorker$Params = {
      workerId: this.selectedEmployeeId,
      body: requestData as any
    };

    this.adminControllerService.changePunchInForWorker(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = "Punch time was changed successfully!";
      },
      error => {
        this.loading = false;
        this.errorMessage = "Failed to change punch time: " + (error.message || 'Unknown error');
        console.error('Error details:', error);
      }
    );
  }
  changeNewPunchOut() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.dateWhenWorkerDidntMakePunchOut || !this.newPunchOutDate) {
      this.errorMessage = "Please fill in all required date fields";
      this.loading = false;
      return;
    }

    const formattedDateWhenMissed = `${this.dateWhenWorkerDidntMakePunchOut}T00:00:00`;

    const requestData = {
      dateWhenWorkerDidntMakePunchOut: formattedDateWhenMissed,
      newPunchOutDate: this.newPunchOutDate,
      newPunchOutTime: `${(this.newPunchOutTime.hour || 0).toString().padStart(2, '0')}:${(this.newPunchOutTime.minute || 0).toString().padStart(2, '0')}:00`,
      workerId: this.selectedEmployeeId
    };

    const params: ChangePunchOutForWorker$Params = {
      workerId: this.selectedEmployeeId,
      body: requestData as any
    };

    this.adminControllerService.changePunchOutForWorker(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = "Punch out time was changed successfully!";
      },
      error => {
        this.loading = false;
        this.errorMessage = "Failed to change punch out time: " + (error.message || 'Unknown error');
        console.error('Error details:', error);
      }
    );
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
          this.companyName2 = response.companyName;
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

  openEmployeeInfoModal(workerId: number | undefined) {
    if (workerId === undefined) {
      this.errorMessage = "Cannot show employee info: Employee ID is not defined";
      return;
    }

    this.selectedEmployeeId = workerId;
    this.selectedEmployee = this.employees.find(emp => emp.workerId === workerId) || null;
    this.showEmployeeInfoModal = true;
    this.showScheduleForm = false;

    this.findWorkerPersonalInformation(workerId);
  }

  closeEmployeeInfoModal() {
    this.showEmployeeInfoModal = false;
    this.employeePersonalInfo = null;
    this.employeeGeneratedSchedule = null;
  }

  toggleScheduleForm() {
    this.showScheduleForm = !this.showScheduleForm;
    this.startTime = {};
    this.endTime = {};
  }

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
        this.closeAddEmployeeModal();
        this.resetForm();
        this.loadAllEmployeesRelatedToCertainCompany();
      },
      error => {
        this.errorMessage = 'Cannot register new employee: ' + (error.message || 'Unknown error');
        this.loading = false;
      }
    );
  }

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
        this.loadAllEmployeesRelatedToCertainCompany();
      },
      error => {
        this.errorMessage = 'Cannot fire employee: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

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
    this.router.navigate(['verification/employee'], {
      queryParams: { id: workerId }
    });
  }

  promoteToForeman(){
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const params: PromoteToForeman$Params = {
      employeeId: this.selectedEmployeeId
    }
    this.companyService.promoteToForeman(params).subscribe(
      () => {
        this.successMessage = 'Successfully promoted to FOREMAN role'
        this.openEmployeeInfoModal(this.selectedEmployeeId);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot promote to Foreman role' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  promoteToAdmin(){
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const params: PromoteToAdmin$Params = {
      employeeId: this.selectedEmployeeId
    }
    this.companyService.promoteToAdmin$Response(params).subscribe(
      () => {
        this.successMessage = 'Successfully promoted to ADMIN role'
        this.openEmployeeInfoModal(this.selectedEmployeeId);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot promote to ADMIN role' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  demoteToUser(){
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const params: DemoteFromForemanToUser$Params = {
      workerId: this.selectedEmployeeId
    }
    this.companyService.demoteFromForemanToUser(params).subscribe(
      () => {
        this.successMessage = 'Successfully demoted to USER role'
        this.openEmployeeInfoModal(this.selectedEmployeeId);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot demote to User role' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  demoteToForeman(){
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const params: DemoteFromAdminToForeman$Params = {
      workerId: this.selectedEmployeeId
    }
    this.companyService.demoteFromAdminToForeman(params).subscribe(
      () => {
        this.successMessage = 'Successfully demoted to FOREMAN role'
        this.openEmployeeInfoModal(this.selectedEmployeeId);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot demote to Foreman role' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }
}
