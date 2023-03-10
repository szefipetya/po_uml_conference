package com.szefi.uml_conference.editor.service.socket.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.service.socket.threads.ActionResponseProcessor;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.service.SocketSessionService;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.NotFoundException;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

/**
 *
 * @author h9pbcl

 */
@Component
@Scope("prototype")
public class SessionInitProcessor extends CustomProcessor {
  
    ObjectMapper mapper;
    
    SocketSessionService service;
    @Autowired
    public SessionInitProcessor(
            
            SocketSessionService socketService,
           @Qualifier("initQueue")  BlockingQueue<EditorAction> initQueue
           
    ){
      this.actionQueue=actionQueue;
     //  this.adcionResponseQueue=adcionResponseQueue;
       this.sessionStateResponseQueue=sessionStateResponseQueue;
       this.service=socketService;
        mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
    }
  
  //  private  BlockingQueue<EditorAction> adcionResponseQueue=null;
    private  BlockingQueue<SessionStateResponse> sessionStateResponseQueue=null;
  
    private  BlockingQueue<EditorAction> actionQueue=null;
  
    @Override
    public void run() {
        while(!isClosed){
            
                 System.out.println("waiting this:");  
                EditorAction action=null;
               try {
                action  = actionQueue.take();
                 System.out.println("stuff taken"); 
            } catch (InterruptedException ex) {
                Logger.getLogger(ActionResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
                 if(action==null) continue;
                System.out.println("processing this:");         
                if(action.getAction()==ACTION_TYPE.SELECT){
                    System.out.println(action.getTarget().getTarget_id());
                     try {
                         System.out.println(service.tokenToSession(action.getSession_jwt()).isLockedById(action.getTarget().getTarget_id()));
                     } catch (JwtParseException ex) {
                         Logger.getLogger(SessionInitProcessor.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (NotFoundException ex) {
                         Logger.getLogger(SessionInitProcessor.class.getName()).log(Level.SEVERE, null, ex);
                     }
                 
                  try{
                 if( service.tokenToSession(action.getSession_jwt()).lockObjectById(action.getTarget().getTarget_id(), action.getUser_id(),new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT,LOCK_TYPE.NO_MOVE})){
                     //the object is free
                     SessionStateResponse resp=new SessionStateResponse(service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()), action.getId());       
                     System.out.println(resp.getSessionState().getLockerUser_id());
                     resp.setTarget_id(action.getTarget().getTarget_id());
                     sessionStateResponseQueue.add(resp);
                     System.out.println("object is free, putted on the response queue");

                 }else{
                     //TEMP T??T??LNI MAJD
                   
                     System.out.println("object is locked");
                     //Locked
                 }
                  }catch(Exception e){
                     // System.out.println(e.getMessage());
                      e.printStackTrace();
                      System.out.println(action.getTarget().getTarget_id());
                  }
                          
            
                }else if(action.getAction()==ACTION_TYPE.UPDATE){
                    
                
                        
                         
                     try { 
                         if( service.tokenToSession(action.getSession_jwt()).unLockObjectById(action.getTarget().getTarget_id(), action.getUser_id())){
                             
                             //  Pair<SessionState,DynamicSerialObject> result=  service.updateObjectAndUnlock(mapper.readValue(action.getJson(),DynamicSerialObject.class));
                              
                             //the object is free
                             /*   SessionStateResponse resp=new SessionStateResponse(
                             service.getSessionStateById(action.getTarget().getTarget_id())
                             , action_id);
                             sessionStateResponseQueue.add(resp);*/
                             //TODO itt m??r EditorActionResponse-t kell vissza adni.
                             //+ egy sessionstate v??ltoz??st.
                             System.out.println("object is free, putted on the response queue");
                             
                         }else{
                             //Can not update
                         }
                     } catch (JwtParseException ex) {
                         Logger.getLogger(SessionInitProcessor.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (NotFoundException ex) {
                         Logger.getLogger(SessionInitProcessor.class.getName()).log(Level.SEVERE, null, ex);
                     }
                        
                }
            
        }
    }
}
    

