import { Injectable } from '@angular/core';
import { Action } from 'rxjs/internal/scheduler/Action';
import { AttributeElement } from 'src/app/components/models/DiagramObjects/AttributeElement';
/* import Stomp from '../../../../../js/webjars/stomp.js';
import SockJS from '../../../../../js/webjars/sockjs.min.js'; */
import { ACTION_TYPE } from '../../../../models/socket/ACTION_TYPE';
import { EditorAction } from '../../../../models/socket/EditorAction';
declare var SockJS: any;
declare var Stomp: any;
@Injectable({
  providedIn: 'root',
})
export class EditorSocketControllerService {
  constructor() {}
  setConnected(connected) {
    /*  $('#connect').prop('disabled', connected);
    $('#disconnect').prop('disabled', !connected);
    if (connected) {
      $('#conversation').show();
    } else {
      $('#conversation').hide();
    }
    $('#userinfo').html(''); */
  }
  socket;
  public connect() {
    this.socket = new WebSocket('ws://localhost:8080/test');
    var socket = this.socket;
    socket.onopen = (e) => {
      this.setConnected(true);
      console.log('Connected: ' + e);
    };
    socket.onmessage = (e: MessageEvent) => {
      console.log('message', e);
      let action: EditorAction;
      action = JSON.parse(e.data);
      console.log(action);
      let attr: AttributeElement = JSON.parse(action.json);
      console.log(attr);
    };
    socket.onclose = (e) => {
      console.log('closed', e);
    };

    /*  this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, function (frame) {
      this.setConnected(true);
      console.log('Connected: ' + frame);
      this.stompClient.subscribe('/topic/user', function (greeting) {
        this.showGreeting(JSON.parse(greeting.body).content);
      });
    });*/
  }
  public send(action: ACTION_TYPE, body) {}
  send_test() {
    let action: EditorAction = new EditorAction();
    action.id = 'nemtom';
    action.user_id = 'nemtom';

    action.action = ACTION_TYPE.CREATE;
    let newAttr: AttributeElement = {
      edit: true,
      id: 'id',
      visibility: 'visibility',
      name: 'name',
      type: 'type',
      viewModel: null,
    };
    action.json = JSON.stringify(newAttr);
    this.socket.send(JSON.stringify(action));
  }
  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    this.setConnected(false);
    console.log('Disconnected');
  }

  sendName() {
    this.stompClient.send('/app/user', {}, JSON.stringify({ name: 'Peti' }));
  }

  showGreeting(message) {
    // $('#userinfo').append('<tr><td>' + message + '</td></tr>');
  }

  stompClient = null;
}
