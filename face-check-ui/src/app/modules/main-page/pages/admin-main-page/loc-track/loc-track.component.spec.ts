import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocTrackComponent } from './loc-track.component';

describe('LocTrackComponent', () => {
  let component: LocTrackComponent;
  let fixture: ComponentFixture<LocTrackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LocTrackComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LocTrackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
