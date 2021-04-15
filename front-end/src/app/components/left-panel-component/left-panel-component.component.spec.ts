import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LeftPanelComponentComponent } from './left-panel-component.component';

describe('LeftPanelComponentComponent', () => {
  let component: LeftPanelComponentComponent;
  let fixture: ComponentFixture<LeftPanelComponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LeftPanelComponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LeftPanelComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
