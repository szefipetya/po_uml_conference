import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { FileManagerService } from 'src/app/components/left-panel-component/service/file-manager.service';
import { eraseCookie, getCookie } from 'src/app/utils/cookieUtils';
import { environment } from 'src/environments/environment';
import { AuthModule } from '../auth.module';
import { LogoutResponse } from "../models/LogoutResponse";
@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {

  constructor(private http: HttpClient,
    private _snackBar: MatSnackBar
    , private fmService: FileManagerService) {

  }



  openSnackBar(msg) {
    this._snackBar.open(msg, "", {
      duration: 2000,
    });
  }
  logOut() {

    let url = environment.api_url_http + 'log_me_out'
    console.log(url)//http://localhost:8101/logout
    //GET http://localhost:8101/login?logout //WTFFFF rossz helyre küldi.
    this.http
      .post<LogoutResponse>(url, { jwt_token: getCookie("jwt_token") },
        {
          headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
        })
      .pipe(catchError(this.handleError<LogoutResponse>(this, 'Logout', new LogoutResponse())))

      .subscribe((resp: any) => {
        if (resp?.success) { this.openSnackBar("Logout Succesful"); }
        else { this.openSnackBar(resp.msg); }
      })



    eraseCookie("jwt_token");
    eraseCookie("user");
    eraseCookie('dg_id')
    eraseCookie('session_jwt')
    eraseCookie('actual_folder')
    this.fmService.triggerEvent('logout')
  }


  private handleError<T>(service: AuthServiceService, operation = 'operation', result?: T) {
    return (error: HttpErrorResponse): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      //  console.log(`${operation} failed: ${error.message}`);
      this.openSnackBar(`Error (code: ${error.status}) ${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
