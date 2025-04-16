import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificationForEmployeeComponent } from './verification-for-employee.component';

describe('VerificationForEmployeeComponent', () => {
  let component: VerificationForEmployeeComponent;
  let fixture: ComponentFixture<VerificationForEmployeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationForEmployeeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerificationForEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
