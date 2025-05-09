import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../../additionalServices/auth-service";
import { UserServiceControllerService } from "../../../../../services/services/user-service-controller.service";
import { CompanyControllerService } from "../../../../../services/services/company-controller.service";
import {DeleteCompany$Params} from "../../../../../services/fn/company-controller/delete-company";
import {Router} from "@angular/router";
import {FileControllerService} from "../../../../../services/services/file-controller.service";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";


@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  userName: string = '';
  companyName: string = '';
  companyId: number = 0;
  errorMsg: string = '';


  showDeleteModal: boolean = false;
  deleteConfirmInput: string = '';


  companyNameInput: string = '';
  companyEmailInput: string = '';
  companyPhoneInput: string = '';
  companyAddressInput: string = '';

  adminEmailInput: string = '';
  adminPhoneInput: string = '';
  adminAddressInput: string = '';
  adminPasswordInput: string = '';

  companyNameSuccess: boolean = false;
  companyNameError: string | null = null;
  companyEmailSuccess: boolean = false;
  companyEmailError: string | null = null;
  companyPhoneSuccess: boolean = false;
  companyPhoneError: string | null = null;
  companyAddressSuccess: boolean = false;
  companyAddressError: string | null = null;

  adminEmailSuccess: boolean = false;
  adminEmailError: string | null = null;
  adminPhoneSuccess: boolean = false;
  adminPhoneError: string | null = null;
  adminAddressSuccess: boolean = false;
  adminAddressError: string | null = null;
  adminPasswordSuccess: boolean = false;
  adminPasswordError: string | null = null;

  private http: HttpClient;

  userPhotoUrl: string = '';
  isUploadingPhoto: boolean = false;
  photoUploadError: string | null = null;
  photoUploadSuccess: boolean = false;


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

  logout(): void {
    this.authService.logout();
  }

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
        this.companyNameInput = name;
      },
      error => {
        console.error('Error loading company name:', error);
      }
    );

    this.companyService.getCompanyEmail().subscribe(
      email => {
        this.companyEmailInput = email;
      },
      error => {
        console.error('Error loading company email:', error);
      }
    );

    this.companyService.getCompanyPhone().subscribe(
      phone => {
        this.companyPhoneInput = phone;
      },
      error => {
        console.error('Error loading company phone:', error);
      }
    );

    this.companyService.getCompanyAddress().subscribe(
      address => {
        this.companyAddressInput = address;
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

  updateCompanyName(): void {
    this.companyNameSuccess = false;
    this.companyNameError = null;

    if (!this.companyNameInput || this.companyNameInput.trim() === '') {
      this.companyNameError = 'Company name cannot be empty';
      return;
    }

    const params = {
      body: this.companyNameInput
    };

    this.companyService.updateCompanyName(params).subscribe(
      () => {
        this.companyNameSuccess = true;
        this.companyName = String(this.companyNameInput);
        setTimeout(() => {
          this.companyNameSuccess = false;
        }, 3000);
      },
      error => {
        this.companyNameError = 'Error updating company name: ' + (error.message || 'Unknown error');
        console.error('Error updating company name:', error);
      }
    );
  }

  updateCompanyEmail(): void {
    this.companyEmailSuccess = false;
    this.companyEmailError = null;

    if (!this.companyEmailInput || this.companyEmailInput.trim() === '') {
      this.companyEmailError = 'Company email cannot be empty';
      return;
    }
    const params = {
      body: this.companyEmailInput
    };

    this.companyService.updateCompanyEmail(params).subscribe(
      () => {
        this.companyEmailSuccess = true;
        setTimeout(() => {
          this.companyEmailSuccess = false;
        }, 3000);
      },
      error => {
        this.companyEmailError = 'Error updating company email: ' + (error.message || 'Unknown error');
        console.error('Error updating company email:', error);
      }
    );
  }

  updateCompanyPhone(): void {
    this.companyPhoneSuccess = false;
    this.companyPhoneError = null;

    if (!this.companyPhoneInput || this.companyPhoneInput.trim() === '') {
      this.companyPhoneError = 'Company phone cannot be empty';
      return;
    }
    const params = {
      body: this.companyPhoneInput
    };

    this.companyService.updateCompanyPhone(params).subscribe(
      () => {
        this.companyPhoneSuccess = true;
        setTimeout(() => {
          this.companyPhoneSuccess = false;
        }, 3000);
      },
      error => {
        this.companyPhoneError = 'Error updating company phone: ' + (error.message || 'Unknown error');
        console.error('Error updating company phone:', error);
      }
    );
  }

  updateCompanyAddress(): void {
    this.companyAddressSuccess = false;
    this.companyAddressError = null;

    if (!this.companyAddressInput || this.companyAddressInput.trim() === '') {
      this.companyAddressError = 'Company address cannot be empty';
      return;
    }

    const params = {
      body: this.companyAddressInput
    };

    this.companyService.updateCompanyAddress(params).subscribe(
      () => {
        this.companyAddressSuccess = true;


        setTimeout(() => {
          this.companyAddressSuccess = false;
        }, 3000);
      },
      error => {
        this.companyAddressError = 'Error updating company address: ' + (error.message || 'Unknown error');
        console.error('Error updating company address:', error);
      }
    );
  }

  updateAdminEmail(): void {
    this.adminEmailSuccess = false;
    this.adminEmailError = null;

    if (!this.adminEmailInput || this.adminEmailInput.trim() === '') {
      this.adminEmailError = 'Email cannot be empty';
      return;
    }

    const params = {
      email: this.adminEmailInput
    };

    this.userService.updateEmail(params).subscribe(
      () => {
        this.adminEmailSuccess = true;
        setTimeout(() => {
          this.adminEmailSuccess = false;
        }, 3000);
      },
      error => {
        this.adminEmailError = 'Error updating email: ' + (error.message || 'Unknown error');
        console.error('Error updating admin email:', error);
      }
    );
  }

  updateAdminPhone(): void {
    this.adminPhoneSuccess = false;
    this.adminPhoneError = null;

    if (!this.adminPhoneInput || this.adminPhoneInput.trim() === '') {
      this.adminPhoneError = 'Phone number cannot be empty';
      return;
    }

    const params = {
      phone: this.adminPhoneInput
    };

    this.userService.updatePhone(params).subscribe(
      () => {
        this.adminPhoneSuccess = true;
        setTimeout(() => {
          this.adminPhoneSuccess = false;
        }, 3000);
      },
      error => {
        this.adminPhoneError = 'Error updating phone: ' + (error.message || 'Unknown error');
        console.error('Error updating admin phone:', error);
      }
    );
  }

  updateAdminAddress(): void {
    this.adminAddressSuccess = false;
    this.adminAddressError = null;

    if (!this.adminAddressInput || this.adminAddressInput.trim() === '') {
      this.adminAddressError = 'Address cannot be empty';
      return;
    }

    const params = {
      homeAddress: String(this.adminAddressInput)
    };

    this.userService.updateHomeAddress(params).subscribe(
      () => {
        this.adminAddressSuccess = true;
        setTimeout(() => {
          this.adminAddressSuccess = false;
        }, 3000);
      },
      error => {
        this.adminAddressError = 'Error updating address: ' + (error.message || 'Unknown error');
        console.error('Error updating admin address:', error);
      }
    );
  }

  updateAdminPassword(): void {
    this.adminPasswordSuccess = false;
    this.adminPasswordError = null;

    if (!this.adminPasswordInput || this.adminPasswordInput.trim() === '') {
      this.adminPasswordError = 'Password cannot be empty';
      return;
    }

    if (this.adminPasswordInput.length < 6) {
      this.adminPasswordError = 'Password must be at least 6 characters long';
      return;
    }

    const params = {
      password: this.adminPasswordInput
    };

    this.userService.updatePassword(params).subscribe(
      () => {
        this.adminPasswordSuccess = true;
        this.adminPasswordInput = '';
        setTimeout(() => {
          this.adminPasswordSuccess = false;
        }, 3000);
      },
      error => {
        this.adminPasswordError = 'Error updating password: ' + (error.message || 'Unknown error');
        console.error('Error updating admin password:', error);
      }
    );
  }

  async deleteCompany(){
    this.errorMsg = '';
    const loadedCompanyId = await this.loadAdminsCompanyId();

    const params: DeleteCompany$Params = {
      companyId: loadedCompanyId
    }

    this.companyService.deleteCompany(params).subscribe(
      () => {
        this.router.navigate(['/sign-in'])
        console.log('Successfully deleted company');
      },
      error => {
        this.errorMsg = 'Cannot delete company! ' + (error.message || 'Unknown problem');
      }
    )
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

  showDeleteConfirmation() {
    this.showDeleteModal = true;
    this.deleteConfirmInput = '';
    this.errorMsg = '';
  }

  cancelDelete() {
    this.showDeleteModal = false;
  }

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
          this.photoUploadError = 'Please, select file';
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






}
