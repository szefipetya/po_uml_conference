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
import com.szefi.uml_conference.socket.threads.SocketThreadManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
@Component
public class WebSocketHandler extends TextWebSocketHandler {
  
    private final List<WebSocketSession> sessions = new ArrayList<>();
   
    ObjectMapper mapper ;

   
    public WebSocketHandler() {
            System.out.print("hello im started");
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);                
       threadManager=new SocketThreadManager(sessions);
    threadManager.start();
        
       
    }

   
    SocketThreadManager threadManager;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
       // System.out.println(message);
        threadManager.post(mapper.readValue(message.getPayload(), EditorAction.class));
         System.out.println(message.getPayload());
       //  System.out.println("posted");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

    }
    int initCount=0;
 //@Inject
   /* public void setResponseQueue(BlockingQueue<EditorAction> responseQueue) {
        this.responseQueue = responseQueue;
        initCount++;
        initCheck();
    }
 //@Inject
    public void setActionQueue(BlockingQueue<EditorAction> actionQueue) {
        this.actionQueue = actionQueue;
            EditorAction t=new EditorAction();
        t.setJson("json");
        t.setId("id");
           actionQueue.add(t);
           initCount++;
                   initCheck();

    }

    private void initCheck() {
if(initCount>=2){
        threadManager=new SocketThreadManager(sessions,actionQueue,responseQueue);
    threadManager.start();
}
        }*/

}
