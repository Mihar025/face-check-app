import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Subscription } from "rxjs";
import { AuthService } from "../../additionalServices/auth-service";
import { finalize } from "rxjs/operators";
import { Router } from "@angular/router";

@Component({
  selector: 'app-register-admin',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
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
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phoneNumber: ['', [Validators.required]],
      homeAddress: ['', [Validators.required]],
      dateOfBirth: ['', [Validators.required]],
      gender: ['MALE', [Validators.required]],
      ssn_WORKER: ['', [Validators.required, Validators.pattern(/^\d{3}-\d{2}-\d{4}$/)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const formValues = {...this.registerForm.value};

    if (formValues.ssn_WORKER) {
      formValues.ssn_WORKER = Number(formValues.ssn_WORKER.replace(/-/g, ''));
    }

    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }

    this.authSubscription = this.authService.registerAdmin(
      formValues.firstName,
      formValues.lastName,
      formValues.email,
      formValues.password,
      formValues.phoneNumber,
      formValues.homeAddress,
      formValues.dateOfBirth,
      formValues.gender,
      formValues.ssn_WORKER
    )
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      ).subscribe({
        next: () => {
          console.log('Admin registration successful!');
          this.router.navigate(['/verification/admin']);
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
