import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FixedWindowComponent } from './fixed-window.component';

describe('FixedWindowComponent', () => {
  let component: FixedWindowComponent;
  let fixture: ComponentFixture<FixedWindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FixedWindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FixedWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
