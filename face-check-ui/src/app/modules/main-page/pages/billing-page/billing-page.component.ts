import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from "../../../../../environments/environment.prod";

interface CompanyStripeResponse {
  companyId: number;
  workersQuantity: number;
  subscriptionStatus: string;
}

@Component({
  selector: 'app-billing-page',
  templateUrl: './billing-page.component.html',
  styleUrl: './billing-page.component.scss'
})
export class BillingPageComponent implements OnInit {

  companyId!: number;
  subscriptionStatus: string = 'inactive';
  workers: number = 0;
  totalPrice: number = 0;
  message: string = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.companyId = Number(this.route.snapshot.paramMap.get('companyId'));
    this.loadBillingInfo();
  }

  loadBillingInfo() {
    this.http.get<CompanyStripeResponse>(
      `${environment.apiUrl}/company/${this.companyId}/billing-info`
    ).subscribe({
      next: (company) => {
        this.workers = company.workersQuantity;
        this.subscriptionStatus = company.subscriptionStatus ?? 'inactive';
        this.totalPrice = 15 + (this.workers * 8);
      },
      error: () => {
        this.message = 'Cannot load billing information.';
      }
    });
  }

  activateBilling() {
    localStorage.setItem('billing_company_id', String(this.companyId));

    this.http.post<any>(
      `${environment.apiUrl}/billing/activate/${this.companyId}`,
      {}
    ).subscribe({
      next: (res) => {
        window.location.href = res.checkoutUrl;
      },
      error: () => {
        this.message = 'Cannot activate billing at the moment.';
      }
    });
  }

  updateSeats() {
    this.http.post(
      `${environment.apiUrl}/billing/update-seats/${this.companyId}`,
      {}
    ).subscribe({
      next: () => this.message = 'Seats updated successfully.',
      error: () => this.message = 'Update failed.'
    });
  }

  cancelSubscription() {
    this.http.post(
      `${environment.apiUrl}/billing/cancel/${this.companyId}`,
      {}
    ).subscribe({
      next: () => this.message = 'Subscription will be canceled at the period end.',
      error: () => this.message = 'Cancel failed.'
    });
  }
}
