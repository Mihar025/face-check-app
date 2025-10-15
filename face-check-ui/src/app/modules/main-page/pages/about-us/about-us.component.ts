import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ContactSalesFormRequest} from "../../../../services/models/contact-sales-form-request";
import {ContactSalesControllerService} from "../../../../services/services/contact-sales-controller.service";

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrl: './about-us.component.scss'
})
export class AboutUsComponent implements OnInit{

  // Contact form data - ТОЧНО КАК В PRICING
  contactFormData: ContactSalesFormRequest = {
    firstName: '',
    lastName: '',
    phoneNumber: ''
  };

  isSubmitting: boolean = false;
  showSuccessMessage: boolean = false;
  showErrorMessage: boolean = false;
  errorMessage: string = '';

  constructor(
    private router: Router,
    private contactSalesService: ContactSalesControllerService
  ) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
  }

  submitContactForm(event: Event): void {
    event.preventDefault();

    // Frontend validation
    if (!this.contactFormData.firstName ||
      !this.contactFormData.lastName ||
      !this.contactFormData.phoneNumber) {
      this.showError('Please fill all required fields');
      return;
    }

    // ✅ SANITIZE INPUT
    this.contactFormData.firstName = this.sanitizeInput(this.contactFormData.firstName);
    this.contactFormData.lastName = this.sanitizeInput(this.contactFormData.lastName);
    this.contactFormData.phoneNumber = this.sanitizeInput(this.contactFormData.phoneNumber);

    // Validate phone format
    if (!this.isValidPhone(this.contactFormData.phoneNumber)) {
      this.showError('Please enter a valid phone number');
      return;
    }

    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.showSuccessMessage = false;
    this.showErrorMessage = false;

    // ✅ ЛОГИРОВАНИЕ ДЛЯ ДЕБАГА
    console.log('📤 Sending data:', this.contactFormData);

    this.contactSalesService.createContactForm({
      body: this.contactFormData
    }).subscribe({
      next: (response) => {
        console.log('✅ SUCCESS:', response);
        this.showSuccessMessage = true;
        this.resetForm();

        setTimeout(() => {
          this.showSuccessMessage = false;
        }, 5000);

        this.isSubmitting = false;
      },
      error: (error) => {
        // ✅ ДЕТАЛЬНЫЙ ЛОГ ОШИБКИ
        console.error('❌ FULL ERROR:', error);
        console.error('❌ Error status:', error.status);
        console.error('❌ Error message:', error.message);
        console.error('❌ Error details:', error.error);

        // Показываем более детальную ошибку
        let errorMsg = 'Error submitting form. ';

        if (error.status === 0) {
          errorMsg += 'Cannot connect to server. Please check your internet connection.';
        } else if (error.status === 404) {
          errorMsg += 'API endpoint not found.';
        } else if (error.status === 500) {
          errorMsg += 'Server error. Please try again later.';
        } else if (error.error?.message) {
          errorMsg += error.error.message;
        } else {
          errorMsg += 'Please try again.';
        }

        this.showError(errorMsg);
        this.isSubmitting = false;
      }
    });
  }


  // ✅ SANITIZE INPUT - защита от XSS
  sanitizeInput(input: string): string {
    if (!input) return '';

    return input
      .trim()
      .replace(/[<>\"']/g, '') // Remove dangerous characters
      .substring(0, 100); // Limit length
  }

  // ✅ VALIDATE PHONE
  isValidPhone(phone: string): boolean {
    // Basic phone validation (digits, spaces, +, -, (), max 20 chars)
    const phoneRegex = /^[\d\s\+\-\(\)]{7,20}$/;
    return phoneRegex.test(phone);
  }

  showError(message: string): void {
    this.errorMessage = message;
    this.showErrorMessage = true;

    setTimeout(() => {
      this.showErrorMessage = false;
    }, 5000);
  }

  resetForm(): void {
    this.contactFormData = {
      firstName: '',
      lastName: '',
      phoneNumber: ''
    };
    this.isSubmitting = false;
  }

  // Navigation methods
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

  navigateToViewDemo(): void {
    this.router.navigate(['/view-demo']);
  }

  navigateToPrivacy(): void {
    this.router.navigate(['/privacy-policy']);
  }

  navigateToTerms(): void {
    this.router.navigate(['/terms-of-service']);
  }

  navigateToRefundPolicy(): void {
    this.router.navigate(['/refund-policy']);
  }
}
