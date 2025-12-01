import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingCanceledComponent } from './billing-canceled.component';

describe('BillingCanceledComponent', () => {
  let component: BillingCanceledComponent;
  let fixture: ComponentFixture<BillingCanceledComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BillingCanceledComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BillingCanceledComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
