import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotesForPunchAppOwnerComponent } from './notes-for-punch-app-owner.component';

describe('NotesForPunchAppOwnerComponent', () => {
  let component: NotesForPunchAppOwnerComponent;
  let fixture: ComponentFixture<NotesForPunchAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotesForPunchAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotesForPunchAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
