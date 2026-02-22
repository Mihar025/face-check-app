import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {
  AdminControllerService,
  AuthenticationService,
  CompanyControllerService, RemoteWorkerControllerService,
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
import {DatePipe} from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";
import {ChangePunchOutRequest} from "../../../../../services/models/change-punch-out-request";
import {SetWorkerRemote$Params} from "../../../../../services/fn/remote-worker-controller/set-worker-remote";
import {SetWorkerOnPerson$Params} from "../../../../../services/fn/remote-worker-controller/set-worker-on-person";

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
  activeTab: 'newPunchIn' | 'newPunchOut' = 'newPunchIn';
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
    public userDataService: UserDataService,
    private datePipe: DatePipe,
    private remoteWorkerService: RemoteWorkerControllerService
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

  formatTime(time: any): string {
    if (!time) return '--:--';

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
  private convertToISODate(usDate: string): string {
    if (!usDate || usDate.trim().length === 0) {
      return '';
    }

    const parts = usDate.split('/');
    if (parts.length !== 3) {
      console.error('Invalid date format. Expected MM/DD/YYYY, got:', usDate);
      return '';
    }

    const month = parts[0].padStart(2, '0');
    const day = parts[1].padStart(2, '0');
    const year = parts[2];

    return `${year}-${month}-${day}`;
  }

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

    console.log('🔵 Starting punch in change');
    console.log('dateWhenWorkerDidntMakePunchIn:', this.dateWhenWorkerDidntMakePunchIn);
    console.log('newPunchInDate:', this.newPunchInDate);
    console.log('newPunchInTime:', this.newPunchInTime);
    console.log('selectedEmployeeId:', this.selectedEmployeeId);

    if (!this.dateWhenWorkerDidntMakePunchIn || !this.newPunchInDate) {
      this.errorMessage = "Please fill in all required date fields";
      this.loading = false;
      return;
    }

    const isoMissedDate = this.convertToISODate(this.dateWhenWorkerDidntMakePunchIn);
    const isoNewDate = this.convertToISODate(this.newPunchInDate);

    console.log('📅 Converted dates:');
    console.log('isoMissedDate:', isoMissedDate);
    console.log('isoNewDate:', isoNewDate);

    if (!isoMissedDate || !isoNewDate) {
      this.errorMessage = 'Please enter valid dates in MM/DD/YYYY format';
      this.loading = false;
      return;
    }

    // ✅ ИЗМЕНИЛ НА СТРОКУ!
    const requestData = {
      dateWhenWorkerDidntMakePunchIn: `${isoMissedDate}T00:00:00`,
      newPunchInDate: isoNewDate,
      newPunchInTime: `${(this.newPunchInTime.hour || 0).toString().padStart(2, '0')}:${(this.newPunchInTime.minute || 0).toString().padStart(2, '0')}:00`
    };

    console.log('📤 Final request data:', JSON.stringify(requestData, null, 2));

    const params: ChangePunchInForWorker$Params = {
      workerId: this.selectedEmployeeId,
      body: requestData as any
    };

    console.log('📤 Full params:', JSON.stringify(params, null, 2));

    this.adminControllerService.changePunchInForWorker(params).subscribe(
      (response) => {
        this.loading = false;
        this.successMessage = "Punch In time was changed successfully!";
        console.log('✅ Success:', response);
      },
      error => {
        this.loading = false;
        console.error('❌ Full Error:', error);
        console.error('❌ Error body:', error.error);

        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = "Failed to change punch time: " + (error.message || 'Unknown error');
        }
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

    const isoMissedDate = this.convertToISODate(this.dateWhenWorkerDidntMakePunchOut);
    const isoNewDate = this.convertToISODate(this.newPunchOutDate);

    if (!isoMissedDate || !isoNewDate) {
      this.errorMessage = 'Please enter valid dates in MM/DD/YYYY format';
      this.loading = false;
      return;
    }

    // ✅ Создаём объект БЕЗ workerId и с правильным форматом времени
    const requestData = JSON.parse(JSON.stringify({
      dateWhenWorkerDidntMakePunchOut: `${isoMissedDate}T00:00:00`,
      newPunchOutDate: isoNewDate,
      newPunchOutTime: `${(this.newPunchOutTime.hour || 17).toString().padStart(2, '0')}:${(this.newPunchOutTime.minute || 0).toString().padStart(2, '0')}:00`
    }));

    console.log('📤 Final request data:', JSON.stringify(requestData, null, 2));

    const params = {
      workerId: this.selectedEmployeeId,
      body: requestData  // БЕЗ as any!
    };

    this.adminControllerService.changePunchOutForWorker(params as any).subscribe(
      (response) => {
        this.loading = false;
        this.successMessage = "Punch Out time was changed successfully!";
        console.log('✅ Success:', response);

        this.dateWhenWorkerDidntMakePunchOut = '';
        this.newPunchOutDate = '';
        this.newPunchOutTime = { hour: 17, minute: 0 };
      },
      error => {
        this.loading = false;
        console.error('❌ Full Error:', error);
        console.error('❌ Error body:', error.error);

        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = "Failed to change punch out time: " + (error.message || 'Unknown error');
        }
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

  createNewEmployee() {
    // 1. Сначала валидация
    const validationError = this.validateEmployeeForm();
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const data: RegistrationRequest = this.buildRegistrationRequest();

    const params: Register$Params = {
      body: data
    };

    // Добавляем логирование для дебага
    console.log('Registering employee with data:', data);

    this.authenticationControllerService.register(params).subscribe({
      next: (response: any) => {
        console.log('Registration response:', response);

        // ВАЖНО: Проверяем что регистрация действительно прошла успешно
        // Бэкенд должен возвращать созданного юзера или хотя бы его ID
        if (response && (response.id || response.userId)) {
          this.successMessage = `Employee ${data.firstName} ${data.lastName} registered successfully!`;
          this.loading = false;

          // Закрываем модалку и обновляем список
          setTimeout(() => {
            this.closeAddEmployeeModal();
            this.resetForm();
            this.loadAllEmployeesRelatedToCertainCompany();
          }, 1000); // Даём время показать success message

        } else {
          // Сервер вернул 200, но без ID - значит что-то пошло не так
          this.errorMessage = 'Registration completed but no user ID returned. Please check if employee was created.';
          this.loading = false;
          console.error('No user ID in response:', response);

          // Всё равно обновляем список, чтобы проверить
          this.loadAllEmployeesRelatedToCertainCompany();
        }
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        console.error('Registration error:', error);

        // Детальная обработка ошибок
        if (error.status === 0) {
          this.errorMessage = '❌ Cannot connect to server. Please check your connection.';
        } else if (error.status === 400) {
          // Validation errors from backend
          if (error.error?.errors) {
            const errors = Object.values(error.error.errors).join(', ');
            this.errorMessage = `❌ Validation failed: ${errors}`;
          } else {
            this.errorMessage = `❌ Invalid data: ${error.error?.message || 'Please check all fields'}`;
          }
        } else if (error.status === 409) {
          this.errorMessage = `❌ Employee with email ${data.email} already exists`;
        } else if (error.status === 404) {
          this.errorMessage = `❌ Company "${data.companyName}" not found`;
        } else if (error.status === 500) {
          this.errorMessage = '❌ Server error occurred. Please try again or contact support.';

          // При серверной ошибке логируем детали
          console.error('Server error details:', {
            message: error.error?.message,
            trace: error.error?.trace,
            timestamp: new Date().toISOString()
          });
        } else {
          this.errorMessage = `❌ ${error.error?.message || error.message || 'Unknown error occurred'}`;
        }
      }
    });
  }

// Вспомогательный метод для валидации
  private validateEmployeeForm(): string | null {
    // Обязательные поля
    if (!this.firstName?.trim()) return 'First name is required';
    if (!this.lastName?.trim()) return 'Last name is required';
    if (!this.email?.trim()) return 'Email is required';
    if (!this.password?.trim()) return 'Password is required';
    if (this.password.length < 6) return 'Password must be at least 6 characters';
    if (!this.phoneNumber?.trim()) return 'Phone number is required';
    if (!this.homeAddress?.trim()) return 'Home address is required';
    if (!this.city?.trim()) return 'City is required';
    if (!this.state?.trim()) return 'State is required';
    if (!this.zipcode?.trim()) return 'Zipcode is required';
    if (!this.gender) return 'Gender is required';
    if (!this.dateOfBirth) return 'Date of birth is required';
    if (!this.companyName2?.trim()) return 'Company name is required';
    if (!this.companyAddress?.trim()) return 'Company address is required';
    if (!this.wcRiskClassCode?.trim()) return 'WC Risk Class Code is required';

    // Валидация email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) return 'Invalid email format';

    // Валидация SSN если указан
    if (this.ssn_WORKER) {
      const ssnRegex = /^\d{3}-?\d{2}-?\d{4}$/;
      if (!ssnRegex.test(this.ssn_WORKER)) {
        return 'Invalid SSN format (should be XXX-XX-XXXX or XXXXXXXXX)';
      }
    }

    // Валидация телефона
    const phoneRegex = /^\+?[\d\s\-\(\)]+$/;
    if (!phoneRegex.test(this.phoneNumber)) return 'Invalid phone number format';

    // Валидация zipcode
    const zipRegex = /^\d{5}(-\d{4})?$/;
    if (!zipRegex.test(this.zipcode)) return 'Invalid zipcode format (should be XXXXX or XXXXX-XXXX)';

    return null; // Всё валидно
  }

// Вспомогательный метод для построения request
  private buildRegistrationRequest(): RegistrationRequest {
    return {
      // Обязательные поля
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      email: this.email.trim().toLowerCase(),
      password: this.password,
      phoneNumber: this.phoneNumber.trim(),
      homeAddress: this.homeAddress.trim(),
      apt: this.apt?.trim() || '',
      city: this.city.trim(),
      state: this.state.trim(),
      zipcode: this.zipcode.trim(),
      dateOfBirth: this.dateOfBirth || '',
      gender: this.gender as 'MALE' | 'FEMALE' | 'OTHER',
      companyAddress: this.companyAddress.trim(),
      companyName: this.companyName2.trim(),
      employmentType: this.employmentType,
      payFrequency: this.payFrequency,
      wcRiskClassCode: this.wcRiskClassCode.trim(),
      filingStatus: this.filingStatus,
      exemptFromWithholding: this.exemptFromWithholding || false,
      multipleJobsOrSpouseWorks: this.multipleJobsOrSpouseWorks || false,
      twoJobsCheckBox: this.twoJobsCheckBox || false,
      livesInNYC: this.livesInNYC || false,
      isCitizen: this.isCitizen || false,
      isNonCitizenNationalOfTheUS: this.isNonCitizenNationalOfTheUS || false,
      isPermanentResident: this.isPermanentResident || false,
      isANonCitizen: this.isANonCitizen || false,

      // Опциональные поля
      ...(this.ssn_WORKER && { ssn_WORKER: this.ssn_WORKER.replace(/\-/g, '') }), // Убираем дефисы из SSN
      ...(this.middleInitial && { middleInitial: this.middleInitial.trim() }),
      ...(this.extraWithHoldings !== undefined && { extraWithHoldings: this.extraWithHoldings }),
      ...(this.dependents !== undefined && { dependents: this.dependents }),
      ...(this.dependentsList?.length && { dependentsList: this.dependentsList }),
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
      ...(this.uscisNumber && { uscisNumber: this.uscisNumber.trim() }),
      ...(this.formI94AdmissionNumber && { formI94AdmissionNumber: this.formI94AdmissionNumber.trim() }),
      ...(this.passportNumber && { passportNumber: this.passportNumber.trim() }),
      ...(this.passportCountryOfIssuance && { passportCountryOfIssuance: this.passportCountryOfIssuance.trim() }),
      ...(this.workAuthorizationExpiryDate && { workAuthorizationExpiryDate: this.workAuthorizationExpiryDate }),
      ...(this.i9Documents?.length && { i9Documents: this.i9Documents })
    };
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
        (response: EmployeeSalaryResponse) => {  // ← Получаем ответ от сервера
          this.loading = false;
          this.successMessage = "Hourly rate updated successfully.";

          // ✅ ОБНОВЛЯЕМ ДАННЫЕ В ВЫБРАННОМ РАБОТНИКЕ
          if (this.selectedEmployee) {
            this.selectedEmployee.baseHourlyRate = this.newHourlyRate;
          }

          // ✅ ОБНОВЛЯЕМ В ОСНОВНОМ СПИСКЕ EMPLOYEES
          const employeeIndex = this.employees.findIndex(e => e.workerId === this.selectedEmployeeId);
          if (employeeIndex !== -1) {
            this.employees[employeeIndex].baseHourlyRate = this.newHourlyRate;
          }

          // ✅ ОБНОВЛЯЕМ В ОТФИЛЬТРОВАННОМ СПИСКЕ (если используешь поиск)
          const filteredIndex = this.filteredEmployees.findIndex(e => e.workerId === this.selectedEmployeeId);
          if (filteredIndex !== -1) {
            this.filteredEmployees[filteredIndex].baseHourlyRate = this.newHourlyRate;
          }

          // ✅ ОБНОВЛЯЕМ employeeRateInfo если она используется в модалке
          if (this.employeeRateInfo) {
            this.employeeRateInfo.baseHourlyRate = this.newHourlyRate;
          }

          // Теперь открываем модалку с обновленными данными
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

  setUpAsRemoteWorker(){
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const params: SetWorkerRemote$Params = {
      workerId: this.selectedEmployeeId
    }

    this.remoteWorkerService.setWorkerRemote(params).subscribe(
      () => {
        this.successMessage = 'Successfully set up as remote worker';
        this.openEmployeeModal(this.selectedEmployee!);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot set up as remote worker: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    )
  }

  setUpAsOnPersonWorker(){
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const params: SetWorkerOnPerson$Params = {
      workerId: this.selectedEmployeeId
    }

    this.remoteWorkerService.setWorkerOnPerson(params).subscribe(
      () => {
        this.successMessage = 'Successfully set up as on person worker';
        this.openEmployeeModal(this.selectedEmployee!);
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Cannot set up as on person worker: ' + (error.message || 'Unknown problem');
        this.loading = false;
      }
    )
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
