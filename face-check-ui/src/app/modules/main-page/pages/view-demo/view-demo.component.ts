import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-demo',
  templateUrl: './view-demo.component.html',
  styleUrls: ['./view-demo.component.scss']
})
export class ViewDemoComponent implements OnInit {

  // Tutorial videos data
  tutorialVideos = [
    {
      id: 'getting-started',
      title: 'Getting Started',
      description: 'Learn the basics of Facecheck in 2 minutes',
      thumbnail: 'tutorial-1.jpg',
      duration: '2:15',
      videoUrl: 'getting-started.mp4'
    },
    {
      id: 'employee-setup',
      title: 'Employee Setup',
      description: 'How to add and manage employees',
      thumbnail: 'tutorial-2.jpg',
      duration: '3:45',
      videoUrl: 'employee-setup.mp4'
    },
    {
      id: 'reports',
      title: 'Generate Reports',
      description: 'Create and export time tracking reports',
      thumbnail: 'tutorial-3.jpg',
      duration: '4:20',
      videoUrl: 'reports.mp4'
    },
    {
      id: 'mobile-app',
      title: 'Mobile App Guide',
      description: 'Complete mobile app walkthrough',
      thumbnail: 'tutorial-4.jpg',
      duration: '5:10',
      videoUrl: 'mobile-guide.mp4'
    },
    {
      id: 'tax-setup',
      title: 'Tax Calculations',
      description: 'Setting up IRS tax calculations',
      thumbnail: 'tutorial-5.jpg',
      duration: '3:30',
      videoUrl: 'tax-setup.mp4'
    },
    {
      id: 'integrations',
      title: 'Integrations',
      description: 'Connect with your favorite tools',
      thumbnail: 'tutorial-6.jpg',
      duration: '2:50',
      videoUrl: 'integrations.mp4'
    }
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {
    // Scroll to top when component loads
    window.scrollTo(0, 0);

    // Animate numbers on load
    this.animateNumbers();
  }

  animateNumbers(): void {
    setTimeout(() => {
      const numbers = document.querySelectorAll('.stat-number');
      numbers.forEach((num) => {
        const element = num as HTMLElement;
        const target = parseFloat(element.getAttribute('data-value') || '0');
        const duration = 2000;
        const increment = target / (duration / 16);
        let current = 0;

        const timer = setInterval(() => {
          current += increment;
          if (current >= target) {
            current = target;
            clearInterval(timer);
          }
          element.textContent = current.toFixed(target % 1 !== 0 ? 1 : 0);
        }, 16);
      });
    }, 1000);
  }

  scrollToVideo(): void {
    const videoSection = document.getElementById('mainVideoSection');
    if (videoSection) {
      videoSection.scrollIntoView({ behavior: 'smooth' });
    }
  }

  // Video functions
  playMainVideo(): void {
    const videoPlaceholder = document.querySelector('.video-placeholder') as HTMLElement;
    const video = document.getElementById('mainVideo') as HTMLVideoElement;

    if (videoPlaceholder && video) {
      videoPlaceholder.style.display = 'none';
      video.style.display = 'block';
      video.play();
    }
  }

  playVideo(videoId: string): void {
    console.log('Playing video:', videoId);
    // Implement video player modal or navigation
    // You can open a modal with the video or navigate to a video page
  }

  watchFeatureDemo(feature: string): void {
    console.log('Watching feature demo:', feature);
    // Implement feature-specific demo
    // Could open a modal or navigate to specific demo
  }

  startInteractiveDemo(): void {
    console.log('Starting interactive demo');
    // Implement interactive demo launch
    // Could open in fullscreen or new tab
  }

  schedulePersonalDemo(): void {
    this.router.navigate(['/schedule-consultation']);
  }

  startFreeTrial(): void {
    this.router.navigate(['/pricing']);
  }

  contactSales(): void {
    this.router.navigate(['/pricing']);
    // Or open contact modal
  }

  // Navigation methods
  navigateToFaceCheck(): void {
    this.router.navigate(['/']);
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
