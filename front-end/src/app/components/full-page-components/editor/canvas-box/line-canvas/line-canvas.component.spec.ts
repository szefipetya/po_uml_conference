import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LineCanvasComponent } from './line-canvas.component';

describe('LineCanvasComponent', () => {
  let component: LineCanvasComponent;
  let fixture: ComponentFixture<LineCanvasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LineCanvasComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LineCanvasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
