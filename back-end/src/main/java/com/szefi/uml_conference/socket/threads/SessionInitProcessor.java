package com.szefi.uml_conference.socket.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.socket.threads.ActionResponseProcessor;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                    System.out.println(service.isLockedById(action.getTarget().getTarget_id()));
                 
                  try{
                 if( service.lockObjectById(action.getTarget().getTarget_id(), action.getUser_id(),new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT,LOCK_TYPE.NO_MOVE})){
                     //the object is free
                     SessionStateResponse resp=new SessionStateResponse(service.getSessionStateById(action.getTarget().getTarget_id()), action.getId());       
                     System.out.println(resp.getSessionState().getLockerUser_id());
                     resp.setTarget_id(action.getTarget().getTarget_id());
                     sessionStateResponseQueue.add(resp);
                     System.out.println("object is free, putted on the response queue");

                 }else{
                     //TEMP TÖTÖLNI MAJD
                   
                     System.out.println("object is locked");
                     //Locked
                 }
                  }catch(Exception e){
                     // System.out.println(e.getMessage());
                      e.printStackTrace();
                      System.out.println(action.getTarget().getTarget_id());
                  }
                          
            
                }else if(action.getAction()==ACTION_TYPE.UPDATE){
                    
                
                        
                         
                         if( service.unLockObjectById(action.getTarget().getTarget_id(), action.getUser_id())){ 
                             
                          //  Pair<SessionState,DynamicSerialObject> result=  service.updateObjectAndUnlock(mapper.readValue(action.getJson(),DynamicSerialObject.class));
                              
                             //the object is free
                          /*   SessionStateResponse resp=new SessionStateResponse(
                                     service.getSessionStateById(action.getTarget().getTarget_id())
                                     , action_id);
                             sessionStateResponseQueue.add(resp);*/
                          //TODO itt már EditorActionResponse-t kell vissza adni.
                          //+ egy sessionstate változást.
                             System.out.println("object is free, putted on the response queue");
                             
                         }else{
                             //Can not update
                         }
                        
                }
            
        }
    }
}
    

