import { Injectable } from '@angular/core';
import { LogInteractive_I } from 'src/app/components/models/socket/interface/LogInteractive_I';
import { soft_copy } from 'src/app/utils/utils';
import { SessionMessageWindowComponent } from 'src/app/components/windows/windowed-components/_instances/editor/session-message-window/session-message-window.component';
export enum MSG_TYPE {
  INFO = 'INFO',
  ERROR = 'ERROR',
  WARNING = 'WARNING',
}
class SessionMsg {
  constructor(type, msg, source) {
    this.source = source;
    this.type = type;
    this.msg = msg;
  }
  source: LogInteractive_I;
  type: MSG_TYPE;
  msg: string;
}

@Injectable({
  providedIn: 'root',
})
export class CommonService {
  constructor() {
    // for (let i = 0; i < 60; i++) this.putError('msg' + i);
  }
  private sessionLogQueue: SessionMsg[] = [];
  private msgView: SessionMessageWindowComponent;
  highlightSource(on, msg: SessionMsg) {
    msg.source.highlightMe(on, this.convert_typeToColor(msg.type));
  }

  putLog(str: string, type: MSG_TYPE, source?: LogInteractive_I) {
    this.sessionLogQueue.push(new SessionMsg(type, str, source));
    setTimeout(() => this.msgView.scrollToBottom(), 0);
  }
  registerMessageVisualizer(view: SessionMessageWindowComponent) {
    this.msgView = view;
  }
  getLatestLog(maxItems) {
    let i = 0;
    return this.sessionLogQueue.filter((k) => {
      i = i + 1;
      return i < maxItems;
    });
  }

  convert_typeToColor(type: MSG_TYPE): string {
    switch (type) {
      case MSG_TYPE.INFO:
        return 'aqua';
      case MSG_TYPE.ERROR:
        return 'crimson';
      case MSG_TYPE.WARNING:
        return 'orange';
    }
  }
}
