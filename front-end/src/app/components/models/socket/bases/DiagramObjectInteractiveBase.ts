import { Input, TemplateRef } from '@angular/core';
import { CommonService } from '../../../full-page-components/editor/services/common/common.service';
import { EditorSocketControllerService } from '../../../full-page-components/editor/services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../../full-page-components/editor/services/global-editor/global-editor.service';
import { soft_copy } from '../../../utils/utils';
import { DiagramObject } from '../../DiagramObjects/DiagramObject';
import { SimpleClass_General } from '../../DiagramObjects/SimpleClass_General';

import { ACTION_TYPE } from '../ACTION_TYPE';
import { EditorAction } from '../EditorAction';
import { CallbackItem } from '../interface/CallbackItem';
import { InteractiveItemBase } from './InteractiveItemBase';
export abstract class DiagramObjectInteractiveItemBase extends InteractiveItemBase {
  onMouseDown(e) {
    this.editBegin();
    this.dragged = true;
    console.log('mousedown');
  }
  onMouseUp(e) {
    this.sendDimensionUpdate();
    this.dragged = false;
    console.log('update');
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
    console.log('edit ended', action.json);
    this.sendAction(action);
  }
  dragged = false;
  updateModel(model: any, action_id: string, msg?: string): void {
    this.model.dimensionModel = model.dimensionModel;
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
  }
  saveEvent(wastrue: any): void {}

  onSelect() {}
  editBegin() {
    //   if (!this.isAccessible()) return;
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
    console.log(copy);
    action.json = JSON.stringify(copy);
    console.log('edit ended', action.json);
    this.sendAction(action);
  }
  isLocked(): string {
    if (this.sessionState == undefined) return 'null';
    if (this.sessionState.lockerUser_id == this.socket.user.id)
      return 'editing';
    if (this.sessionState.locks.length > 0)
      return 'locked:' + this.sessionState.lockerUser_id;
    else return '';
  }

  // socket: EditorSocketControllerService;

  deleteSelfFromParent() {}
  callback_queue: CallbackItem[];
  restoreModel(model: any) {
    // throw new Error('Method not implemented.');
  }
  log(msg: string) {
    //  throw new Error('Method not implemented.');
  }
  // sessionState: SessionState;

  updateScales(scale): void {}
  update(): void {}
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
  /* @Input() */ public contentTemplate: TemplateRef<any>;
  /*  @Input() */ public model: DiagramObject;
  /*   @Input() */ public general: SimpleClass_General;

  //SET viewModel+register/*  */!!!!!!!!!!!!!!!!
  /* ngAfterContentInit(): void {
    this.model.viewModel = this;
  }
  ngOnInit(): void {
    //this.model._type = 'SimpleClass';
    this.model.viewModel = this;
    this.init_register();
  }*/
  /* ngOnChanges(): void {
    console.log('changed');
  }*/
}
