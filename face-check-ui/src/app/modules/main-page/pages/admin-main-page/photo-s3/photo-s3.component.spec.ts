import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhotoS3Component } from './photo-s3.component';

describe('PhotoS3Component', () => {
  let component: PhotoS3Component;
  let fixture: ComponentFixture<PhotoS3Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PhotoS3Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PhotoS3Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
