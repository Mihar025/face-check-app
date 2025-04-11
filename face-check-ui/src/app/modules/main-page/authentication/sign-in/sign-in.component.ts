import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from "../../additionalServices/auth-service";
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent implements OnDestroy {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  private authSubscription: Subscription | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    // Initialize form with validators
    this.loginForm = this.fb.group({
      login: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const { login, password } = this.loginForm.value;

    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }

    this.authSubscription = this.authService.login(login, password)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: () => {
          // Ничего делать не нужно, перенаправление происходит в AuthService
          console.log('Login successful');
        },
        error: (error) => {
          console.error('Login error in component:', error);
          this.errorMessage = error.error?.message || 'Login failed. Please check your credentials.';
        }
      });
  }

  //

  signInWithGoogle(): void {
    console.log('Google sign-in clicked');
  }

  ngOnDestroy(): void {
    // Отписываемся при уничтожении компонента
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
