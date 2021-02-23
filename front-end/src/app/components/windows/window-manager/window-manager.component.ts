import { Component, OnInit } from '@angular/core';
import { GlobalEditorService } from '../../full-page-components/editor/services/global-editor/global-editor.service';

@Component({
  selector: 'app-window-manager',
  templateUrl: './window-manager.component.html',
  styleUrls: ['./window-manager.component.scss'],
})
export class WindowManagerComponent implements OnInit {
  constructor(private editorService: GlobalEditorService) {}
  onMouseDown(e) {
    this.editorService.model.canvas.viewModel.onMouseDown(e);
  }
  onMouseMove(e) {
    this.editorService.model.canvas.viewModel.onMouseMove(e);
  }
  onMouseUp(e) {
    this.editorService.model.canvas.viewModel.onMouseUp(e);
  }
  onDockMouseEnter(e, side) {
    console.log(e, side);
  }
  ngOnInit(): void {}
}
