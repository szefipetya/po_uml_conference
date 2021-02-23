import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { Window_c } from 'src/app/components/models/windows/Window_c';

@Component({
  selector: 'app-window',
  templateUrl: './window.component.html',
  styleUrls: ['./window.component.scss'],
})
export class WindowComponent implements OnInit {
  constructor() {}
  @Input() public contentTemplate: TemplateRef<any>;
  @Input() model: Window_c;
  ngOnInit(): void {}
}
