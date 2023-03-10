import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { getCookie, setCookie } from 'src/app/utils/cookieUtils';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  constructor(public router: Router) {
    /*setTimeout(() => {
      Array.from(document.querySelectorAll(".class-element-group-new_element-button")).forEach((g) => { g.style.height = '20px' })
    }, 1000)*/


  }
  toggleWindowVisibility(id) {
    setCookie('window_toggle', JSON.stringify(JSON.parse(getCookie('window_toggle')).map(w => {
      if (w.id == id)
        return { id: w.id, visible: !w.visible };
      else return { id: w.id, visible: w.visible };
    })), 8);

  }
  eventsSubject: Subject<{ str: string, extra: any }> = new Subject<{ str: string, extra: any }>();
  @ViewChild('drawer') drawer: MatDrawer;
  isEditorActive() {
    return this.router.url == '/editor'
  }
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
