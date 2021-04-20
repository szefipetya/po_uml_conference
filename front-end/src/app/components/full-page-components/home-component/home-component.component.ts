import { Component, OnInit } from '@angular/core';
import { getCookie } from 'src/app/utils/cookieUtils';

@Component({
  selector: 'app-home-component',
  templateUrl: './home-component.component.html',
  styleUrls: ['./home-component.component.scss']
})
export class HomeComponentComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }
  isLoggedIn() {
    return getCookie("jwt_token");
  }
  getUser_Name() {
    return JSON.parse(getCookie("user"))?.name;
  }
}
