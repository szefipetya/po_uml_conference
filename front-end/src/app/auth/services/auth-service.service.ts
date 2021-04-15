import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { eraseCookie, getCookie } from 'src/app/utils/cookieUtils';
import { environment } from 'src/environments/environment';
import { LogoutResponse } from "../models/LogoutResponse";
@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {

  constructor(private http: HttpClient, private _snackBar: MatSnackBar) {
    environment.api_url_http
  }



  openSnackBar(msg) {
    this._snackBar.open(msg, "", {
      duration: 2000,
    });
  }
  logOut() {
    console.log(environment.api_url_http + 'logout')

    this.http
      .post<LogoutResponse>(environment.api_url_http + 'logout', getCookie("jwt_token"), {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      }).subscribe((resp) => {
        if (resp.success) { this.openSnackBar("Logout Succesful"); }
        else { this.openSnackBar(resp.msg); }
      })



    eraseCookie("jwt_token");
    eraseCookie("user");

  }
}
