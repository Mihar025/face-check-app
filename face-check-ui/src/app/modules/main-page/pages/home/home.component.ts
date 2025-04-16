import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import Swiper from 'swiper';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, AfterViewInit {

  constructor(private router: Router) {}

  ngOnInit(): void {

  }

  ngAfterViewInit(): void {

    setTimeout(() => {
      this.initSwiper();
    }, 0);
  }

  private initSwiper(): void {
    const swiper = new Swiper('.swiper', {
      direction: 'horizontal',
      slidesPerView: 3,
      spaceBetween: 30,
      loop: true,

      pagination: {
        el: '.swiper-pagination',
        clickable: true,
        dynamicBullets: true
      },


      navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
      },


      breakpoints: {
        0: {
          slidesPerView: 1,
          spaceBetween: 20
        },
        768: {
          slidesPerView: 2,
          spaceBetween: 30
        },
        1024: {
          slidesPerView: 3,
          spaceBetween: 30
        }
      }
    });
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
}
