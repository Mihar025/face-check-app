import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-billing-canceled',
  templateUrl: './billing-canceled.component.html',
  styleUrls: ['./billing-canceled.component.scss']
})
export class BillingCanceledComponent implements OnInit {

  companyId: number = 0;

  ngOnInit(): void {
    const storedId = localStorage.getItem('billing_company_id');
    if (storedId) {
      this.companyId = Number(storedId);
    }
  }
}
