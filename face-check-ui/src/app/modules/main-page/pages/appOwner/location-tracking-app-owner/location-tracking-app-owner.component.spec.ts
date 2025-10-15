import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationTrackingAppOwnerComponent } from './location-tracking-app-owner.component';

describe('LocationTrackingAppOwnerComponent', () => {
  let component: LocationTrackingAppOwnerComponent;
  let fixture: ComponentFixture<LocationTrackingAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LocationTrackingAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LocationTrackingAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
