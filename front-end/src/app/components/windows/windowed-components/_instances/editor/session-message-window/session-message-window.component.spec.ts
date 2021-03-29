import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionMessageWindowComponent } from './session-message-window.component';

describe('SessionMessageWindowComponent', () => {
  let component: SessionMessageWindowComponent;
  let fixture: ComponentFixture<SessionMessageWindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SessionMessageWindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionMessageWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
