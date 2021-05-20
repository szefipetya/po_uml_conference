import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse
} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { mergeMap } from 'rxjs/operators';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor() {

  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (!environment.mockTestMode)
      return next.handle(request);
    console.log("url catched:" + request.urlWithParams);
    if (request.url.endsWith("register")) {
      return of(new HttpResponse({ body: { "username": "test1", "email": "test@test.com", "password": null, "fullName": "TestUser1" } }))
    }
    if (request.url.endsWith("login")) {
      return of(new HttpResponse({ body: { "jwt_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsImV4cCI6MTYyMTM4Mjg1MSwiaWF0IjoxNjIxMzQ2ODUxfQ.OW-f1CmKI8IS2gfwBDZWF3HAL26-Vx2yHA3t7ImtUHk", "success": true, "user": { "id": 8, "userName": "test1", "email": "test@test.com", "name": "TestUser1" } } }))
    }

  }
}
