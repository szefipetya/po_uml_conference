import { Component, OnInit } from '@angular/core';
import { EditorSocketControllerService } from 'src/app/components/full-page-components/editor/services/editor-socket-controller/editor-socket-controller.service';
import { WindowComponent } from '../../../window/window.component';

@Component({
  selector: 'app-social-window',
  templateUrl: './social-window.component.html',
  styleUrls: ['./social-window.component.scss']
})
export class SocialWindowComponent extends WindowComponent {
  socketService;
  constructor(socketService: EditorSocketControllerService) {
    super();
    this.socketService = socketService;
  }

  ngOnInit(): void { }

}
