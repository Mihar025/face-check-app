import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttendenceTrackEmployeeAppOwnerComponent } from './attendence-track-employee-app-owner.component';

describe('AttendenceTrackEmployeeAppOwnerComponent', () => {
  let component: AttendenceTrackEmployeeAppOwnerComponent;
  let fixture: ComponentFixture<AttendenceTrackEmployeeAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AttendenceTrackEmployeeAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttendenceTrackEmployeeAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
