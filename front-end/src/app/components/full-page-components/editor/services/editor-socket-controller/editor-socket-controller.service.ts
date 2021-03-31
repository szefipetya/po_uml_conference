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
declare var SockJS: any;
declare var Stomp: any;
class Test {
  ping: number = 0;
  changePing(v) {
    this.ping = v;
  }
}
export class Pair<K, V> {
  key: K;
  value: V;
  constructor(key: K, value: V) {
    this.key = key;
    this.value = value;
  }
}

@Injectable({
  providedIn: 'root',
})
export class EditorSocketControllerService {
  [x: string]: any;

  //url_pre = 'ws://84.2.193.197:8101/';
  url_pre = 'ws://localhost:8101/';
  constructor(private editorService: GlobalEditorService) {
    this.itemViewModelMap = [];
    this.waitingForResponse_queue = [];
    this.user = editorService.user;
    this.test = new Test();
  }
  user: User;
  injectionQueue: InjectionToken_c[] = [];

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
    // console.log('injqueue', this.injectionQueue);
    // console.log('vm map', this.itemViewModelMap);

    // if (token.target_type == TARGET_TYPE.ITEM) {
    // if (token.type == TOKEN_TYPE.SESSION_STATE) {

    //   }
    //  }
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

  public register(target_id: string, view: SessionInteractiveItem) {
    this.itemViewModelMap.push(new Pair(target_id, view));
  }
  public unregister(view: SessionInteractiveItem) {
    this.itemViewModelMap = this.itemViewModelMap.filter(
      (f) => f.value != view
    );
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
    this.sessionSocket = new SessionSocket(this);

    this.sessionSocket.connect(this.url_pre + 'state');

    this.actionSocket.connect(this.url_pre + 'action');
  }
  public disconnect() {
    this.sessionSocket.socket.close();
    this.actionSocket.socket.close();
  }

  public send(action: EditorAction) {
    // this.waitingForResponse_queue.push(new Pair(action, sender));
    action.user_id = this.user.id;
    this.actionSocket.send(action);
  }

  test: Test;
}
