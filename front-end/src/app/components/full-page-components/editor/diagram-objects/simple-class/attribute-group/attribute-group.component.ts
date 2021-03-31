import {
  AfterContentInit,
  AfterViewInit,
  Component,
  Input,
  OnInit,
} from '@angular/core';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClassElementGroup } from 'src/app/components/models/DiagramObjects/SimpleClassElementGroup';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { SessionState } from 'src/app/components/models/socket/SessionState';
import { EditorSocketControllerService } from '../../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../../services/global-editor/global-editor.service';
import { SimpleClassComponent } from '../simple-class.component';
import { SessionInteractiveContainer } from '../../../../../models/socket/interface/SessionInteractiveContainer';
import { DynamicSerialObject } from 'src/app/components/models/common/DynamicSerialObject';
import { soft_copy, uniqId } from 'src/app/components/utils/utils';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { Action } from 'rxjs/internal/scheduler/Action';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { CallbackItem } from 'src/app/components/models/socket/interface/CallbackItem';

@Component({
  selector: 'app-attribute-group',
  templateUrl: './attribute-group.component.html',
  styleUrls: ['./attribute-group.component.scss'],
})
export class AttributeGroupComponent
  implements OnInit, AfterContentInit, SessionInteractiveContainer {
  @Input() private model: SimpleClassElementGroup;
  inputDOM: any;
  @Input() parent: SimpleClassComponent;

  constructor(private socket: EditorSocketControllerService) {
    // this.model._type = 'SimpleClassElementGroup';
  }
  public getModel() {
    return this.model;
  }
  hasItem(target_id: string) {
    return this.model.attributes.find((t) => target_id == t.id) != null;
  }
  public getId(): string {
    return this.model.id;
  }
  callback_queue: CallbackItem[] = [];
  updateItemWithOld(model, extra) {
    this.model.attributes.map((i) => {
      if (i.id == extra.old_id) {
        soft_copy(i, model, ['viewModel', 'edit']);
        i.id = model.id;
        i.extra = extra;
        console.log('old item found, id injected.');
        console.log(i);
        return;
      }
    });
  }
  sessionState: SessionState;

  updateState(state: SessionState, action_id: string): void {}
  createItem(model: any, extra?: any) {
    model.edit = false;
    model.extra = extra;
    this.model.attributes.push(model);
    console.log('attr created');
    this.sort();
  }
  restoreItem(item_id: string, model: DynamicSerialObject) {}
  deleteItem(item_id: string) {}
  msgPopup(msg: string) {}

  ngAfterContentInit(): void {
    if (this.inputDOM) this.inputDOM.focus();
  }

  ngOnInit(): void {
    this.socket.registerContainer(this.model.id, this);
  }
  delete(id) {
    console.dir('before: this.state.elements', this.model.attributes);
    /*  this.setState({
             elements: this.state.elements.filter(function (el) {
                 return el.id !== id || el.name.length > 0
             })
         });
  */
    this.model.attributes = this.model.attributes.filter(function (el) {
      return el.id !== id || el.name.length > 0;
    });

    console.dir('this.state.elements', this.model.attributes);
    //        this.state.elements = this.state.elements.filter(el => el.id != id)
    this.sort();
  }
  sort() {
    this.model.attributes = this.model.attributes.sort(
      (a, b) => a.index - b.index
    );
    console.log('sorted');
  }
  onMouseOver = (e) => {
    // e.target.
  };

  getNewId = () => {
    const found = false;
    const match = 0;
    const id = undefined;
    let maxid = 0;
    this.parent.model.groups.map((clas, ind) => {
      clas.attributes.map((e, ind2) => {
        if (Number.parseInt(e.id) > maxid) maxid = Number.parseInt(e.id);
      });
    });

    return '' + (maxid + 1);
    // return '_' + Math.random().toString(36).substr(2, 9);
  };
  getHghestIndex() {
    let max = 0;
    this.model.attributes.map((i) => {
      if (i.index > max) max = i.index;
    });
    return max;
  }

  createLocal(visibility, name, type) {
    // this..clientModel.canvas.edit_element_id = id;
    if (name == '') {
      name = '';
    }
    let id = uniqId('a');
    let newAttr = {
      doc: '',
      index: this.getHghestIndex() + 1,
      extra: null,
      edit: true,
      id: id,
      visibility: visibility,
      name: name,
      attr_type: type,
      viewModel: null,
      _type: 'AttributeElement',
    };
    this.model.attributes.push(newAttr);

    let action: EditorAction = new EditorAction(
      this.model.id,
      this.model._type,
      this.parent.model.id
    );
    action.extra = { old_id: id };
    action.action = ACTION_TYPE.CREATE;
    action.json = JSON.stringify(newAttr);

    setTimeout(() => {
      this.inputDOM = document.querySelector('#editor-input');
      if (this.inputDOM) {
        newAttr.name = '';
        this.inputDOM.click();
        this.inputDOM.focus();
        console.log('focused');
      }
    }, 0);
    this.socket.send(action);
    this.sort();
  }

  onNewButtonClick = (e) => {
    const inputDOM = document.querySelector('#editor-input');
    if (!inputDOM) {
      this.createLocal('', '', '');
    }
  };
}
