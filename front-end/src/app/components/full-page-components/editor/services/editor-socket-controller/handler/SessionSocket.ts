import { InteractivityChecker } from '@angular/cdk/a11y';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { SessionStateResponse } from 'src/app/components/models/socket/response/SessionStateResponse';
import { TARGET_TYPE } from 'src/app/components/models/socket/response/TARGET_TYPE';
import { EditorSocketControllerService } from '../editor-socket-controller.service';
import { TOKEN_TYPE } from '../InjectionToken_c';
import { SocketWrapper } from './SocketWrapper_I';
import { Pair } from '../../../../../../utils/utils';

export class SessionSocket implements SocketWrapper {
  [x: string]: any;
  socket: any;
  service: EditorSocketControllerService;
  constructor(service) {
    this.service = service;
  }
  onmessage(e: any) {
    setTimeout(() => {
      console.log(this);

      let resp: SessionStateResponse;
      resp = JSON.parse(e.data);
      console.log('SESSION MESSAGE', resp);
      let si: SessionInteractiveItem;
      let sc: SessionInteractiveContainer;

      switch (resp.target_type) {
        case 'CONTAINER':
          sc = this.parent.getContainer(resp.target_id);
          if (sc) sc.updateState(resp.sessionState, resp.action_id);
          break;

        case 'ITEM':
          si = this.parent.getItem(resp.target_id);
          if (si) si.updateState(resp.sessionState, resp.action_id);
          console.log('ITEMS SESSION UPDATED');
          break;
        case 'ITEM_INJECTION':
          this.parent.service.popFutureCallbackInjectionQueue(
            resp.target_id,
            TOKEN_TYPE.SESSION_STATE,
            { sessionState: resp.sessionState }
          );
        case 'CONTAINER_INJECTION':
          this.parent.service.popFutureCallbackInjectionQueue(
            resp.target_id,
            TOKEN_TYPE.SESSION_STATE,
            { sessionState: resp.sessionState }
          );
      }
      this.parent.service.triggerEvent('update');
    }, this.parent.service.test.ping);
  }
  onopen(m: any) {
    console.log('Connected: ' + m);
    setTimeout(() => this.send(this.parent.service.user.id), 50);
  }
  oninitmessage(e: any) {
    let responses: SessionStateResponse[] = JSON.parse(e.data);
    console.log(responses);
    responses.map((r) => {
      console.log(r);
      let it = this.parent.service.itemViewModelMap.find(
        (v) => v.key == r.target_id
      );
      if (it) it.value.updateState(r.sessionState);
      else console.error('No view for SessionState:', r);
    });
    this.parent.socket.onmessage = this.parent.onmessage;
    console.log(this);
  }
  onclose(m: any) {
    console.log('closed', m);
    let a;
  }
  connect(source: string) {
    this.socket = new WebSocket(source);
    this.socket['parent'] = this;

    this.socket.onmessage = this.oninitmessage;
    this.socket.onopen = this.onopen;
    this.socket.onclose = this.onclose;
  }

  disconnect() {
    throw new Error('Method not implemented.');
  }
  getItem(id) {
    let p: Pair<
      String,
      SessionInteractiveItem
    > = this.service.itemViewModelMap.find((i) => i.key == id);
    if (p) return p.value;
    else return null;
  }
  getContainer(id) {
    let p: Pair<
      String,
      SessionInteractiveContainer
    > = this.service.containerViewModelMap.find((i) => i.key == id);
    if (p) return p.value;
    else return null;
  }
}
