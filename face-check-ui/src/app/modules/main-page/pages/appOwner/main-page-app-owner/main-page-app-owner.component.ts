import { Component } from '@angular/core';

@Component({
  selector: 'app-main-page-app-owner',
  templateUrl: './main-page-app-owner.component.html',
  styleUrl: './main-page-app-owner.component.scss'
})
export class MainPageAppOwnerComponent {

  userName: string = 'John Doe';
  companyName: string = '';
  totalEmployees: number = 0;
  totalWorksites: number = 0;
  userPhotoUrl: string = '';

  logout() {

  }
}
