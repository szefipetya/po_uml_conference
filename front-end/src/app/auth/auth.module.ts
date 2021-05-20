import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthRoutingModule } from './auth-routing.module';
import { LoginComponent } from "./components/login/login.component";
import { RegisterComponent } from "./components/register/register.component";
import { ResetPasswordComponent } from "./components/reset-password/reset-password.component";
import { FormsModule } from '@angular/forms';
//Material
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from "../testing/auth.interceptor";


@NgModule({
  declarations: [LoginComponent, RegisterComponent, ResetPasswordComponent],
  imports: [
    CommonModule,
    AuthRoutingModule, FormsModule, MatSnackBarModule
  ],
  exports: [LoginComponent, RegisterComponent, ResetPasswordComponent],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
export class AuthModule {


}
