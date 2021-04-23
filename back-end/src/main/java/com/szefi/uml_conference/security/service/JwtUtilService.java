/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.service;

import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.security.model.jwt.BlackListedJwtCollectorEntity;
import com.szefi.uml_conference.security.model.jwt.JwtEntity;
import com.szefi.uml_conference.security.repository.BlackJwtRepository;
import com.szefi.uml_conference.security.repository.BlackListedJwtCollectorEntityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 */
@Service
public class JwtUtilService {
      private String SECRET_KEY = "lmao_nice_secret";

    //  @Autowired
     // BlackListedJwtCollectorEntityRepository blackListRepo;
        @Autowired
        BlackJwtRepository blackJwtsRepo;
      
    public String extractUsername(String token) throws JwtParseException {
        try{
        return extractClaim(token, Claims::getSubject);
         }catch(Exception ex){
            throw new JwtParseException("jwt parse error");
        }
    }

    public Date extractExpiration(String token) throws JwtParseException {
        try{
        return extractClaim(token, Claims::getExpiration); }
        catch(Exception ex){
            throw new JwtParseException("jwt parse error");
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtParseException {
        try{
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); }catch(Exception ex){
            throw new JwtParseException("jwt parse error");
        }
    }
    public Claims extractAllClaims(String token) throws JwtParseException {
     
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(this.SECRET_KEY)
                    .parseClaimsJws(token);
            return claims.getBody();
                  
                        
                       // return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
                   
                    
     
   
    }

    public Boolean isTokenExpired(String token) throws JwtParseException {
     
        return extractExpiration(token).before(new Date());
       
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
     public String generateToken( Map<String, Object> claims,UserDetails userDetails) {
       
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
    
    public Boolean blackListToken(String token) throws JwtParseException{
        if(!isTokenExpired(token)){
            JwtEntity ent=new JwtEntity(token);
         blackJwtsRepo.save(ent);
         return true;
        }
       
       
      return false;
    }
    public Boolean validateToken(String token, UserDetails userDetails) throws JwtParseException {
        final String username = extractUsername(token);
          JwtEntity ent=blackJwtsRepo.findToken(token);
          //equals felül van írva String-re
        return (ent==null&&username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    
       
       @EventListener(ApplicationReadyEvent.class)
    private void init(){
       // blackListRepo.save(new BlackListedJwtCollectorEntity() );
    }
    
}
