/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                        sendRestoreMessage(action,"user "+service.getSessionStateById(action.getTarget().getTarget_id()).getLockerUser_id()+" has lock on this.\n object restored.");
                    }
                } catch (JsonProcessingException e) {
                    System.out.println(action.getTarget().getTarget_id());
                }
                break;
                case UPDATE://-------------------------------------------------------------------
                         try {
                    if (service.unLockObjectById(action.getTarget().getTarget_id(), action.getUser_id())) {
                        //send a session state update to all
                        SessionState s;
                        try{
                            s=service.getSessionStateById(action.getTarget().getTarget_id()) ;
                           if( s!=null){
                        if(s.getExtra().containsKey("placeholder"))       
                             s.getExtra().remove("placeholder");

                           }
                        }catch(Exception e){
                            System.err.println(e.getMessage());
                        }
                        // EditorAction is történt, mert update volt. Ezt broadcastolni kell a többiek felé  
                        sendAll(action, Q.STATE);
                        sendAll(action, Q.ACTION);

                    } else {
                        //Can not update
                        sendRestoreMessage(action,"this object is locked"); 
//+service.getSessionStateById(action.getTarget().getTarget_id()).getLockerUser_id()+" has lock on this.\n object restored.");

                    }
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
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
                             service.getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+action.getUser_id());
                   
                            action.getExtra().put("sessionState", 
                                    mapper.writeValueAsString(service.getSessionStateById(obj.getId())));
                           EditorActionResponse resp2 = new EditorActionResponse(action);
                              resp2.setTarget_id(action.getTarget().getTarget_id());
                               resp2.setTarget_type(TARGET_TYPE.CONTAINER);
                               resp2.setTarget_user_id(action.getUser_id());
                
                actionResponseQueue.add(resp2);
                            
                        /*   SessionStateResponse resp = new SessionStateResponse(
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

                    break;
                default:
                    break;
            }
            // if(action==null) continue;   

        }
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

    private enum Q {
        STATE, ACTION
    }

    void sendRestoreMessage(EditorAction action,String message) throws JsonProcessingException {
        SessionStateResponse resp = new SessionStateResponse(
                service.getSessionStateById(action.getTarget().getTarget_id()),
                 action.getId(), action.getTarget().getTarget_id(), action.getUser_id());

        sessionStateResponseQueue.add(resp);
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.
        EditorAction ar = new EditorAction();
        ar.setAction(ACTION_TYPE.RESTORE);
        ar.setId(action.getId());
        ar.setUser_id(action.getUser_id());
        
        ar.setJson(mapper.writeValueAsString(service.getRestoredModel(action.getTarget().getTarget_id())));
        EditorActionResponse resp2 = new EditorActionResponse(ar, action.getUser_id());
        resp2.setTarget_user_id(action.getUser_id());
        resp2.setTarget_id(action.getTarget().getTarget_id());
        resp2.setScope(RESPONSE_SCOPE.PRIVATE);
       resp2.setResponse_msg(message);
        actionResponseQueue.add(resp2);
        
        System.out.println("object restoration is sent");
    }

}
