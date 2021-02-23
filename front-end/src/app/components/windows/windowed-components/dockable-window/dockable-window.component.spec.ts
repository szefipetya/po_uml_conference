import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DockableWindowComponent } from './dockable-window.component';

describe('DockableWindowComponent', () => {
  let component: DockableWindowComponent;
  let fixture: ComponentFixture<DockableWindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DockableWindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DockableWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
