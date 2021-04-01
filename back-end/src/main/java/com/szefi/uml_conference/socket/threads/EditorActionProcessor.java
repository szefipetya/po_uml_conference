/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.model.dto.do_related.AttributeElement;
import com.szefi.uml_conference.model.dto.do_related.SimpleClass;
import com.szefi.uml_conference.model.dto.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import com.szefi.uml_conference.socket.threads.ActionResponseProcessor;
import com.szefi.uml_conference.model.dto.socket.ACTION_TYPE;
import com.szefi.uml_conference.model.dto.socket.EditorAction;
import com.szefi.uml_conference.model.dto.socket.LOCK_TYPE;
import com.szefi.uml_conference.model.dto.socket.Response.EditorActionResponse;
import com.szefi.uml_conference.model.dto.socket.Response.RESPONSE_SCOPE;
import com.szefi.uml_conference.model.dto.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.model.dto.socket.Response.TARGET_TYPE;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
import java.util.HashMap;
import java.util.List;
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
 *
 */
@Component
@Scope("prototype")
public class EditorActionProcessor extends CustomProcessor {

    ObjectMapper mapper;

    SocketSessionService service;

    @Autowired
    public EditorActionProcessor(
            SocketSessionService socketService,
            @Qualifier("actionQueue") BlockingQueue<EditorAction> actionQueue,
            @Qualifier("sessionStateResponseQueue") BlockingQueue<SessionStateResponse> sessionStateResponseQueue,
             @Qualifier("actionResponseQueue") BlockingQueue<EditorActionResponse> actionResponseQueue
    ) {
        this.actionQueue = actionQueue;
        this.actionResponseQueue = actionResponseQueue;
        this.sessionStateResponseQueue = sessionStateResponseQueue;
        this.service = socketService;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    private BlockingQueue<EditorActionResponse> actionResponseQueue = null;
    private BlockingQueue<SessionStateResponse> sessionStateResponseQueue = null;

    private BlockingQueue<EditorAction> actionQueue = null;

    @Override
    public void run() {
        while (!isClosed) {
            EditorAction action = null;
            try {
                action = actionQueue.take();
                System.out.println("stuff taken");
            } catch (InterruptedException ex) {
                Logger.getLogger(ActionResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            switch (action.getAction()) {
            
                case SELECT:    //---------------------------------------------------
                        try {
                    //ha az első feltétel nem teljesül, akkor a lusta kiértékelés miatt a feltétel le se fut
                    if (service.lockObjectById(action.getTarget().getTarget_id(), action.getUser_id(),
                            new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE})) {
                        //the object is free
                        sendAll(action, Q.STATE);
                        System.out.println("object is free, putted on the response queue");

                    } else {
                        //rákattintott, de mégsem szerkeszthető
                        System.out.println("object is locked");
                        sendRestoreMessage(action,"user "+service.getSessionStateById(action.getTarget().getTarget_id()).getLockerUser_id()+" has lock on this.\n Object restored.");
                    }
                } catch (JsonProcessingException e) {
                    System.out.println(action.getTarget().getTarget_id());
                }catch(NullPointerException ex){
                try {
                    sendRestoreMessage(action,"[lock error] the object does not exists");
                } catch (JsonProcessingException ex1) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex1);
                }
 
                }
                break;
                case UPDATE://-------------------------------------------------------------------
                         try {
                    if (service.updateObjectAndUnlock(mapper.readValue(action.getJson(), DynamicSerialObject.class), action.getUser_id())!=null) {
                        //send a session state update to all
                        
                        SessionState s;
                        try{
                            s=service.getSessionStateById(action.getTarget().getTarget_id()) ;
                           if( s!=null){
                        if(   service.getSessionStateById(action.getTarget().getTarget_id()).isDraft())     {  
                             s.getExtra().remove("placeholder");
                             service.getSessionStateById(action.getTarget().getTarget_id()).setDraft(false);

                        }
                           }
                        }catch(Exception e){
                            //send simple msg, because the id does
                            System.err.println(e.getMessage());
                        }
                        // EditorAction is történt, mert update volt. Ezt broadcastolni kell a többiek felé  
                        sendAll(action, Q.STATE);
                        sendAll(action, Q.ACTION);

                    } else {
                        //Can not update
                        sendRestoreMessage(action,"[lock error] (locker's id: "+this.getLockerIdIfexists(action.getTarget().getTarget_id())+").\n [Object RESTORED]");
                   
//+service.getSessionStateById(action.getTarget().getTarget_id()).getLockerUser_id()+" has lock on this.\n object restored.");

                    }
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                case DIMENSION_UPDATE:
                {
                    try {
                        if (service.updateObjectAndHoldLock(mapper.readValue(action.getJson(), DynamicSerialObject.class), action.getUser_id())!=null) {
                            // sendAll(action, Q.STATE);
                            action.setAction(ACTION_TYPE.UPDATE);//átállítom update-ra, hogy a kliens oldal ugyan úgy kezelje
                        sendAll(action, Q.ACTION); 
                        sendAll(action, Q.STATE); 
                        }else{
                               sendRestoreMessage(action,"[lock error] Can not update dimensions (locker's id: "+this.getLockerIdIfexists(action.getTarget().getTarget_id())+").\n [Object RESTORED]");

                        }
                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                        
                    break;

                case CREATE:
                    try {
                        //----------------------------------------------------------------------
                        DynamicSerialObject obj= service.createItemForContainer(action.getUser_id(),action.getTarget().getTarget_id(),
                                mapper.readValue(action.getJson(), DynamicSerialObject.class) );
                        if(obj!=null){
                            
                            action.setJson(mapper.writeValueAsString(obj));
                            service.getSessionStateById(obj.getId()).setExtra(new HashMap<String,String>());
                            service.getSessionStateById(obj.getId()).setDraft(true);
                             service.getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+action.getUser_id());
                   
                            action.getExtra().put("sessionState", 
                                    mapper.writeValueAsString(service.getSessionStateById(obj.getId())));
                              EditorActionResponse resp2 = new EditorActionResponse(action);
                              resp2.setTarget_id(action.getTarget().getTarget_id());
                               resp2.setTarget_type(TARGET_TYPE.CONTAINER);
                               resp2.setTarget_user_id(action.getUser_id());
                
                actionResponseQueue.add(resp2);
                            
              /*             SessionStateResponse resp = new SessionStateResponse(
                        service.getSessionStateById(obj.getId()),action.getId());
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_id(obj.getId());
                resp.setTarget_type(TARGET_TYPE.ITEM);
              
                sessionStateResponseQueue.add(resp);*/
                            System.out.println("object created and sent back with json"+mapper.writeValueAsString(obj));
                        }
                        else{
                            System.out.println("can not create obj");
                        }
                        
                            
                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                

                    break;

                case DELETE:
                    System.out.println("DELETE REQUEST "+action.getTarget().getTarget_id()+" parent:"+action.getTarget().getParent_id());
                       if(this.service.deleteItemFromContainerById(action.getUser_id(), action.getTarget().getTarget_id(),action.getTarget().getParent_id())){
                           //delete succesful
                           sendAll(action, Q.ACTION);
                           
                       }else{
                try {
                   
                    DynamicSerialObject obj=service.getItemById(action.getTarget().getTarget_id());
                     System.out.println("THIS IS A"+obj.getType());
                    if(obj instanceof SimpleClass||"SimpleClass".equals(obj.getType())){
                    this.sendSimpleClassDeleteRestoreMessage((SimpleClass)obj, action,   "[lock error] you can't delete an item if you don't have a lock on it (locker's id: "
                                    +getLockerIdIfexists(action.getTarget().getTarget_id())+"). \n Object restored");
                        System.out.println("OBJECT IS A SIMPLE CLASS");
                    }else{
                         this.sendDeleteRestoreMessage(action, 
                            "[lock error] you can't delete an item if you don't have a lock on it (locker's id: "
                                    +getLockerIdIfexists(action.getTarget().getTarget_id())+"). \n Object restored");
                    }
                    //  sendBackPrivate(action, Q.ACTION, "te item does not exists or you dont have a lock on it.");
                } catch (JsonProcessingException ex) {
                    
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                       }
                    break;
                    
                //SERVER_SIDE ACTIONS ////////////////////////////////////////////
                case S_USER_DISCONNECT:            
                  //delete draft elements, send message about them
                       List<String> deleted=service.deleteDraftsByUser(action.getUser_id());
                  for(String id:deleted){
                   action.setAction(ACTION_TYPE.DELETE);
                       EditorActionResponse resp2 = new EditorActionResponse(action);
                resp2.setTarget_id(id);
                actionResponseQueue.add(resp2);
                  }
                     List<String> unlocked= service.deleteLocksRelatedToUser(action.getUser_id());
                     for(String id:unlocked){
                         SessionStateResponse resp = new SessionStateResponse(service.getSessionStateById(id),"");
                   
                         resp.setTarget_user_id(action.getUser_id());
                         resp.setTarget_id(id);
                         sessionStateResponseQueue.add(resp);
                     }
                          //list of target_id, that have been deleted
                  
                  //unlock everything, that had been locked by him.
                     /* */
                default:
                    break;
            }
            // if(action==null) continue;   
                
        }
    }
    
    String getLockerIdIfexists(String target_id){
       if(service.getSessionStateById(target_id).getLockerUser_id()!=null){
           return service.getSessionStateById(target_id).getLockerUser_id();
       }
       return "null";
    }
    
    void sendAll(EditorAction action, Q queue) {
        switch (queue) {
            case STATE:
                SessionStateResponse resp = new SessionStateResponse(
                        service.getSessionStateById(action.getTarget().getTarget_id()),
                         action.getId());
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_id(action.getTarget().getTarget_id());
                sessionStateResponseQueue.add(resp);
                break;
            case ACTION:
                EditorActionResponse resp2 = new EditorActionResponse(action);
                resp2.setTarget_id(action.getTarget().getTarget_id());
                actionResponseQueue.add(resp2);
                break;
        }
    }
    
      void sendBackPrivate(EditorAction action, Q queue,String msg) {
        switch (queue) {
            case STATE:
                SessionStateResponse resp = new SessionStateResponse(
                        service.getSessionStateById(action.getTarget().getTarget_id()),
                         action.getId());
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_id(action.getTarget().getTarget_id());
                resp.setScope(RESPONSE_SCOPE.PRIVATE);
                sessionStateResponseQueue.add(resp);
                break;
            case ACTION:
                EditorActionResponse resp2 = new EditorActionResponse(action);
                resp2.setTarget_id(action.getTarget().getTarget_id());
                resp2.setScope(RESPONSE_SCOPE.PRIVATE);
                resp2.setResponse_msg("could not complete delete request");
                actionResponseQueue.add(resp2);
                break;
        }
    }
      /**
       * this one only sends sessions
      */
    private void sendSimpleClassDeleteRestoreMessage(SimpleClass obj, EditorAction action, String message) {
        for(SimpleClassElementGroup g:obj.getGroups()){
                    sendCustomMessage(g.getId(),action,Q.STATE,TARGET_TYPE.CONTAINER_INJECTION,RESPONSE_SCOPE.PRIVATE,message);

            for(AttributeElement e:g.getAttributes()){
        sendCustomMessage(e.getId(),action,Q.STATE,TARGET_TYPE.ITEM_INJECTION,RESPONSE_SCOPE.PRIVATE,message);
            }
        }
       
          sendCustomMessage(obj.getTitleModel().getId(),action,Q.STATE,TARGET_TYPE.ITEM_INJECTION,RESPONSE_SCOPE.PRIVATE,message);

            
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.
      
      
        
        System.out.println("object restoration is sent");
    }

    private void sendCustomMessage(String target_id, EditorAction action,Q queue, TARGET_TYPE target_type,RESPONSE_SCOPE response_scope,String message) {
   
         switch(queue){
         case ACTION:
                EditorActionResponse resp2 = new EditorActionResponse(action);
                resp2.setTarget_id(target_id);
                resp2.setTarget_user_id(action.getUser_id());
                resp2.setTarget_type(target_type);
                resp2.setScope(response_scope);
                resp2.setResponse_msg(message);
                actionResponseQueue.add(resp2);
                break;
         case STATE:
             SessionStateResponse resp = new SessionStateResponse(
                        service.getSessionStateById(target_id),
                         action.getId());
            resp.setTarget_id(target_id);
            resp.setTarget_user_id(action.getUser_id());
            resp.setTarget_type(target_type);
            resp.setScope(response_scope);
            resp.setResponse_msg(message);
        sessionStateResponseQueue.add(resp);
             break;
        }
    }

      private void sendCustomMessage(EditorAction action,Q queue, TARGET_TYPE target_type,RESPONSE_SCOPE response_scope,String message) {
      
        switch(queue){
         case ACTION:
                EditorActionResponse resp2 = new EditorActionResponse(action);
                resp2.setTarget_id(action.getTarget().getTarget_id());
                resp2.setTarget_user_id(action.getUser_id());
                resp2.setTarget_type(target_type);
                resp2.setScope(response_scope);
                resp2.setResponse_msg(message);
                actionResponseQueue.add(resp2);
                break;
         case STATE:
             SessionStateResponse resp = new SessionStateResponse(
                        service.getSessionStateById(action.getTarget().getTarget_id()),
                         action.getId());
            resp.setTarget_id(action.getTarget().getTarget_id());
            resp.setTarget_user_id(action.getUser_id());
            resp.setTarget_type(target_type);
            resp.setScope(response_scope);
            resp.setResponse_msg(message);
        sessionStateResponseQueue.add(resp);
             break;
        }
    }
    private enum Q {
        STATE, ACTION
    }

    void sendRestoreMessage(EditorAction action,String message) throws JsonProcessingException {
        SessionStateResponse resp = new SessionStateResponse(
                service.getSessionStateById(action.getTarget().getTarget_id()),
                 action.getId(), action.getTarget().getTarget_id(), action.getUser_id());
       resp.setTarget_id(action.getTarget().getTarget_id());
       resp.setTarget_user_id(action.getUser_id());
      
        sessionStateResponseQueue.add(resp);
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.
      
        action.setAction(ACTION_TYPE.RESTORE);
        action.setJson(mapper.writeValueAsString(service.getItemById(action.getTarget().getTarget_id())));
        EditorActionResponse resp2 = new EditorActionResponse(action, action.getUser_id());
        resp2.setTarget_user_id(action.getUser_id());
        resp2.setTarget_id(action.getTarget().getTarget_id());
        resp2.setScope(RESPONSE_SCOPE.PRIVATE);
       resp2.setResponse_msg(message);
        actionResponseQueue.add(resp2);
        
        System.out.println("object restoration is sent");
    }
    
     void sendDeleteRestoreMessage(EditorAction action,String message) throws JsonProcessingException {
            SessionStateResponse resp = new SessionStateResponse(
                service.getSessionStateById(action.getTarget().getTarget_id()),
                 action.getId(), action.getTarget().getTarget_id(), action.getUser_id());
            resp.setTarget_id(action.getTarget().getTarget_id());
            resp.setTarget_user_id(action.getUser_id());
        sessionStateResponseQueue.add(resp);
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.
      
        action.setAction(ACTION_TYPE.RESTORE);
        action.getExtra().put("sessionState", mapper.writeValueAsString( service.getSessionStateById(action.getTarget().getTarget_id())));
        action.setJson(mapper.writeValueAsString(service.getItemById(action.getTarget().getTarget_id())));
        EditorActionResponse resp2 = new EditorActionResponse(action, action.getUser_id());
        resp2.setTarget_user_id(action.getUser_id());
        resp2.setTarget_id(action.getTarget().getTarget_id());
        resp2.setScope(RESPONSE_SCOPE.PRIVATE);
       resp2.setResponse_msg(message);
        actionResponseQueue.add(resp2);
        
        System.out.println("object restoration is sent");
    }

}
