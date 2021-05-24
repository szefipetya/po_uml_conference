import { Component, OnInit } from '@angular/core';
import { getCookie } from 'src/app/utils/cookieUtils';
import { AuthServiceService } from "src/app/auth/services/auth-service.service";
import { GlobalEditorService } from '../full-page-components/editor/services/global-editor/global-editor.service';

@Component({
  selector: 'app-top-menu',
  templateUrl: './top-menu.component.html',
  styleUrls: ['./top-menu.component.scss']
})
export class TopMenuComponent implements OnInit {

  constructor(private service: AuthServiceService, private editorService: GlobalEditorService) {

  }
  isLoggedIn() {
    return JSON.parse(getCookie('user'))?.id && getCookie('jwt_token')
  }
  ngOnInit(): void {
  }
  onEditorClick() {
    this.editorService.initFromServer(getCookie('dg_id'));

  }
  logOut() {
    this.service.logOut();
  }


}
