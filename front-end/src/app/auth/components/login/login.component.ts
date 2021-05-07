import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthRequest } from '../../models/authrequest'
import { AuthResponse } from '../../models/AuthResponse'
import { getCookie, setCookie } from "src/app/utils/cookieUtils";
import { environment } from 'src/environments/environment';
import { connectableObservableDescriptor } from 'rxjs/internal/observable/ConnectableObservable';
import { catchError, tap } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { GlobalEditorService } from 'src/app/components/full-page-components/editor/services/global-editor/global-editor.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(private http: HttpClient) { }
  responseMsg: string = "---";
  isLoading: boolean = false;
  ngOnInit(): void {
  }
  sendLogin(authReq: AuthRequest) {
    return this.http.post(environment.api_url_http + 'login', authReq, {
      observe: 'response'
    }).pipe(tap(
      (r) => { console.log(r) }
    )).pipe(
      catchError((err) => {
        return this.handleError(this, err);
      }) // then handle the error
    )
    console.log("req sent")

  }
  private handleError(component: LoginComponent, error: HttpErrorResponse) {
    this.isLoading = false;
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
      console.log(error.status)

      if (error.status == 403)
        component.responseMsg = "Error: Incorrect username or password"
      else if (error.status == 500) {
        component.responseMsg = "Error: Internal server exception"
      }
      else if (error.status == 404) {
        component.responseMsg = "Error: Server endpoint unreachable"
      } else if (error.status == 0) {
        component.responseMsg = "Error: Server unreachable"
      } else {
        component.responseMsg = error.error;
      }
    }
    // Return an observable with a user-facing error message.
    return throwError(
      'Something bad happened; please try again later.');
  }
  onSubmit(f: NgForm) {
    this.isLoading = true;
    let authReq = new AuthRequest(f.value.username, f.value.password);
    this.sendLogin(authReq).subscribe((r: any) => {
      console.log(r); this.isLoading = false;
      this.responseMsg = "Login Succesful";
      setCookie("jwt_token", r.body.jwt_token, 8);
      setCookie("user", JSON.stringify(r.body.user), 8);
    });;
  }
}
