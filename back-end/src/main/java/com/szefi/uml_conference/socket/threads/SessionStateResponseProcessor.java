package com.szefi.uml_conference.socket.threads;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.model.dto.socket.EditorAction;
import com.szefi.uml_conference.model.dto.socket.Response.RESPONSE_SCOPE;
import com.szefi.uml_conference.model.dto.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.socket.tech.UserWebSocket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author h9pbcl
 * @param <T>
 */
@Component
@Scope("prototype")
public class SessionStateResponseProcessor extends CustomProcessor{
ObjectMapper mapper;
@Autowired
    public SessionStateResponseProcessor(
            @Qualifier("stateSockets") List<UserWebSocket> sessions,
             @Qualifier("sessionStateResponseQueue") BlockingQueue<SessionStateResponse> stateResponseQueue) {
      this.stateResponseQueue=stateResponseQueue;
        this.sessions=sessions;
            mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);      
    }

    BlockingQueue<SessionStateResponse> stateResponseQueue;
     private final List<UserWebSocket> sessions;
@Override
    public void run() {
        while(!isClosed){
                  System.out.println("waiting for response item");
            SessionStateResponse response=null;
            try {
                response = stateResponseQueue.take();
            } catch (InterruptedException ex) {
                Logger.getLogger(ActionResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(response==null) continue;
            System.out.println("response taken");
            System.out.println(response.getAction_id());
          //  System.out.println(response.getSessionState().getLockerUser_id());
            System.out.println("----");
           
            for(UserWebSocket s:sessions){
                System.out.println(s.getUser_id());
                if(response.getScope()==RESPONSE_SCOPE.PUBLIC||s.getUser_id().equals(response.getTarget_user_id())){
                    try {
                        if(s.getSocket().isOpen()){
                        s.getSocket().sendMessage(new TextMessage(mapper.writeValueAsString(response)));
                        System.out.println("stateresponse sent to user "+s.getUser_id());
                            if(response.getScope()==RESPONSE_SCOPE.PRIVATE) break;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SessionStateResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                    }
            }
                
                
           /*     try {
            System.out.println("sending msg to a user");         
                    
                //    s.getSocket().sendMessage(new TextMessage( mapper.writeValueAsString(action)));
                } catch (IOException ex) {
                    Logger.getLogger(ActionResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            
        }
    }
    
}
