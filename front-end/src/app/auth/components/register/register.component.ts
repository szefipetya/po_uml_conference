import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { setCookie } from 'src/app/utils/cookieUtils';
import { environment } from 'src/environments/environment';
import { AuthRequest } from '../../models/authrequest';
import { RegistrationRequest } from '../../models/RegistrationRequest';
import { LoginComponent } from '../login/login.component';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  constructor(private http: HttpClient) { }
  responseMsg: string = "---";
  isLoading: boolean = false;
  ngOnInit(): void {
  }
  sendRegister(authReq: RegistrationRequest) {



    return this.http.post(environment.api_url_http + 'register', authReq, {
      observe: 'response'
    }).pipe(tap(
      (r) => { console.log(r) }
    )).pipe(
      catchError((err) => {
        return this.handleError(this, err);
      }) // then handle the error
    )


  }
  private handleError(component: RegisterComponent, error: HttpErrorResponse) {
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
        component.responseMsg = error.error
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
  validate(req: RegistrationRequest, pw2) {

    if (pw2 !== req.password) {
      this.responseMsg = "Passwords does not match";
      return false;
    }
    return true;
  }
  onSubmit(f: NgForm) {

    let authReq = new RegistrationRequest(f.value.username.trim(), f.value.name.trim(), f.value.email.trim(), f.value.password);
    if (this.validate(authReq, f.value.password2)) {
      this.isLoading = true;
      this.sendRegister(authReq).subscribe((r: any) => {
        console.log(r); this.isLoading = false;
        if (r.status == 200) {
          this.responseMsg = "Registration for " + r.body.username + " succesful"
        }
        //  setCookie("jwt_token", r.body.jwt_token, 8);
        // setCookie("user", JSON.stringify(r.body.user), 8);
      });;

    }


  }

}
