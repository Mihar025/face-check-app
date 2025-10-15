import { Component, OnInit, Inject } from '@angular/core';
import { Router } from "@angular/router";
import { DOCUMENT } from '@angular/common';
import {ContactSalesFormRequest} from "../../../../services/models/contact-sales-form-request";
import {ContactSalesControllerService} from "../../../../services/services/contact-sales-controller.service";


@Component({
  selector: 'app-pricing',
  templateUrl: './pricing.component.html',
  styleUrl: './pricing.component.scss'
})
export class PricingComponent implements OnInit {

  // Form data model - убрали email и company
  trialFormData: ContactSalesFormRequest = {
    firstName: '',
    lastName: '',
    phoneNumber: ''
  };

  selectedPlan: string = '';
  selectedPlanName: string = '';
  showSuccessModal: boolean = false;
  isSubmitting: boolean = false;

  constructor(
    private router: Router,
    @Inject(DOCUMENT) private document: Document,
    private contactSalesService: ContactSalesControllerService
  ) {}

  ngOnInit() {
    window.scrollTo(0, 0);
  }


  // Modal functions
  openTrialModal(plan?: string): void {
    const modal = this.document.getElementById('trialModal');
    if (modal) {
      modal.style.display = 'flex';

      // Set selected plan if provided
      if (plan) {
        this.selectedPlan = plan;
        this.selectedPlanName = plan === 'starter' ? 'Starter Plan' : 'Professional Plan';
      } else {
        this.selectedPlan = '';
        this.selectedPlanName = '';
      }

      // Prevent body scroll when modal is open
      this.document.body.style.overflow = 'hidden';
    }
  }

  closeTrialModal(): void {
    const modal = this.document.getElementById('trialModal');
    if (modal) {
      modal.style.display = 'none';
      // Re-enable body scroll
      this.document.body.style.overflow = 'auto';

      this.resetForm();
    }
  }

  closeModalOnOverlay(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (target.classList.contains('modal-overlay')) {
      this.closeTrialModal();
    }
  }

  submitTrialForm(event: Event): void {
    event.preventDefault();

    // Validation
    if (!this.trialFormData.firstName || !this.trialFormData.lastName ||
      !this.trialFormData.phoneNumber) {
      console.log('Please fill all required fields');
      return;
    }

    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;

    // Отправляем данные на бекенд
    this.contactSalesService.createContactForm({
      body: this.trialFormData
    }).subscribe({
      next: (response) => {
        console.log('Form submitted successfully:', response);

        // Close trial modal
        this.closeTrialModal();

        // Show success modal
        this.showSuccessModal = true;

        // Auto close success modal after 3 seconds
        setTimeout(() => {
          this.closeSuccessModal();
        }, 3000);

        this.isSubmitting = false;
      },
      error: (error) => {
        console.error('Error submitting form:', error);
        alert('Error submitting form. Please try again.');
        this.isSubmitting = false;
      }
    });
  }

  closeSuccessModal(): void {
    this.showSuccessModal = false;
    this.document.body.style.overflow = 'auto';
  }

  resetForm(): void {
    this.trialFormData = {
      firstName: '',
      lastName: '',
      phoneNumber: ''
    };
    this.selectedPlan = '';
    this.selectedPlanName = '';
    this.isSubmitting = false;
  }

  // Existing navigation methods
  navigateToSignIn(): void {
    this.router.navigate(['/sign-in']);
  }

  navigateToAboutUs(): void {
    this.router.navigate(['/about-us']);
  }

  navigateToPricing(): void {
    this.router.navigate(['/pricing']);
  }

  navigateToFaceCheck(): void {
    this.router.navigate(['/face-check']);
  }

  selectPlan(plan: string): void {
    console.log(`Selected plan: ${plan}`);
    this.openTrialModal(plan);
  }

  scrollToPricingPlans(): void {
    const pricingPlans = this.document.getElementById('pricingPlans');
    if (pricingPlans) {
      pricingPlans.scrollIntoView({ behavior: 'smooth' });
      console.log('Скролл выполнен');
    } else {
      console.error('Элемент pricingPlans не найден');
    }
  }

  startFreeTrial(): void {
    this.openTrialModal();
  }

  navigateToTerms(): void {
    this.router.navigate(['/terms-of-service']);
  }

  navigateToPrivacy(): void {
    this.router.navigate(['/privacy-policy']);
  }

  navigateToScheduleConsultation(): void {
    this.router.navigate(['/schedule-consultation']);
  }

  navigateToViewDemo(): void {
    this.router.navigate(['/view-demo']);
  }

  navigateToRefundPolicy(): void {
    this.router.navigate(['/refund-policy']);
  }

  contactSales(): void {
    // Implement contact sales logic
    console.log('Contact sales');
  }
}
