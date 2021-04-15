import { supportsPassiveEventListeners } from '@angular/cdk/platform';
import { rendererTypeName } from '@angular/compiler';
import {
  AfterContentInit,
  Component,
  Input,
  OnChanges,
  OnInit,
} from '@angular/core';
import { throwToolbarMixedModesError } from '@angular/material/toolbar';
import { SimpleClassElementGroup } from 'src/app/components/models/DiagramObjects/SimpleClassElementGroup';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { LOCK_TYPE } from 'src/app/components/models/socket/LOCK_TYPE';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { SessionState } from 'src/app/components/models/socket/SessionState';
import { AttributeElement } from '../../../../../../models/DiagramObjects/AttributeElement';
import { EditorSocketControllerService } from '../../../../services/editor-socket-controller/editor-socket-controller.service';
import {
  CommonService,
  MSG_TYPE,
} from '../../../../services/common/common.service';
import { AttributeGroupComponent } from '../attribute-group.component';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClassComponent } from '../../simple-class.component';
import { CallbackItem } from 'src/app/components/models/socket/interface/CallbackItem';
import { uniqId } from 'src/app/utils/utils';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { InteractiveItemBase } from 'src/app/components/models/socket/bases/InteractiveItemBase';
import { LogInteractive_I } from 'src/app/components/models/socket/interface/LogInteractive_I';
@Component({
  selector: 'app-attribute',
  templateUrl: './attribute.component.html',
  styleUrls: ['./attribute.component.scss'],
})
export class AttributeComponent
  extends InteractiveItemBase
  implements
  OnInit,
  OnChanges,
  AfterContentInit,
  SessionInteractiveItem,
  LogInteractive_I {
  getParentId(): string {
    return this.getParentContainer().getId();
  }
  disableEdit(): void {
    //  throw new Error('Method not implemented.');
  }
  targetDOM: any;
  inputDOM: Element;
  clname: string;
  dots: string;
  type_dispayed: any;
  str_displayed: any;

  name: string;
  type: string;
  responseMsg: string = '';
  @Input() parent: any;

  @Input() isTitle: boolean;
  @Input() model: AttributeElement;
  extra_overlay: string = '';

  constructor(
    protected socket: EditorSocketControllerService,
    protected commonService: CommonService
  ) {
    super(socket, commonService);
  }

  getId() {
    return this.model.id;
  }
  getParentClass(): SimpleClassComponent {
    if (this.isTitle) {
      return this.parent;
    } else return this.parent.parent;
  }
  getParentContainer(): SessionInteractiveContainer {
    if (this.isTitle) {
      return this.parent;
    } else return this.parent;
  }
  /*abstract override*/
  editBegin() {
    let action = new EditorAction(this.model.id, this.model._type, '');

    action.action = ACTION_TYPE.SELECT;
    action.json = '{}';
    action.target.target_id = this.model.id;
    if (this.isTitle) action.target.parent_id = this.parent.model.id;
    else action.target.parent_id = this.parent.model.id;

    this.sendAction(action);
  }
  /*abstract override*/
  editEnd() {
    let action = new EditorAction(this.model.id, this.model._type, '');
    action.action = ACTION_TYPE.UPDATE;

    this.model.viewModel = null;
    action.json = JSON.stringify(this.model);
    this.model.viewModel = this;

    if (this.isTitle) action.target.parent_id = this.parent.model.id;
    else action.target.parent_id = this.parent.model.id;
    console.log('edit ended', action.json);
    this.sendAction(action);
  }
  /*abstract override*/
  updateModel(model: any, action_id: string, msg: string, extra?: string) {
    this.model.id = model.id;
    if (this.sessionState != null) {
      this.model.name = model.name;
      this.model.attr_type = model.attr_type;
      this.model.visibility = model.visibility;
    }
    if (this.sessionState?.extra) this.sessionState.extra.placeholder = null;
    console.log('view updated: ', this.model.id);
    this.render();
  }

  ngAfterContentInit(): void {
    this.render();
  }
  //view needs this to represent the remaining unended transactions
  isLoading(): string {
    if (this.callback_queue.length > 0)
      return 'loading ' + this.callback_queue.length;
    else return '';
  }
  //view needs this to represent lock state
  isLocked(): string {
    if (this.sessionState == undefined) return 'null';
    if (this.sessionState.lockerUser_id == this.socket.user.id)
      return 'editing';
    if (this.sessionState.locks.length > 0)
      return 'locked:' + this.sessionState.lockerUser_id;
    else return '';
  }

  // queuedActionsAfterLockReceived: EditorAction[] = [];

  ngOnInit(): void {
    this.model.viewModel = this;
    if (this.isTitle) {
      this.getParentClass().model.titleModel.viewModel = this;
    }
    this.init_register();
  }

  ngOnChanges() {
    //  this.render();
  }

  save(wastrue) {
    this.saveEvent(wastrue);
    this.render();
  }
  parentClass;
  /*abstract override*/
  deleteSelfFromParent() {
    // console.log('del triggered');
    this.model.name = '';
    this.socket.unregister(this);
    if (!this.isTitle) this.parent?.delete(this.model.id); //if its a titlemodel, this is not relevant.
  }

  saveEvent(wastrue) {
    this.model.edit = false;

    if (this.isTitle) {
      if (this.model.name == '') {
        this.model.name = '';
        this.model.edit = true;
      } else {
        if (wastrue) this.editEnd();
      }
    } else {
      //NOT TITLE
      if (this.model.name == '' && this.isEditLockedByMe()) {
        this.deleteMessageToServer(this.getParentContainer().getId());
        console.log('deleted', this.model);
      } else {
        if (this.model.name == '' && !this.isEditLockedByMe()) {
          console.log('PUTTED ON DEL LIST');
          this.queuedActionsAfterLockReceived.push(
            this.queueDeleteAction(this.getParentContainer().getId())
          );
        }

        if (this.model.name != '' && !this.isEditLockedByMe()) {
          this.queuedActionsAfterLockReceived.push(
            this.queueUpdateAction(this.getParentContainer().getId())
          );
        } else if (wastrue) this.editEnd();
      }
    }
  }

  onClick(e) {
    if (!this.isAccessible()) return;
    this.editBegin();
    this.model.edit = true;
    this.targetDOM = e.target;

    // this.parent.editorService.clientModel.canvas.edit_element = this;
    if (!this.isTitle) this.renderMode = 'ATTRIBUTE_EDIT';

    this.render();

    let inputDOM = document.querySelector('#editor-input');
    if (inputDOM) {
      this.inputDOM = inputDOM;
      // inputDOM.focus();
    }
  }
  setVisibility = (e) => {
    let vis = '+';
    let l = false;
    if (
      e.target.value[0] == '+' ||
      e.target.value[0] == '-' ||
      e.target.value[0] == '#' ||
      e.target.value[0] == '~'
    ) {
      l = true;
      vis = e.target.value[0];
    }
    this.model.visibility = vis;
    return l;
  };
  setNameAndType = (e, l) => {
    let splitted = e.target.value.split(':');
    this.model.name = splitted[0].trim();
    if (l) {
      this.model.name = this.model.name.substr(1);
    }
    if (splitted[1]) this.model.attr_type = splitted[1].trim();
    else {
      this.model.attr_type = '';
    }
  };
  onInput = (e) => {
    console.log(this.model);
    let isVisibilitySymbolWritten = this.setVisibility(e);
    this.setNameAndType(e, isVisibilitySymbolWritten);
    this.render();
  };
  showedText = '';
  titleScale = 1.5;
  elementScale = 2.35;
  inputWidth = 20;
  renderMode: string;

  str;
  render() {
    if (this.model) {
      if (
        this.sessionState &&
        this.sessionState.lockerUser_id == this.socket.user.id
      ) {
        this.model.edit = true;
      }

      let { name, attr_type, id } = this.model;

      if (attr_type == '') {
        this.str = '';
      } else {
        this.str = ':';
      }
      let strfull;
      if (attr_type) strfull = name + this.str + attr_type;
      else {
        strfull = name;
      }
      this.showedText = strfull;

      if (this.isTitle) {
        this.clname = 'class-title class-element';

        if (this.model.edit) {
          //TITLE_EDIT parent.parent helyett parent simán
          let val1 =
            (this.parent.general.fontsize_scaled / this.elementScale) *
            1.418 *
            strfull.length;
          let val2 =
            this.parent.model.scaledModel.width_scaled -
            (this.parent.general.padding_scaled +
              this.parent.general.border_scaled) *
            2;
          this.inputWidth = Math.max(val1, val2);
          //old width: `${(strfull.length + 1) * (this.state.parent.parent.clientModel.class_general.fontsize_scaled / this.titleScale)}px`
        } else {
          //TITLE
          let rescale = this.titleScale;
          let charwidth = this.parent.general.fontsize_scaled / rescale;
          let textwidth =
            (this.parent.general.fontsize_scaled / rescale) * strfull.length;
          let width =
            this.parent.model.scaledModel.width_scaled -
            (this.parent.general.padding_scaled +
              this.parent.general.border_scaled) *
            2;
          let l = false;

          this.showedText = this.model.name.substr(
            0,
            this.model.name.length -
            Math.round(((textwidth - width) / charwidth) * 1.1 + 1.5)
          );
          if (textwidth > width) {
            l = true;
          }

          this.dots = '';
          if (l) {
            this.dots += '...';
          }
        }
      } else {
        this.clname = 'class-element';
        if (this.model.edit) {
          //ATTRIBUTE EDIT

          this.renderMode = 'ATTRIBUTE_EDIT';

          let val1 =
            (this.parent.parent.general.fontsize_scaled / this.elementScale) *
            0.92 *
            strfull.length;
          let val2 =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.parent.general.padding_scaled +
              this.parent.parent.general.border_scaled) *
            2;
          this.inputWidth = Math.max(val1, val2);
        } else {
          //ATTRIBUTE
          let l = false;

          let rescale = this.elementScale;
          let charwidth = this.parent.parent.general.fontsize_scaled / rescale;
          let textwidth =
            (this.parent.parent.general.fontsize_scaled / rescale) *
            (strfull.length + 1);
          let width =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.parent.general.padding_scaled +
              this.parent.parent.general.border_scaled) *
            2;
          if (textwidth > width) {
            this.showedText = strfull.substr(
              0,
              strfull.length - Math.round((textwidth - width) / charwidth)
            );
            l = true;
          }
          this.name = this.showedText.split(':')[0];
          this.str = this.showedText.includes(':');
          this.type = this.showedText.split(':')[1];
          this.type_dispayed;
          this.str_displayed;
          if (this.str) {
            this.str_displayed = ':';
          } else this.str_displayed = '';
          if (attr_type) {
            this.type_dispayed = this.type;
          } else this.type_dispayed = '';
          this.dots = '';
          if (l) {
            this.dots += '...';
          }
        }
      }
    }
    if (this.sessionState?.extra?.placeholder) {
      this.extra_overlay = this.sessionState.extra.placeholder;
    } else {
      this.extra_overlay = '';
    }
  }
}
