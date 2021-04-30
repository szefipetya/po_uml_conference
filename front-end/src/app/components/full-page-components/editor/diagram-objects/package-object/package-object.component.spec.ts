import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PackageObjectComponent } from './package-object.component';

describe('PackageObjectComponent', () => {
  let component: PackageObjectComponent;
  let fixture: ComponentFixture<PackageObjectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PackageObjectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PackageObjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
