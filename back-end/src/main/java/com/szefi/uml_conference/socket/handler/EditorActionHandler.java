/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.model.dto.diagram.Diagram;
import com.szefi.uml_conference.model.dto.socket.EditorAction;
import com.szefi.uml_conference.model.dto.socket.tech.UserWebSocket;
import com.szefi.uml_conference.socket.threads.SocketThreadManager;
import com.szefi.uml_conference.socket.threads.service.SOCKET;
import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
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
        for(UserWebSocket s:sessionService.getSockets(SOCKET.ACTION)){
          
            if(s.getSocket()==null||s.getSocket().equals(session)) { 
                
                sessionService.getSockets(SOCKET.ACTION).remove(s);
            break;
            }
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
     //init step to identify the session
  //  Random r=new Random();
   //  int num=r.nextInt(2500-1500)+1500;
    // Thread.sleep(2300);
        if(initMap.get(session)==0){
          for(UserWebSocket u:sessionService.getSockets(SOCKET.ACTION)){
              if(u.getSocket()==session){
                  u.setUser_id(message.getPayload());
                   System.out.println(initMap.get(session));
             System.out.println("action msg"+message.getPayload());
              }
          }
           
         initMap.replace(session,1);
    }
        else{
      threadManager.postAction(mapper.readValue(message.getPayload(), EditorAction.class));
         System.out.println(message.getPayload());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserWebSocket s=new UserWebSocket(session);
        sessionService.getSockets(SOCKET.ACTION).add(s);
        initMap.put(s.getSocket(), 0);
    }
  

}
