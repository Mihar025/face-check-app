import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-demo',
  templateUrl: './view-demo.component.html',
  styleUrls: ['./view-demo.component.scss']
})
export class ViewDemoComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
    // Scroll to top when component loads
    window.scrollTo(0, 0);
  }

  // Scroll to features section
  scrollToFeatures(): void {
    const featuresSection = document.getElementById('featuresSection');
    if (featuresSection) {
      featuresSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  // Open screenshot in modal or lightbox
  openScreenshot(screenshotId: number): void {
    console.log('Opening screenshot:', screenshotId);
    // TODO: Implement modal/lightbox to show full screenshot
    // You can add a modal component or use a library like ngx-gallery

    // Example implementation:
    // this.showScreenshotModal(screenshotId);
    alert(`Opening screenshot ${screenshotId} - Implement modal here!`);
  }

  // Contact sales
  contactSales(): void {
    // Navigate to pricing or open contact modal
    this.router.navigate(['/pricing']);
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
