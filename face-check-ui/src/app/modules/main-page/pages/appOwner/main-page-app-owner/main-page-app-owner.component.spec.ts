import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainPageAppOwnerComponent } from './main-page-app-owner.component';

describe('MainPageAppOwnerComponent', () => {
  let component: MainPageAppOwnerComponent;
  let fixture: ComponentFixture<MainPageAppOwnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MainPageAppOwnerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainPageAppOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
