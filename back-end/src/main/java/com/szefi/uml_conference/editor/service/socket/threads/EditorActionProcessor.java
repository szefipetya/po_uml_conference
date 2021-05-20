/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.service.socket.threads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.D;
import com.szefi.uml_conference.DLEVEL;
import com.szefi.uml_conference._exceptions.JwtException;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.DiagramObject;
import com.szefi.uml_conference.editor.model.do_related.Element_c;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.service.socket.threads.ActionResponseProcessor;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.Response.EditorActionResponse;
import com.szefi.uml_conference.editor.model.socket.Response.RESPONSE_SCOPE;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.Response.TARGET_TYPE;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.service.SocketSessionService;
import java.util.HashMap;
import java.util.List;
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
 *
 */
@Component(value = "EditorActionProcessor")
@Scope("prototype")
public class EditorActionProcessor extends CustomProcessor {

  protected  ObjectMapper mapper;

   protected SocketSessionService service;

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
public EditorActionProcessor(){}
    protected BlockingQueue<EditorActionResponse> actionResponseQueue = null;
    protected BlockingQueue<SessionStateResponse> sessionStateResponseQueue = null;

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
try{
            switch (action.getAction()) {

                case SELECT:    //---------------------------------------------------
                        try {
                    //ha az első feltétel nem teljesül, akkor a lusta kiértékelés miatt a feltétel le se fut
                    if (service.tokenToSession(action.getSession_jwt()).lockObjectById(action.getTarget().getTarget_id(), action.getUser_id(),
                            new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE})) {
                        //the object is free
                        sendAll(action, Q.STATE);
                        System.out.println("object is free, putted on the response queue");

                    } else {
                        //rákattintott, de mégsem szerkeszthető
                        System.out.println("object is locked");
                        sendRestoreMessage(action, "user " + service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()).getLockerUser_id() + " has lock on this.\n Object restored.");
                    }
                } catch (NullPointerException ex) {
                    try {
                        sendRestoreMessage(action, "[lock error] the object does not exists");
                    } catch (JsonProcessingException ex1) {
                        Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (JwtParseException ex1) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex1);
                } catch (NotFoundException ex1) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex1);
                }

                } catch (JwtParseException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotFoundException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;




                case UPDATE://-------------------------------------------------------------------
                         try {
                    if (service.tokenToSession(action.getSession_jwt()).updateObjectAndUnlock(mapper.readValue(action.getJson(), DynamicSerialObject.class), action.getUser_id()) != null) {
                        //send a session state update to all

                        SessionState s;
                        try {
                            s = service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id());
                            if (s != null) {
                                if (service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()).isDraft()) {
                                    s.getExtra().remove("placeholder");
                                    service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()).setDraft(false);

                                }
                            }
                        } catch (Exception e) {
                            //send simple msg, because the id does
                            System.err.println(e.getMessage());
                        }
                        // EditorAction is történt, mert update volt. Ezt broadcastolni kell a többiek felé  
                        sendAll(action, Q.STATE);
                        sendAll(action, Q.ACTION);

                    } else {
                        //Can not update
                        sendRestoreMessage(action, "[lock error] (locker's id: " + this.getLockerIdIfexists(action,action.getTarget().getTarget_id()) + ").\n [Object RESTORED]");

//+service.getSessionStateById(action.getTarget().getTarget_id()).getLockerUser_id()+" has lock on this.\n object restored.");
                    }
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JwtParseException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotFoundException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;


                case DIMENSION_UPDATE: {
                    try {
                        if (service.tokenToSession(action.getSession_jwt()).updateObjectAndHoldLock(mapper.readValue(action.getJson(), DynamicSerialObject.class), action.getUser_id()) != null) {
                            // sendAll(action, Q.STATE);
                            action.setAction(ACTION_TYPE.UPDATE);//átállítom update-ra, hogy a kliens oldal ugyan úgy kezelje
                            sendAll(action, Q.ACTION);
                            sendAll(action, Q.STATE);
                        } else {
                          //  sendRestoreMessage(action, "[lock error] Can not update dimensions (locker's id: " + this.getLockerIdIfexists(action.getTarget().getTarget_id()) + ").\n [Object RESTORED]");

                        }
                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JwtParseException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotFoundException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                }

                break;

                case CREATE:
                    try {
                    //----------------------------------------------------------------------

                    if (action.getExtra().containsKey("create_method")) {
                        System.out.println(action.getExtra().get("create_method"));
                        if (action.getExtra().get("create_method").equals("individual")) {
                            DynamicSerialObject obj = service.tokenToSession(action.getSession_jwt()).createItemForContainer(action.getUser_id(), action.getTarget().getParent_id(),
                                    mapper.readValue(action.getJson(), DynamicSerialObject.class));
                            if (obj != null) {
                                //DynamicSerialObject  originalObj=   mapper.readValue(action.getJson(), DynamicSerialObject.class);
                                    
                                action.setJson(mapper.writeValueAsString(obj));
                                service.tokenToSession(action.getSession_jwt()).getSessionStateById(obj.getId()).setExtra(new HashMap<>());
                                service.tokenToSession(action.getSession_jwt()).getSessionStateById(obj.getId()).setDraft(true);
                                service.tokenToSession(action.getSession_jwt()).getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:" + action.getUser_id());

                                action.getExtra().put("sessionState",
                                        mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getSessionStateById(obj.getId())));
                                action.getExtra().put("old_id",
                                        action.getTarget().getTarget_id().toString());
                                EditorActionResponse resp2 = new EditorActionResponse(action);
                                resp2.setTarget_id(action.getTarget().getTarget_id());
                                resp2.setTarget_type(TARGET_TYPE.ITEM);
                                resp2.setTarget_user_id(action.getUser_id());
                                resp2.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                                actionResponseQueue.add(resp2);
                                System.out.println("object created and sent back with json" + mapper.writeValueAsString(obj));

                            }
                        } if (action.getExtra().get("create_method").equals("nested")) {
                            DynamicSerialObject obj = service.tokenToSession(action.getSession_jwt()).createItemForContainer(action.getUser_id(), action.getTarget().getParent_id(),
                                    mapper.readValue(action.getJson(), DynamicSerialObject.class));
                            
                            
                            if (obj instanceof SimpleClass) {
                                SimpleClass casted = (SimpleClass) obj;
                              
                                //the class
                                  EditorAction action1=new EditorAction();
                                  action1.setUser_id(action.getUser_id());
                                 action1.getExtra().put("old_id", casted.getExtra().get("old_id")); 
                              
                               action1.setJson(mapper.writeValueAsString(casted));
                                 action1.getTarget().setParent_id(SocketSessionService.ROOT_ID);
                                 action1.setAction(ACTION_TYPE.CREATE);
                                 action1.setSession_jwt(action.getSession_jwt());
                                    action1.getExtra().put("sessionState",
                                        mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getContainerSessionStateById(obj.getId())));
                                 this.sendCustomMessage(casted.getId(), action1, Q.ACTION, TARGET_TYPE.CONTAINER, RESPONSE_SCOPE.PUBLIC, "");

                                 //title model
                             EditorAction action2=new EditorAction();
                                  action2.setAction(ACTION_TYPE.CREATE);
                                     action2.setUser_id(action.getUser_id());
                                   action2.getExtra().put("old_id", casted.getTitleModel().getExtra().get("old_id"));
                                    action2.setSession_jwt(action.getSession_jwt());
                                 action2.getTarget().setParent_id(casted.getId());
                                 action2.setJson(mapper.writeValueAsString(casted.getTitleModel()));
                                    action2.getExtra().put("sessionState",
                                        mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getSessionStateById( casted.getTitleModel().getId())));
                                 this.sendCustomMessage(casted.getTitleModel().getId(), action2, Q.ACTION, TARGET_TYPE.ITEM, RESPONSE_SCOPE.PUBLIC, "");
                                 
                                for (SimpleClassElementGroup g : casted.getGroups()) {
                                    //groups
                                    EditorAction actiong=new EditorAction();
                                     actiong.setAction(ACTION_TYPE.CREATE);
                                        actiong.setUser_id(action.getUser_id());
                                      actiong.getExtra().put("old_id", g.getExtra().get("old_id"));
                                      actiong.getTarget().setParent_id(casted.getId());
                                         actiong.setSession_jwt(action.getSession_jwt());
                                       actiong.setJson(mapper.writeValueAsString(g));
                                           actiong.getExtra().put("sessionState",
                                        mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getContainerSessionStateById(g.getId())));
                                     this.sendCustomMessage(g.getId(), actiong, Q.ACTION, TARGET_TYPE.CONTAINER, RESPONSE_SCOPE.PUBLIC, "");
                                    for (Element_c e : g.getAttributes()) {
                                        //elements
                                          EditorAction actione=new EditorAction();
                                             actione.setUser_id(action.getUser_id());
                                                actione.setSession_jwt(action.getSession_jwt());
                                         actione.setAction(ACTION_TYPE.CREATE);
                                        actione.getExtra().put("old_id", e.getExtra().get("old_id"));
                                        actione.getTarget().setParent_id(g.getId());
                                        
                                         actione.setJson(mapper.writeValueAsString(e));
                                             actione.getExtra().put("sessionState",
                                        mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getSessionStateById( e.getId())));
                                     this.sendCustomMessage(e.getId(), actione, Q.ACTION, TARGET_TYPE.ITEM, RESPONSE_SCOPE.PUBLIC, "");

                                    }

                                }
                            
                              
                                   
                            }else if(obj instanceof DiagramObject){
                                  EditorAction action1=new EditorAction();
                                  action1.setUser_id(action.getUser_id());
                                 action1.getExtra().put("old_id", obj.getExtra().get("old_id")); 
                              
                               action1.setJson(mapper.writeValueAsString(obj));
                                 action1.getTarget().setParent_id(SocketSessionService.ROOT_ID);
                                 action1.setAction(ACTION_TYPE.CREATE);
                                 action1.setSession_jwt(action.getSession_jwt());
                                    action1.getExtra().put("sessionState",
                                        mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getSessionStateById(obj.getId())));
                                 this.sendCustomMessage(obj.getId(), action1, Q.ACTION, TARGET_TYPE.CONTAINER, RESPONSE_SCOPE.PUBLIC, "");
                            }
                        }
                    } else {
                        System.out.println("can not create obj");
                    }

                } catch (JsonProcessingException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JwtParseException ex) {
                    Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;


                case DELETE:
                    System.out.println("DELETE REQUEST " + action.getTarget().getTarget_id() + " parent:" + action.getTarget().getParent_id());
                {
                    try {
                        if (this.service.tokenToSession(action.getSession_jwt()).deleteItemFromContainerById(action.getUser_id(), action.getTarget().getTarget_id(), action.getTarget().getParent_id())) {
                            //delete succesful
                            sendAll(action, Q.ACTION);
                            
                        } else {
                            try {
                                
                                DynamicSerialObject obj = service.tokenToSession(action.getSession_jwt()).getItemById(action.getTarget().getTarget_id());
                                //  System.out.println("THIS IS A"+obj.getType());
                                if (obj instanceof SimpleClass) {
                                    this.sendSimpleClassDeleteRestoreMessage((SimpleClass) obj, action, "[lock error] you can't delete an item if you don't have a lock on it (locker's id: "
                                            + getLockerIdIfexists(action,action.getTarget().getTarget_id()) + "). \n Object restored");
                                    System.out.println("OBJECT IS A SIMPLE CLASS");
                                } else {
                                    this.sendDeleteRestoreMessage(action,
                                            "[lock error] you can't delete an item if you don't have a lock on it (locker's id: "
                                                    + getLockerIdIfexists(action,action.getTarget().getTarget_id()) + "). \n Object restored");
                                }
                                //  sendBackPrivate(action, Q.ACTION, "te item does not exists or you dont have a lock on it.");
                            } catch (JsonProcessingException ex) {
                                
                                Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (JwtParseException ex) {
                        Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }catch(NotFoundException ex){
                                
                            }
                }
                    break;


                //SERVER_SIDE ACTIONS ////////////////////////////////////////////
                case S_USER_DISCONNECT:
                    //delete draft elements, send message about them
                    try{
                   List<Integer> deleted = service.getSessionById(Long.valueOf(action.getExtra().get("session_id"))).deleteDraftsByUser(action.getUser_id());
                    for (Integer id : deleted) {
                        action.setAction(ACTION_TYPE.DELETE);
                        EditorActionResponse resp2 = new EditorActionResponse(action);
                        resp2.setTarget_id(id);
                        resp2.setTargetsUsers(service.getSessionById(Long.valueOf(action.getExtra().get("session_id"))).getUserSockets());//IMPORTANT 
                        actionResponseQueue.add(resp2);
                    }
                    List<Integer> unlocked = service.getSessionById(Long.valueOf(action.getExtra().get("session_id"))).deleteLocksRelatedToUser(action.getUser_id());
                    for (Integer id : unlocked) {
                        SessionStateResponse resp = new SessionStateResponse(service.getSessionById(Long.valueOf(action.getExtra().get("session_id"))).getSessionStateById(id), "");
                        resp.setTargetsUsers(service.getSessionById(Long.valueOf(action.getExtra().get("session_id"))).getUserSockets());
                        resp.setTarget_user_id(action.getUser_id());
                        resp.setTarget_id(id);
                        sessionStateResponseQueue.add(resp);
                    }
                //list of target_id, that have been deleted

                //unlock everything, that had been locked by him.
                /* */
                    }catch(NotFoundException ex){
                        Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                default:
                    break;
            }
}catch(Exception ex){
    D.log("EditorActionProcessor thread catched an exception:\n"+ex.getMessage(),ex.getClass(),DLEVEL.ERR);
    ex.printStackTrace();
}
            // if(action==null) continue;   

        }
    }
//METHODS_BEG
   protected Integer getLockerIdIfexists(EditorAction action,Integer target_id) {
        try {
            if (service.tokenToSession(action.getSession_jwt()).getSessionStateById(target_id) != null && service.tokenToSession(action.getSession_jwt()).getSessionStateById(target_id).getLockerUser_id() != null) {
                return service.tokenToSession(action.getSession_jwt()).getSessionStateById(target_id).getLockerUser_id();
            }
         
        } catch (JwtParseException ex) {
            Logger.getLogger(EditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }   return null;
    }

   protected void sendAll(EditorAction action, Q queue) throws JwtParseException {
        switch (queue) {
            case STATE:
             /*   SessionStateResponse resp = new SessionStateResponse(
                        service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                        action.getId());
                  resp.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_id(action.getTarget().getTarget_id());
                sessionStateResponseQueue.add(resp);*/
                  sessionStateResponseQueue.add(this.buildStateResponse(
                action.getTarget().getTarget_id()
                , action
                , service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id())
                , TARGET_TYPE.ITEM
                , RESPONSE_SCOPE.PUBLIC
                , ""));
               
                break;
            case ACTION:
              /*  EditorActionResponse resp2 = new EditorActionResponse(action);
                  resp2.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp2.setTarget_id(action.getTarget().getTarget_id());
                actionResponseQueue.add(resp2);*/
                        actionResponseQueue.add(this.buildActionResponse(action.getTarget().getTarget_id(), action,TARGET_TYPE.ITEM, RESPONSE_SCOPE.PUBLIC, ""));

                break;
        }
    }

   protected void sendBackPrivate(EditorAction action, Q queue, String msg) throws JwtParseException {
        switch (queue) {
            case STATE:
             /*   SessionStateResponse resp = new SessionStateResponse(
                        service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                        action.getId());
              //  resp.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_id(action.getTarget().getTarget_id());
                resp.setScope(RESPONSE_SCOPE.PRIVATE);
                sessionStateResponseQueue.add(resp);*/
                  sessionStateResponseQueue.add(this.buildStateResponse(
                action.getTarget().getTarget_id()
                , action
                , service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id())
                , TARGET_TYPE.ITEM
                , RESPONSE_SCOPE.PRIVATE
                , msg));
                break;
            case ACTION:
              /*  EditorActionResponse resp2 = new EditorActionResponse(action);
                //resp2.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp2.setTarget_id(action.getTarget().getTarget_id());
                resp2.setScope(RESPONSE_SCOPE.PRIVATE);
                resp2.setResponse_msg("could not complete delete request");
                actionResponseQueue.add(resp2);*/
                
        actionResponseQueue.add(this.buildActionResponse(action.getTarget().getTarget_id(), action,TARGET_TYPE.ITEM, RESPONSE_SCOPE.PRIVATE, msg));

                break;
        }
    }

    /**
     * this one only sends sessions
     */
  protected   void sendSimpleClassDeleteRestoreMessage(SimpleClass obj, EditorAction action, String message) throws JwtParseException {
        for (SimpleClassElementGroup g : obj.getGroups()) {
            sendCustomMessage(g.getId(), action, Q.STATE, TARGET_TYPE.CONTAINER_INJECTION, RESPONSE_SCOPE.PRIVATE, message);

            for (AttributeElement e : g.getAttributes()) {
                sendCustomMessage(e.getId(), action, Q.STATE, TARGET_TYPE.ITEM_INJECTION, RESPONSE_SCOPE.PRIVATE, message);
            }
        }

        sendCustomMessage(obj.getTitleModel().getId(), action, Q.STATE, TARGET_TYPE.ITEM_INJECTION, RESPONSE_SCOPE.PRIVATE, message);

        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.
        System.out.println("object restoration is sent");
    }

  
   protected EditorActionResponse buildActionResponse(Integer target_id, EditorAction action, TARGET_TYPE target_type, RESPONSE_SCOPE response_scope, String message) throws JwtParseException{
         EditorActionResponse resp2 = new EditorActionResponse(action);
                resp2.setTarget_id(target_id);
                resp2.setTarget_user_id(action.getUser_id());
                resp2.setTarget_type(target_type);
                resp2.getAction().getTarget().setTarget_id(target_id);
                resp2.setResponse_msg(message);
                resp2.setScope(response_scope);
                if(response_scope==RESPONSE_SCOPE.PUBLIC)
                        resp2.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                else if(response_scope==RESPONSE_SCOPE.PRIVATE)
                resp2.setTargetsUsers( service.tokenToSession(action.getSession_jwt()).getSocketListContainingUserWithId(action.getUser_id()));
                return resp2;
    }
   protected SessionStateResponse buildStateResponse(Integer target_id,EditorAction action, SessionState state, TARGET_TYPE target_type, RESPONSE_SCOPE response_scope, String message) throws JwtParseException{
         SessionStateResponse resp = new SessionStateResponse(
                      state,
                      action.getId());
                resp.setTarget_id(target_id);
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_type(target_type);
                resp.setScope(response_scope);
                resp.setResponse_msg(message);
                 if(response_scope==RESPONSE_SCOPE.PUBLIC)
                        resp.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                else if(response_scope==RESPONSE_SCOPE.PRIVATE)
                     resp.setTargetsUsers( service.tokenToSession(action.getSession_jwt()).getSocketListContainingUserWithId(action.getUser_id()));
                return resp;
   }
  
  
  
  
    protected void sendCustomMessage(Integer target_object_id, EditorAction action, Q queue, TARGET_TYPE target_type, RESPONSE_SCOPE response_scope, String message) throws JwtParseException {
        switch (queue) {
            case ACTION:
                actionResponseQueue.add(buildActionResponse(target_object_id, action, target_type, response_scope, message));
                break;
            case STATE:
                sessionStateResponseQueue.add(buildStateResponse(target_object_id, action,   service.tokenToSession(action.getSession_jwt()).getSessionStateById(target_object_id), target_type, response_scope, message));
                break;
        }
    }
/*send it to all*/
    protected void sendCustomMessage(EditorAction action, Q queue, TARGET_TYPE target_type, RESPONSE_SCOPE response_scope, String message) throws JwtParseException {
        sendCustomMessage(action.getTarget().getTarget_id(), action, queue, target_type, response_scope, message);
    }

    public enum Q {
        STATE, ACTION
    }

  protected  void sendRestoreMessage(EditorAction action, String message) throws JsonProcessingException, JwtParseException, NotFoundException {
      /*  SessionStateResponse resp = new SessionStateResponse(
                service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                action.getId(), action.getTarget().getTarget_id(), action.getUser_id());
        resp.setTarget_id(action.getTarget().getTarget_id());
        resp.setTarget_user_id(action.getUser_id());
        resp.setTargetsUsers( service.tokenToSession(action.getSession_jwt()).getSocketListContainingUserWithId(action.getTarget().getTarget_id()));*/
        sessionStateResponseQueue.add(this.buildStateResponse(
                action.getTarget().getTarget_id()
                , action
                , service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id())
                , TARGET_TYPE.ITEM
                , RESPONSE_SCOPE.PRIVATE
                , message));
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.

        action.setAction(ACTION_TYPE.RESTORE);
        action.setJson(mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getItemById(action.getTarget().getTarget_id())));
   
        actionResponseQueue.add(this.buildActionResponse(action.getTarget().getTarget_id(), action,TARGET_TYPE.ITEM, RESPONSE_SCOPE.PRIVATE, message));

        System.out.println("object restoration is sent");
    }

   protected void sendDeleteRestoreMessage(EditorAction action, String message) throws JsonProcessingException, JwtParseException, NotFoundException {
       /* SessionStateResponse resp = new SessionStateResponse(
                service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                action.getId(), action.getTarget().getTarget_id(), action.getUser_id());
        resp.setTarget_id(action.getTarget().getTarget_id());
        resp.setTarget_user_id(action.getUser_id());
        resp.setTargetsUsers( service.tokenToSession(action.getSession_jwt()).getSocketListContainingUserWithId(action.getTarget().getTarget_id()));
        
        sessionStateResponseQueue.add(resp);*/
         sessionStateResponseQueue.add(this.buildStateResponse(
                action.getTarget().getTarget_id()
                , action
                , service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id())
                , TARGET_TYPE.ITEM
                , RESPONSE_SCOPE.PRIVATE
                , message));
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.

        action.setAction(ACTION_TYPE.RESTORE);
        action.getExtra().put("sessionState", mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id())));
        action.setJson(mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getItemById(action.getTarget().getTarget_id())));
      /*  EditorActionResponse resp2 = new EditorActionResponse(action, action.getUser_id());
        resp2.setTarget_user_id(action.getUser_id());
        resp2.setTarget_id(action.getTarget().getTarget_id());
        resp2.setScope(RESPONSE_SCOPE.PRIVATE);
        resp2.setResponse_msg(message);
        resp2.setTargetsUsers( service.tokenToSession(action.getSession_jwt()).getSocketListContainingUserWithId(action.getTarget().getTarget_id()));
        actionResponseQueue.add(resp2);*/

        actionResponseQueue.add(this.buildActionResponse(action.getTarget().getTarget_id(), action,TARGET_TYPE.ITEM, RESPONSE_SCOPE.PRIVATE, message));

        System.out.println("object restoration is sent");
    }
//METHODS_END
}
