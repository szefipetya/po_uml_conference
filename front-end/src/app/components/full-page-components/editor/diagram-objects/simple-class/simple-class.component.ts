import {
  Component,
  OnInit,
  Input,
  OnChanges,
  AfterContentInit,
  TemplateRef,
} from '@angular/core';
import { AttributeElement } from 'src/app/components/models/DiagramObjects/AttributeElement';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { InteractiveItemBase } from 'src/app/components/models/socket/bases/InteractiveItemBase';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
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
  implements OnInit, OnChanges, AfterContentInit {
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
    private editorService: GlobalEditorService
  ) {
    super(socket, commonService);
  }
  saveEvent(wastrue: any): void {}
  updateScales(scale: any): void {
    if (this.model.titleModel.edit) {
      this.model.titleModel.viewModel.render();
    }
    this.model.groups.map((group) => {
      group.attributes.map((e2) => {
        if (e2.edit) {
          e2.viewModel.inputWidth *= scale;
          e2.viewModel.render();
        }
      });
    });
  }
  onSelect() {}
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
  update(): void {
    this.model.groups.map((group) => {
      group.attributes.map((a) => {
        a.viewModel.render();
      });
    });
    this.model.titleModel.viewModel.render();
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
    this.model.titleModel.viewModel.save(prev);

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
  ngOnInit(): void {
    this.model._type = 'SimpleClass';
    this.model.viewModel = this;
    this.init_register();
  }
  ngOnChanges(): void {
    console.log('changed');
  }
}
