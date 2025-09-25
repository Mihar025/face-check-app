// settings.component.ts
import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../../additionalServices/auth-service";
import { UserServiceControllerService } from "../../../../../services/services/user-service-controller.service";
import { CompanyControllerService } from "../../../../../services/services/company-controller.service";
import { DeleteCompany$Params } from "../../../../../services/fn/company-controller/delete-company";
import { Router } from "@angular/router";
import { FileControllerService } from "../../../../../services/services/file-controller.service";
import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import {UpdateCompanyEmailRequest} from "../../../../../services/models/update-company-email-request";
import {UpdateCompanyPhoneNumberRequest} from "../../../../../services/models/update-company-phone-number-request";
import {UpdateCompanyNameRequest} from "../../../../../services/models/update-company-name-request";
import {UpdateCompanyAddressRequest} from "../../../../../services/models/update-company-address-request";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  // User and Company Info
  userName: string = '';
  companyName: string = '';
  companyId: number = 0;
  errorMsg: string = '';

  // Simple delete confirmation
  showDeleteConfirm: boolean = false;
  deleteConfirmInput: string = '';

  // Company Fields
  companyNameInput: string = '';
  companyEmailInput: string = '';
  companyPhoneInput: string = '';
  companyAddressInput: string = '';

  // Admin Fields
  adminEmailInput: string = '';
  adminPhoneInput: string = '';
  adminAddressInput: string = '';

  // Password Fields
  newPassword: string = '';
  confirmPassword: string = '';

  // Success/Error States
  companyUpdateSuccess: boolean = false;
  companyUpdateError: string | null = null;
  adminUpdateSuccess: boolean = false;
  adminUpdateError: string | null = null;
  passwordUpdateSuccess: boolean = false;
  passwordUpdateError: string | null = null;

  // Loading States
  isSavingCompany: boolean = false;
  isSavingAdmin: boolean = false;
  isSavingPassword: boolean = false;
  isDeleting: boolean = false;

  // Photo Upload
  userPhotoUrl: string = '';
  isUploadingPhoto: boolean = false;
  photoUploadError: string | null = null;
  photoUploadSuccess: boolean = false;

  showCompanyModal: boolean = false;
  showAdminModal: boolean = false;
  showSecurityModal: boolean = false;
  showDeleteModal: boolean = false;

// For the password fields in security modal:
  currentPassword: string = '';
  adminPasswordInput: string = '';
  adminPasswordSuccess: boolean = false;
  adminPasswordError: string | null = null;

  // Password Validation
  get hasUpperCase(): boolean {
    return /[A-Z]/.test(this.newPassword);
  }

  get hasNumber(): boolean {
    return /[0-9]/.test(this.newPassword);
  }


  openCompanyModal(): void {
    this.showCompanyModal = true;
  }

  closeCompanyModal(): void {
    this.showCompanyModal = false;
  }

// Admin Modal
  openAdminModal(): void {
    this.showAdminModal = true;
  }

  closeAdminModal(): void {
    this.showAdminModal = false;
  }

// Security Modal
  openSecurityModal(): void {
    this.showSecurityModal = true;
  }

  closeSecurityModal(): void {
    this.showSecurityModal = false;
  }

// Delete Modal
  showDeleteConfirmation(): void {
    this.showDeleteModal = true;
  }

  cancelDelete(): void {
    this.showDeleteModal = false;
    this.deleteConfirmInput = '';
  }



  get passwordStrength(): number {
    if (!this.newPassword) return 0;
    let strength = 0;
    if (this.newPassword.length >= 6) strength += 33;
    if (this.hasUpperCase) strength += 33;
    if (this.hasNumber) strength += 34;
    return strength;
  }

  get passwordStrengthText(): string {
    const strength = this.passwordStrength;
    if (strength < 34) return 'Weak';
    if (strength < 67) return 'Fair';
    return 'Strong';
  }

  private http: HttpClient;

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private fileService: FileControllerService,
    http: HttpClient,
    private router: Router
  ) {
    this.http = http;
  }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN') {
      let targetUrl = '/';
      if (userRole === 'USER') {
        targetUrl = '/main-page/user';
      }
      window.location.href = targetUrl;
      return;
    }

    this.loadUserFullName();
    this.loadCompanyName();
    this.loadCompanyData();
    this.loadAdminData();
    this.getUserPhoto();
  }

  async saveCompanyInfo(): Promise<void> {
    this.companyUpdateError = null;
    this.companyUpdateSuccess = false;
    this.isSavingCompany = true;

    try {
      if (this.companyNameInput?.trim()) {

        const request : UpdateCompanyNameRequest = {
          companyName: this.companyNameInput
        };

        await this.companyService.updateCompanyName({
          body: request
        }).toPromise();
        this.companyName = this.companyNameInput;
      }

      if (this.companyEmailInput?.trim()) {

        const request : UpdateCompanyEmailRequest = {
          email: this.companyEmailInput
        };

        await this.companyService.updateCompanyEmail({
          body: request
        }).toPromise();
      }



      if (this.companyPhoneInput?.trim()) {

        const request : UpdateCompanyPhoneNumberRequest = {
          phoneNumber: this.companyPhoneInput
        };


        await this.companyService.updateCompanyPhone({
          body: request
        }).toPromise();
      }

      if (this.companyAddressInput?.trim()) {

        const request : UpdateCompanyAddressRequest = {
          companyAddress: this.companyAddressInput
        };

        await this.companyService.updateCompanyAddress({
          body: request
        }).toPromise();
      }

      this.companyUpdateSuccess = true;
      setTimeout(() => this.companyUpdateSuccess = false, 3000);

    } catch (error: any) {
      this.companyUpdateError = 'Error: ' + error.message;
    } finally {
      this.isSavingCompany = false;
    }
  }
  async saveAdminInfo(): Promise<void> {
    this.adminUpdateError = null;
    this.adminUpdateSuccess = false;
    this.isSavingAdmin = true;

    try {
      if (this.adminEmailInput && this.adminEmailInput.trim() !== '') {
        await this.userService.updateEmail({ email: this.adminEmailInput }).toPromise();
        // Убрал кавычки и исправил переменную
      }

      if (this.adminPhoneInput && this.adminPhoneInput.trim() !== '') {
        await this.userService.updatePhone({ phone: this.adminPhoneInput }).toPromise();
        // Убрал кавычки
      }

      if (this.adminAddressInput && this.adminAddressInput.trim() !== '') {
        await this.userService.updateHomeAddress({ homeAddress: this.adminAddressInput }).toPromise();
        // Исправил переменную и убрал кавычки
      }

      this.adminUpdateSuccess = true;
      setTimeout(() => this.adminUpdateSuccess = false, 3000);

    } catch (error: any) {
      this.adminUpdateError = 'Error updating personal information: ' + (error.message || 'Unknown error');
      console.error('Error updating admin info:', error);
    } finally {
      this.isSavingAdmin = false;
    }
  }


  isPasswordValid(): boolean {
    return this.newPassword.length >= 6 &&
      this.hasUpperCase &&
      this.hasNumber &&
      this.newPassword === this.confirmPassword;
  }

  async updatePassword(): Promise<void> {
    this.passwordUpdateSuccess = false;
    this.passwordUpdateError = null;

    if (!this.isPasswordValid()) {
      this.passwordUpdateError = 'Please meet all password requirements';
      return;
    }

    this.isSavingPassword = true;

    try {
      await this.userService.updatePassword({ password: this.newPassword }).toPromise();
      this.passwordUpdateSuccess = true;
      this.isSavingPassword = false;
      this.newPassword = '';
      this.confirmPassword = '';

      setTimeout(() => {
        this.passwordUpdateSuccess = false;
      }, 3000);

    } catch (error: any) {
      this.passwordUpdateError = 'Error updating password: ' + (error.message || 'Unknown error');
      this.isSavingPassword = false;
      console.error('Error updating password:', error);
    }
  }

  // Data Loading Functions
  loadUserFullName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response && response.fullName) {
          this.userName = response.fullName;
        }
      },
      error => {
        console.error('Error loading user full name:', error);
      }
    );
  }

  loadCompanyName(): void {
    this.userService.findWorkerCompanyName().subscribe(
      response => {
        if (response && response.companyName) {
          this.companyName = response.companyName;
        }
      },
      error => {
        console.error('Error loading company name:', error);
      }
    );
  }

  loadCompanyData(): void {
    this.companyService.getCompanyName().subscribe(
      name => {
        this.companyNameInput = String(name || '');
      },
      error => {
        console.error('Error loading company name:', error);
      }
    );

    this.companyService.getCompanyEmail().subscribe(
      email => {
        this.companyEmailInput = String(email || '');
      },
      error => {
        console.error('Error loading company email:', error);
      }
    );

    this.companyService.getCompanyPhone().subscribe(
      phone => {
        // Преобразуем число в строку
        this.companyPhoneInput = String(phone || '');
      },
      error => {
        console.error('Error loading company phone:', error);
      }
    );

    this.companyService.getCompanyAddress().subscribe(
      address => {
        this.companyAddressInput = String(address || '');
      },
      error => {
        console.error('Error loading company address:', error);
      }
    );
  }



  loadAdminData(): void {
    this.userService.findWorkerEmail().subscribe(
      response => {
        if (response && response.email) {
          this.adminEmailInput = response.email;
        }
      },
      error => {
        console.error('Error loading admin email:', error);
      }
    );

    this.userService.findWorkerPhoneNumber().subscribe(
      response => {
        if (response && response.phoneNumber) {
          this.adminPhoneInput = response.phoneNumber;
        }
      },
      error => {
        console.error('Error loading admin phone:', error);
      }
    );

    this.userService.findWorkerHomeAddress().subscribe(
      response => {
        if (response && response.homeAddress) {
          this.adminAddressInput = response.homeAddress;
        }
      },
      error => {
        console.error('Error loading admin address:', error);
      }
    );
  }

  // Delete Company
  async deleteCompany(): Promise<void> {
    this.errorMsg = '';
    this.isDeleting = true;
    const loadedCompanyId = await this.loadAdminsCompanyId();

    const params: DeleteCompany$Params = {
      companyId: loadedCompanyId
    }

    this.companyService.deleteCompany(params).subscribe(
      () => {
        this.isDeleting = false;
        this.router.navigate(['/sign-in']);
        console.log('Successfully deleted company');
      },
      error => {
        this.isDeleting = false;
        this.errorMsg = 'Cannot delete company! ' + (error.message || 'Unknown problem');
      }
    );
  }

  private async loadAdminsCompanyId(): Promise<number> {
    try {
      const response = await this.userService.findWorkerCompanyIdByAuthentication().toPromise();
      if (response && response.companyId) {
        this.companyId = response.companyId;
        return response.companyId;
      }
      return 0;
    } catch (error) {
      console.error('Error loading company Id', error);
      return 0;
    }
  }

  // Photo Management
  getUserPhoto(): void {
    this.userService.findWorkerFullContactInformation().subscribe(
      response => {
        if (response && response.photoUrl) {
          this.userPhotoUrl = response.photoUrl;
        }
      },
      error => {
        console.error('Error loading user photo:', error);
      }
    );
  }

  pickAndUploadImage(): void {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/jpeg, image/png, image/gif, image/webp';

    fileInput.onchange = (event) => {
      const target = event.target as HTMLInputElement;
      const files = target.files;

      if (files && files.length > 0) {
        const selectedFile = files[0];

        if (!selectedFile.type.startsWith('image/')) {
          this.photoUploadError = 'Please select an image file';
          return;
        }

        this.isUploadingPhoto = true;
        this.photoUploadError = null;
        this.photoUploadSuccess = false;

        const formData = new FormData();
        formData.append('photo', selectedFile);
        formData.append('email', this.adminEmailInput);
        formData.append('prefix', 'profile');

        const apiUrl = this.fileService['rootUrl'] + '/files/upload/photo';

        const headers = new HttpHeaders({
          'Authorization': 'Bearer ' + this.authService.getToken()
        });

        this.http.post(apiUrl, formData, {
          headers: headers,
          responseType: 'text'
        }).subscribe(
          (result: string) => {
            this.userPhotoUrl = result;
            this.isUploadingPhoto = false;
            this.photoUploadSuccess = true;
            this.photoUploadError = null;

            this.getUserPhoto();

            setTimeout(() => {
              this.photoUploadSuccess = false;
            }, 3000);
          },
          (error: HttpErrorResponse) => {
            console.error('Error uploading photo:', error);
            this.isUploadingPhoto = false;
            this.photoUploadError = 'Error: ' + (error.message || 'Unknown');
          }
        );
      }
    };

    fileInput.click();
  }

  logout(): void {
    this.authService.logout();
  }
}
