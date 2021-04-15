import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { Window_c } from 'src/app/components/models/windows/Window_c';

@Component({
  selector: 'app-window',
  templateUrl: './window.component.html',
  styleUrls: ['./window.component.scss'],
})
export class WindowComponent implements OnInit {
  constructor() { }
  @Input() public contentTemplate: TemplateRef<any>;
  @Input() public model: Window_c;

  ngOnInit(): void {
    this.model.viewModelInstance = this;
  }
  mousex_inside;
  mousey_inside;
  parentPos;
  onHeadMouseDown(e) {
    console.log('headmousedown');
    console.log(e);
    this.parentPos = document.querySelector('.window-manager-bg').getBoundingClientRect();
    let relativePos = { top: 0, left: 0 };
    var rect = e.target.getBoundingClientRect();
    console.log(rect);
    this.mousex_inside = e.offsetX - relativePos.left; //x position within the element.
    this.mousey_inside = e.offsetY - relativePos.top; //y position within the element.
    //  console.log('Left? : ' + x + ' ; Top? : ' + y + '.');
  }
  //move window according to mouse position
  moveWindow(e) {
    this.model.top = e.pageY - this.mousey_inside - this.parentPos.top;
    this.model.left = e.pageX - this.mousex_inside - this.parentPos.left;
  }
}
