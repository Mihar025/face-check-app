import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationAdminPageComponent } from './notification-admin-page.component';

describe('NotificationAdminPageComponent', () => {
  let component: NotificationAdminPageComponent;
  let fixture: ComponentFixture<NotificationAdminPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationAdminPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationAdminPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
