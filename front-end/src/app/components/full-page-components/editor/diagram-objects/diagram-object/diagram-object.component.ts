import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { InteractiveItemBase } from 'src/app/components/models/socket/bases/InteractiveItemBase';
import { CallbackItem } from 'src/app/components/models/socket/interface/CallbackItem';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { SessionState } from 'src/app/components/models/socket/SessionState';
import { CommonService } from '../../services/common/common.service';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';

@Component({
  selector: 'app-diagram-object',
  templateUrl: './diagram-object.component.html',
  styleUrls: ['./diagram-object.component.scss'],
})
export class DiagramObjectComponent
  extends InteractiveItemBase
  implements OnInit, SessionInteractiveItem {
  public editBegin(): void {
    //  throw new Error('Method not implemented.');
  }
  public editEnd(): void {
    //  throw new Error('Method not implemented.');
  }
  updateModel(model: any, action_id: string, msg?: string): void {
    //  throw new Error('Method not implemented.');
  }
  saveEvent(wastrue: any): void {
    //  throw new Error('Method not implemented.');
  }
  // socket: EditorSocketControllerService;
  constructor(
    protected socket: EditorSocketControllerService,
    protected commonService: CommonService
  ) {
    super(socket, commonService);
  }
  deleteSelfFromParent() {}
  callback_queue: CallbackItem[];
  restoreModel(model: any) {
    // throw new Error('Method not implemented.');
  }
  log(msg: string) {
    //  throw new Error('Method not implemented.');
  }
  sessionState: SessionState;
  onMouseDown(e) {}
  onMouseUp(e) {}
  updateScales(scale): void {}
  update(): void {}
  disableEdit() {
    this.model.edit = false;
  }
  sendDimensionUpdate() {}
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
