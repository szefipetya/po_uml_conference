import {
  Component,
  Directive,
  Input,
  OnInit,
  TemplateRef,
} from '@angular/core';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { DiagramObject_Scaled } from 'src/app/components/models/DiagramObjects/DiagramObject_Scaled';
import { DiagramObject_General } from 'src/app/components/models/DiagramObjects/DiagramObject_General';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { InteractiveItemBase } from 'src/app/components/models/socket/bases/InteractiveItemBase';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { CallbackItem } from 'src/app/components/models/socket/interface/CallbackItem';
import { LogInteractive_I } from 'src/app/components/models/socket/interface/LogInteractive_I';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { SessionState } from 'src/app/components/models/socket/SessionState';
import { soft_copy } from 'src/app/utils/utils';
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
  getParentId(): string {
    return GlobalEditorService.ROOT_ID;
  }
  onMouseDown(e) {
    this.editBegin();
    this.dragged = true;
    console.log('mousedown');
  }
  onMouseUp(e) {
    this.MouseUpEffect(e);
  }
  MouseUpEffect(e) {
    this.sendDimensionUpdate();
    this.dragged = false;
    console.log('UPDATE SENT OBJJJJ');
  }
  sendDimensionUpdate() {
    let action = new EditorAction(this.model.id, this.model._type, '');
    action.action = ACTION_TYPE.DIMENSION_UPDATE;
    let copy = {};
    soft_copy(this.model, copy, [
      'viewModel',
      'groups',
      'titleModel',
      'scaledModel',
    ]);
    console.log(copy);
    action.json = JSON.stringify(copy);
    console.log('dimension update', action.json);
    this.sendAction(action);
  }
  dragged = false;
  updateModel(model: any, action_id: string, msg?: string): void {
    let vm = this.model.viewModel;
    this.model.id = model.id;
    //  soft_copy(model, this.model, ['edit', 'viewModel', 'scaledModel']);
    this.model.viewModel = vm;
    this.model.dimensionModel = model.dimensionModel;
    if (!this.model.scaledModel) this.model.scaledModel = new DiagramObject_Scaled();
    this.model.scaledModel.posy_scaled =
      this.model.dimensionModel.y * this.editorService.clientModel.canvas.scale;
    this.model.scaledModel.width_scaled =
      this.model.dimensionModel.width *
      this.editorService.clientModel.canvas.scale;
    this.model.scaledModel.height_scaled =
      this.model.dimensionModel.height *
      this.editorService.clientModel.canvas.scale;
    this.model.scaledModel.posx_scaled =
      this.model.dimensionModel.x * this.editorService.clientModel.canvas.scale;
  }
  constructor(
    protected socket: EditorSocketControllerService,
    protected commonService: CommonService,
    protected editorService: GlobalEditorService
  ) {
    super(socket, commonService);
    editorService.addListenerToEvent(this, (t) => {
    }, '');
  }
  getId() {
    return this.model.id;
  }
  saveEvent(wastrue: any): void { }

  onSelect() { }
  editBegin() {
    if (!this.isAccessible()) return;
    let action = new EditorAction(this.model.id, this.model._type, '');

    action.action = ACTION_TYPE.SELECT;
    action.json = '{}';
    action.target.target_id = this.model.id;
    this.sendAction(action);
    console.log('action sent');
  }

  editEnd() {
    let action = new EditorAction(this.model.id, this.model._type, '');
    action.action = ACTION_TYPE.UPDATE;
    let copy = {};

    soft_copy(this.model, copy, [
      'viewModel',
      'groups',
      'titleModel',
      'scaledModel',
    ]);

    /*   console.log('MODEL', this.model);
    console.log('COPY', copy); */
    action.json = JSON.stringify(copy);
    console.log('edit ended', action.json);
    this.sendAction(action);
  }
  isLocked(): string {
    if (this.sessionState == undefined) return 'null';
    if (this.sessionState.lockerUser_id == this.socket.getUser().id)
      return 'editing';
    if (this.sessionState.locks.length > 0)
      return 'locked:' + this.sessionState.lockerUser_id;
    else return '';
  }

  // socket: EditorSocketControllerService;

  deleteSelfFromParent() {
    this.editorService.deleteGlobalObject(this.model);
  }
  callback_queue: CallbackItem[];
  restoreModel(model: any) {
    // throw new Error('Method not implemented.');
  }
  log(msg: string) {
    //  throw new Error('Method not implemented.');
  }
  sessionState: SessionState;

  updateScales(scale): void { }
  update(): void { }
  disableEdit(): void {
    this.model.edit = false;
    console.log('base.disableedit');
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
  @Input() public general: DiagramObject_General;

  ngAfterContentInit(): void {
    //  this.model.viewModel = this;
  }
  ngOnInit(): void {
    //this.model._type = 'SimpleClass';
    // this.model.viewModel = this;
    this.init_register();
  }
  ngOnChanges(): void {
    console.log('changed');
  }
}
