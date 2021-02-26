import { global } from '@angular/compiler/src/util';
import {
  Component,
  OnInit,
  ViewChild,
  ɵALLOW_MULTIPLE_PLATFORMS,
} from '@angular/core';
import { AppModule } from 'src/app/app.module';
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
        width: 300,
        id: 'tools',
        left: 300,
        top: 200,
        viewModelInstance: null,
        contentViewModelInstance: null,
      },
    ];
  }
  findWindowModelById(id): Window_c {
    return this.windows.filter((w) => w.id == id)[0];
  }
  draggedWindowModel: Window_c;
  onMouseDown(e) {
    if (e.target.className == 'window-head') {
      this.draggedWindowDOM = e.target.closest('.window');
      this.draggedWindowModel = this.findWindowModelById(
        this.draggedWindowDOM.id
      );
      this.draggedWindowModel.viewModelInstance.onHeadMouseDown(e);
    } //else this.editorService.model.canvas.viewModel.onMouseDown(e);
  }
  onMouseMove(e) {
    if (this.draggedWindowDOM && this.draggedWindowModel) {
      this.draggedWindowModel.viewModelInstance.moveWindow(e);
    } else this.editorService.model.canvas.viewModel.onMouseMove(e);
  }
  onMouseUp(e) {
    this.draggedWindowDOM = null;
    this.draggedWindowModel = null;
    this.editorService.model.canvas.viewModel.onMouseUp(e);
  }
  onDockMouseEnter(e, side) {
    console.log(e, side);
  }
  onClick(e) {}

  //event handlers
  onToolSelected(tool) {}

  createElementFromHTML(htmlString, model) {
    var div = document.createElement('div');
    div.innerHTML = htmlString.trim();
    // Change this to div.childNodes to support multiple top-level nodes
    return div.firstChild;
  }
  ngOnInit(): void {}
  draggedWindowDOM: any;
  windows: Window_c[];
}
