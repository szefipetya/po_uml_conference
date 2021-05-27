import { global } from '@angular/compiler/src/util';
import {
  Component,
  OnInit,
  ViewChild,
  ɵALLOW_MULTIPLE_PLATFORMS,
} from '@angular/core';
import { AppModule } from 'src/app/app.module';
import { getCookie, setCookie } from 'src/app/utils/cookieUtils';
import { GlobalEditorService } from '../../full-page-components/editor/services/global-editor/global-editor.service';
import { Window_c } from '../../models/windows/Window_c';

@Component({
  selector: 'app-window-manager',
  templateUrl: './window-manager.component.html',
  styleUrls: ['./window-manager.component.scss'],
})
export class WindowManagerComponent implements OnInit {
  @ViewChild('app-main') main;

  constructor(public editorService: GlobalEditorService) {
    this.windows = [
      {
        selector: 'app-tool-box-window',
        fixed: false,
        head: {
          height: 25,
          title: 'tools',
        },
        height: null,
        width: 100,
        id: 'tools',
        left: 0,
        top: 0,
        viewModelInstance: null,
        contentViewModelInstance: null,
        defaultVisible: true
      },
      {
        selector: 'app-session-message-window',
        fixed: false,
        head: {
          height: 25,
          title: 'Log',
        },
        height: 120,
        width: 250,
        id: 'log',
        left: 0,
        top: window.innerHeight - 220,
        viewModelInstance: null,
        contentViewModelInstance: null,
        defaultVisible: true
      },
      {
        selector: 'app-socket-communication-window',
        fixed: false,
        head: {
          height: 25,
          title: 'Socket',
        },
        height: null,
        width: 100,
        id: 'socket',
        left: 0,
        top: 370,
        viewModelInstance: null,
        contentViewModelInstance: null,
        defaultVisible: false
      },
      {
        selector: 'app-social-window',
        fixed: false,
        head: {
          height: 25,
          title: 'Users in this session',
        },
        height: null,
        width: 200,
        id: 'social',
        left: 250,
        top: window.innerHeight - 220,
        viewModelInstance: null,
        contentViewModelInstance: null,
        defaultVisible: true
      },


    ];

    if (!getCookie('window_toggle')) {
      setCookie('window_toggle', JSON.stringify(this.windows.map(w => { return { id: w.id, visible: w.defaultVisible }; })), 8);
    }
  }
  findWindowModelById(id): Window_c {
    return this.windows.filter((w) => w.id == id)[0];
  }
  isWindowVisible(id) {
    return JSON.parse(getCookie('window_toggle')).find(w => w.id == id)?.visible;
  }
  draggedWindowModel: Window_c;
  onMouseDown(e) {
    console.log(e);
    if (e.target.className == 'window-head') {
      this.draggedWindowDOM = e.target.closest('.window');
      this.draggedWindowModel = this.findWindowModelById(
        this.draggedWindowDOM.id
      );
      this.draggedWindowModel.viewModelInstance.onHeadMouseDown(e);
    } // else this.editorService.clientModel.canvas.viewModel.onMouseDown(e);
  }
  onMouseMove(e) {
    //console.log('move1');
    if (this.draggedWindowDOM && this.draggedWindowModel) {
      //  this.draggedWindowModel.viewModelInstance.onHeadMouseDown(e);
      // console.log('move');
      this.draggedWindowModel.viewModelInstance.moveWindow(e);
    } //else this.editorService.clientModel.canvas.viewModel.onMouseMove(e);
  }
  onMouseUp(e) {
    this.draggedWindowDOM = null;
    this.draggedWindowModel = null;
    //this.editorService.clientModel.canvas.viewModel.onMouseUp(e);
  }
  onDockMouseEnter(e, side) {
    //  console.log(e, side);
  }
  onClick(e) { }

  //event handlers
  onToolSelected(tool) { }

  createElementFromHTML(htmlString, model) {
    var div = document.createElement('div');
    div.innerHTML = htmlString.trim();
    // Change this to div.childNodes to support multiple top-level nodes
    return div.firstChild;
  }
  ngOnInit(): void { }
  draggedWindowDOM: any;
  windows: Window_c[];
}
