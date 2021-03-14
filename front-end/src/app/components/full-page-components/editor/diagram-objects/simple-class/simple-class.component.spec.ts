import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SimpleClassComponent } from './simple-class.component';

describe('SimpleClassComponent', () => {
  let component: SimpleClassComponent;
  let fixture: ComponentFixture<SimpleClassComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SimpleClassComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SimpleClassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
