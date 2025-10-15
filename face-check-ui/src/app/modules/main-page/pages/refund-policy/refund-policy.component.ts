import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-refund-policy',
  templateUrl: './refund-policy.component.html',
  styleUrl: './refund-policy.component.scss'
})
export class RefundPolicyComponent implements OnInit {

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Scroll to top when component loads
    window.scrollTo(0, 0);
  }

  // Navigation methods
  navigateToFaceCheck(): void {
    this.router.navigate(['/face-check']);
  }

  navigateToPricing(): void {
    this.router.navigate(['/pricing']);
  }

  navigateToAboutUs(): void {
    this.router.navigate(['/about-us']);
  }

  navigateToViewDemo(): void {
    this.router.navigate(['/view-demo']);
  }

  navigateToSignIn(): void {
    this.router.navigate(['/sign-in']);
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
