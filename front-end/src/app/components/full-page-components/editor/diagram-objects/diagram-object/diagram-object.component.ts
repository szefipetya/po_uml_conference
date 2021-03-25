import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { CallbackItem } from 'src/app/components/models/socket/interface/CallbackItem';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { SessionState } from 'src/app/components/models/socket/SessionState';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';

@Component({
  selector: 'app-diagram-object',
  templateUrl: './diagram-object.component.html',
  styleUrls: ['./diagram-object.component.scss'],
})
export class DiagramObjectComponent implements OnInit, SessionInteractiveItem {
  socket: EditorSocketControllerService;
  constructor(socket: EditorSocketControllerService) {
    this.socket = socket;
  }
  callback_queue: CallbackItem[];
  restoreModel(model: any) {
    throw new Error('Method not implemented.');
  }
  msgPopup(msg: string) {
    throw new Error('Method not implemented.');
  }
  sessionState: SessionState;
  updateState(state: SessionState): void {
    console.log('MetupdateStateod not implemented.');
  }
  editBegin() {
    console.log('editBegin not implemented.');
  }
  editEnd() {
    console.log('editEnd not implemented.');
  }
  updateModel(model: any) {
    console.log('restoreModel not implemented.');
  }
  updateScales(scale): void {}
  update(): void {}
  disableEdit() {
    this.model.edit = false;
  }
  getInnerWidth() {
    return (
      this.model.scaledModel.width_scaled - this.general.padding_scaled * 2
    );
  }
  getInnerHeight() {
    return (
      this.model.scaledModel.height_scaled - this.general.padding_scaled * 2
    );
  }
  @Input() public contentTemplate: TemplateRef<any>;
  @Input() public model: DiagramObject;
  @Input() public general: SimpleClass_General;
  ngOnInit(): void {
    this.socket.register(this.model.id, this);
  }
}
