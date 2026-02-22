import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RemoteWorkersAdminComponentComponent } from './remote-workers-admin-component.component';

describe('RemoteWorkersAdminComponentComponent', () => {
  let component: RemoteWorkersAdminComponentComponent;
  let fixture: ComponentFixture<RemoteWorkersAdminComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RemoteWorkersAdminComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RemoteWorkersAdminComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
