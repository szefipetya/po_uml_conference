import { Component, OnInit } from '@angular/core';
import { getCookie } from 'src/app/utils/cookieUtils';
import { AuthServiceService } from "src/app/auth/services/auth-service.service";

@Component({
  selector: 'app-top-menu',
  templateUrl: './top-menu.component.html',
  styleUrls: ['./top-menu.component.scss']
})
export class TopMenuComponent implements OnInit {

  constructor(private service: AuthServiceService) {

  }
  isLoggedIn() {
    return JSON.parse(getCookie('user'))?.id && getCookie('jwt_token')
  }
  ngOnInit(): void {
  }
  logOut() {
    this.service.logOut();
  }


}
