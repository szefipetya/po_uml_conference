import {
  AfterViewChecked,
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { DiagramObject_General } from 'src/app/components/models/DiagramObjects/DiagramObject_General';
import { NoteBox } from '../../../../models/DiagramObjects/NoteBox';
import { CommonService } from '../../services/common/common.service';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
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
  @Input() public general: DiagramObject_General;
  @ViewChild('texta') textarea: ElementRef<HTMLTextAreaElement>;
  constructor(
    protected socket: EditorSocketControllerService,
    protected commonService: CommonService,
    protected editorService: GlobalEditorService
  ) {
    super(socket, commonService, editorService);
  }
  updateScales(scale): void {
    this.updateFont();
  }
  updateFont() {
    if (!this.textarea) return;
    this.textarea.nativeElement.style.fontSize =
      this.general.fontsize_scaled + 'px';
  }
  updateModel(model: any, action_id: string, msg?: string): void {
    super.updateModel(model, action_id, msg);
    this.model.content = model.content;
  }
  update(): void { }
  ngAfterViewChecked(): void {
    if (this.model.edit) {
      this.updateFont();

      this.updateHeight();
    }
  }
  onContentClick(e) {
    if (this.isAccessible()) this.model.edit = true;
  }

  inputClick(e) {
    this.model.edit = true;
  }
  updateHeight() {
    if (!this.textarea) return;
    this.textarea.nativeElement.style.height = '';
    this.textarea.nativeElement.style.height =
      Math.max(
        this.textarea.nativeElement.scrollHeight,
        this.getInnerHeight()
      ) + 'px';
    //  this.sendDimensionUpdate();
  }
  textInput(e) {
    this.updateHeight();
  }
  onKeyPress(e) {
    console.log(e);
  }

  disableEdit() {
    console.log('notebox edit false');
    if (this.textarea) this.model.content = this.textarea.nativeElement.value;
    if (this.model.edit) this.editEnd();
    this.model.edit = false;
  }
  ngOnInit(): void {
    //this.model._type = 'SimpleClass';
    this.model.viewModel = this;
    super.ngOnInit();
  }
  ngOnChanges(): void {
    console.log('changed');
  }
}
