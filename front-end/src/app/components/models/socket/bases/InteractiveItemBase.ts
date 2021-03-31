import { CallbackItem } from '../interface/CallbackItem';
import { SessionInteractiveItem } from '../interface/SessionInteractiveItem';
import { SessionState } from '../SessionState';
import { EditorAction } from '../EditorAction';
import { EditorSocketControllerService } from '../../../full-page-components/editor/services/editor-socket-controller/editor-socket-controller.service';
import { uniqId } from '../../../utils/utils';
import {
  CommonService,
  MSG_TYPE,
} from '../../../full-page-components/editor/services/common/common.service';
import { ACTION_TYPE } from '../ACTION_TYPE';
import { DynamicSerialObject } from '../../common/DynamicSerialObject';
export abstract class InteractiveItemBase implements SessionInteractiveItem {
  model: DynamicSerialObject;
  //session requirements
  callback_queue: CallbackItem[] = [];
  protected sessionState: SessionState;
  queuedActionsAfterLockReceived: EditorAction[] = [];

  constructor(
    protected socket: EditorSocketControllerService,
    protected commonService: CommonService
  ) {}
  //Session funcions---------------------------------
  updateState(state: SessionState, callback_action_id = ''): void {
    if (state == undefined) return;
    console.log('state to be inserted', state);
    this.callback_queue = this.callback_queue.filter(
      (q) => q.action_id != callback_action_id
    );
    this.sessionState = state;
    while (this.queuedActionsAfterLockReceived.length > 0) {
      let action = this.queuedActionsAfterLockReceived.pop();
      //this is needed because if its a new object, the fresh id is now injected
      console.log('ACTUAL ID', this.model.id);
      action.target.target_id = this.model.id;
      if (action.json != null) {
        let parsed = JSON.parse(action.json);
        parsed.id = this.model.id;
        action.json = JSON.stringify(parsed);
      }
      this.sendAction(action);
    }

    if (state.lockerUser_id != this.socket.user.id) {
      this.model.edit = false;
    } else {
      //we are the owner
      if (this.sessionState?.extra?.placeholder) {
        this.sessionState.extra.placeholder = null;
      }
    }
    this.render();
  }
  public abstract editBegin(): void;
  public abstract editEnd(): void;
  abstract updateModel(model: any, action_id: string, msg?: string): void;
  restoreModel(model: any, action_id: string, msg: string) {
    this.updateModel(model, action_id, msg);
    this.model.edit = false;
    console.log('RESTORED');
    this.callback_queue = this.callback_queue.filter(
      (q) => q.action_id != action_id
    );
    if (msg) this.log(msg, MSG_TYPE.ERROR);
    this.render();
  }
  isEditLockedByMe(): boolean {
    return this.sessionState?.lockerUser_id == this.socket.user.id;
  }

  //_Session Functions---------------------------------
  //Logging Utils-------------------------------------
  box_shadow: string = '';
  log(msg: string, type: MSG_TYPE) {
    this.commonService.putLog(msg, type, this);
  }
  highlightMe(on: boolean, color: string): void {
    if (on) this.box_shadow = ' 0px 0px 21px 1px ' + color;
    else this.box_shadow = '';
  }
  //_Logging Utils--------------------------------------
  sendAction(action: EditorAction) {
    this.callback_queue.push(new CallbackItem(action.id));
    this.socket.send(action);
  }
  init_register() {
    this.socket.register(this.model.id, this);
    this.socket.popInjectionQueue(this.model.id);
    //  this.log('init', MSG_TYPE.ERROR);
    this.render();
  }
  render(): void {}
  //DML functions-v-v-v-v-v-v-v-v-v-v-
  deleteAsync(parent_id: string) {
    if (this.isEditLockedByMe()) {
      this.deleteMessageToServer(parent_id);
    } else {
      this.queuedActionsAfterLockReceived.push(
        this.queueDeleteAction(parent_id)
      );
    }
  }
  abstract deleteSelfFromParent(): void;
  deleteMessageToServer(parent_id: string) {
    let a = new EditorAction(this.model.id, this.model._type, parent_id);
    a.action = ACTION_TYPE.DELETE;
    a.id = uniqId();
    this.socket.send(a);
    this.deleteSelfFromParent();
    this.socket.unregister(this);
  }
  abstract saveEvent(wastrue): void;
  queueDeleteAction(parent_id: string): EditorAction {
    let a = new EditorAction(this.model.id, this.model._type, parent_id);
    a.action = ACTION_TYPE.DELETE;
    a.id = uniqId();

    this.deleteSelfFromParent();
    this.socket.unregister(this);

    return a;
  }
  queueUpdateAction(parent_id: string): EditorAction {
    let action = new EditorAction(this.model.id, this.model._type, parent_id);
    action.action = ACTION_TYPE.UPDATE;
    this.model.viewModel = null;
    action.json = JSON.stringify(this.model);
    this.model.viewModel = this;
    return action;
  }
  isLoading(): string {
    if (this.callback_queue.length > 0)
      return 'loading ' + this.callback_queue.length;
    else return '';
  }
  isAccessible(): boolean {
    if (this.sessionState == null) {
      return false;
    }
    if (
      this.sessionState.locks.length > 0 &&
      this.socket.user.id != this.sessionState.lockerUser_id
    ) {
      if (this.sessionState.lockerUser_id != this.socket.user.id) {
        this.log(
          "Object is locked (locker's id: " +
            this.sessionState.lockerUser_id +
            ')',
          MSG_TYPE.INFO
        );
      }
      return false;
    }

    return true;
  }
  abstract disableEdit(): void;
}
