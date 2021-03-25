/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import com.szefi.uml_conference.model.dto.diagram.Diagram;
import com.szefi.uml_conference.model.dto.socket.EditorAction;
import com.szefi.uml_conference.model.dto.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.socket.tech.UserWebSocket;
import com.szefi.uml_conference.socket.threads.SocketThreadManager;
import com.szefi.uml_conference.socket.threads.service.SOCKET;
import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import javax.annotation.Resource;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author h9pbcl
 */

public class SessionStateHandler extends TextWebSocketHandler {
    
    @Autowired
    SocketSessionService service;
      @Autowired SocketThreadManager threadManager;
    Map<WebSocketSession,Integer> initMap=new HashMap<>();
    ObjectMapper mapper ;
    
      @Autowired
     @Qualifier("sessionStateResponseQueue")
    private  BlockingQueue<SessionStateResponse> sessionStateResponseQueue;
    
    public SessionStateHandler() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        for(UserWebSocket s:service.getSockets(SOCKET.STATE)){
            if(s.getSocket()==null||s.getSocket().equals(session)) { 
             
                 for( Map.Entry<String,Pair<SessionState,DynamicSerialObject>> e : service.getSessionItemMap().entrySet()){
                     if(s.getUser_id().equals(e.getValue().getFirst().getLockerUser_id()==null?"":e.getValue().getFirst().getLockerUser_id())){
                         if(service.unLockObjectById(e.getKey(), s.getUser_id())){
                             System.out.println("unlock success"+e.getKey());
                         }else{
                             System.out.println("unlock false");
                         }
                         
                         SessionStateResponse resp = new SessionStateResponse(
                                 e.getValue().getFirst(),"");
                         resp.setTarget_user_id(s.getUser_id());
                         resp.setTarget_id(e.getKey());
                         sessionStateResponseQueue.add(resp);
                     } else {
                     }
    }
               
                   service.getSockets(SOCKET.STATE).remove(s);
                   
            break;
            }
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
     //init step to identify the session
    if(initMap.get(session)==0){
          for(UserWebSocket u:service.getSockets(SOCKET.STATE)){
              if(u.getSocket()==session){
                  u.setUser_id(message.getPayload());
                   System.out.println(initMap.get(session));
             System.out.println("session msg"+message.getPayload());
              }
          }
           
         initMap.replace(session,1);
    }
      else{
      //  threadManager.postAction(mapper.readValue(message.getPayload(), EditorAction.class));
         System.out.println("session msg"+message.getPayload());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserWebSocket s=new UserWebSocket(session);
        service.getSockets(SOCKET.STATE).add(s);
        initMap.put(s.getSocket(), 0);
        
        
        
        Map<String,Pair<SessionState,DynamicSerialObject>> a=service.getSessionItemMap();
        List<SessionStateResponse> responses=new ArrayList<>();
        for(Map.Entry<String,Pair<SessionState,DynamicSerialObject>> entry:a.entrySet()){
            SessionStateResponse r= new SessionStateResponse(entry.getValue().getFirst(),"no action");
            r.setTarget_id(entry.getKey());
          //  System.out.println(entry.getValue().getFirst().getLocks().length);
          //  System.out.println(entry.getKey());
            responses.add(r);
        }
        session.sendMessage(new TextMessage(mapper.writeValueAsString(responses)));
    }
  

}
