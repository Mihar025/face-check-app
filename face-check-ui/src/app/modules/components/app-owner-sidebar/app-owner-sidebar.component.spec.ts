import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppOwnerSidebarComponent } from './app-owner-sidebar.component';

describe('AppOwnerSidebarComponent', () => {
  let component: AppOwnerSidebarComponent;
  let fixture: ComponentFixture<AppOwnerSidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AppOwnerSidebarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppOwnerSidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
