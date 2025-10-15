import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsAppOwnerComponent } from './settings-app-owner.component';

describe('SettingsAppOwnerComponent', () => {
  let component: SettingsAppOwnerComponent;
  let fixture: ComponentFixture<SettingsAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SettingsAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
