import {
  Component,
  OnInit,
  Input,
  OnChanges,
  AfterContentInit,
  TemplateRef,
} from '@angular/core';
import { DynamicSerialObject } from 'src/app/components/models/common/DynamicSerialObject';
import { AttributeElement } from 'src/app/components/models/DiagramObjects/AttributeElement';
import { DiagramObject_Scaled } from 'src/app/components/models/DiagramObjects/DiagramObject_Scaled';
import { Element_c } from 'src/app/components/models/DiagramObjects/Element_c';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClassElementGroup } from 'src/app/components/models/DiagramObjects/SimpleClassElementGroup';
import { DiagramObject_General } from 'src/app/components/models/DiagramObjects/DiagramObject_General';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { InteractiveItemBase } from 'src/app/components/models/socket/bases/InteractiveItemBase';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { soft_copy } from 'src/app/utils/utils';
import { CommonService } from '../../services/common/common.service';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { DiagramObjectComponent } from '../diagram-object/diagram-object.component';
import { AttributeComponent } from './attribute-group/attribute/attribute.component';
import { SessionState } from 'src/app/components/models/socket/SessionState';

@Component({
  selector: 'app-simple-class',
  templateUrl: './simple-class.component.html',
  styleUrls: ['./simple-class.component.scss'],
})
export class SimpleClassComponent
  extends DiagramObjectComponent
  implements OnInit, OnChanges, AfterContentInit, SessionInteractiveContainer {
  updateItemWithOld(old_id: string, model: any) {
    throw new Error('Method not implemented.');

  }
  updateState(state: SessionState, callback_action_id = ''): void {
    super.updateState(state, callback_action_id);

  }
  updateModel(model: any, action_id: string, msg?: string): void {
    let vm = this.model.viewModel;
    let tvm = this.model.titleModel.viewModel;
    let gvms = [];
    /* this.model.groups.map((g) => {
      gvms.push(g.viewModel);
    });*/
    console.log('CLASS SIDE CUCC FUTOTT LE');
    // soft_copy(model, this.model, ['viewModel']);
    this.model.extra = model.extra;
    this.model.id = model.id;
    /*  this.model.groups.map((g, i) => {
      this.model.groups[i].viewModel = gvms[i];
    });*/

    /* this.model.titleModel.viewModel = tvm;
    this.model.viewModel = vm;*/
    //{

    (this.getTitleVm() as AttributeComponent).render();
    console.log('users', this.sessionState?.lockerUser_id, this.socket.getUser().id);
    console.log('DRAFT', this.model.extra);
    this.model.dimensionModel = model.dimensionModel;
    if (
      this.model.dimensionModel.width <
      this.editorService.clientModel.class_general.min_width ||
      this.model.dimensionModel.height <
      this.editorService.clientModel.class_general.min_height
    ) {
      this.model.dimensionModel.width = this.editorService.clientModel.class_general.min_width;
      this.model.dimensionModel.height = this.editorService.clientModel.class_general.min_height;
    }
    console.log('DIMENSION UPDATEFD');
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
    (this.getTitleVm() as AttributeComponent).render();
    this.socket.triggerEvent('update');

    // this.callback_queue = [];

  }
  createItem(model: DynamicSerialObject, extra?: any) {
    console.log('creating:', model);
    if (model._type == 'SimpleClassElementGroup') {
      if (!this.hasItem(model.id))
        this.model.groups.push(model as SimpleClassElementGroup);
    }
    let vm = this.model.viewModel;

    if (model._type == 'TitleElement') {
      this.getTitleVm().updateModel(model, "", "");
      this.getTitleVm().updateState(JSON.parse(extra.sessionState))
      console.log(this.model.titleModel);
      //title model
    }
    //  this.callback_queue = [];
  }

  getTitleVm() {
    return this.socket.getItem(this.model.titleModel.id);
  }
  hasItem(target_id: string) {
    return this.model.groups.filter((g) => g.id == target_id).length > 0;
  }
  restoreItem(item_id: string, model: DynamicSerialObject) {
    throw new Error('Method not implemented.');
  }
  deleteItem(item_id: string) {
    throw new Error('Method not implemented.');
  }
  msgPopup(msg: string) {
    throw new Error('Method not implemented.');
  }
  getParentId(): string {
    return GlobalEditorService.ROOT_ID;
  }

  update(): void {
    this.model.groups.map((group) => {
      group.attributes.map((a) => {
        a.viewModel?.render();
      });
    });

    (this?.getTitleVm() as InteractiveItemBase)?.render();
  }

  disableEdit() {
    console.log('class', this.model.name, 'edit false');
    if (this.model.edit)
      if (this.model.name.trim() != '') {
        this.model.edit = false;
        //   this.editorService.clientModel.canvas.edit_classTitle_id = null;
      } else {
        this.model.name = 'Class';
        this.model.edit = false;
        // this.editorService.clientModel.canvas.edit_classTitle_id = null;
      }
    let prev = this.model.titleModel.edit;
    this.model.titleModel.edit = false;
    (this.getTitleVm() as InteractiveItemBase).saveEvent(prev);

    this.model.groups.map((egroup) => {
      egroup.attributes.map((e) => {
        if (e && e.edit) {
          // this.editorService.clientModel.canvas.edit_element_id = null;
          console.log('EDITED FOUND ');
          let prev = e.edit;

          e.viewModel.save(prev);
        }
      });
    });
  }
  // editorService;
  @Input() public model: SimpleClass;
  @Input() public general: DiagramObject_General;

  ngAfterContentInit(): void {
    this.model.viewModel = this;
  }
  //we need custom Init, because its a container
  ngOnInit(): void {
    this.model._type = 'SimpleClass';
    this.model.viewModel = this;
    this.socket.registerContainer(this.model.id, this);
    this.socket.register(this.model.id, this);
    this.socket.popInjectionQueue(this.model.id);
    //  this.log('init', MSG_TYPE.ERROR);
    this.render();
    //super.ngOnInit();
  }
  ngOnChanges(): void {
    console.log('changed');
  }

  deleteSelfFromParent() {
    this.editorService.deleteGlobalObject(this.model);
    this.model.groups.map((g) => {
      g.attributes.map((e) => {
        this.socket.unregister(e.viewModel);
      });
      this.socket.unregisterContainer(g.viewModel);
    });
    this.socket.unregister(this.getTitleVm() as InteractiveItemBase);
    this.socket.unregister(this);
    this.socket.unregisterContainer(this);
  }
}
