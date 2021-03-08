import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SocketCommunicationWindowComponent } from './socket-communication-window.component';

describe('SocketCommunicationWindowComponent', () => {
  let component: SocketCommunicationWindowComponent;
  let fixture: ComponentFixture<SocketCommunicationWindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SocketCommunicationWindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SocketCommunicationWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
