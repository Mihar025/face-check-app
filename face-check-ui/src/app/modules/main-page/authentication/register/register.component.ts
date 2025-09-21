import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Subscription } from "rxjs";
import { AuthService } from "../../additionalServices/auth-service";
import { finalize } from "rxjs/operators";
import { Router } from "@angular/router";

@Component({
  selector: 'app-register-admin',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnDestroy {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  private authSubscription: Subscription | null = null;
  dependentsList: Array<any> = [];
  i9Documents: Array<any> = [];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      middleInitial: [''],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phoneNumber: ['', [Validators.required]],

      homeAddress: ['', [Validators.required]],
      apt: ['', [Validators.required]],
      city: ['', [Validators.required]],
      state: ['NY', [Validators.required]],
      zipcode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]],

      dateOfBirth: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      ssn_WORKER: ['', [Validators.pattern(/^\d{3}-\d{2}-\d{4}$/)]],

      employmentType: ['W2', [Validators.required]],
      payFrequency: ['BIWEEKLY', [Validators.required]],
      wcRiskClassCode: [''],
      isRehired: [false],
      dateWhenRehired: [''],

      filingStatus: ['SINGLE', [Validators.required]],
      exemptFromWithholding: [false, [Validators.required]],
      extraWithHoldings: [0, [Validators.required, Validators.min(0)]],
      multipleJobsOrSpouseWorks: [false, [Validators.required]],
      twoJobsCheckBox: [false, [Validators.required]],
      livesInNYC: [false, [Validators.required]],

      dependents: [0, [Validators.required, Validators.min(0)]],
      dependentsList: [[]],
      dependentsUnder17: [0, [Validators.min(0)]],
      otherDependents: [0, [Validators.min(0)]],
      totalDependentsCredit: [0, [Validators.min(0)]],

      multipleJobsWorksheetLine2a: [0],
      multipleJobsWorksheetLine2b: [0],
      multipleJobsAdditionalWithholding: [0],
      adjustmentsSchedule1: [0],
      deductions: [0],
      estimatedItemizedDeductions: [0],
      otherIncome: [0],

      enrolledInHealthPlan: [false],
      monthlyHealthPremium: [0],
      coverageStartDate: [''],

      isCitizen: [false],
      isNonCitizenNationalOfTheUS: [false],
      isPermanentResident: [false],
      isANonCitizen: [false],
      uscisNumber: [''],
      formI94AdmissionNumber: [''],
      passportNumber: [''],
      passportCountryOfIssuance: [''],
      workAuthorizationExpiryDate: [''],
      i9Documents: [[]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const formValues = {...this.registerForm.value};

    formValues.dependentsList = this.dependentsList;
    formValues.i9Documents = this.i9Documents;

    if (formValues.ssn_WORKER) {
      formValues.ssn_WORKER = formValues.ssn_WORKER.replace(/-/g, '');
    }

    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }



    this.authSubscription = this.authService.registerAdminExtended(

      formValues.firstName,
      formValues.lastName,
      formValues.middleInitial || undefined,
      formValues.email,
      formValues.password,
      formValues.phoneNumber,

      formValues.homeAddress,
      formValues.apt || '',
      formValues.city || '',
      formValues.state || '',
      formValues.zipcode || '',

      formValues.dateOfBirth,
      formValues.gender || 'MALE',
      formValues.ssn_WORKER || undefined,

      formValues.employmentType || 'W2',
      formValues.payFrequency || 'BIWEEKLY',
      formValues.wcRiskClassCode || undefined,
      formValues.isRehired || undefined,
      formValues.dateWhenRehired || undefined,

      formValues.filingStatus || 'SINGLE',
      formValues.exemptFromWithholding !== undefined ? formValues.exemptFromWithholding : false,
      formValues.extraWithHoldings || 0,
      formValues.multipleJobsOrSpouseWorks !== undefined ? formValues.multipleJobsOrSpouseWorks : false,
      formValues.twoJobsCheckBox !== undefined ? formValues.twoJobsCheckBox : false,
      formValues.livesInNYC !== undefined ? formValues.livesInNYC : false,

      formValues.dependents || 0,
      formValues.dependentsList || [],
      formValues.dependentsUnder17 || undefined,
      formValues.otherDependents || undefined,
      formValues.totalDependentsCredit || undefined,

      formValues.multipleJobsWorksheetLine2a || undefined,
      formValues.multipleJobsWorksheetLine2b || undefined,
      formValues.multipleJobsAdditionalWithholding || undefined,
      formValues.adjustmentsSchedule1 || undefined,
      formValues.deductions || undefined,
      formValues.estimatedItemizedDeductions || undefined,
      formValues.otherIncome || undefined,

      formValues.enrolledInHealthPlan || undefined,
      formValues.monthlyHealthPremium || undefined,
      formValues.coverageStartDate || undefined,

      formValues.isCitizen || undefined,
      formValues.isNonCitizenNationalOfTheUS || undefined,
      formValues.isPermanentResident || undefined,
      formValues.isANonCitizen || undefined,
      formValues.uscisNumber || undefined,
      formValues.formI94AdmissionNumber || undefined,
      formValues.passportNumber || undefined,
      formValues.passportCountryOfIssuance || undefined,
      formValues.workAuthorizationExpiryDate || undefined,
      formValues.i9Documents || undefined
    )
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      ).subscribe({
        next: (response) => {
          console.log('Admin registration successful!', response);
          this.router.navigate(['/verification/admin']);
        },
        error: (error) => {
          console.error('Registration error in component:', error);
          this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        }
      });
  }



// Методы для управления зависимыми лицами
  addDependent() {
    this.dependentsList.push({
      firstName: '',
      lastName: '',
      birthDate: ''
    });
  }

  removeDependent(index: number) {
    this.dependentsList.splice(index, 1);
  }

// Методы для управления I-9 документами
  addI9Document() {
    this.i9Documents.push({
      documentTitle: '',
      documentNumber: '',
      issuingAuthority: '',
      expirationDate: ''
    });
  }

  removeI9Document(index: number) {
    this.i9Documents.splice(index, 1);
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
