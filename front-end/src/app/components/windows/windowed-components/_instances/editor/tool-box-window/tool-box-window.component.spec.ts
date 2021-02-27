import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolBoxWindowComponent } from './tool-box-window.component';

describe('ToolBoxWindowComponent', () => {
  let component: ToolBoxWindowComponent;
  let fixture: ComponentFixture<ToolBoxWindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ToolBoxWindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToolBoxWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
