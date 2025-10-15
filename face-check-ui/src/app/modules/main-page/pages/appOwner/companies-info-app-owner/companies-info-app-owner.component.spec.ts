import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompaniesInfoAppOwnerComponent } from './companies-info-app-owner.component';

describe('CompaniesInfoAppOwnerComponent', () => {
  let component: CompaniesInfoAppOwnerComponent;
  let fixture: ComponentFixture<CompaniesInfoAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompaniesInfoAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompaniesInfoAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
