/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.service;

import com.szefi.uml_conference._exceptions.InvalidInputFormatException;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.security.model.ROLE;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.model.User_PublicDto;
import com.szefi.uml_conference.security.model.auth.AuthRequest;
import com.szefi.uml_conference.security.model.auth.AuthResponse;
import com.szefi.uml_conference.security.model.auth.LogoutResponse;
import com.szefi.uml_conference.security.model.auth.RegistrationRequest;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
final String EMAIL_REG = "^[A-Za-z0-9+_.-]+@.+\\..+";
final String USERNAME_REG = "^[a-zA-Z0-9\\._-]+$";
final String NAME_REG = "^[a-zA-Z ]+$";
final String PASSWORD_REG = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,}$";
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
   String errorStr="";
         if(!validate(USERNAME_REG,req.getUsername()))errorStr+="username format is not valid. Make sure, it only contains alphanumeric characters and [-._] characters\n\n";// throw new InvalidInputFormatException("username format is not valid.\n Make sure, it only contains alphanumeric characters and [-._] characters");
           if(!validate(PASSWORD_REG,req.getPassword())) errorStr+="Password format is not valid (minimum 4 characters, must contain at least one number and letter)\n\n";//throw new InvalidInputFormatException("Password is too Short");
         if(!errorStr.equals("")) throw new InvalidInputFormatException(errorStr);
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
    boolean validate(String regex,String input){
        Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);
   return matcher.matches();
    }
     public RegistrationRequest register(RegistrationRequest req) throws BadCredentialsException, Exception,InvalidInputFormatException {
         
         String errorStr="";
         StringBuilder builder=new StringBuilder("");
        
         if(!validate(USERNAME_REG,req.getUsername())) builder.append("username format is not valid. Make sure, it only contains alphanumeric characters and [-._] characters\n\n");// throw new InvalidInputFormatException("username format is not valid.\n Make sure, it only contains alphanumeric characters and [-._] characters");
         if(!validate(EMAIL_REG,req.getEmail())) builder.append("email format is not valid \n\n");// throw new InvalidInputFormatException("email format is not valid ");
      //   if(req.getFullName().length()<1)builder.append("You didn't fill out the Name cell"); //throw new InvalidInputFormatException("Name format is not valid (only Letters)");
         if(!validate(PASSWORD_REG,req.getPassword())) builder.append("Password format is not valid (minimum 4 characters, must contain at least one number and letter)\n");//throw new InvalidInputFormatException("Password is too Short");
         if(!builder.toString().equals("")) throw new InvalidInputFormatException(builder.toString());
      /*  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );*/

      /*  final MyUserDetails userDetails = userDetailsService
                .loadUserByUsername(req.getUsername());*/
              if(!userDetailsService.validateRegistration(req) )throw new BadCredentialsException("username or email have been already taken by another user.");
        UserEntity newUser=new UserEntity();
        newUser.setName(req.getFullName());
        newUser.setPassword(req.getPassword());
        newUser.setUserName(req.getUsername());
        newUser.setEmail(req.getEmail());
          newUser.setRoles(new ArrayList<ROLE>() {{
                add(ROLE.ROLE_USER);
           }});
        userDetailsService.registerUser(newUser);

       /* final String jwt = jwtService.generateToken(userDetails);
        AuthResponse r=new AuthResponse(jwt);
        User_PublicDto user=new User_PublicDto();
        user.setName(userDetails.getName());
        user.setId(userDetails.getId());
        user.setUserName(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        
        r.setUser(user);
        r.setSuccess(true);*/
        return req;

    }
    
}
