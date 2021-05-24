import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { getCookie } from 'src/app/utils/cookieUtils';
import { Pair } from 'src/app/utils/utils';
import { endP, environment } from 'src/environments/environment';
import { FileShareRequest } from '../../models/management/request/FileShareRequest';
import { FileResponse } from '../../models/management/response/FileResponse';
import { LeftPanelComponentComponent } from '../left-panel-component.component';

@Injectable(
)
export class FileManagerService {
  errorMsg: string;

  constructor(private http: HttpClient) { }
  public getFile(id, _type): Observable<any> {
    return this.http
      .get<FileResponse>(environment.api_url_http + endP.management + _type + '/' + id, {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      });

  }
  public shareFile(req: FileShareRequest): Observable<any> {
    return this.http
      .post<FileResponse>(environment.api_url_http + endP.management + endP.share, req, {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      });
  }

  setActualProjectFolder(r: any) {
    throw new Error('Method not implemented.');
  }
  setActualFolder(r: any) {
    throw new Error('Method not implemented.');
  }

  public deleteFile(id, _type): Observable<any> {
    console.log('deleting')
    return this.http
      .delete<FileResponse>(environment.api_url_http + endP.management + _type + '/' + id, {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      })
  }
  eventListenerFunctions: Pair<Pair<string, any>, Function>[] = [];

  public triggerEvent(wich: string) {

    this.eventListenerFunctions.map((p) => {
      if (p.key.key == wich)
        p.value(p.key.value);
    });

  }
  addListenerToEvent(target, fn, alias: string = '') {
    this.eventListenerFunctions.push(new Pair(new Pair(alias, target), fn));
  }
}
