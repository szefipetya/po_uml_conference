/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.service.filters;

import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author h9pbcl
 */

@Component
public class JwtAuthRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtilService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException,UsernameNotFoundException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt_token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt_token = authorizationHeader.substring(7);
            try {
                username = jwtService.extractUsername(jwt_token);
            } catch (JwtParseException ex) {
             //   Logger.getLogger(JwtAuthRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
                //  response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            try {
                if (jwtService.validateToken(jwt_token, userDetails)) {
                    
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (JwtParseException ex) {
              //  Logger.getLogger(JwtAuthRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
                response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}