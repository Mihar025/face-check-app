import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PayrollsAppOwnerComponent } from './payrolls-app-owner.component';

describe('PayrollsAppOwnerComponent', () => {
  let component: PayrollsAppOwnerComponent;
  let fixture: ComponentFixture<PayrollsAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PayrollsAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PayrollsAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
