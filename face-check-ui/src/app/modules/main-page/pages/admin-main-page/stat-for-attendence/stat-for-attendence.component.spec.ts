import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatForAttendenceComponent } from './stat-for-attendence.component';

describe('StatForAttendenceComponent', () => {
  let component: StatForAttendenceComponent;
  let fixture: ComponentFixture<StatForAttendenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StatForAttendenceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatForAttendenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
