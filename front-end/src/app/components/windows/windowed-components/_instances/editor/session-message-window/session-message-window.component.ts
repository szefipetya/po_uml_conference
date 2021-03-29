import {
  AfterViewChecked,
  Component,
  ElementRef,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import {
  CommonService,
  MSG_TYPE,
} from 'src/app/components/full-page-components/editor/services/common/common.service';
import { WindowComponent } from '../../../window/window.component';
@Component({
  selector: 'app-session-message-window',
  templateUrl: './session-message-window.component.html',
  styleUrls: ['./session-message-window.component.scss'],
})
export class SessionMessageWindowComponent
  extends WindowComponent
  implements OnInit {
  @ViewChild('log_cont') private log_cont: ElementRef;
  constructor(public commonService: CommonService) {
    super();
  }

  onLoghover(item): void {
    this.commonService.highlightSource(true, item);
  }
  onLoghoverEnd(item): void {
    this.commonService.highlightSource(false, item);
  }
  ngOnInit(): void {
    console.log(this.model);
    this.commonService.registerMessageVisualizer(this);
  }

  scrollToBottom(): void {
    try {
      this.log_cont.nativeElement.scrollTop = this.log_cont.nativeElement.scrollHeight;
    } catch (err) {}
  }
}
