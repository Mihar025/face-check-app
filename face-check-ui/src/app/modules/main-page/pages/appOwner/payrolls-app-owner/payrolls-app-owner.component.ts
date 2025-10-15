import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {PaymentHistoryIrsControllerService} from "../../../../../services/services/payment-history-irs-controller.service";
import {PaymentHistoryResponse} from "../../../../../services/models/payment-history-response";
import {PageResponsePaymentHistoryResponse} from "../../../../../services/models/page-response-payment-history-response";

@Component({
  selector: 'app-payrolls-app-owner',
  templateUrl: './payrolls-app-owner.component.html',
  styleUrl: './payrolls-app-owner.component.scss'
})
export class PayrollsAppOwnerComponent implements OnInit {
  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';

  // Payments data
  payments: PaymentHistoryResponse[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Pagination
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  // Delete confirmation
  showDeleteModal: boolean = false;
  selectedPayment: PaymentHistoryResponse | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private paymentService: PaymentHistoryIrsControllerService
  ) { }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN') {
      window.location.href = '/';
      return;
    }

    this.loadUserInfo();
    this.loadPayments();
  }

  private loadUserInfo(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response && response.fullName) {
          this.userName = response.fullName;
        }
      },
      error => console.error('Error loading user name:', error)
    );

    this.userService.findWorkerCompanyName().subscribe(
      response => {
        if (response && response.companyName) {
          this.companyName = response.companyName;
        }
      },
      error => console.error('Error loading company name:', error)
    );

    this.userService.findWorkerFullContactInformation().subscribe(
      response => {
        if (response && response.photoUrl) {
          this.userPhotoUrl = response.photoUrl;
        }
      },
      error => console.error('Error loading user photo:', error)
    );
  }

  loadPayments(): void {
    this.loading = true;
    this.errorMessage = '';

    this.paymentService.findAllPaymentsAppOwner({
      page: this.page,
      size: this.size
    }).subscribe(
      (response: PageResponsePaymentHistoryResponse) => {
        this.payments = response.content || [];
        this.totalPages = response.totalPages || 0;
        this.totalElements = response.totalElement || 0;
        this.loading = false;
      },
      error => {
        this.errorMessage = 'Error loading payments: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Error loading payments:', error);
      }
    );
  }

  changePage(newPage: number): void {
    this.page = newPage;
    this.loadPayments();
  }

  openDeleteModal(payment: PaymentHistoryResponse): void {
    this.selectedPayment = payment;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedPayment = null;
  }

  confirmDelete(): void {
    if (!this.selectedPayment || !this.selectedPayment.paymentHistoryIrsId) {
      return;
    }

    this.loading = true;
    this.paymentService.deletePayment({
      paymentId: this.selectedPayment.paymentHistoryIrsId
    }).subscribe(
      () => {
        this.successMessage = 'Payment deleted successfully!';
        this.closeDeleteModal();
        this.loadPayments();

        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error => {
        this.errorMessage = 'Error deleting payment: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.error('Error deleting payment:', error);

        setTimeout(() => {
          this.errorMessage = '';
        }, 5000);
      }
    );
  }

  formatCurrency(amount: number | undefined): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount || 0);
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  }

  formatPaymentType(type: string | undefined): string {
    if (!type) return 'Unknown';
    const typeMap: { [key: string]: string } = {
      'PAYROLL_TAX_941': '941 Tax',
      'UNEMPLOYMENT_TAX_940': '940 Tax',
      'STATE_UNEMPLOYMENT_TAX': 'State Tax',
      'PERSONAL_INSURANCE': 'Insurance',
      'WC_Payment': 'Workers Comp',
      'MCTMT_PREPAYMENT': 'MCTMT Pre',
      'MCTMT_CREDIT': 'MCTMT Credit'
    };
    return typeMap[type] || type;
  }

  getPaymentTypeClass(type: string | undefined): string {
    if (!type) return 'default';
    const classMap: { [key: string]: string } = {
      'PAYROLL_TAX_941': 'payroll',
      'UNEMPLOYMENT_TAX_940': 'unemployment',
      'STATE_UNEMPLOYMENT_TAX': 'state',
      'PERSONAL_INSURANCE': 'insurance',
      'WC_Payment': 'workers-comp',
      'MCTMT_PREPAYMENT': 'mctmt',
      'MCTMT_CREDIT': 'credit'
    };
    return classMap[type] || 'default';
  }
}
