import { Component, Input, OnInit } from '@angular/core';
import { GlobalEditorService } from 'src/app/components/full-page-components/editor/services/global-editor/global-editor.service';
import { DockableWindow } from 'src/app/components/models/windows/DockableWindow';
import { Window_c } from 'src/app/components/models/windows/Window_c';
import { WindowComponent } from '../../../window/window.component';

@Component({
  selector: 'app-tool-box-window',
  templateUrl: './tool-box-window.component.html',
  styleUrls: ['./tool-box-window.component.scss'],
})
export class ToolBoxWindowComponent implements OnInit {
  constructor(private editorService: GlobalEditorService) {}
  @Input() public model: Window_c;
  ngOnInit(): void {
    this.model.contentViewModelInstance = this;
  }
  toolSelected(e) {
    console.log(e.target);
    if (e.target.className == 'toolbox-item') {
      this.editorService.model.canvas.drawMode = e.target.dataset.action;
    }
    console.log(this.editorService.model.canvas.drawMode);
  }
}
