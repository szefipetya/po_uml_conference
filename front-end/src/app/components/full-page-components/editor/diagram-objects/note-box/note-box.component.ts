import {
  AfterViewChecked,
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { NoteBox } from '../../../../models/DiagramObjects/NoteBox';
import { DiagramObjectComponent } from '../diagram-object/diagram-object.component';
@Component({
  selector: 'app-note-box',
  templateUrl: './note-box.component.html',
  styleUrls: ['./note-box.component.scss'],
})
export class NoteBoxComponent
  extends DiagramObjectComponent
  implements OnInit, AfterViewChecked {
  @Input()
  model: NoteBox;
  @Input() public general: SimpleClass_General;
  @ViewChild('texta') textarea: ElementRef<HTMLTextAreaElement>;
  constructor() {
    super();
  }
  updateScales(scale): void {
    this.updateFont();
  }
  updateFont() {
    if (!this.textarea) return;
    this.textarea.nativeElement.style.fontSize =
      this.general.fontsize_scaled + 'px';
  }
  update(): void {}
  ngAfterViewChecked(): void {
    if (this.model.edit) {
      this.updateFont();

      this.updateHeight();
    }
  }
  onContentClick(e) {
    this.model.edit = true;
  }
  inputClick(e) {}
  updateHeight() {
    if (!this.textarea) return;
    this.textarea.nativeElement.style.height = '';
    this.textarea.nativeElement.style.height =
      Math.max(
        this.textarea.nativeElement.scrollHeight,
        this.getInnerHeight()
      ) + 'px';
  }
  textInput(e) {
    this.updateHeight();
  }
  ngOnInit(): void {
    console.log('GENERAL', this.general);
    this.model.viewModel = this;
  }
  disableEdit() {
    this.model.edit = false;
    console.log('notebox edit false');
    if (this.textarea) this.model.content = this.textarea.nativeElement.value;
  }
}
