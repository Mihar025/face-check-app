import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";
import {CompanyResponse} from "../../../../../services/models/company-response";
import {CompanyUpdatingResponse} from "../../../../../services/models/company-updating-response";
import {CompanyUpdatingRequest} from "../../../../../services/models/company-updating-request";
import { BillingControllerService } from "../../../../../services/services/billing-controller.service";
import {finalize} from 'rxjs/operators';

@Component({
  selector: 'app-companies-info-app-owner',
  templateUrl: './companies-info-app-owner.component.html',
  styleUrl: './companies-info-app-owner.component.scss'
})
export class CompaniesInfoAppOwnerComponent implements OnInit {
  userName: string = 'Admin User';
  companyName: string = 'My Company';
  userPhotoUrl: string = '';

  companies: CompanyResponse[] = [];
  totalElement: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;

  page: number = 0;
  size: number = 10;

  // For editing
  editingCompanyId: number | null = null;
  editForm: CompanyUpdatingRequest = {
    companyName: '',
    companyEmail: '',
    companyPhone: '',
    companyAddress: '',
    workersQuantity: 0
  };

  // For registration modal
  isRegisterModalOpen: boolean = false;
  registerForm: FormGroup;
  isRegisterLoading = false;
  registerErrorMessage = '';
  registerSuccessMessage = '';



  isOfferModalOpen: boolean = false;
  selectedCompanyForOffer: CompanyResponse | null = null;
  offerForm = {
    monthlySubscription: 15,
    pricePerEmployee: 8
  };
  checkoutUrl: string = '';
  isCreatingOffer: boolean = false;
  offerErrorMessage: string = '';



  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private billingService: BillingControllerService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService
  ) {

    this.registerForm = this.fb.group({
      adminCompanyId: ['', [Validators.required]],
      companyName: ['', [Validators.required]],
      companyAddress: ['', [Validators.required]],
      companyCity: ['', [Validators.required]],
      companyState: ['NY', [Validators.required]],
      companyZipCode: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]],
      companyPhone: ['', [Validators.required]],
      companyEmail: ['', [Validators.required, Validators.email]],

      employerEIN: ['', [Validators.required]],
      companyStateIdNumber: ['', [Validators.required]],
      socialSecurityTaxForCompany: [0, [Validators.required, Validators.min(0)]],
      specialTwoCharConditionCodeForMTA305: ['', [Validators.required]],

      companyPaymentPosition: ['BIWEEKLY', [Validators.required]],
      defaultMemo: ['', [Validators.required]],

      fundingBankName: ['', [Validators.required]],
      fundingAccountNumber: ['', [Validators.required]],
      fundingRoutingNumber: ['', [Validators.required, Validators.pattern(/^\d{9}$/)]],

      wcInsuranceCarrier: ['', [Validators.required]],
      wcPolicyNumber: ['', [Validators.required]],
      experienceModRate: [1.0, [Validators.required, Validators.min(0)]],

      returnMailingAddress: ['', [Validators.required]],

      signatureName: ['', [Validators.required]],
      signatureTitle: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }
    this.loadAllCompanies();
  }

  loadAllCompanies(): void {
    const params = {
      page: this.page,
      size: this.size
    };

    this.companyService.findAllCompanies(params).subscribe({
      next: (response) => {
        if(response.content){
          this.companies = response.content;
          this.totalElement = response.totalElement || 0;
          this.totalPages = Math.ceil(this.totalElement / this.size);
          this.currentPage = this.page;
        }
      },
      error: (error) => {
        console.error('Cant find all companies', error);
      }
    });
  }



  openOfferModal(company: CompanyResponse): void {
    this.selectedCompanyForOffer = company;
    this.isOfferModalOpen = true;
    this.checkoutUrl = '';
    this.offerErrorMessage = '';
    this.offerForm = {
      monthlySubscription: 15,
      pricePerEmployee: 8
    };
  }

  closeOfferModal(): void {
    this.isOfferModalOpen = false;
    this.selectedCompanyForOffer = null;
    this.checkoutUrl = '';
    this.offerErrorMessage = '';
  }

  createOffer(): void {
    if (!this.selectedCompanyForOffer?.companyId) return;

    this.isCreatingOffer = true;
    this.offerErrorMessage = '';

    this.billingService.createSubscriptionV2({
      body: {
        companyId: this.selectedCompanyForOffer.companyId,
        monthlySubscription: this.offerForm.monthlySubscription,
        pricePerEmployee: this.offerForm.pricePerEmployee
      }
    }).subscribe({
      next: (response) => {
        this.checkoutUrl = response.checkoutUrl || '';
        this.isCreatingOffer = false;
      },
      error: (error) => {
        console.error('Error creating offer:', error);
        this.offerErrorMessage = error.error?.message || 'Failed to create offer';
        this.isCreatingOffer = false;
      }
    });
  }

  copyCheckoutUrl(): void {
    navigator.clipboard.writeText(this.checkoutUrl);
    alert('Link copied to clipboard!');
  }

  getTotalPrice(): number {
    const workers = this.selectedCompanyForOffer?.workersQuantity || 0;
    return this.offerForm.monthlySubscription + (this.offerForm.pricePerEmployee * workers);
  }


  // Registration modal methods
  openRegisterModal(): void {
    this.isRegisterModalOpen = true;
    this.registerErrorMessage = '';
    this.registerSuccessMessage = '';
    // Reset form to default values
    this.registerForm.reset({
      companyState: 'NY',
      companyPaymentPosition: 'BIWEEKLY',
      socialSecurityTaxForCompany: 0,
      experienceModRate: 1.0
    });
  }

  closeRegisterModal(): void {
    this.isRegisterModalOpen = false;
    this.registerErrorMessage = '';
    this.registerSuccessMessage = '';
    this.registerForm.reset();
  }

  onRegisterSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isRegisterLoading = true;
    this.registerErrorMessage = '';

    const formValues = this.registerForm.value;

    this.authService.registerCompanyByAppOwner(
      formValues.adminCompanyId,
      formValues.companyAddress,
      formValues.companyCity,
      formValues.companyEmail,
      formValues.companyName,
      formValues.companyPhone,
      formValues.companyPaymentPosition,
      formValues.companyState,
      formValues.companyStateIdNumber,
      formValues.companyZipCode,
      formValues.defaultMemo,
      formValues.employerEIN,
      formValues.experienceModRate,
      formValues.fundingAccountNumber,
      formValues.fundingBankName,
      formValues.fundingRoutingNumber,
      formValues.returnMailingAddress,
      formValues.signatureName,
      formValues.signatureTitle,
      formValues.socialSecurityTaxForCompany,
      formValues.specialTwoCharConditionCodeForMTA305,
      formValues.wcInsuranceCarrier,
      formValues.wcPolicyNumber
    )
      .pipe(
        finalize(() => {
          this.isRegisterLoading = false;
        })
      ).subscribe({
      next: () => {
        console.log('Company registration successful!');
        this.registerSuccessMessage = 'Company registered successfully!';

        // Close modal and reload companies after short delay
        setTimeout(() => {
          this.closeRegisterModal();
          this.loadAllCompanies();
        }, 1500);
      },
      error: (error) => {
        console.error('Registration error:', error);
        if (error.status === 401 || error.status === 403) {
          this.registerErrorMessage = 'Your session has expired. Please sign in again.';
          setTimeout(() => {
            this.authService.logout();
          }, 2000);
        } else {
          this.registerErrorMessage = error.error?.message || 'Registration failed. Please try again.';
        }
      }
    });
  }

  // Pagination methods
  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadAllCompanies();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadAllCompanies();
    }
  }

  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.page = pageNumber;
      this.loadAllCompanies();
    }
  }

  changePageSize(newSize: number): void {
    this.size = newSize;
    this.page = 0;
    this.loadAllCompanies();
  }

  // Edit methods
  startEdit(company: CompanyResponse): void {
    this.editingCompanyId = company.companyId!;
    this.editForm = {
      companyName: company.companyName || '',
      companyEmail: company.companyEmail || '',
      companyPhone: company.companyPhone || '',
      companyAddress: company.companyAddress || '',
      workersQuantity: company.workersQuantity || 0
    };
  }

  cancelEdit(): void {
    this.editingCompanyId = null;
    this.editForm = {
      companyName: '',
      companyEmail: '',
      companyPhone: '',
      companyAddress: '',
      workersQuantity: 0
    };
  }

  saveCompany(): void {
    if (!this.editingCompanyId) return;

    this.companyService.updateCompany({
      companyId: this.editingCompanyId,
      body: this.editForm
    }).subscribe({
      next: (response: CompanyUpdatingResponse) => {
        console.log('Company updated successfully:', response);
        this.editingCompanyId = null;
        this.loadAllCompanies();
      },
      error: (error) => {
        console.error('Error updating company:', error);
        alert('Failed to update company. Please try again.');
      }
    });
  }

  deleteCompany(company: CompanyResponse): void {
    if (confirm(`Are you sure you want to delete "${company.companyName}"?`)) {
      this.companyService.deleteCompany({
        companyId: company.companyId!
      }).subscribe({
        next: () => {
          console.log('Company deleted successfully');
          if (this.companies.length === 1 && this.page > 0) {
            this.page--;
          }
          this.loadAllCompanies();
        },
        error: (error) => {
          console.error('Error deleting company:', error);
          alert('Failed to delete company. Please try again.');
        }
      });
    }
  }

  // Helper methods
  get hasCompanies(): boolean {
    return this.companies && this.companies.length > 0;
  }

  get isFirstPage(): boolean {
    return this.page === 0;
  }

  get isLastPage(): boolean {
    return this.page >= this.totalPages - 1;
  }

  get pageInfo(): string {
    const start = this.page * this.size + 1;
    const end = Math.min((this.page + 1) * this.size, this.totalElement);
    return `Showing ${start}-${end} of ${this.totalElement}`;
  }
}
