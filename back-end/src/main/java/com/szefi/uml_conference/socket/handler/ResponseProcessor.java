/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.model.dto.socket.EditorAction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author h9pbcl
 * @param <T>
 */
public class ResponseProcessor extends CustomProcessor{
ObjectMapper mapper;
    public ResponseProcessor(List<WebSocketSession> sessions,BlockingQueue<EditorAction> responseQueue) {
      this.responseQueue=responseQueue;
        this.sessions=sessions;
            mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);      
    }

    BlockingQueue<EditorAction> responseQueue;
     private final List<WebSocketSession> sessions;
    public void run() {
        while(!isClosed){
               System.out.println("waiting to this response:");    
            EditorAction action=null;
            try {
                action = responseQueue.take();
            } catch (InterruptedException ex) {
                Logger.getLogger(ResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(action==null) continue;
            System.out.println("responding to this:");         
            System.out.println(action.getJson());         
            for(WebSocketSession s:sessions){
                try {
                                System.out.println("sending msg to a user");         
                    
                    s.sendMessage(new TextMessage( mapper.writeValueAsString(action)));
                } catch (IOException ex) {
                    Logger.getLogger(ResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
