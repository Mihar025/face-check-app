import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-privacy-policy',
  templateUrl: './privacy-policy.component.html',
  styleUrl: './privacy-policy.component.scss'
})
export class PrivacyPolicyComponent implements OnInit {

  constructor(private router: Router) {}

  ngOnInit(): void {
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
