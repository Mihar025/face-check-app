import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {
  AdminControllerService,
  AuthenticationService,
  CompanyControllerService,
  UserServiceControllerService,
  WorkScheduleControllerService
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
import {EmployeeSalaryResponse} from "../../../../../services/models/employee-salary-response";
import {LocalTime} from "../../../../../services/models/local-time";
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
import {DependentsRequest} from "../../../../../services/models/dependents-request";
import {I9DocumentRequest} from "../../../../../services/models/i-9-document-request";
import {UserDataService} from "../../../../components/user-data-service/user-data-service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-manage-employees',
  templateUrl: './manage-employees.component.html',
  styleUrl: './manage-employees.component.scss'
})
export class ManageEmployeesComponent implements OnInit, OnDestroy {
  userName = '';
  companyName = '';
  employeeId: number = 0;

  // Schedule modal states
  showScheduleModal: boolean = false;
  hasExistingSchedule: boolean = false;
  isLoadingSchedule: boolean = false;

  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Search
  searchQuery: string = '';
  filteredEmployees: Array<RelatedUserInCompanyResponse> = [];

  page: number = 0;
  size: number = 10;
  currentPage: number = 0;

  companyId: number = 0;
  employees: Array<RelatedUserInCompanyResponse> = [];
  totalElements: number = 0;
  totalElement: number = 0;
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

  lunchStartTime: LocalTime = { hour: 12, minute: 0 };
  lunchEndTime: LocalTime = { hour: 13, minute: 0 };
  isCompanyPayingLunch: boolean = false;

  showFireModal: boolean = false;
  showRateModal: boolean = false;
  showAddEmployeeModal: boolean = false;
  showEmployeeInfoModal: boolean = false;
  showDeleteModal: boolean = false;
  selectedEmployeeId: number = 0;

  newHourlyRate: number = 0;

  selectedEmployee: RelatedUserInCompanyResponse | null = null;
  employeeRateInfo: EmployeeSalaryResponse | null = null;
  employeePersonalInfo: WorkerPersonalInformationResponse | null = null;
  employeeGeneratedSchedule: WorkSchedulerResponse | null = null;

  // Employee form fields
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
  apt: string = '';
  city: string = '';
  state: string = '';
  zipcode: string = '';
  employmentType: 'W2' | 'CONTRACTOR_1099' = 'W2';
  payFrequency: 'WEEKLY' | 'BIWEEKLY' | 'MONTHLY' = 'BIWEEKLY';
  wcRiskClassCode: string = '';
  filingStatus: 'SINGLE' | 'HEAD_OF_HOUSEHOLD' | 'MARRIED_FILLING_SEPARATELY' | 'MARRIED_FILLING_JOINTLY' = 'SINGLE';
  exemptFromWithholding: boolean = false;
  multipleJobsOrSpouseWorks: boolean = false;
  twoJobsCheckBox: boolean = false;
  livesInNYC: boolean = false;
  isCitizen: boolean = false;
  isNonCitizenNationalOfTheUS: boolean = false;
  isPermanentResident: boolean = false;
  isANonCitizen: boolean = false;
  middleInitial?: string;
  extraWithHoldings?: number;
  dependents?: number;
  dependentsList?: Array<DependentsRequest>;
  dependentsUnder17?: number;
  otherDependents?: number;
  totalDependentsCredit?: number;
  isRehired?: boolean;
  dateWhenRehired?: string;
  enrolledInHealthPlan?: boolean;
  monthlyHealthPremium?: number;
  coverageStartDate?: string;
  adjustmentsSchedule1?: number;
  deductions?: number;
  estimatedItemizedDeductions?: number;
  otherIncome?: number;
  multipleJobsWorksheetLine2a?: number;
  multipleJobsWorksheetLine2b?: number;
  multipleJobsAdditionalWithholding?: number;
  uscisNumber?: string;
  formI94AdmissionNumber?: string;
  passportNumber?: string;
  passportCountryOfIssuance?: string;
  workAuthorizationExpiryDate?: string;
  i9Documents?: Array<I9DocumentRequest>;

  userPhotoUrl: string = '';
  activeTab: 'updatePunchIn' | 'newPunchIn' | 'newPunchOut' = 'updatePunchIn';

  endTime: LocalTime = {};
  startTime: LocalTime = {};

  showScheduleForm: boolean = false;

  private subscriptions = new Subscription();


  // Flexible schedule days
  flexibleDays: Array<{
    day: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
    dayLabel: string;
    startTime: LocalTime;
    endTime: LocalTime;
    lunchStart: LocalTime;
    lunchEnd: LocalTime;
    isCompanyPayingLunch: boolean;
    isDayOff: boolean;
  }> = [
    {
      day: 'MONDAY',
      dayLabel: 'Monday',
      startTime: { hour: 9, minute: 0 },
      endTime: { hour: 17, minute: 0 },
      lunchStart: { hour: 12, minute: 0 },
      lunchEnd: { hour: 13, minute: 0 },
      isCompanyPayingLunch: false,
      isDayOff: false
    },
    {
      day: 'TUESDAY',
      dayLabel: 'Tuesday',
      startTime: { hour: 9, minute: 0 },
      endTime: { hour: 17, minute: 0 },
      lunchStart: { hour: 12, minute: 0 },
      lunchEnd: { hour: 13, minute: 0 },
      isCompanyPayingLunch: false,
      isDayOff: false
    },
    {
      day: 'WEDNESDAY',
      dayLabel: 'Wednesday',
      startTime: { hour: 9, minute: 0 },
      endTime: { hour: 17, minute: 0 },
      lunchStart: { hour: 12, minute: 0 },
      lunchEnd: { hour: 13, minute: 0 },
      isCompanyPayingLunch: false,
      isDayOff: false
    },
    {
      day: 'THURSDAY',
      dayLabel: 'Thursday',
      startTime: { hour: 9, minute: 0 },
      endTime: { hour: 17, minute: 0 },
      lunchStart: { hour: 12, minute: 0 },
      lunchEnd: { hour: 13, minute: 0 },
      isCompanyPayingLunch: false,
      isDayOff: false
    },
    {
      day: 'FRIDAY',
      dayLabel: 'Friday',
      startTime: { hour: 9, minute: 0 },
      endTime: { hour: 17, minute: 0 },
      lunchStart: { hour: 12, minute: 0 },
      lunchEnd: { hour: 13, minute: 0 },
      isCompanyPayingLunch: false,
      isDayOff: false
    },
    {
      day: 'SATURDAY',
      dayLabel: 'Saturday',
      startTime: { hour: 9, minute: 0 },
      endTime: { hour: 17, minute: 0 },
      lunchStart: { hour: 12, minute: 0 },
      lunchEnd: { hour: 13, minute: 0 },
      isCompanyPayingLunch: false,
      isDayOff: true
    },
    {
      day: 'SUNDAY',
      dayLabel: 'Sunday',
      startTime: { hour: 9, minute: 0 },
      endTime: { hour: 17, minute: 0 },
      lunchStart: { hour: 12, minute: 0 },
      lunchEnd: { hour: 13, minute: 0 },
      isCompanyPayingLunch: false,
      isDayOff: true
    }
  ];

  constructor(
    private authService: AuthService,
    private authenticationControllerService: AuthenticationService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private adminControllerService: AdminControllerService,
    private scheduleService: WorkScheduleControllerService,
    private router: Router,
    public userDataService: UserDataService
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

    this.subscriptions.add(
      this.userDataService.userName$.subscribe(name => {
        if (name) this.userName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.companyName$.subscribe(name => {
        if (name) this.companyName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.userPhoto$.subscribe(photo => {
        if (photo) this.userPhotoUrl = photo;
      })
    );
    this.loadAllEmployeesRelatedToCertainCompany();
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // SCHEDULE MODAL METHODS
  // ═══════════════════════════════════════════════════════════════════════════

  openScheduleModal(employee: RelatedUserInCompanyResponse) {
    if (!employee.workerId) {
      this.errorMessage = "Cannot open schedule: Employee ID is not defined";
      return;
    }

    this.selectedEmployee = employee;
    this.selectedEmployeeId = employee.workerId;
    this.showScheduleModal = true;
    this.employeeGeneratedSchedule = null;
    this.hasExistingSchedule = false;
    this.errorMessage = '';
    this.successMessage = '';

    // Load existing schedule
    this.loadExistingSchedule(employee.workerId);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  closeScheduleModal() {
    this.showScheduleModal = false;
    this.selectedEmployee = null;
    this.employeeGeneratedSchedule = null;
    this.hasExistingSchedule = false;
    this.errorMessage = '';
    this.successMessage = '';
    this.resetScheduleForm();
  }

  loadExistingSchedule(workerId: number) {
    this.isLoadingSchedule = true;
    this.errorMessage = '';

    this.scheduleService.getScheduleTemplate({ workerId: workerId }).subscribe({
      next: (response: WorkSchedulerResponse) => {
        this.isLoadingSchedule = false;

        if (response.schedules && response.schedules.length > 0) {
          this.employeeGeneratedSchedule = response;
          this.hasExistingSchedule = true;
          this.populateScheduleForm(response);
        } else {
          this.hasExistingSchedule = false;
          this.resetScheduleForm();
        }
      },
      error: (error) => {
        this.isLoadingSchedule = false;
        console.error('Error loading schedule:', error);

        // If schedule not found (404) - it's OK, just no schedule exists
        if (error.status === 404 || error.status === 0) {
          this.hasExistingSchedule = false;
          this.resetScheduleForm();
        } else {
          this.errorMessage = 'Failed to load schedule: ' + (error.message || 'Unknown error');
        }
      }
    });
  }

  resetScheduleForm() {
    this.flexibleDays = [
      {
        day: 'MONDAY',
        dayLabel: 'Monday',
        startTime: { hour: 9, minute: 0 },
        endTime: { hour: 17, minute: 0 },
        lunchStart: { hour: 12, minute: 0 },
        lunchEnd: { hour: 13, minute: 0 },
        isCompanyPayingLunch: false,
        isDayOff: false
      },
      {
        day: 'TUESDAY',
        dayLabel: 'Tuesday',
        startTime: { hour: 9, minute: 0 },
        endTime: { hour: 17, minute: 0 },
        lunchStart: { hour: 12, minute: 0 },
        lunchEnd: { hour: 13, minute: 0 },
        isCompanyPayingLunch: false,
        isDayOff: false
      },
      {
        day: 'WEDNESDAY',
        dayLabel: 'Wednesday',
        startTime: { hour: 9, minute: 0 },
        endTime: { hour: 17, minute: 0 },
        lunchStart: { hour: 12, minute: 0 },
        lunchEnd: { hour: 13, minute: 0 },
        isCompanyPayingLunch: false,
        isDayOff: false
      },
      {
        day: 'THURSDAY',
        dayLabel: 'Thursday',
        startTime: { hour: 9, minute: 0 },
        endTime: { hour: 17, minute: 0 },
        lunchStart: { hour: 12, minute: 0 },
        lunchEnd: { hour: 13, minute: 0 },
        isCompanyPayingLunch: false,
        isDayOff: false
      },
      {
        day: 'FRIDAY',
        dayLabel: 'Friday',
        startTime: { hour: 9, minute: 0 },
        endTime: { hour: 17, minute: 0 },
        lunchStart: { hour: 12, minute: 0 },
        lunchEnd: { hour: 13, minute: 0 },
        isCompanyPayingLunch: false,
        isDayOff: false
      },
      {
        day: 'SATURDAY',
        dayLabel: 'Saturday',
        startTime: { hour: 9, minute: 0 },
        endTime: { hour: 17, minute: 0 },
        lunchStart: { hour: 12, minute: 0 },
        lunchEnd: { hour: 13, minute: 0 },
        isCompanyPayingLunch: false,
        isDayOff: true
      },
      {
        day: 'SUNDAY',
        dayLabel: 'Sunday',
        startTime: { hour: 9, minute: 0 },
        endTime: { hour: 17, minute: 0 },
        lunchStart: { hour: 12, minute: 0 },
        lunchEnd: { hour: 13, minute: 0 },
        isCompanyPayingLunch: false,
        isDayOff: true
      }
    ];
  }

  deleteExistingSchedule() {
    if (!this.selectedEmployee?.workerId) return;

    if (!confirm(`Are you sure you want to delete the schedule for ${this.selectedEmployee.firstName} ${this.selectedEmployee.lastName}?`)) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.scheduleService.deleteWorkerScheduleTemplate({
      workerId: this.selectedEmployee.workerId
    }).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Schedule deleted successfully!';
        this.employeeGeneratedSchedule = null;
        this.hasExistingSchedule = false;
        this.resetScheduleForm();
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Failed to delete schedule: ' + (error.message || 'Unknown error');
        console.error('Delete schedule error:', error);
      }
    });
  }


  generateScheduleForWorker(): void {
    if (!this.selectedEmployee?.workerId) return;

    // If schedule already exists - show warning
    if (this.hasExistingSchedule) {
      if (!confirm('A schedule already exists. Do you want to overwrite it?')) {
        return;
      }
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Функция для форматирования времени в строку для Java LocalTime
    const formatTimeForJava = (time: LocalTime): string => {
      const hour = (time.hour ?? 0).toString().padStart(2, '0');
      const minute = (time.minute ?? 0).toString().padStart(2, '0');
      return `${hour}:${minute}:00`;
    };

    const weeklySchedule: any = {};

    this.flexibleDays.forEach(day => {
      // Для Java нужно отправлять LocalTime как строку "HH:mm:ss"
      if (day.isDayOff) {
        // Для выходного дня можно отправить null или дефолтные значения
        weeklySchedule[day.day] = {
          startTime: formatTimeForJava({ hour: 9, minute: 0 }),
          endTime: formatTimeForJava({ hour: 17, minute: 0 }),
          lunchStart: formatTimeForJava({ hour: 12, minute: 0 }),
          lunchEnd: formatTimeForJava({ hour: 13, minute: 0 }),
          isCompanyPayingLunch: false,
          isDayOff: true
        };
      } else {
        weeklySchedule[day.day] = {
          startTime: formatTimeForJava(day.startTime),
          endTime: formatTimeForJava(day.endTime),
          lunchStart: formatTimeForJava(day.lunchStart),
          lunchEnd: formatTimeForJava(day.lunchEnd),
          isCompanyPayingLunch: day.isCompanyPayingLunch,
          isDayOff: false
        };
      }
    });

    const requestData = {
      weeklySchedule: weeklySchedule
    };

    // Логируем для дебага
    console.log('Sending schedule request:', JSON.stringify(requestData, null, 2));

    this.scheduleService.setWeeklySchedule({
      workerId: this.selectedEmployee.workerId,
      body: requestData
    }).subscribe({
      next: (response) => {
        console.log('Schedule saved successfully:', response);
        this.employeeGeneratedSchedule = response;
        this.hasExistingSchedule = true;
        this.successMessage = 'Schedule saved successfully!';
        this.loading = false;
      },
      error: (error) => {
        console.error('Schedule error details:', error);
        console.error('Error response:', error?.error);
        console.error('Error status:', error?.status);

        // Более детальная обработка ошибок
        let errorMsg = 'Failed to save schedule: ';

        if (error?.status === 500) {
          errorMsg += 'Server error. Check server logs for details.';

          // Проверяем специфические ошибки валидации
          if (error?.error?.message?.includes('validate')) {
            errorMsg = 'Validation error: ' + error.error.message;
          } else if (error?.error?.message?.includes('null')) {
            errorMsg = 'Required fields are missing. Please fill all schedule times.';
          }
        } else if (error?.status === 400) {
          errorMsg += error?.error?.message || 'Invalid request format';
        } else {
          errorMsg += error?.error?.message || error?.message || 'Unknown error';
        }

        this.errorMessage = errorMsg;
        this.loading = false;
      }
    });
  }

// Обновленный метод populateScheduleForm для правильного парсинга времени из ответа
  populateScheduleForm(scheduleResponse: WorkSchedulerResponse) {
    if (!scheduleResponse.schedules) return;

    scheduleResponse.schedules.forEach(schedule => {
      const day = this.flexibleDays.find(d => d.day === schedule.dayOfWeek);
      if (day) {
        day.isDayOff = schedule.isDayOff || false;

        if (!day.isDayOff) {
          // Парсим время из строки или объекта
          day.startTime = this.parseTime(schedule.startTime);
          day.endTime = this.parseTime(schedule.endTime);
          day.lunchStart = this.parseTime(schedule.lunchStart);
          day.lunchEnd = this.parseTime(schedule.lunchEnd);
          day.isCompanyPayingLunch = schedule.isCompanyPayingLunch || false;
        }
      }
    });
  }

// Вспомогательный метод для парсинга времени
  private parseTime(time: any): LocalTime {
    if (!time) {
      return { hour: 9, minute: 0 };
    }

    // Если это уже объект LocalTime
    if (typeof time === 'object' && 'hour' in time) {
      return {
        hour: time.hour || 0,
        minute: time.minute || 0
      };
    }

    // Если это строка вида "HH:mm:ss" или "HH:mm"
    if (typeof time === 'string') {
      const parts = time.split(':');
      return {
        hour: parseInt(parts[0]) || 0,
        minute: parseInt(parts[1]) || 0
      };
    }

    return { hour: 9, minute: 0 };
  }

// Обновленный метод форматирования для отображения
  formatTime(time: any): string {
    if (!time) return '--:--';

    // Если это строка
    if (typeof time === 'string') {
      const parts = time.split(':');
      if (parts.length >= 2) {
        return `${parts[0]}:${parts[1]}`;
      }
      return time;
    }

    // Если это объект
    if (typeof time === 'object' && time.hour !== undefined) {
      const hour = time.hour.toString().padStart(2, '0');
      const minute = (time.minute || 0).toString().padStart(2, '0');
      return `${hour}:${minute}`;
    }

    return '--:--';
  }


  // ═══════════════════════════════════════════════════════════════════════════
  // PUNCH IN/OUT METHODS
  // ═══════════════════════════════════════════════════════════════════════════

  updatePunchIn() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const punchInUpdateRequest: PunchInUpdateRequest = {
      newCheckInTIme: this.newCheckInTime
    };

    const params: UpdatePunchInTime$Params = {
      workerId: this.selectedEmployeeId,
      body: punchInUpdateRequest
    };

    this.adminControllerService.updatePunchInTime(params).subscribe(
      () => {
        this.loading = false;
        this.successMessage = "Punch time was updated successfully!";
      },
      error => {
        this.loading = false;
        this.errorMessage = "Failed to update punch time: " + (error.message || 'Unknown error');
      }
    );
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
      }
    );
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // EMPLOYEE MANAGEMENT METHODS
  // ═══════════════════════════════════════════════════════════════════════════

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

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadAllEmployeesRelatedToCertainCompany();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadAllEmployeesRelatedToCertainCompany();
    }
  }

  openEmployeeModal(employee: RelatedUserInCompanyResponse) {
    if (!employee.workerId) {
      this.errorMessage = "Cannot show employee info: Employee ID is not defined";
      return;
    }

    this.selectedEmployeeId = employee.workerId;
    this.selectedEmployee = employee;
    this.showEmployeeInfoModal = true;

    this.findWorkerPersonalInformation(employee.workerId);
  }

  closeEmployeeModal() {
    this.showEmployeeInfoModal = false;
    this.employeePersonalInfo = null;
  }

  openAddEmployeeModal() {
    this.resetForm();
    this.showAddEmployeeModal = true;
  }

  closeAddEmployeeModal() {
    this.showAddEmployeeModal = false;
  }

  openAddAdminModal() {
    this.resetForm();
    this.showAddEmployeeModal = true;
  }

  closeAddAdminModal() {
    this.showAddEmployeeModal = false;
  }

  createNewEmployee() {
    this.loading = true;
    this.errorMessage = '';

    if (!this.gender) {
      this.errorMessage = 'Please select gender';
      this.loading = false;
      return;
    }

    const data: RegistrationRequest = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      password: this.password,
      phoneNumber: this.phoneNumber,
      homeAddress: this.homeAddress,
      apt: this.apt,
      city: this.city,
      state: this.state,
      zipcode: this.zipcode,
      dateOfBirth: this.dateOfBirth || '',
      gender: this.gender,
      companyAddress: this.companyAddress,
      companyName: this.companyName2,
      employmentType: this.employmentType,
      payFrequency: this.payFrequency,
      wcRiskClassCode: this.wcRiskClassCode,
      filingStatus: this.filingStatus,
      exemptFromWithholding: this.exemptFromWithholding,
      multipleJobsOrSpouseWorks: this.multipleJobsOrSpouseWorks,
      twoJobsCheckBox: this.twoJobsCheckBox,
      livesInNYC: this.livesInNYC,
      isCitizen: this.isCitizen,
      isNonCitizenNationalOfTheUS: this.isNonCitizenNationalOfTheUS,
      isPermanentResident: this.isPermanentResident,
      isANonCitizen: this.isANonCitizen,
      ...(this.ssn_WORKER && { ssn_WORKER: this.ssn_WORKER }),
      ...(this.middleInitial && { middleInitial: this.middleInitial }),
      ...(this.extraWithHoldings !== undefined && { extraWithHoldings: this.extraWithHoldings }),
      ...(this.dependents !== undefined && { dependents: this.dependents }),
      ...(this.dependentsList && this.dependentsList.length > 0 && { dependentsList: this.dependentsList }),
      ...(this.dependentsUnder17 !== undefined && { dependentsUnder17: this.dependentsUnder17 }),
      ...(this.otherDependents !== undefined && { otherDependents: this.otherDependents }),
      ...(this.totalDependentsCredit !== undefined && { totalDependentsCredit: this.totalDependentsCredit }),
      ...(this.isRehired !== undefined && { isRehired: this.isRehired }),
      ...(this.dateWhenRehired && { dateWhenRehired: this.dateWhenRehired }),
      ...(this.enrolledInHealthPlan !== undefined && { enrolledInHealthPlan: this.enrolledInHealthPlan }),
      ...(this.monthlyHealthPremium !== undefined && { monthlyHealthPremium: this.monthlyHealthPremium }),
      ...(this.coverageStartDate && { coverageStartDate: this.coverageStartDate }),
      ...(this.adjustmentsSchedule1 !== undefined && { adjustmentsSchedule1: this.adjustmentsSchedule1 }),
      ...(this.deductions !== undefined && { deductions: this.deductions }),
      ...(this.estimatedItemizedDeductions !== undefined && { estimatedItemizedDeductions: this.estimatedItemizedDeductions }),
      ...(this.otherIncome !== undefined && { otherIncome: this.otherIncome }),
      ...(this.multipleJobsWorksheetLine2a !== undefined && { multipleJobsWorksheetLine2a: this.multipleJobsWorksheetLine2a }),
      ...(this.multipleJobsWorksheetLine2b !== undefined && { multipleJobsWorksheetLine2b: this.multipleJobsWorksheetLine2b }),
      ...(this.multipleJobsAdditionalWithholding !== undefined && { multipleJobsAdditionalWithholding: this.multipleJobsAdditionalWithholding }),
      ...(this.uscisNumber && { uscisNumber: this.uscisNumber }),
      ...(this.formI94AdmissionNumber && { formI94AdmissionNumber: this.formI94AdmissionNumber }),
      ...(this.passportNumber && { passportNumber: this.passportNumber }),
      ...(this.passportCountryOfIssuance && { passportCountryOfIssuance: this.passportCountryOfIssuance }),
      ...(this.workAuthorizationExpiryDate && { workAuthorizationExpiryDate: this.workAuthorizationExpiryDate }),
      ...(this.i9Documents && this.i9Documents.length > 0 && { i9Documents: this.i9Documents })
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

  createNewAdmin() {
    this.createNewEmployee();
  }

  openDeleteModal(employee: RelatedUserInCompanyResponse) {
    if (!employee.workerId) {
      this.errorMessage = "Cannot delete employee: Employee ID is not defined";
      return;
    }

    this.selectedEmployeeId = employee.workerId;
    this.selectedEmployee = employee;
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
  }

  deleteEmployee() {
    if (!this.selectedEmployeeId) return;

    this.loading = true;
    this.errorMessage = '';

    const params: FireEmployee$Params = {
      employeeId: this.selectedEmployeeId
    };

    this.companyService.fireEmployee(params).subscribe(
      () => {
        this.loading = false;
        this.showDeleteModal = false;
        this.successMessage = "Employee removed successfully.";
        this.loadAllEmployeesRelatedToCertainCompany();
      },
      error => {
        this.errorMessage = 'Cannot remove employee: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
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
          this.successMessage = "Hourly rate updated successfully.";
          this.openEmployeeModal(this.selectedEmployee!);
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
    };

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

  promoteToForeman() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const params: PromoteToForeman$Params = {
      employeeId: this.selectedEmployeeId
    };

    this.companyService.promoteToForeman(params).subscribe(
      () => {
        this.successMessage = 'Successfully promoted to FOREMAN role';
        this.openEmployeeModal(this.selectedEmployee!);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot promote to Foreman role: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  promoteToAdmin() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const params: PromoteToAdmin$Params = {
      employeeId: this.selectedEmployeeId
    };

    this.companyService.promoteToAdmin$Response(params).subscribe(
      () => {
        this.successMessage = 'Successfully promoted to ADMIN role';
        this.openEmployeeModal(this.selectedEmployee!);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot promote to ADMIN role: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  demoteToUser() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const params: DemoteFromForemanToUser$Params = {
      workerId: this.selectedEmployeeId
    };

    this.companyService.demoteFromForemanToUser(params).subscribe(
      () => {
        this.successMessage = 'Successfully demoted to USER role';
        this.openEmployeeModal(this.selectedEmployee!);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot demote to User role: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  demoteToForeman() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const params: DemoteFromAdminToForeman$Params = {
      workerId: this.selectedEmployeeId
    };

    this.companyService.demoteFromAdminToForeman(params).subscribe(
      () => {
        this.successMessage = 'Successfully demoted to FOREMAN role';
        this.openEmployeeModal(this.selectedEmployee!);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot demote to Foreman role: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    );
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // UTILITY METHODS
  // ═══════════════════════════════════════════════════════════════════════════
  showAddAdminModal: boolean = false;

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
    this.apt = '';
    this.city = '';
    this.state = '';
    this.zipcode = '';
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

  handleImageError(event: any): void {
    event.target.style.display = 'none';
    const placeholder = event.target.parentElement.querySelector('.worker-photo-placeholder');
    if (placeholder) {
      placeholder.style.display = 'flex';
    }
  }

  searchEmployees() {
    // Implement search functionality
    this.loadAllEmployeesRelatedToCertainCompany();
  }

  clearSearch() {
    // Implement clear search functionality
    this.loadAllEmployeesRelatedToCertainCompany();
  }
}
