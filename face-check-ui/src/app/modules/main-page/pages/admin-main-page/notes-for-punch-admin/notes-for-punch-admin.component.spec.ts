import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotesForPunchAdminComponent } from './notes-for-punch-admin.component';

describe('NotesForPunchAdminComponent', () => {
  let component: NotesForPunchAdminComponent;
  let fixture: ComponentFixture<NotesForPunchAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotesForPunchAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotesForPunchAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
