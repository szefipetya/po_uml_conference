/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.controller;

import com.szefi.uml_conference._exceptions.InvalidInputFormatException;
import com.szefi.uml_conference.editor.service.SocketSessionService;
import com.szefi.uml_conference.security.model.auth.AuthRequest;
import com.szefi.uml_conference.security.model.auth.LogoutRequest;
import com.szefi.uml_conference.security.model.auth.RegistrationRequest;
import com.szefi.uml_conference.security.service.JwtAuthRequestHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author h9pbcl
 */
@RestController
public class AuthController {
    
    
   @Autowired
   JwtAuthRequestHandlerService jwtAuthService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationJwt(@RequestBody AuthRequest req) throws Exception {
       try{
            return ResponseEntity.ok( jwtAuthService.jwtAuth(req));
       }catch(BadCredentialsException ex){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
       }catch(InvalidInputFormatException ex){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
       }
    }
    
        @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody RegistrationRequest req) throws Exception {
       try{
            return ResponseEntity.ok( jwtAuthService.register(req));
       }catch(BadCredentialsException ex){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
       }catch(InvalidInputFormatException ex){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
       }
    }
     @PostMapping("/log_me_out")//angular miatt van ez, bugos
    public ResponseEntity<?> logoutWithJwt(@RequestBody LogoutRequest token) throws Exception {
      try{
            return ResponseEntity.ok( jwtAuthService.jwtLogout(token.getJwt_token()));
       }catch(BadCredentialsException ex){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
       }
    
    }
}
