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
import { Element_c } from 'src/app/components/models/DiagramObjects/Element_c';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClassElementGroup } from 'src/app/components/models/DiagramObjects/SimpleClassElementGroup';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { InteractiveItemBase } from 'src/app/components/models/socket/bases/InteractiveItemBase';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { soft_copy } from 'src/app/components/utils/utils';
import { CommonService } from '../../services/common/common.service';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { DiagramObjectComponent } from '../diagram-object/diagram-object.component';

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
  updateModel(model: any, action_id: string, msg?: string): void {
    //let vm = this.model.viewModel;
    //  let tvm = this.model.titleModel.viewModel;
    /*  let gvms = [];
    this.model.groups.map((g) => {
      gvms.push(g.viewModel);
    });*/
    console.log('CLASS SIDE CUCC FUTOTT LE');
    //  soft_copy(model, this.model, ['edit', 'viewModel', 'scaledModel']);

    this.model.id = model.id;
    /* this.model.groups.map((g, i) => {
      this.model.groups[i].viewModel = gvms[i];
    });*/

    // this.model.titleModel.viewModel = tvm;
    //   this.model.viewModel = vm;
    //{
    //if (this.sessionState.lockerUser_id != this.socket.user.id) {

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
    // }
  }
  createItem(model: DynamicSerialObject, extra?: any) {
    console.log('creating:', model);
    if (model._type == 'SimpleClassElementGroup') {
      if (!this.hasItem(model.id))
        this.model.groups.push(model as SimpleClassElementGroup);
    }
    let vm = this.model.viewModel;

    if (model._type == 'Element_c') {
      this.getTitleVm().updateModel(model, '', '');
      console.log(this.model.titleModel);
      //title model
    }
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
    return 'root';
  }

  update(): void {
    this.model.groups.map((group) => {
      group.attributes.map((a) => {
        a.viewModel?.render();
      });
    });
    (this.getTitleVm() as InteractiveItemBase).render();
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
    //   this.model.titleModel.edit = false;
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
  @Input() public general: SimpleClass_General;

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
