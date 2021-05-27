package com.szefi.uml_conference.editor.service.socket.threads;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.debug.D;
import com.szefi.uml_conference.debug.DLEVEL;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.Response.EditorActionResponse;
import com.szefi.uml_conference.editor.model.socket.Response.RESPONSE_SCOPE;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocketWrapper;
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
public class ActionResponseProcessor extends CustomProcessor{
ObjectMapper mapper;
@Autowired
    public ActionResponseProcessor(
            @Qualifier("actionSockets") List<UserWebSocketWrapper> actionSessions,
             @Qualifier("actionResponseQueue") BlockingQueue<EditorActionResponse> actionResponseQueue) {
      this.actionResponseQueue=actionResponseQueue;
       // this.actionSessions=actionSessions;
            mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);      
    }

    BlockingQueue<EditorActionResponse> actionResponseQueue;
   //  private final List<UserWebSocket> actionSessions;
@Override
    public void run() {
        while(!isClosed){
            try{
               //  System.out.println("waiting for response item");
            EditorActionResponse response=null;
            try {
                response = actionResponseQueue.take();
            } catch (InterruptedException ex) {
                Logger.getLogger(ActionResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(response==null) continue;
          //  System.out.println("response taken");
          //  System.out.println(response.getAction().get);
           // System.out.println(response.getSessionState().getLockerUser_id());
           // System.out.println("----");
           
            for(UserWebSocketWrapper s:response.getTargetsUsers()){
                System.out.println(s.getUser_id());
                if(response.getScope()==RESPONSE_SCOPE.PUBLIC||s.getUser_id().equals(response.getTarget_user_id())){
                    try {
                         if(s.getActionSocket().isOpen()){
                        s.getActionSocket().sendMessage(new TextMessage(mapper.writeValueAsString(response)));
                        System.out.println("action response sent to user "+s.getUser_id());
                           if(response.getScope()==RESPONSE_SCOPE.PRIVATE) break;
                         }
                    } catch (IOException ex) {
                        Logger.getLogger(SessionStateResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                    }
            
            
        }
            }catch(Exception e){
                D.log("EditorActionResponseProcessor catched an exception:", this.getClass(), DLEVEL.DEBUG);
                e.printStackTrace();
            }
        }
    }
    
}
