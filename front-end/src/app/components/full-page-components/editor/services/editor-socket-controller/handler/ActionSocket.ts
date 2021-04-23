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
import { DynamicSerialObject } from 'src/app/components/models/common/DynamicSerialObject';
import { GlobalEditorService } from '../../global-editor/global-editor.service';
import { Pair } from '../../../../../../utils/utils';
import { JsonpClientBackend } from '@angular/common/http';
import { SocketAuthenticationRequest } from '../../../../../models/socket/security/SocketAuthenticationRequest'
import { getCookie, setCookie } from 'src/app/utils/cookieUtils';
import { environment } from 'src/environments/environment';
export class ActionSocket implements SocketWrapper {
  [x: string]: any;
  socket: any;
  service: EditorSocketControllerService;
  constructor(service, private editorService: GlobalEditorService) {
    this.service = service;
  }
  initDone: boolean = false;
  onmessage(e: any) {
    setTimeout(() => {
      if (!this.parent.initDone) {
        setCookie("session_jwt", e.data, 10);
        this.parent.initDone = true;
        this.parent.service.initSessionSocket();
        return;
      }
      let resp: EditorActionResponse;
      resp = JSON.parse(e.data);
      let load = JSON.parse(resp.action.json);
      let si: SessionInteractiveItem;
      let sc: SessionInteractiveContainer;
      console.log(
        'ACTION MESSAGE',
        JSON.parse(e.data).action,
        this.parent.service.getUser().id,
        resp.action.user_id
      );
      switch (resp.action.action) {
        case ACTION_TYPE.UPDATE:
          si = this.parent.getItem(resp.target_id);
          console.log(si);
          if (si) { this.parent.service.triggerEvent('update'); }
          if (resp.action.user_id != this.parent.service.getUser().id) {
            if (si) {
              si.updateModel(load, resp.action.id);
            }
          } else
            console.log('UPDATE RECEIVED OWNER', this.parent.service.getUser().id);
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
              if (resp.target_user_id == this.parent.service.getUser().id) {
                if (sc) {
                  //we are the owner of the object
                  // load.edit = true;
                  if (resp.action.extra.sessionState) {
                    console.log('injection added');
                    this.parent.addToInjectionQueue(
                      load.id,
                      TOKEN_TYPE.COMBINED,
                      TARGET_TYPE.ITEM,
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
                      TARGET_TYPE.ITEM,
                      {
                        sessionState: JSON.parse(
                          resp.action.extra.sessionState
                        ),
                        model: load,
                      }
                    );
                  }
                  if (!this.parent.editorService.hasGlobalObject(load)) {
                    console.log('STATE INJECTION PUT IN');
                    if (load?._type == 'SimpleClass')
                      this.parent.service.createGlobalObjectAndRequestStateInjectionForSimpleClass(
                        load
                      );
                  }
                }
              }
            }
          }
          break;
        case ACTION_TYPE.CREATE:
          console.log('CREATE UZENET');
          this.parent.service.containerViewModelMap.map(p => {
            console.log(p.key + '   ' + p.value.getId())
          })
          // console.log('containers' + this.parent.service.containerViewModelMap);
          sc = this.parent.service.containerViewModelMap.find(i => i.key == resp.action.target.parent_id)?.value;
          console.log(resp.action.target);
          console.log(sc);
          console.log(resp.target_user_id);
          console.log(this.parent.service.getUser().id);
          if (resp.target_user_id == this.parent.service.getUser().id) {
            //we are the owner of the object
            load.edit = true;
            //we need to replace the old id with the new one
            console.log('owner megtalálva');
            if (resp.target_type == 'CONTAINER') {
              this.parent.service.containerViewModelMap.map((i) => {
                if (i.key == resp.action.extra['old_id']) {
                  i.key = load.id;
                  i.value.updateModel(load, resp.action.id);
                  i.value.updateState(
                    JSON.parse(resp.action.extra.sessionState)
                  );
                  console.log('id kicserélve', resp.action.extra);
                }
              });
            }
            this.parent.service.itemViewModelMap.map((i) => {
              if (i.key == resp.action.extra['old_id']) {
                i.key = load.id;
                i.value.updateModel(load, resp.action.id);
                i.value.updateState(JSON.parse(resp.action.extra.sessionState));
                console.log('id kicserélve', resp.action.extra);
              }
            });

            // sc.updateItemWithOld(resp.action.extra.old_id, load);
          } else {
            load.edit = false;
            //we are not the owner

            if (resp.action.extra)
              this.parent.addToInjectionQueue(
                load.id,
                TOKEN_TYPE.SESSION_STATE,
                resp.target_type,
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
      this.parent.service.triggerEvent('update');
    }, this.parent.service.test.ping);
  }
  onopen(m: any) {
    console.log('Connected: ' + m);
    setTimeout(() => this.parent.socket.send(JSON.stringify(new SocketAuthenticationRequest(getCookie("jwt_token"), environment.testdg_id)), 50));
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

  onclose(m: any) { }
  connect(source: string) {
    this.socket = new WebSocket(source);
    this.socket.parent = this;
    this.socket.onmessage = this.onmessage;
    this.socket.onopen = this.onopen;
    this.socket.onclose = this.onclose;
  }
  send(action: EditorAction) {
    action.user_id = this.service.getUser().id;
    console.log('sending', action);
    setTimeout(() => {
      this.socket.send(JSON.stringify(action));
    }, this.service.test.ping);
  }
  disconnect() { }
  addToInjectionQueue(id, type: TOKEN_TYPE, target: TARGET_TYPE, data) {
    console.log('data', data);
    this.service.addToInjectionQ(type, id, target, data);
  }
}
