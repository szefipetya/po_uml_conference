import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditorRootComponent } from './editor-root.component';

describe('EditorRootComponent', () => {
  let component: EditorRootComponent;
  let fixture: ComponentFixture<EditorRootComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditorRootComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditorRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
