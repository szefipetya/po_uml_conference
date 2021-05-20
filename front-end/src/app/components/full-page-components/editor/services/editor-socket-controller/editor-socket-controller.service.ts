import { Injectable } from '@angular/core';
import { Action } from 'rxjs/internal/scheduler/Action';
import { AttributeElement } from 'src/app/components/models/DiagramObjects/AttributeElement';
import { ActionTarget } from 'src/app/components/models/socket/ActionTarget';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { SessionState } from 'src/app/components/models/socket/SessionState';
/* import Stomp from '../../../../../js/webjars/stomp.js';
import SockJS from '../../../../../js/webjars/sockjs.min.js'; */
import { ACTION_TYPE } from '../../../../models/socket/ACTION_TYPE';
import { EditorAction } from '../../../../models/socket/EditorAction';
import { GlobalEditorService } from '../global-editor/global-editor.service';
import { SessionStateResponse } from '../../../../models/socket/response/SessionStateResponse';
import { Statement } from '@angular/compiler';
import { RESPONSE_SCOPE } from 'src/app/components/models/socket/response/RESPONSE_SCOPE';
import { EditorActionResponse } from 'src/app/components/models/socket/response/EditorActionResponse';
import { User } from 'src/app/components/models/User';
import { Element_c } from 'src/app/components/models/DiagramObjects/Element_c';
import { ActionSocket } from './handler/ActionSocket';
import { SessionSocket } from './handler/SessionSocket';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { TARGET_TYPE } from 'src/app/components/models/socket/response/TARGET_TYPE';
import { InjectionToken_c } from './InjectionToken_c';
import { TOKEN_TYPE } from './InjectionToken_c';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { Pair } from '../../../../../utils/utils';
import { environment } from 'src/environments/environment';
import { getCookie } from 'src/app/utils/cookieUtils';
declare var SockJS: any;
declare var Stomp: any;
class Test {
  ping: number = 0;
  changePing(v) {
    this.ping = v;
  }
}
export class SessionUser {
  user: User;
  color: string;
  constructor(user, color) {
    this.user = user;
    this.color = color;
  }
}

@Injectable({
  providedIn: 'root',
})
export class EditorSocketControllerService {
  [x: string]: any;

  url_pre = 'ws://' + environment.api_url_raw;
  // url_pre = 'ws://localhost:8101/';
  initSessionSocket() {
    this.sessionSocket = new SessionSocket(this);

    this.sessionSocket.connect(this.url_pre + 'state');
  }
  constructor(private editorService: GlobalEditorService) {
    this.itemViewModelMap = [];
    this.waitingForResponse_queue = [];
    // this.user = editorService.getUser();
    this.test = new Test();
    this.editorService.addListenerToEvent(
      this,
      (target) => {
        target.connect();
      },
      'diagram_fetch'
    );
    this.addListenerToEvent(this, (target) => {
      target.injectionQueue = [];
      target.itemViewModelMap = [];
      target.containerViewModelMap = [];
      target.disconnect();
      console.log("megvan az init");
    }, 'pre_setup')
  }

  //the data exists. we just need to wait for the view
  // to be ready and inject its own state and model
  injectionQueue: InjectionToken_c[] = [];
  //this is for future injection, the data is not known yet,
  // the injection process is started by a response
  futureCallbackInjectionQueue: InjectionToken_c[] = [];

  createGlobalObjectAndRequestStateInjectionForSimpleClass(model: SimpleClass) {
    console.log('traceback waiting activated for' + model);
    model.groups.map((g) => {
      g.attributes.forEach((e) => {
        console.log('item is ' + this.getItem(e.id));
        // if (this.getItem(e.id)) {
        this.addToFutureCallbackInjectionQueue(
          TOKEN_TYPE.SESSION_STATE,
          e.id,
          TARGET_TYPE.ITEM
        );
        console.log('traceback waiting activated for' + e.id);
        //}
      });
    });
    this.addToFutureCallbackInjectionQueue(
      TOKEN_TYPE.SESSION_STATE,
      model.titleModel.id,
      TARGET_TYPE.ITEM
    );
    this.editorService.createGlobalObject(model);
  }

  popInjectionQueue(req_target_id: string) {
    let self = this;
    this.injectionQueue.map((i) => {
      if (i.target_id == req_target_id) {
        let item: SessionInteractiveItem = self.itemViewModelMap.find(
          (i) => i.key == req_target_id
        )?.value;
        if (item) {
          if (i.type == TOKEN_TYPE.COMBINED) {
            item.restoreModel(i.data.model, '');
            item.updateState(i.data.sessionState);
          } else if (i.type == TOKEN_TYPE.SESSION_STATE) {
            item.updateState(i.data.sessionState);
          }
        }
      }
    });
  }
  popFutureCallbackInjectionQueue(
    req_target_id: string,
    type: TOKEN_TYPE,
    data: any
  ) {
    let self = this;
    this.futureCallbackInjectionQueue.map((i) => {
      if (i.target_id == req_target_id) {
        let item: SessionInteractiveItem = self.itemViewModelMap.find(
          (i) => i.key == req_target_id
        )?.value;
        console.log('future pop' + i);
        if (item) {
          if (i.type == type && type == TOKEN_TYPE.SESSION_STATE) {
            item.updateState(data.sessionState);
          } else if (i.type == type && type == TOKEN_TYPE.COMBINED) {
            item.updateState(data.sessionState);
            item.restoreModel(data.model, '');
          }
        } else {
          //add to normal injection queue, because the view is not ready yet
          this.addToInjectionQueue(req_target_id, type, data);
        }
      }
    });
  }
  addToInjectionQ(
    type: TOKEN_TYPE,
    target_id: string,
    target_type: TARGET_TYPE,
    data: any
  ) {
    let token = new InjectionToken_c();
    token.type = type;
    token.data = data;
    token.target_id = target_id;
    token.target_type = target_type;
    this.injectionQueue.push(token);
  }
  addToFutureCallbackInjectionQueue(
    type: TOKEN_TYPE,
    target_id: string,
    target_type: TARGET_TYPE
  ) {
    let token = new InjectionToken_c();
    token.type = type;
    token.target_id = target_id;
    token.target_type = target_type;
    this.futureCallbackInjectionQueue.push(token);
  }

  public register(target_id: string, view: SessionInteractiveItem) {
    this.itemViewModelMap.push(new Pair(target_id, view));
    /* console.log('REGISTERED: ' + target_id);*/
  }
  public unregister(view: SessionInteractiveItem) {
    this.itemViewModelMap = this.itemViewModelMap.filter((f) => {
      /*  if (f.key == view.getId()) console.log('UNREGISTERED: ' + f.key);*/
      return f.key != view.getId();
    });
  }
  public unregisterContainer(view: SessionInteractiveContainer) {
    this.containerViewModelMap = this.containerViewModelMap.filter((f) => {
      /* if (f.key == view.getId()) console.log('UNREGISTERED: ' + f.key);*/
      return f.key != view.getId();
    });
  }
  public registerContainer(
    target_id: string,
    view: SessionInteractiveContainer
  ) {
    this.containerViewModelMap.push(new Pair(target_id, view));
  }

  //<Id,viewModel>
  itemViewModelMap: Pair<String, SessionInteractiveItem>[] = [];
  containerViewModelMap: Pair<String, SessionInteractiveContainer>[] = [];

  sessionSocket: SessionSocket;
  actionSocket: ActionSocket;

  public connect() {
    this.actionSocket = new ActionSocket(this, this.editorService);


    this.actionSocket.connect(this.url_pre + 'action');
  }
  public disconnect() {
    this.sessionSocket?.socket?.close();
    this.actionSocket?.socket?.close();
  }

  public send(action: EditorAction) {
    // this.waitingForResponse_queue.push(new Pair(action, sender));
    action.user_id = this.getUser().id;
    console.log(getCookie("session_jwt"))
    action.session_jwt = getCookie("session_jwt");
    console.log('sent', action);
    this.actionSocket.send(action);
  }
  public getUser() {
    return this.editorService.getUser();
  }
  getItem(id) {
    let p: Pair<String, SessionInteractiveItem> = this.itemViewModelMap.find(
      (i) => i.key == id
    );
    if (p) return p.value;
    else return null;
  }
  public triggerEvent(wich: string) {

    this.editorService.triggerEvent(wich);
  }
  getDiagramId() {
    return this.editorService.getDiagramId();
  }


  addListenerToEvent(target, fn, alias: string = '') {
    this.editorService.addListenerToEvent(target, fn, alias);
  }
  test: Test;
}
