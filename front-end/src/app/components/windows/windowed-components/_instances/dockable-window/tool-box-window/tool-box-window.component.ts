import { Component, Input, OnInit } from '@angular/core';
import { DockableWindow } from 'src/app/components/models/windows/DockableWindow';
import { Window_c } from 'src/app/components/models/windows/Window_c';
import { WindowComponent } from '../../../window/window.component';

@Component({
  selector: 'app-tool-box-window',
  templateUrl: './tool-box-window.component.html',
  styleUrls: ['./tool-box-window.component.scss'],
})
export class ToolBoxWindowComponent implements OnInit {
  constructor() {
    this.model = {
      fixed: false,
      head: {
        height: 50,
        title: 'tools',
      },
      height: 200,
      width: 300,
      id: '1',
      left: 300,
      top: 200,
    };
  }
  model: Window_c;
  ngOnInit(): void {}
}
