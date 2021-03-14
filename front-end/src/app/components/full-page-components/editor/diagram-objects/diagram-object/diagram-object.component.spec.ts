import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DiagramObjectComponent } from './diagram-object.component';

describe('DiagramObjectComponent', () => {
  let component: DiagramObjectComponent;
  let fixture: ComponentFixture<DiagramObjectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DiagramObjectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DiagramObjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
