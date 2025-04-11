import {Component, OnDestroy} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Subscription} from "rxjs";
import {AuthService} from "../../additionalServices/auth-service";
import {finalize} from "rxjs/operators";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnDestroy {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
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
          this.router.navigate(['/register/admin']); // Используем Angular Router вместо window.location
        },




        error: (error) => {
          console.error('Registration error in component:', error);
          this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        }
      });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
