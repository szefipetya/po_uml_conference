import { SessionSocket } from './SessionSocket';
import { SocketWrapper } from './SocketWrapper_I';
import { EditorSocketControllerService } from '../editor-socket-controller.service';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { EditorActionResponse } from 'src/app/components/models/socket/response/EditorActionResponse';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { RESPONSE_SCOPE } from 'src/app/components/models/socket/response/RESPONSE_SCOPE';
import { TARGET_TYPE } from 'src/app/components/models/socket/response/TARGET_TYPE';
import { TOKEN_TYPE } from '../InjectionToken_c';

export class ActionSocket implements SocketWrapper {
  [x: string]: any;
  socket: any;
  service: EditorSocketControllerService;
  constructor(service) {
    this.service = service;
  }
  onmessage(e: any) {
    setTimeout(() => {
      console.log('ACTION MESSAGE', JSON.parse(e.data).action);

      let resp: EditorActionResponse;
      resp = JSON.parse(e.data);
      let load = JSON.parse(resp.action.json);
      let si: SessionInteractiveItem;
      let sc: SessionInteractiveContainer;
      switch (resp.action.action) {
        case ACTION_TYPE.UPDATE:
          si = this.parent.service.itemViewModelMap.find(
            (i) => i.key == resp.target_id
          ).value;
          console.log(si);
          si.updateModel(load, resp.action.id);
          console.log('UPDATE RECEIVED', load);
          break;
        case ACTION_TYPE.RESTORE:
          si = this.parent.service.itemViewModelMap.find(
            (i) => i.key == resp.target_id
          ).value;
          console.log(si);
          si.restoreModel(load, resp.action.id, resp.response_msg);
          break;
        case ACTION_TYPE.CREATE:
          console.log('CREATE UZENET');
          sc = this.parent.service.containerViewModelMap.find(
            (i) => i.key == resp.target_id
          ).value;
          console.log(sc);
          console.log(resp.target_user_id);
          console.log(this.parent.service.user.id);
          if (resp.target_user_id == this.parent.service.user.id) {
            //we are the owner of the object
            load.edit = true;
            //we need to replace the old id with the new one
            console.log('owner megtalálva');
            this.parent.service.itemViewModelMap.map((i) => {
              if (i.key == resp.action.extra['old_id']) {
                i.key = load.id;
                i.value.updateState(JSON.parse(resp.action.extra.sessionState));
                console.log('id kicserélve', resp.action.extra);
                i.value.updateModel(load, resp.action.id);
              }
              //this.service.
            });

            sc.updateItemWithOld(load, resp.action.extra);
          } else {
            load.edit = false;

            //we are not the owner
            this.parent.service.addToInjectionQ(
              TOKEN_TYPE.SESSION_STATE,
              load.id,
              TARGET_TYPE.ITEM,
              JSON.parse(resp.action.extra.sessionState)
            );
            sc.createItem(load, resp.action.extra);
            console.log('new item exists');
          }
          console.log(sc);

          break;
      }
    }, this.parent.service.test.ping);
  }
  onopen(m: any) {
    console.log('Connected: ' + m);
    setTimeout(() => this.parent.socket.send(this.parent.service.user.id), 50);
  }
  onclose(m: any) {}
  connect(source: string) {
    this.socket = new WebSocket(source);
    this.socket.parent = this;
    this.socket.onmessage = this.onmessage;
    this.socket.onopen = this.onopen;
    this.socket.onclose = this.onclose;
  }
  send(action: EditorAction) {
    action.user_id = this.service.user.id;
    console.log('sending', action);
    setTimeout(() => {
      this.socket.send(JSON.stringify(action));
    }, this.service.test.ping);
  }
  disconnect() {
    throw new Error('Method not implemented.');
  }
}
