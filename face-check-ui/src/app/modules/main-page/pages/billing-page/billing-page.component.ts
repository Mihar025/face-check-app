import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { environment } from '../../../../../environments/environment.prod';

interface BillingInfo {
  companyId: number;
  workersQuantity: number;
  subscriptionStatus: string | null;
  monthlySubscription: number | null;
  pricePerEmployee: number | null;
  stripeBasePriceId: string | null;
  stripeSeatsPriceId: string | null;
  subscriptionCurrentPeriodEnd: string | null;
}

@Component({
  selector: 'app-billing-page',
  templateUrl: './billing-page.component.html',
  styleUrls: ['./billing-page.component.scss']
})
export class BillingPageComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  // Data
  companyId: number = 0;
  subscriptionStatus: string = 'inactive';
  workers: number = 0;
  monthlySubscription: number = 0;
  pricePerEmployee: number = 0;
  totalPrice: number = 0;
  periodEnd: Date | null = null;

  // States
  isLoading: boolean = true;
  isActivating: boolean = false;
  isUpdating: boolean = false;
  isCanceling: boolean = false;

  // Messages
  successMessage: string = '';
  errorMessage: string = '';

  // Modal
  showCancelModal: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.companyId = Number(this.route.snapshot.paramMap.get('companyId'));

    if (!this.companyId || this.companyId === 0) {
      this.errorMessage = 'Invalid company ID';
      this.isLoading = false;
      return;
    }

    this.loadBillingInfo();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get pricingConfigured(): boolean {
    return (this.monthlySubscription > 0 || this.pricePerEmployee > 0);
  }

  loadBillingInfo(): void {
    this.isLoading = true;
    this.clearMessages();

    this.http.get<BillingInfo>(
    //  `${environment.apiUrl}/company/${this.companyId}/billing-info`
        `http://localhost:8088/api/v1/company/${this.companyId}/billing-info`
    )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.workers = data.workersQuantity || 0;
          this.subscriptionStatus = data.subscriptionStatus || 'inactive';
          this.monthlySubscription = data.monthlySubscription || 0;
          this.pricePerEmployee = data.pricePerEmployee || 0;
          this.calculateTotal();

          if (data.subscriptionCurrentPeriodEnd) {
            this.periodEnd = new Date(data.subscriptionCurrentPeriodEnd);
          }

          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading billing info:', err);
          this.errorMessage = 'Cannot load billing information. Please try again.';
          this.isLoading = false;
        }
      });
  }

  calculateTotal(): void {
    this.totalPrice = this.monthlySubscription + (this.workers * this.pricePerEmployee);
  }

  getStatusText(): string {
    switch (this.subscriptionStatus) {
      case 'active': return 'Active';
      case 'past_due': return 'Past Due';
      case 'canceled': return 'Canceled';
      case 'trialing': return 'Trial';
      case 'incomplete': return 'Incomplete';
      case 'unpaid': return 'Unpaid';
      default: return 'Inactive';
    }
  }

  activateBilling(): void {
    if (this.workers === 0) {
      this.errorMessage = 'You need at least 1 employee to activate billing.';
      return;
    }

    this.isActivating = true;
    this.clearMessages();

    // Save company ID for success/cancel pages
    localStorage.setItem('billing_company_id', String(this.companyId));

    this.http.post<{ checkoutUrl: string }>(
      //`${environment.apiUrl}/billing/activate/${this.companyId}`,
      `http://localhost:8088/api/v1/billing/activate/${this.companyId}`,
      {}
    )

      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          if (res && res.checkoutUrl) {
            window.location.href = res.checkoutUrl;
          } else {
            this.errorMessage = 'Failed to create checkout session.';
            this.isActivating = false;
          }
        },
        error: (err) => {
          console.error('Activation error:', err);
          this.errorMessage = err.error?.message || 'Cannot activate billing at the moment.';
          this.isActivating = false;
        }
      });
  }

  updateSeats(): void {
    this.isUpdating = true;
    this.clearMessages();

    this.http.post(
      `${environment.apiUrl}/billing/update-seats/${this.companyId}`,
      {}
    )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Seats updated successfully!';
          this.isUpdating = false;
          this.loadBillingInfo();
          this.autoClearSuccess();
        },
        error: (err) => {
          console.error('Update error:', err);
          this.errorMessage = err.error?.message || 'Update failed.';
          this.isUpdating = false;
        }
      });
  }

  cancelSubscription(): void {
    this.isCanceling = true;
    this.clearMessages();

    this.http.post(
      `${environment.apiUrl}/billing/cancel/${this.companyId}`,
      {}
    )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Subscription will be canceled at the end of billing period.';
          this.showCancelModal = false;
          this.isCanceling = false;
          this.loadBillingInfo();
          this.autoClearSuccess();
        },
        error: (err) => {
          console.error('Cancel error:', err);
          this.errorMessage = err.error?.message || 'Cancel failed.';
          this.isCanceling = false;
        }
      });
  }

  openCancelModal(): void {
    this.showCancelModal = true;
  }

  closeCancelModal(): void {
    this.showCancelModal = false;
  }

  goBack(): void {
    this.router.navigate(['/main-page/admin/settings']);
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  private autoClearSuccess(): void {
    setTimeout(() => {
      this.successMessage = '';
    }, 5000);
  }
}
