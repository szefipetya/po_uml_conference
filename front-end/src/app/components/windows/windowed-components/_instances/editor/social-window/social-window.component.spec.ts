import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SocialWindowComponent } from './social-window.component';

describe('SocialWindowComponent', () => {
  let component: SocialWindowComponent;
  let fixture: ComponentFixture<SocialWindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SocialWindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SocialWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
