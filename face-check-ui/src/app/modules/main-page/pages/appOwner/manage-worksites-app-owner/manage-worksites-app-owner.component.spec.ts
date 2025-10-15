import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageWorksitesAppOwnerComponent } from './manage-worksites-app-owner.component';

describe('ManageWorksitesAppOwnerComponent', () => {
  let component: ManageWorksitesAppOwnerComponent;
  let fixture: ComponentFixture<ManageWorksitesAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManageWorksitesAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageWorksitesAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
