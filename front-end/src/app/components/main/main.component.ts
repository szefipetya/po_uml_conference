import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  constructor() { }
  eventsSubject: Subject<{ str: string, extra: any }> = new Subject<{ str: string, extra: any }>();
  @ViewChild('drawer') drawer: MatDrawer;

  ngOnInit(): void {
  }
  toggle() {
    this.drawer.toggle();
    this.eventsSubject.next({ str: 'toggle', extra: true });
  }
  panelClick(e) {
    this.eventsSubject.next({ str: 'panel-click', extra: e });
  }
  onKeyDown(e) {

    this.eventsSubject.next({ str: 'keydown', extra: e });
  }
  onKeyUp(e) {

    this.eventsSubject.next({ str: 'keyup', extra: e });
  }
  clickHandler(str: string) {
    if (str == 'close') {
      this.drawer.close();
    }
    if (str == 'open') {
      this.drawer.open();
    }
  }

}
