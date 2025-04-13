import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageWorksitesComponent } from './manage-worksites.component';

describe('ManageWorksitesComponent', () => {
  let component: ManageWorksitesComponent;
  let fixture: ComponentFixture<ManageWorksitesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManageWorksitesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageWorksitesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
