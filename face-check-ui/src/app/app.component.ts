import {Component, OnInit} from '@angular/core';
import {AuthService} from "./modules/main-page/additionalServices/auth-service";
import {UserDataService} from "./modules/components/user-data-service/user-data-service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit{
  title = 'face-check-ui';

  constructor(
    private authService: AuthService,
    private userDataService: UserDataService
  ) {}

  ngOnInit() {
    if (this.authService.isUserAuthenticated()) {
      console.log('User authenticated, loading data...');
      this.userDataService.loadUserData();
    }
  }


}
