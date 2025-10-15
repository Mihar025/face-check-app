import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageEmployeesAppOwnerComponent } from './manage-employees-app-owner.component';

describe('ManageEmployeesAppOwnerComponent', () => {
  let component: ManageEmployeesAppOwnerComponent;
  let fixture: ComponentFixture<ManageEmployeesAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManageEmployeesAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageEmployeesAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
