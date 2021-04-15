/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.service;

import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.security.model.jwt.BlackListedJwtCollectorEntity;
import com.szefi.uml_conference.security.model.jwt.JwtEntity;
import com.szefi.uml_conference.security.repository.BlackListedJwtCollectorEntityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 */
@Service
public class JwtUtilService {
      private String SECRET_KEY = "lmao_nice_secret";

      @Autowired
      BlackListedJwtCollectorEntityRepository blackListRepo;
      
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
    private Claims extractAllClaims(String token) throws JwtParseException {
        try{
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        }catch(Exception ex){
            throw new JwtParseException("jwt parse error");
        }
        
    }

    private Boolean isTokenExpired(String token) throws JwtParseException {
        try{
        return extractExpiration(token).before(new Date());
         }catch(Exception ex){
            throw new JwtParseException("jwt parse error");
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
    
    public Boolean blackListToken(String token) throws JwtParseException{
        if(!isTokenExpired(token)){
        BlackListedJwtCollectorEntity collection=blackListRepo.getBlackList();
        collection.getJwts().add(new JwtEntity(token));
         blackListRepo.save(collection);
         return true;
        }
       
       
      return false;
    }
    public Boolean validateToken(String token, UserDetails userDetails) throws JwtParseException {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
}
