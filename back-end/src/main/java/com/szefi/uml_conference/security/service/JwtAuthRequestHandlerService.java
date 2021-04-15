/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.service;

import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.security.model.User_PublicDto;
import com.szefi.uml_conference.security.model.auth.AuthRequest;
import com.szefi.uml_conference.security.model.auth.AuthResponse;
import com.szefi.uml_conference.security.model.auth.LogoutResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 */
@Service
public class JwtAuthRequestHandlerService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtilService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;
    public LogoutResponse jwtLogout(String token){
      
    
       LogoutResponse r=new LogoutResponse();
     
    
        try {
              boolean l= jwtService.blackListToken(token);
              r.setSuccess(l);
            if(!l){
            r.setMsg("Jwt Token Expired ");
            }
        } catch (JwtParseException ex) {
            r.setSuccess(false);
            r.setMsg("Incorrect Jwt signature");
        }
            
       return r;
    }
    public AuthResponse jwtAuth(AuthRequest req) throws BadCredentialsException, Exception {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        final MyUserDetails userDetails = userDetailsService
                .loadUserByUsername(req.getUsername());
        

        final String jwt = jwtService.generateToken(userDetails);
        AuthResponse r=new AuthResponse(jwt);
        User_PublicDto user=new User_PublicDto();
        user.setName(userDetails.getName());
        user.setId(userDetails.getId());
        user.setUserName(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        r.setUser(user);
        r.setSuccess(true);
        return r;

    }
}
