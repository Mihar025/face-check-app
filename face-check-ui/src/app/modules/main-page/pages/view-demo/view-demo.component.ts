import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-demo',
  templateUrl: './view-demo.component.html',
  styleUrls: ['./view-demo.component.scss']
})
export class ViewDemoComponent implements OnInit {

  // Screenshot modal
  showScreenshotModal: boolean = false;
  selectedScreenshot: string = '';

  // Screenshot paths mapping - CORRECT ORDER FROM HTML
  screenshots: { [key: number]: string } = {
    1: 'assets/01.png',  // Smart Tracking Dashboard
    2: 'assets/02.png',  // Real-Time Notifications
    3: 'assets/04.png',  // Productivity Analytics
    4: 'assets/03.png'   // Finance Management
  };

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

  // Open screenshot in fullscreen modal
  openScreenshot(screenshotId: number): void {
    console.log('Opening screenshot:', screenshotId);
    this.selectedScreenshot = this.screenshots[screenshotId] || '';
    if (this.selectedScreenshot) {
      this.showScreenshotModal = true;
      // Prevent body scroll when modal is open
      document.body.style.overflow = 'hidden';
    }
  }

  // Close screenshot modal
  closeScreenshotModal(): void {
    this.showScreenshotModal = false;
    this.selectedScreenshot = '';
    // Restore body scroll
    document.body.style.overflow = 'auto';
  }

  // Prevent modal close when clicking inside
  preventModalClose(event: Event): void {
    event.stopPropagation();
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
