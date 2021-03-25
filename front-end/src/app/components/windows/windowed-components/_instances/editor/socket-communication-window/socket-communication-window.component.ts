import { Component, OnInit } from '@angular/core';
import { WindowComponent } from '../../../window/window.component';
import { EditorSocketControllerService } from '../../../../../full-page-components/editor/services/editor-socket-controller/editor-socket-controller.service';
@Component({
  selector: 'app-socket-communication-window',
  templateUrl: './socket-communication-window.component.html',
  styleUrls: ['./socket-communication-window.component.scss'],
})
export class SocketCommunicationWindowComponent
  extends WindowComponent
  implements OnInit {
  constructor(public socketService: EditorSocketControllerService) {
    super();
  }
  connect(l) {
    if (l) {
      this.socketService.connect();
    } else {
      this.socketService.disconnect();
    }
  }
  test() {}
  changePing(e) {
    this.socketService.test.changePing(e.target.value);
  }
  ngOnInit(): void {}
}
