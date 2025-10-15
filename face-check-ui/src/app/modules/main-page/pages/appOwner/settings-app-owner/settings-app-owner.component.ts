import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../additionalServices/auth-service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {CompanyControllerService} from "../../../../../services/services/company-controller.service";

@Component({
  selector: 'app-settings-app-owner',
  templateUrl: './settings-app-owner.component.html',
  styleUrl: './settings-app-owner.component.scss'
})
export class SettingsAppOwnerComponent implements OnInit {
  userName: string = '';
  companyName: string = '';
  companyEmail: string = '';
  companyPhone: string = '';
  companyAddress: string = '';
  userPhotoUrl: string = '';
  companyId: number | null = null;



  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService
) { }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
    this.authService.logout();
    return;
  }
}


}
