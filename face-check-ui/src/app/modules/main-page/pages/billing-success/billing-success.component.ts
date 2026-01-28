import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-billing-success',
  templateUrl: './billing-success.component.html',
  styleUrls: ['./billing-success.component.scss']
})
export class BillingSuccessComponent implements OnInit {

  ngOnInit(): void {
    // Clear stored company ID
    localStorage.removeItem('billing_company_id');
  }
}
