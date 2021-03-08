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
  constructor(private socketService: EditorSocketControllerService) {
    super();
  }
  connect(l) {
    if (l) {
      this.socketService.connect();
    } else {
      this.socketService.disconnect();
    }
  }

  ngOnInit(): void {}
}
