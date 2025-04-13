import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Subscription } from "rxjs";
import { AuthService } from "../../additionalServices/auth-service";
import { finalize } from "rxjs/operators";
import { Router } from "@angular/router";

@Component({
  selector: 'app-register-company',
  templateUrl: './register-company.component.html',
  styleUrls: ['./register-company.component.scss']
})
export class RegisterCompanyComponent implements OnInit, OnDestroy {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  isAuthenticated = false;
  private authSubscription: Subscription | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      companyName: ['', [Validators.required]],
      companyAddress: ['', [Validators.required]],
      companyPhone: ['', [Validators.required]],
      companyEmail: ['', [Validators.required, Validators.email]],
    });
  }

  ngOnInit(): void {
    // Проверка аутентификации пользователя
    this.isAuthenticated = this.authService.isUserAuthenticated();

    // Если пользователь не аутентифицирован, перенаправляем на страницу входа
    if (!this.isAuthenticated) {
      this.errorMessage = 'Authentication required. Please sign in to register a company.';
      // Сохраняем URL для перенаправления после входа
      localStorage.setItem('redirectAfterLogin', '/registration/company');
      setTimeout(() => {
        this.router.navigate(['/sign-in']);
      }, 2000);
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const {
      companyName,
      companyAddress,
      companyPhone,
      companyEmail
    } = this.registerForm.value;

    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }

    // Проверяем еще раз наличие токена
    if (!this.authService.getToken()) {
      this.errorMessage = 'Authentication token not found. Please sign in again.';
      this.isLoading = false;
      return;
    }

    this.authSubscription = this.authService.registerCompany(
      companyName,
      companyAddress,
      companyPhone,
      companyEmail
    )
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      ).subscribe({
        next: () => {
          console.log('Registration Company successful!');
          // Показываем пользователю сообщение об успехе
          this.showSuccessMessage();
          // Перенаправляем на страницу регистрации администратора
          setTimeout(() => {
            this.router.navigate(['/main-page/admin']);
          }, 1500);
        },
        error: (error) => {
          console.error('Registration error in component:', error);
          if (error.status === 401 || error.status === 403) {
            this.errorMessage = 'Your session has expired. Please sign in again.';
            setTimeout(() => {
              this.authService.logout();
            }, 2000);
          } else {
            this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
          }
        }
      });
  }

  // Метод для отображения сообщения об успешной регистрации
  private showSuccessMessage(): void {
    // Можно реализовать через сервис уведомлений или через состояние компонента
    this.errorMessage = ''; // Очищаем ошибки
    // Здесь можно добавить логику для отображения сообщения об успехе
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  navigateToLogin(): void {
    this.router.navigate(['/sign-in']);
  }
}
