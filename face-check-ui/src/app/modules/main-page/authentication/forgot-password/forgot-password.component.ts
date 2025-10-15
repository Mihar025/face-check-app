import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Subscription } from "rxjs";
import { AuthenticationService } from "../../../../services/services/authentication.service";
import { finalize } from "rxjs/operators";
import { Router } from "@angular/router";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent implements OnDestroy {
  // Forms
  emailForm: FormGroup;
  resetForm: FormGroup;

  // State management
  currentStep: 'email' | 'code' | 'success' = 'email';
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  userEmail = '';
  verificationCode = '';

  private subscription: Subscription | null = null;

  constructor(
    private fb: FormBuilder,
    private authApiService: AuthenticationService,
    private router: Router
  ) {
    // Initialize email form
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    // Initialize reset form with code and passwords
    this.resetForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      newPassword: [{value: '', disabled: true}, [
        Validators.required,
        Validators.minLength(6)
      ]],
      confirmPassword: [{value: '', disabled: true}, [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  // Custom validator for password matching
  passwordMatchValidator(formGroup: FormGroup) {
    const newPassword = formGroup.get('newPassword')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;

    if (newPassword !== confirmPassword) {
      formGroup.get('confirmPassword')?.setErrors({ mismatch: true });
    } else {
      const errors = formGroup.get('confirmPassword')?.errors;
      if (errors) {
        delete errors['mismatch'];
        if (Object.keys(errors).length === 0) {
          formGroup.get('confirmPassword')?.setErrors(null);
        }
      }
    }
    return null;
  }

  // Step 1: Send verification code to email
  sendVerificationCode(): void {
    if (this.emailForm.invalid) {
      this.emailForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.userEmail = this.emailForm.value.email;

    if (this.subscription) {
      this.subscription.unsubscribe();
    }

    // Call the API to send reset code
    this.subscription = this.authApiService.sendResetCode({
      body: { email: this.userEmail }
    })
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.currentStep = 'code';
          this.successMessage = 'Verification code has been sent to your email';
          localStorage.setItem('reset_email', this.userEmail);
        },
        error: (error) => {
          console.error('Error sending code:', error);
          this.errorMessage = error.error?.message || 'Failed to send verification code. Please try again.';
        }
      });
  }

  // Step 2: Handle code completion from code-input component
  onCodeCompleted(code: string): void {
    this.verificationCode = code;
    this.resetForm.patchValue({ code: code });

    // Automatically verify the code when it's complete
    if (code.length === 6) {
      this.verifyCode();
    }
  }

  // Step 2.5: Verify the code before allowing password reset
  verifyCode(): void {
    if (!this.verificationCode || this.verificationCode.length !== 6) {
      this.errorMessage = 'Please enter a valid 6-digit code';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    if (this.subscription) {
      this.subscription.unsubscribe();
    }

    // Call the API to verify the code
    this.subscription = this.authApiService.verifyCode({
      body: {
        email: this.userEmail,
        code: this.verificationCode
      }
    })
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.successMessage = 'Code verified successfully. You can now set your new password.';
          // Enable the password form after successful verification
          this.resetForm.get('newPassword')?.enable();
          this.resetForm.get('confirmPassword')?.enable();
        },
        error: (error) => {
          console.error('Error verifying code:', error);
          this.errorMessage = error.error?.message || 'Invalid or expired code. Please try again.';
          // Reset the code field on error
          this.verificationCode = '';
          this.resetForm.patchValue({ code: '' });
        }
      });
  }

  // Step 3: Reset password with all data
  resetPassword(): void {
    if (this.resetForm.invalid || !this.verificationCode) {
      this.resetForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    const { newPassword, confirmPassword } = this.resetForm.value;

    if (this.subscription) {
      this.subscription.unsubscribe();
    }

    // Call the API to reset password with all required data
    this.subscription = this.authApiService.resetPassword({
      body: {
        email: this.userEmail,
        code: this.verificationCode,
        newPassword: newPassword,
        confirmNewPassword: confirmPassword
      }
    })
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.currentStep = 'success';
          this.successMessage = 'Password has been reset successfully!';

          // Clean up localStorage
          localStorage.removeItem('reset_email');

          // Redirect to login after 3 seconds
          setTimeout(() => {
            this.router.navigate(['/sign-in']);
          }, 3000);
        },
        error: (error) => {
          console.error('Error resetting password:', error);
          this.errorMessage = error.error?.message || 'Failed to reset password. Please try again.';
        }
      });
  }

  // Navigate back to email step
  goBack(): void {
    this.currentStep = 'email';
    this.errorMessage = '';
    this.successMessage = '';
    this.verificationCode = '';
    this.resetForm.reset();
  }

  // Navigate to sign in page
  redirectToSignIn(): void {
    this.router.navigate(['/sign-in']);
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }

    // Clean up localStorage on component destroy
    localStorage.removeItem('reset_email');
  }
}
