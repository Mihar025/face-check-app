import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CallToCustomersAppOwnerComponent } from './call-to-customers-app-owner.component';

describe('CallToCustomersAppOwnerComponent', () => {
  let component: CallToCustomersAppOwnerComponent;
  let fixture: ComponentFixture<CallToCustomersAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CallToCustomersAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CallToCustomersAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
