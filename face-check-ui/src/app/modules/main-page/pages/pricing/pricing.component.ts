import { Component, OnInit, Inject } from '@angular/core';
import { Router } from "@angular/router";
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-pricing',
  templateUrl: './pricing.component.html',
  styleUrl: './pricing.component.scss'
})
export class PricingComponent implements OnInit {

  constructor(
    private router: Router,
    @Inject(DOCUMENT) private document: Document
  ) {}

  ngOnInit() {
    // Инициализация компонента
  }

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
    this.router.navigate(['/sign-in'], { queryParams: { plan: plan } });
  }

  // Добавляем метод для скролла
  scrollToPricingPlans(): void {
    const pricingPlans = this.document.getElementById('pricingPlans');
    if (pricingPlans) {
      pricingPlans.scrollIntoView({ behavior: 'smooth' });
      console.log('Скролл выполнен');
    } else {
      console.error('Элемент pricingPlans не найден');
    }
  }
}
