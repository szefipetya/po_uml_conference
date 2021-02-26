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
  @Input() public model: Window_c;

  ngOnInit(): void {
    this.model.viewModelInstance = this;
  }
  mousex_inside;
  mousey_inside;
  onHeadMouseDown(e) {
    console.log('headmousedown');
    console.log(e);
    var rect = e.target.getBoundingClientRect();
    this.mousex_inside = e.clientX - rect.left; //x position within the element.
    this.mousey_inside = e.clientY - rect.top; //y position within the element.
    //  console.log('Left? : ' + x + ' ; Top? : ' + y + '.');
  }
  //move window according to mouse position
  moveWindow(e) {
    this.model.top = e.clientY - this.mousey_inside;
    this.model.left = e.clientX - this.mousex_inside;
  }
}
