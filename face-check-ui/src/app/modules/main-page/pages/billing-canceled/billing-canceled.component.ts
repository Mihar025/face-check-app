import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-billing-canceled',
  templateUrl: './billing-canceled.component.html',
  styleUrl: './billing-canceled.component.scss'
})
export class BillingCanceledComponent implements OnInit {

  companyId!: number;

  ngOnInit(): void {
    this.companyId = Number(localStorage.getItem('billing_company_id'));
  }



}
