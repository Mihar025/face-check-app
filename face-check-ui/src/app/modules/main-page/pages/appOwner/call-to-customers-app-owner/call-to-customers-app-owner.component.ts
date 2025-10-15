import {Component, OnInit} from '@angular/core';
import {CompanyResponse} from "../../../../../services/models/company-response";
import {ContactSalesControllerService} from "../../../../../services/services/contact-sales-controller.service";
import {AuthService} from "../../../additionalServices/auth-service";
import {
  PageResponseContactSalesFormResponse
} from "../../../../../services/models/page-response-contact-sales-form-response";
import {ContactSalesFormResponse} from "../../../../../services/models/contact-sales-form-response";
import {GetContactFormById$Params} from "../../../../../services/fn/contact-sales-controller/get-contact-form-by-id";
import {DeleteContactForm$Params} from "../../../../../services/fn/contact-sales-controller/delete-contact-form";

@Component({
  selector: 'app-call-to-customers-app-owner',
  templateUrl: './call-to-customers-app-owner.component.html',
  styleUrl: './call-to-customers-app-owner.component.scss'
})
export class CallToCustomersAppOwnerComponent implements OnInit {

  userName: string = 'Admin User';
  companyName: string = 'My Company';
  userPhotoUrl: string = '';


  contactForms: ContactSalesFormResponse[] = [];
  totalElement: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;

  firstName: string = '';
  formId: number = 0;
  lastName: string = '';
  phoneNumber: string = '';

  page: number = 0;
  size: number = 10;


  constructor (
    private authService: AuthService,
      private contactSalesService: ContactSalesControllerService,
  ){

  }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }
    this.loadAllCustomersForms();
    }

  loadAllCustomersForms(){
    const params = {
      page: this.page,
      size: this.size
    };

    this.contactSalesService.getAllContactForms(params).subscribe({
      next: (response) => {
        if(response.content){
          this.contactForms = response.content;
         this.totalElement = response.totalElement || 0;
          this.totalPages = Math.ceil(this.totalElement / this.size);
          this.currentPage = this.page;
        }
      },
      error: (error) => {
        console.error('Cant find all companies', error);
      }
    });
  }


  findFormById(){
    const params : GetContactFormById$Params = {id: this.formId};
    if(!this.formId){
      return;
    }
    this.contactSalesService.getContactFormById(params).subscribe({
      next: (response) => {
        if(response){
          this.formId = response.id ?? 0;
          this.firstName = response.firstName ?? '';
          this.lastName = response.lastName ?? '';
          this.phoneNumber = response.phoneNumber ?? '';
        }
      },
      error: (error) => console.error('Cant find form!')
    });
  }

  deleteFormById(id: number){
    const params : DeleteContactForm$Params = {id};
    this.contactSalesService.deleteContactForm(params).subscribe({
      next: () => this.loadAllCustomersForms(),
      error: (err) => console.error('Cannot delete!', err)
    });
  }




  // Pagination methods
  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadAllCustomersForms();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadAllCustomersForms();
    }
  }

}
