import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeesAttendanceAppOwnerComponent } from './employees-attendance-app-owner.component';

describe('EmployeesAttendanceAppOwnerComponent', () => {
  let component: EmployeesAttendanceAppOwnerComponent;
  let fixture: ComponentFixture<EmployeesAttendanceAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmployeesAttendanceAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeesAttendanceAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
