import { Component, Input, OnInit } from '@angular/core';
import { Window_c } from '../../../models/windows/Window_c';
import { DockableWindow } from '../../../models/windows/DockableWindow';
@Component({
  selector: 'app-dockable-window',
  templateUrl: './dockable-window.component.html',
  styleUrls: ['./dockable-window.component.scss'],
})
export class DockableWindowComponent implements OnInit {
  constructor() {}

  @Input() injectedTemplate: any;
  @Input() model: Window_c;
  framedWindow: DockableWindow;
  ngOnInit(): void {}
}
