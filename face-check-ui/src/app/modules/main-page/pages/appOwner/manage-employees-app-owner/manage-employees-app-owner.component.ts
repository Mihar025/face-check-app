import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../additionalServices/auth-service';
import { UserServiceControllerService } from '../../../../../services/services/user-service-controller.service';
import { CompanyControllerService } from '../../../../../services/services/company-controller.service';
import { AdminControllerService } from '../../../../../services/services/admin-controller.service';
import { WorkScheduleControllerService } from '../../../../../services/services/work-schedule-controller.service';
import { AuthenticationService } from '../../../../../services/services/authentication.service';
import { RelatedUserInCompanyResponse } from '../../../../../services/models/related-user-in-company-response';
import { WorkerPersonalInformationResponse } from '../../../../../services/models/worker-personal-information-response';
import { WorkSchedulerResponse } from '../../../../../services/models/work-scheduler-response';
import { LocalTime } from '../../../../../services/models/local-time';
import { PunchInUpdateRequest } from '../../../../../services/models/punch-in-update-request';
import { RegistrationRequest } from '../../../../../services/models/registration-request';
import { DependentsRequest } from '../../../../../services/models/dependents-request';
import { I9DocumentRequest } from '../../../../../services/models/i-9-document-request';
import {
  RegistrationRequestEmployeeAppOwner
} from "../../../../../services/models/registration-request-employee-app-owner";

@Component({
  selector: 'app-manage-employees-app-owner',
  templateUrl: './manage-employees-app-owner.component.html',
  styleUrls: ['./manage-employees-app-owner.component.scss']
})
export class ManageEmployeesAppOwnerComponent implements OnInit {
  // ────────────────────────────────────────────────────────────────────────────────
  // User / header
  // ────────────────────────────────────────────────────────────────────────────────
  userName = '';
  companyName = '';
  userPhotoUrl = '';
  companyId = 0;



  // ────────────────────────────────────────────────────────────────────────────────
  // Employees data
  // ────────────────────────────────────────────────────────────────────────────────
  employees: RelatedUserInCompanyResponse[] = [];
  filteredEmployees: RelatedUserInCompanyResponse[] = [];
  totalElement = 0;
  totalPages = 0;
  currentPage = 0;

  // ────────────────────────────────────────────────────────────────────────────────
  // Schedule defaults
  // ────────────────────────────────────────────────────────────────────────────────
  startTime: LocalTime = { hour: 9, minute: 0 };
  endTime: LocalTime = { hour: 17, minute: 0 };
  lunchStartTime: LocalTime = { hour: 12, minute: 0 };
  lunchEndTime: LocalTime = { hour: 13, minute: 0 };
  isCompanyPayingLunch = false;

  // ────────────────────────────────────────────────────────────────────────────────
  // Schedule modal states
  // ────────────────────────────────────────────────────────────────────────────────
  hasExistingSchedule = false;
  isLoadingSchedule = false;

  // ────────────────────────────────────────────────────────────────────────────────
  // Pagination
  // ────────────────────────────────────────────────────────────────────────────────
  page = 0;
  size = 12;

  // ────────────────────────────────────────────────────────────────────────────────
  // Search
  // ────────────────────────────────────────────────────────────────────────────────
  searchQuery = '';

  // ────────────────────────────────────────────────────────────────────────────────
  // Loading & Messages
  // ────────────────────────────────────────────────────────────────────────────────
  loading = false;
  errorMessage = '';
  successMessage = '';

  // ────────────────────────────────────────────────────────────────────────────────
  // Modals
  // ────────────────────────────────────────────────────────────────────────────────
  showEmployeeModal = false;
  showScheduleModal = false;
  showDeleteModal = false;
  showAddAdminModal = false;
  showAddEmployeeModal = false;

  // ────────────────────────────────────────────────────────────────────────────────
  // Selected employee & details
  // ────────────────────────────────────────────────────────────────────────────────
  selectedEmployee: RelatedUserInCompanyResponse | null = null;
  employeePersonalInfo: WorkerPersonalInformationResponse | null = null;
  employeeGeneratedSchedule: WorkSchedulerResponse | null = null;

  // ────────────────────────────────────────────────────────────────────────────────
  // Time management tabs & state
  // ────────────────────────────────────────────────────────────────────────────────
  activeTab: 'newPunchIn' | 'newPunchOut' = 'newPunchIn';

  dateWhenWorkerDidntMakePunchIn = '';
  newPunchInDate = '';
  newPunchInTime: LocalTime = { hour: 9, minute: 0 };
  dateWhenWorkerDidntMakePunchOut = '';
  newPunchOutDate = '';
  newPunchOutTime: LocalTime = { hour: 17, minute: 0 };

  // Hourly rate
  newHourlyRate = 0;

  // ────────────────────────────────────────────────────────────────────────────────
  // Forms (kept as plain objects to match existing AuthService signatures)
  // ────────────────────────────────────────────────────────────────────────────────
  adminForm: any = {
    firstName: '',
    lastName: '',
    middleInitial: '',
    email: '',
    password: '',
    phoneNumber: '',
    homeAddress: '',
    apt: '',
    city: '',
    state: '',
    zipcode: '',
    dateOfBirth: '',
    gender: '',
    ssn_WORKER: '',
    employmentType: 'W2',
    payFrequency: 'BIWEEKLY',
    wcRiskClassCode: '',
    filingStatus: 'SINGLE',
    exemptFromWithholding: false,
    extraWithHoldings: 0,
    multipleJobsOrSpouseWorks: false,
    twoJobsCheckBox: false,
    livesInNYC: false,
    dependents: 0,
    dependentsList: [],
    dependentsUnder17: 0,
    otherDependents: 0,
    totalDependentsCredit: 0,
    isRehired: false,
    dateWhenRehired: '',
    enrolledInHealthPlan: false,
    monthlyHealthPremium: 0,
    coverageStartDate: '',
    adjustmentsSchedule1: 0,
    deductions: 0,
    estimatedItemizedDeductions: 0,
    otherIncome: 0,
    multipleJobsWorksheetLine2a: 0,
    multipleJobsWorksheetLine2b: 0,
    multipleJobsAdditionalWithholding: 0,
    isCitizen: false,
    isNonCitizenNationalOfTheUS: false,
    isPermanentResident: false,
    isANonCitizen: false,
    uscisNumber: '',
    formI94AdmissionNumber: '',
    passportNumber: '',
    passportCountryOfIssuance: '',
    workAuthorizationExpiryDate: '',
    i9Documents: []
  };

  employeeForm: any = {
    firstName: '',
    lastName: '',
    middleInitial: '',
    email: '',
    password: '',
    phoneNumber: '',
    homeAddress: '',
    apt: '',
    city: '',
    state: '',
    zipcode: '',
    dateOfBirth: '',
    gender: '',
    ssn_WORKER: '',
    companyAddress: '',
    companyId: 0,
    employmentType: 'W2',
    payFrequency: 'BIWEEKLY',
    wcRiskClassCode: '',
    filingStatus: 'SINGLE',
    exemptFromWithholding: false,
    extraWithHoldings: 0,
    multipleJobsOrSpouseWorks: false,
    twoJobsCheckBox: false,
    livesInNYC: false,
    dependents: 0,
    dependentsList: [],
    dependentsUnder17: 0,
    otherDependents: 0,
    totalDependentsCredit: 0,
    isRehired: false,
    dateWhenRehired: '',
    enrolledInHealthPlan: false,
    monthlyHealthPremium: 0,
    coverageStartDate: '',
    adjustmentsSchedule1: 0,
    deductions: 0,
    estimatedItemizedDeductions: 0,
    otherIncome: 0,
    multipleJobsWorksheetLine2a: 0,
    multipleJobsWorksheetLine2b: 0,
    multipleJobsAdditionalWithholding: 0,
    isCitizen: false,
    isNonCitizenNationalOfTheUS: false,
    isPermanentResident: false,
    isANonCitizen: false,
    uscisNumber: '',
    formI94AdmissionNumber: '',
    passportNumber: '',
    passportCountryOfIssuance: '',
    workAuthorizationExpiryDate: '',
    i9Documents: []
  };

  // ────────────────────────────────────────────────────────────────────────────────
  // Flexible schedule days
  // ────────────────────────────────────────────────────────────────────────────────
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
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private adminService: AdminControllerService,
    private scheduleService: WorkScheduleControllerService,
    private authenticationService: AuthenticationService
  ) {}

  // ────────────────────────────────────────────────────────────────────────────────
  // Lifecycle
  // ────────────────────────────────────────────────────────────────────────────────
  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    this.loadUserInfo();
    this.loadAllEmployees();
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Loaders
  // ────────────────────────────────────────────────────────────────────────────────
  loadUserInfo(): void {
    this.userService.findWorkerFullName().subscribe({
      next: (response) => {
        if (response?.fullName) this.userName = response.fullName;
      },
      error: (err) => console.error('Error loading user name:', err)
    });

    this.userService.findWorkerCompanyName().subscribe({
      next: (response) => {
        if (response?.companyName) {
          this.companyName = response.companyName;
          this.employeeForm.companyName = response.companyName;
        }
      },
      error: (err) => console.error('Error loading company name:', err)
    });

    this.userService.findWorkerFullContactInformation().subscribe({
      next: (response) => {
        if (response?.photoUrl) this.userPhotoUrl = response.photoUrl;
      },
      error: (err) => console.error('Error loading user photo:', err)
    });
  }

  loadAllEmployees(): void {
    this.loading = true;
    this.errorMessage = '';

    const params = { page: this.page, size: this.size } as any;

    this.companyService.findAllEmployees(params).subscribe({
      next: (response: any) => {
        const content: RelatedUserInCompanyResponse[] = response?.content ?? response?.items ?? [];
        this.employees = content || [];
        this.filteredEmployees = [...this.employees];

        const total =
          response?.totalElement ??
          response?.totalElements ??
          response?.totalElementsCount ??
          response?.total ??
          this.employees.length;

        this.totalElement = total;
        this.totalPages = Math.max(1, Math.ceil(total / this.size));
        this.currentPage = this.page;
        this.loading = false;
      },
      error: (error) => {
        console.error('loadAllEmployees error:', error);
        this.errorMessage = 'Error loading employees: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Search
  // ────────────────────────────────────────────────────────────────────────────────
  onSearchChange(): void {
    const query = (this.searchQuery || '').trim().toLowerCase();
    if (!query) {
      this.filteredEmployees = [...this.employees];
      return;
    }

    this.filteredEmployees = this.employees.filter((emp) =>
      (emp.firstName || '').toLowerCase().includes(query) ||
      (emp.lastName || '').toLowerCase().includes(query) ||
      (emp.email || '').toLowerCase().includes(query) ||
      String(emp.companyName || '').toLowerCase().includes(query)
    );
  }

  searchEmployees(): void {
    this.onSearchChange();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.filteredEmployees = [...this.employees];
    this.loadAllEmployees();
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Pagination
  // ────────────────────────────────────────────────────────────────────────────────
  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadAllEmployees();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadAllEmployees();
    }
  }

  onPageSizeChange(size?: number): void {
    if (size) this.size = size;
    this.page = 0;
    this.loadAllEmployees();
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Admin Modal Methods
  // ────────────────────────────────────────────────────────────────────────────────
  openAddAdminModal(): void {
    this.resetAdminForm();
    this.showAddAdminModal = true;
  }

  closeAddAdminModal(): void {
    this.showAddAdminModal = false;
    this.resetAdminForm();
  }

  resetAdminForm(): void {
    this.adminForm = {
      firstName: '',
      lastName: '',
      middleInitial: '',
      email: '',
      password: '',
      phoneNumber: '',
      homeAddress: '',
      apt: '',
      city: '',
      state: '',
      zipcode: '',
      dateOfBirth: '',
      gender: '',
      ssn_WORKER: '',
      employmentType: 'W2',
      payFrequency: 'BIWEEKLY',
      wcRiskClassCode: '',
      filingStatus: 'SINGLE',
      exemptFromWithholding: false,
      extraWithHoldings: 0,
      multipleJobsOrSpouseWorks: false,
      twoJobsCheckBox: false,
      livesInNYC: false,
      dependents: 0,
      dependentsList: [],
      dependentsUnder17: 0,
      otherDependents: 0,
      totalDependentsCredit: 0,
      isRehired: false,
      dateWhenRehired: '',
      enrolledInHealthPlan: false,
      monthlyHealthPremium: 0,
      coverageStartDate: '',
      adjustmentsSchedule1: 0,
      deductions: 0,
      estimatedItemizedDeductions: 0,
      otherIncome: 0,
      multipleJobsWorksheetLine2a: 0,
      multipleJobsWorksheetLine2b: 0,
      multipleJobsAdditionalWithholding: 0,
      isCitizen: false,
      isNonCitizenNationalOfTheUS: false,
      isPermanentResident: false,
      isANonCitizen: false,
      uscisNumber: '',
      formI94AdmissionNumber: '',
      passportNumber: '',
      passportCountryOfIssuance: '',
      workAuthorizationExpiryDate: '',
      i9Documents: []
    };
  }

  createNewAdmin(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService
      .registerAdminExtended(
        this.adminForm.firstName,
        this.adminForm.lastName,
        this.adminForm.middleInitial || undefined,
        this.adminForm.email,
        this.adminForm.password,
        this.adminForm.phoneNumber,
        this.adminForm.homeAddress,
        this.adminForm.apt,
        this.adminForm.city,
        this.adminForm.state,
        this.adminForm.zipcode,
        this.adminForm.dateOfBirth,
        this.adminForm.gender,
        this.adminForm.ssn_WORKER || undefined,
        this.adminForm.employmentType,
        this.adminForm.payFrequency,
        this.adminForm.wcRiskClassCode || undefined,
        this.adminForm.isRehired || undefined,
        this.adminForm.dateWhenRehired || undefined,
        this.adminForm.filingStatus,
        this.adminForm.exemptFromWithholding,
        this.adminForm.extraWithHoldings,
        this.adminForm.multipleJobsOrSpouseWorks,
        this.adminForm.twoJobsCheckBox,
        this.adminForm.livesInNYC,
        this.adminForm.dependents,
        this.adminForm.dependentsList || [],
        this.adminForm.dependentsUnder17 || undefined,
        this.adminForm.otherDependents || undefined,
        this.adminForm.totalDependentsCredit || undefined,
        this.adminForm.multipleJobsWorksheetLine2a || undefined,
        this.adminForm.multipleJobsWorksheetLine2b || undefined,
        this.adminForm.multipleJobsAdditionalWithholding || undefined,
        this.adminForm.adjustmentsSchedule1 || undefined,
        this.adminForm.deductions || undefined,
        this.adminForm.estimatedItemizedDeductions || undefined,
        this.adminForm.otherIncome || undefined,
        this.adminForm.enrolledInHealthPlan || undefined,
        this.adminForm.monthlyHealthPremium || undefined,
        this.adminForm.coverageStartDate || undefined,
        this.adminForm.isCitizen || undefined,
        this.adminForm.isNonCitizenNationalOfTheUS || undefined,
        this.adminForm.isPermanentResident || undefined,
        this.adminForm.isANonCitizen || undefined,
        this.adminForm.uscisNumber || undefined,
        this.adminForm.formI94AdmissionNumber || undefined,
        this.adminForm.passportNumber || undefined,
        this.adminForm.passportCountryOfIssuance || undefined,
        this.adminForm.workAuthorizationExpiryDate || undefined,
        this.adminForm.i9Documents || undefined
      )
      .subscribe({
        next: () => {
          this.successMessage = 'Admin registered successfully!';
          this.loading = false;
          this.closeAddAdminModal();
          this.loadAllEmployees();
          this.clearMessages();
        },
        error: (error) => {
          this.errorMessage = 'Failed to register admin: ' + (error?.message || 'Unknown error');
          this.loading = false;
        }
      });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Employee Modal
  // ────────────────────────────────────────────────────────────────────────────────
  openEmployeeModal(employee: RelatedUserInCompanyResponse): void {
    this.selectedEmployee = employee;
    this.showEmployeeModal = true;
    if (employee.workerId != null) this.loadEmployeeDetails(employee.workerId);
    this.newHourlyRate = employee.baseHourlyRate || 0;
    this.clearMessages();
  }

  closeEmployeeModal(): void {
    this.showEmployeeModal = false;
    this.selectedEmployee = null;
    this.employeePersonalInfo = null;
    this.clearMessages();
  }

  loadEmployeeDetails(workerId: number): void {
    this.userService.getWorkerPersonalInformation({ employeeId: workerId }).subscribe({
      next: (response) => (this.employeePersonalInfo = response),
      error: (error) => console.error('Error loading employee details:', error)
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Schedule Modal
  // ────────────────────────────────────────────────────────────────────────────────
  openScheduleModal(employee: RelatedUserInCompanyResponse): void {
    if (!employee.workerId) {
      this.errorMessage = "Cannot open schedule: Employee ID is not defined";
      return;
    }

    this.selectedEmployee = employee;
    this.showScheduleModal = true;
    this.employeeGeneratedSchedule = null;
    this.hasExistingSchedule = false;
    this.errorMessage = '';
    this.successMessage = '';

    // Load existing schedule
    this.loadExistingSchedule(employee.workerId);
  }

  closeScheduleModal(): void {
    this.showScheduleModal = false;
    this.selectedEmployee = null;
    this.employeeGeneratedSchedule = null;
    this.hasExistingSchedule = false;
    this.errorMessage = '';
    this.successMessage = '';
    this.resetScheduleForm();
  }

  loadExistingSchedule(workerId: number): void {
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

  populateScheduleForm(scheduleResponse: WorkSchedulerResponse): void {
    if (!scheduleResponse.schedules) return;

    scheduleResponse.schedules.forEach(schedule => {
      const day = this.flexibleDays.find(d => d.day === schedule.dayOfWeek);
      if (day) {
        day.isDayOff = schedule.isDayOff || false;

        if (!day.isDayOff) {
          // Используем parseTime для правильного парсинга
          day.startTime = this.parseTime(schedule.startTime);
          day.endTime = this.parseTime(schedule.endTime);
          day.lunchStart = this.parseTime(schedule.lunchStart);
          day.lunchEnd = this.parseTime(schedule.lunchEnd);
          day.isCompanyPayingLunch = schedule.isCompanyPayingLunch || false;
        }
      }
    });
  }

  resetScheduleForm(): void {
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

  deleteExistingSchedule(): void {
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
        this.clearMessages();
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

    // ✅ ДОБАВЛЯЕМ ФУНКЦИЮ ФОРМАТИРОВАНИЯ (как в первом файле)
    const formatTimeForJava = (time: LocalTime): string => {
      const hour = (time.hour ?? 0).toString().padStart(2, '0');
      const minute = (time.minute ?? 0).toString().padStart(2, '0');
      return `${hour}:${minute}:00`;
    };

    const weeklySchedule: any = {};

    this.flexibleDays.forEach(day => {
      // ✅ ИСПРАВЛЯЕМ - используем formatTimeForJava для всех времён
      if (day.isDayOff) {
        // Для выходного дня тоже отправляем строки, не объекты
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

    const requestData = { weeklySchedule };

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
        this.clearMessages();
      },
      error: (error) => {
        console.error('Schedule error details:', error);

        let errorMsg = 'Failed to save schedule: ';

        if (error?.status === 500) {
          errorMsg += 'Server error. Check server logs for details.';
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
  // ────────────────────────────────────────────────────────────────────────────────
  // Delete Modal
  // ────────────────────────────────────────────────────────────────────────────────
  openDeleteModal(employee: RelatedUserInCompanyResponse): void {
    this.selectedEmployee = employee;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedEmployee = null;
  }

  deleteEmployee(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    this.companyService.fireEmployee({ employeeId: this.selectedEmployee.workerId }).subscribe({
      next: () => {
        this.successMessage = 'Employee removed successfully';
        this.closeDeleteModal();
        this.loadAllEmployees();
        this.loading = false;
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to remove employee: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Role management
  // ────────────────────────────────────────────────────────────────────────────────
  promoteToForeman(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    this.companyService.promoteToForeman({ employeeId: this.selectedEmployee.workerId }).subscribe({
      next: () => {
        this.successMessage = 'Successfully promoted to Foreman';
        this.loadAllEmployees();
        this.loadEmployeeDetails(this.selectedEmployee!.workerId!);
        this.loading = false;
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to promote: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  promoteToAdmin(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    this.companyService.promoteToAdmin({ employeeId: this.selectedEmployee.workerId }).subscribe({
      next: () => {
        this.successMessage = 'Successfully promoted to Admin';
        this.loadAllEmployees();
        this.loadEmployeeDetails(this.selectedEmployee!.workerId!);
        this.loading = false;
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to promote: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  demoteToUser(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    this.companyService.demoteFromForemanToUser({ workerId: this.selectedEmployee.workerId }).subscribe({
      next: () => {
        this.successMessage = 'Successfully demoted to User';
        this.loadAllEmployees();
        this.loadEmployeeDetails(this.selectedEmployee!.workerId!);
        this.loading = false;
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to demote: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  demoteToForeman(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    this.companyService.demoteFromAdminToForeman({ workerId: this.selectedEmployee.workerId }).subscribe({
      next: () => {
        this.successMessage = 'Successfully demoted to Foreman';
        this.loadAllEmployees();
        this.loadEmployeeDetails(this.selectedEmployee!.workerId!);
        this.loading = false;
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to demote: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }


  changeNewPunchIn(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    const formattedDateWhenMissed = `${this.dateWhenWorkerDidntMakePunchIn}T00:00:00`;

    const requestData = {
      dateWhenWorkerDidntMakePunchIn: formattedDateWhenMissed,
      newPunchInDate: this.newPunchInDate,
      newPunchInTime: `${(this.newPunchInTime.hour || 0).toString().padStart(2, '0')}:${(this.newPunchInTime.minute || 0)
        .toString()
        .padStart(2, '0')}:00`,
      workerId: this.selectedEmployee.workerId
    };

    this.adminService.changePunchInForWorker({ workerId: this.selectedEmployee.workerId, body: requestData as any }).subscribe({
      next: () => {
        this.successMessage = 'New punch in set successfully';
        this.loading = false;
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to set punch in: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  changeNewPunchOut(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    const formattedDateWhenMissed = `${this.dateWhenWorkerDidntMakePunchOut}T00:00:00`;

    const requestData = {
      dateWhenWorkerDidntMakePunchOut: formattedDateWhenMissed,
      newPunchOutDate: this.newPunchOutDate,
      newPunchOutTime: `${(this.newPunchOutTime.hour || 0).toString().padStart(2, '0')}:${(this.newPunchOutTime.minute || 0)
        .toString()
        .padStart(2, '0')}:00`,
      workerId: this.selectedEmployee.workerId
    };

    this.adminService.changePunchOutForWorker({ workerId: this.selectedEmployee.workerId, body: requestData as any }).subscribe({
      next: () => {
        this.successMessage = 'New punch out set successfully';
        this.loading = false;
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to set punch out: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Hourly rate
  // ────────────────────────────────────────────────────────────────────────────────
  changeHourlyRate(): void {
    if (!this.selectedEmployee?.workerId) return;

    this.loading = true;
    this.userService.findWorkerCompanyIdByAuthentication().subscribe({
      next: (companyResponse) => {
        const companyId = companyResponse?.companyId ?? 0;

        this.companyService
          .updateEmployeeRate({
            companyId,
            employeeId: this.selectedEmployee!.workerId!,
            body: { baseHourlyRate: this.newHourlyRate }
          })
          .subscribe({
            next: () => {
              this.successMessage = 'Hourly rate updated successfully';
              this.loadAllEmployees();
              this.loading = false;
              this.clearMessages();
            },
            error: (error) => {
              this.errorMessage = 'Failed to update rate: ' + (error?.message || 'Unknown error');
              this.loading = false;
            }
          });
      },
      error: (error) => {
        this.errorMessage = 'Failed to get company info: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Employee add modal
  // ────────────────────────────────────────────────────────────────────────────────
  openAddEmployeeModal(): void {
    this.resetEmployeeForm();
    this.showAddEmployeeModal = true;
  }

  closeAddEmployeeModal(): void {
    this.showAddEmployeeModal = false;
    this.resetEmployeeForm();
  }

  resetEmployeeForm(): void {
    this.employeeForm = {
      firstName: '',
      lastName: '',
      middleInitial: '',
      email: '',
      password: '',
      phoneNumber: '',
      homeAddress: '',
      apt: '',
      city: '',
      state: '',
      zipcode: '',
      dateOfBirth: '',
      gender: '',
      ssn_WORKER: '',
      companyAddress: '',
      companyId: this.companyId,
      employmentType: 'W2',
      payFrequency: 'BIWEEKLY',
      wcRiskClassCode: '',
      filingStatus: 'SINGLE',
      exemptFromWithholding: false,
      extraWithHoldings: 0,
      multipleJobsOrSpouseWorks: false,
      twoJobsCheckBox: false,
      livesInNYC: false,
      dependents: 0,
      dependentsList: [],
      dependentsUnder17: 0,
      otherDependents: 0,
      totalDependentsCredit: 0,
      isRehired: false,
      dateWhenRehired: '',
      enrolledInHealthPlan: false,
      monthlyHealthPremium: 0,
      coverageStartDate: '',
      adjustmentsSchedule1: 0,
      deductions: 0,
      estimatedItemizedDeductions: 0,
      otherIncome: 0,
      multipleJobsWorksheetLine2a: 0,
      multipleJobsWorksheetLine2b: 0,
      multipleJobsAdditionalWithholding: 0,
      isCitizen: false,
      isNonCitizenNationalOfTheUS: false,
      isPermanentResident: false,
      isANonCitizen: false,
      uscisNumber: '',
      formI94AdmissionNumber: '',
      passportNumber: '',
      passportCountryOfIssuance: '',
      workAuthorizationExpiryDate: '',
      i9Documents: []
    };
  }

  createNewEmployee(): void {
    this.loading = true;
    this.errorMessage = '';

    if (!this.employeeForm.gender) {
      this.errorMessage = 'Please select gender';
      this.loading = false;
      return;
    }

    const data: RegistrationRequestEmployeeAppOwner = {
      firstName: this.employeeForm.firstName,
      lastName: this.employeeForm.lastName,
      email: this.employeeForm.email,
      password: this.employeeForm.password,
      phoneNumber: this.employeeForm.phoneNumber,
      homeAddress: this.employeeForm.homeAddress,
      apt: this.employeeForm.apt,
      city: this.employeeForm.city,
      state: this.employeeForm.state,
      zipcode: this.employeeForm.zipcode,
      dateOfBirth: this.employeeForm.dateOfBirth || '',
      gender: this.employeeForm.gender,
      companyAddress: this.employeeForm.companyAddress,
      companyId: Number(this.employeeForm.companyId),
      employmentType: this.employeeForm.employmentType,
      payFrequency: this.employeeForm.payFrequency,
      wcRiskClassCode: this.employeeForm.wcRiskClassCode,
      filingStatus: this.employeeForm.filingStatus,
      exemptFromWithholding: this.employeeForm.exemptFromWithholding,
      multipleJobsOrSpouseWorks: this.employeeForm.multipleJobsOrSpouseWorks,
      twoJobsCheckBox: this.employeeForm.twoJobsCheckBox,
      livesInNYC: this.employeeForm.livesInNYC,
      isCitizen: this.employeeForm.isCitizen,
      isNonCitizenNationalOfTheUS: this.employeeForm.isNonCitizenNationalOfTheUS,
      isPermanentResident: this.employeeForm.isPermanentResident,
      isANonCitizen: this.employeeForm.isANonCitizen,
      ...(this.employeeForm.ssn_WORKER && { ssn_WORKER: this.employeeForm.ssn_WORKER }),
      ...(this.employeeForm.middleInitial && { middleInitial: this.employeeForm.middleInitial }),
      ...(this.employeeForm.extraWithHoldings !== undefined && { extraWithHoldings: this.employeeForm.extraWithHoldings }),
      ...(this.employeeForm.dependents !== undefined && { dependents: this.employeeForm.dependents }),
      ...(Array.isArray(this.employeeForm.dependentsList) && this.employeeForm.dependentsList.length > 0 && {
        dependentsList: this.employeeForm.dependentsList
      }),
      ...(this.employeeForm.dependentsUnder17 !== undefined && { dependentsUnder17: this.employeeForm.dependentsUnder17 }),
      ...(this.employeeForm.otherDependents !== undefined && { otherDependents: this.employeeForm.otherDependents }),
      ...(this.employeeForm.totalDependentsCredit !== undefined && {
        totalDependentsCredit: this.employeeForm.totalDependentsCredit
      }),
      ...(this.employeeForm.isRehired !== undefined && { isRehired: this.employeeForm.isRehired }),
      ...(this.employeeForm.dateWhenRehired && { dateWhenRehired: this.employeeForm.dateWhenRehired }),
      ...(this.employeeForm.enrolledInHealthPlan !== undefined && {
        enrolledInHealthPlan: this.employeeForm.enrolledInHealthPlan
      }),
      ...(this.employeeForm.monthlyHealthPremium !== undefined && {
        monthlyHealthPremium: this.employeeForm.monthlyHealthPremium
      }),
      ...(this.employeeForm.coverageStartDate && { coverageStartDate: this.employeeForm.coverageStartDate }),
      ...(this.employeeForm.adjustmentsSchedule1 !== undefined && { adjustmentsSchedule1: this.employeeForm.adjustmentsSchedule1 }),
      ...(this.employeeForm.deductions !== undefined && { deductions: this.employeeForm.deductions }),
      ...(this.employeeForm.estimatedItemizedDeductions !== undefined && {
        estimatedItemizedDeductions: this.employeeForm.estimatedItemizedDeductions
      }),
      ...(this.employeeForm.otherIncome !== undefined && { otherIncome: this.employeeForm.otherIncome }),
      ...(this.employeeForm.multipleJobsWorksheetLine2a !== undefined && {
        multipleJobsWorksheetLine2a: this.employeeForm.multipleJobsWorksheetLine2a
      }),
      ...(this.employeeForm.multipleJobsWorksheetLine2b !== undefined && {
        multipleJobsWorksheetLine2b: this.employeeForm.multipleJobsWorksheetLine2b
      }),
      ...(this.employeeForm.multipleJobsAdditionalWithholding !== undefined && {
        multipleJobsAdditionalWithholding: this.employeeForm.multipleJobsAdditionalWithholding
      }),
      ...(this.employeeForm.uscisNumber && { uscisNumber: this.employeeForm.uscisNumber }),
      ...(this.employeeForm.formI94AdmissionNumber && { formI94AdmissionNumber: this.employeeForm.formI94AdmissionNumber }),
      ...(this.employeeForm.passportNumber && { passportNumber: this.employeeForm.passportNumber }),
      ...(this.employeeForm.passportCountryOfIssuance && { passportCountryOfIssuance: this.employeeForm.passportCountryOfIssuance }),
      ...(this.employeeForm.workAuthorizationExpiryDate && { workAuthorizationExpiryDate: this.employeeForm.workAuthorizationExpiryDate }),
      ...(Array.isArray(this.employeeForm.i9Documents) && this.employeeForm.i9Documents.length > 0 && {
        i9Documents: this.employeeForm.i9Documents
      })
    } as RegistrationRequestEmployeeAppOwner;

    this.authenticationService.registerEmployeeFromAppOwnerPage({ body: data }).subscribe({
      next: () => {
        this.successMessage = 'Employee registered successfully!';
        this.loading = false;
        this.closeAddEmployeeModal();
        this.loadAllEmployees();
        this.clearMessages();
      },
      error: (error) => {
        this.errorMessage = 'Failed to register employee: ' + (error?.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Helpers
  // ────────────────────────────────────────────────────────────────────────────────
  formatTime(time: any): string {
    if (!time) return '--:--';

    if (typeof time === 'object' && time.hour !== undefined) {
      const hour = time.hour.toString().padStart(2, '0');
      const minute = (time.minute || 0).toString().padStart(2, '0');
      return `${hour}:${minute}`;
    }

    return '--:--';
  }

  clearMessages(): void {
    setTimeout(() => {
      this.errorMessage = '';
      this.successMessage = '';
    }, 5000);
  }

  trackByEmployee = (_: number, item: RelatedUserInCompanyResponse) => item.workerId ?? item.email ?? item.firstName;

   parseTime(time: any): LocalTime {
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
}
