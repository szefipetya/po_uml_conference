/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.security;

import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import com.szefi.uml_conference.socket.security.model.SocketAuthenticationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 * Authentikálja a user-t az auth_jwt alapján.
 * Authorizálás után eldönti, hogy van-e jogosultsága a kért diagram-hoz tartozó sessionba becsatlakozni.
 */
@Service
public class SocketSecurityService {
    /**
        @return returns a new session_jwt, if the auth was succesful.
    */
    @Autowired 
    JwtUtilService jwtService;
    @Autowired
    MyUserDetailsService userDetService;
    @Autowired
    DiagramRepository diagramRepository;
    //test:rossz id-t megadni.
        public MyUserDetails authenticateRequest(SocketAuthenticationRequest req) throws JwtParseException{
            MyUserDetails userDets=  userDetService.loadUserByUsername(jwtService.extractUsername(req.getAuth_jwt()));
            if(jwtService.validateToken(req.getAuth_jwt(),userDets)){
                DiagramEntity ent= diagramRepository.findById(req.getDiagram_id()).get();
               if(ent!=null
                       &&ent.getOwner()!=null
                       &&(ent.getOwner().getId().equals(userDets.getId())
                       ||ent.getUsersIamSaredWith().stream().anyMatch(u->u.getId().equals(userDets.getId()))
                       ))
                    return userDets;    
            }
            return null;
        }
}
