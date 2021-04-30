/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.socket.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.D;
import com.szefi.uml_conference.editor.model.diagram.Diagram;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocket;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.editor.socket.security.SocketSecurityService;
import com.szefi.uml_conference.editor.socket.security.model.SocketAuthenticationRequest;
import com.szefi.uml_conference.editor.socket.threads.SocketThreadManager;
import com.szefi.uml_conference.editor.socket.threads.service.SOCKET;
import com.szefi.uml_conference.editor.service.SocketSessionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import javax.annotation.Resource;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author h9pbcl
 */

public class EditorActionHandler extends TextWebSocketHandler {

    @Autowired
    SocketSessionService sessionService;
    
    @Autowired SocketSecurityService socketSecutiryService;
    
    @Autowired SocketThreadManager threadManager;
    Map<WebSocketSession,Integer> initMap=new HashMap<>();
    ObjectMapper mapper ;
    public EditorActionHandler() {
            System.out.print("hello im started");
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
       
       /*for(UserWebSocket s:sessionService.getSockets(SOCKET.ACTION)){
          
            if(s.getSocket()==null||s.getSocket().equals(session)) { 
                
                sessionService.getSockets(SOCKET.ACTION).remove(s);
            break;
            }
        }*/
    }
    
    /*
    Az action socketen authentikálja magát a user. megkapja a session jwt-t. ezt a jwt-t elküldi a user a session socketre. Majd a statesockettel kiegészíti az EditorSessin-ban a usert.
    */
    List<UserWebSocket>tempSockets=new ArrayList<>();
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
     //init step to identify the session
        if(initMap.get(session)==0){
          for(UserWebSocket u:this.tempSockets){
              if(u.getActionSocket().equals(session)){
                 D.log("user authenticating with "+message.getPayload(),this.getClass());
                  SocketAuthenticationRequest req=mapper.readValue(message.getPayload(), SocketAuthenticationRequest.class);
                  MyUserDetails userDets=socketSecutiryService.authenticateRequest(req);
                  if(userDets!=null){
                    this.sessionService.autoProcessRequest(socketSecutiryService.authenticateRequest(req),u,req);  u.getActionSocket().sendMessage(new TextMessage(u.getSession_jwt()));
                    System.out.println("user authenticated");
                  }
                  else{
                      D.log("user authorization failed: access blocked.",this.getClass());
                  }
   
                   System.out.println(initMap.get(session));
             System.out.println("action msg"+message.getPayload());
              }
          }
         initMap.replace(session,1);
    }
        else{
            //handle message
      threadManager.postAction(mapper.readValue(message.getPayload(), EditorAction.class));
         System.out.println(message.getPayload());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserWebSocket s=new UserWebSocket();
        
        s.setActionSocket(session);
        tempSockets.add(s);
        initMap.put(s.getActionSocket(), 0);
    }
  

}
