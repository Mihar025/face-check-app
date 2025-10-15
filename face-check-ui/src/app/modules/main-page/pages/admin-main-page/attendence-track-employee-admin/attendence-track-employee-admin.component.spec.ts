import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttendenceTrackEmployeeAdminComponent } from './attendence-track-employee-admin.component';

describe('AttendenceTrackEmployeeAdminComponent', () => {
  let component: AttendenceTrackEmployeeAdminComponent;
  let fixture: ComponentFixture<AttendenceTrackEmployeeAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AttendenceTrackEmployeeAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttendenceTrackEmployeeAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
