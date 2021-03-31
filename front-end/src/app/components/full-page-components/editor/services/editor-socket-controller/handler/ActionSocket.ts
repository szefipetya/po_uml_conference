import { SessionSocket } from './SessionSocket';
import { SocketWrapper } from './SocketWrapper_I';
import {
  EditorSocketControllerService,
  Pair,
} from '../editor-socket-controller.service';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { EditorActionResponse } from 'src/app/components/models/socket/response/EditorActionResponse';
import { SessionInteractiveItem } from 'src/app/components/models/socket/interface/SessionInteractiveItem';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { RESPONSE_SCOPE } from 'src/app/components/models/socket/response/RESPONSE_SCOPE';
import { TARGET_TYPE } from 'src/app/components/models/socket/response/TARGET_TYPE';
import { TOKEN_TYPE } from '../InjectionToken_c';
import { DynamicSerialObject } from 'src/app/components/models/common/DynamicSerialObject';
import { GlobalEditorService } from '../../global-editor/global-editor.service';

export class ActionSocket implements SocketWrapper {
  [x: string]: any;
  socket: any;
  service: EditorSocketControllerService;
  constructor(service, private editorService: GlobalEditorService) {
    this.service = service;
  }

  onmessage(e: any) {
    setTimeout(() => {
      let resp: EditorActionResponse;
      resp = JSON.parse(e.data);
      let load = JSON.parse(resp.action.json);
      let si: SessionInteractiveItem;
      let sc: SessionInteractiveContainer;
      console.log(
        'ACTION MESSAGE',
        JSON.parse(e.data).action,
        this.parent.service.user.id,
        resp.action.user_id
      );
      switch (resp.action.action) {
        case ACTION_TYPE.UPDATE:
          si = this.parent.getItem(resp.target_id);
          console.log(si);
          if (resp.action.user_id != this.parent.service.user.id) {
            if (si) si.updateModel(load, resp.action.id);
          } else
            console.log('UPDATE RECEIVED OWNER', this.parent.service.user.id);
          break;
        case ACTION_TYPE.RESTORE:
          if (resp.target_type == TARGET_TYPE.CONTAINER) {
            sc = this.parent.getContainer(resp.target_id);
          } else {
            // TARGET_TYPE.ITEM
            si = this.parent.getItem(resp.target_id);
            if (si) {
              console.log(si);
              si.restoreModel(load, resp.action.id, resp.response_msg);
            } else {
              //user have deleted it, item not found
              sc = this.parent.getContainer(resp.action.target.parent_id);
              if (resp.target_user_id == this.parent.service.user.id) {
                if (sc) {
                  //we are the owner of the object
                  // load.edit = true;
                  if (resp.action.extra.sessionState) {
                    console.log('injection added');
                    //this.parent.addToInjectionQueue(load.id,resp );
                    this.parent.addToInjectionQueue(
                      load.id,
                      TOKEN_TYPE.COMBINED,
                      {
                        sessionState: JSON.parse(
                          resp.action.extra.sessionState
                        ),
                        model: load,
                      }
                    );
                  }
                  sc.createItem(load, resp.action.extra);
                } else {
                  //parent is null-> global object
                  console.log('RESTORE1');
                  if (resp.action.extra.sessionState) {
                    console.log(
                      'injection added',
                      resp.action.extra.sessionState
                    );
                    this.parent.addToInjectionQueue(
                      load.id,
                      TOKEN_TYPE.COMBINED,
                      {
                        sessionState: JSON.parse(
                          resp.action.extra.sessionState
                        ),
                        model: load,
                      }
                    );
                  }
                  if (!this.parent.editorService.hasGlobalObject(load)) {
                    this.parent.editorService.createGlobalObject(load);
                    console.log('RESTORE2');
                  }
                }
              }
            }
          }
          break;
        case ACTION_TYPE.CREATE:
          console.log('CREATE UZENET');
          sc = this.parent.getContainer(resp.target_id);
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
                i.value.updateModel(load, resp.action.id);
                i.value.updateState(JSON.parse(resp.action.extra.sessionState));
                console.log('id kicserélve', resp.action.extra);
              }
            });

            sc.updateItemWithOld(load, resp.action.extra);
          } else {
            load.edit = false;
            //we are not the owner

            if (resp.action.extra)
              this.parent.addToInjectionQueue(
                load.id,
                TOKEN_TYPE.SESSION_STATE,
                { sessionState: JSON.parse(resp.action.extra.sessionState) }
              );
            sc.createItem(load, resp.action.extra);
          }
          console.log(sc);

          break;
        case ACTION_TYPE.DELETE:
          console.log('delete reveived');
          si = this.parent.getItem(resp.target_id);
          si?.deleteSelfFromParent();
          break;
      }
    }, this.parent.service.test.ping);
  }
  onopen(m: any) {
    console.log('Connected: ' + m);
    setTimeout(() => this.parent.socket.send(this.parent.service.user.id), 50);
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
  disconnect() {}
  addToInjectionQueue(id, type: TOKEN_TYPE, data) {
    console.log('data', data);
    this.service.addToInjectionQ(type, id, TARGET_TYPE.ITEM, data);
  }
}
