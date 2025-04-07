import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrl: './about-us.component.scss'
})
export class AboutUsComponent {


  constructor(private router: Router) {}

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
}
