import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingSuccessComponent } from './billing-success.component';

describe('BillingSuccessComponent', () => {
  let component: BillingSuccessComponent;
  let fixture: ComponentFixture<BillingSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BillingSuccessComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BillingSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
