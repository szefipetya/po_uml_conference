import { Injectable } from '@angular/core';
/* import Stomp from '../../../../../js/webjars/stomp.js';
import SockJS from '../../../../../js/webjars/sockjs.min.js'; */

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

  public connect() {
    var socket = new WebSocket('ws://localhost:8080/test');
    socket.onopen = (e) => {
      this.setConnected(true);
      console.log('Connected: ' + e);
    };
    socket.onmessage = (e) => {
      console.log('message', e);
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
