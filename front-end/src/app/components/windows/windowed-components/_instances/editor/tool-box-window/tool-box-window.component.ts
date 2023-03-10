import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Window_c } from 'src/app/components/models/windows/Window_c';
import { WindowComponent } from '../../../window/window.component';

@Component({
  selector: 'app-tool-box-window',
  templateUrl: './tool-box-window.component.html',
  styleUrls: ['./tool-box-window.component.scss'],
})
export class ToolBoxWindowComponent extends WindowComponent {
  constructor() {
    super();
    this.toolSelectedEvent = new EventEmitter();
  }
  @Input() public canvasModel;
  @Input() public lineCanvasModel;
  @Output() public toolSelectedEvent: EventEmitter<{
    type: string;
    extra: number;
  }>;

  toolSelected(e) {
    console.log(e.target);
    if (e.target.className == 'toolbox-item') {
      /* this.toolSelectedEvent.emit({
        type: e.target.dataset.action,
        extra: Number.parseInt(e.target.dataset.line_extra),
      });*/
      this.canvasModel.drawMode = e.target.dataset.action;
      if (e.target.dataset.action == 'line' && e.target.dataset.line_extra) {
        this.lineCanvasModel.drawLineType = Number.parseInt(
          e.target.dataset.line_extra
        );
      }
    }
  }
}
