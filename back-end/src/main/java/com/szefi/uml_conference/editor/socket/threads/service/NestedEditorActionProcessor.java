/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.socket.threads.service;

import com.szefi.uml_conference.editor.service.SocketSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.DiagramObject;
import com.szefi.uml_conference.editor.model.do_related.Element_c;
import com.szefi.uml_conference.editor.model.do_related.PackageObject;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.Response.EditorActionResponse;
import com.szefi.uml_conference.editor.model.socket.Response.RESPONSE_SCOPE;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.Response.TARGET_TYPE;
import com.szefi.uml_conference.editor.model.socket.ServerSideEditorAction;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.socket.threads.ActionResponseProcessor;
import com.szefi.uml_conference.editor.socket.threads.CustomProcessor;
import com.szefi.uml_conference.editor.socket.threads.EditorActionProcessor;
import com.szefi.uml_conference.editor.socket.threads.EditorActionProcessor.Q;
import static com.szefi.uml_conference.editor.socket.threads.service.SOCKET.STATE;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Ez az osztály arra való, hogyha a back-endnek session jellegű socketes akciót kell végrehajtani, vagy küldeni valamit a kliensek felé, akkor itt meg lehet tenni.
 * @author h9pbcl
 */

@Component
@Scope("prototype")
@Qualifier("NestedEditorActionProcessor")
public class NestedEditorActionProcessor extends CustomProcessor {

     ObjectMapper mapper;

    SocketSessionService service;

    @Autowired
    public NestedEditorActionProcessor(
            SocketSessionService socketService,
            @Qualifier("nestedActionQueue") BlockingQueue<EditorAction> nestedActionQueue,
            @Qualifier("sessionStateResponseQueue") BlockingQueue<SessionStateResponse> sessionStateResponseQueue,
            @Qualifier("actionResponseQueue") BlockingQueue<EditorActionResponse> actionResponseQueue
    ) {
        
        this.nestedActionQueue = nestedActionQueue;
        this.actionResponseQueue = actionResponseQueue;
        this.sessionStateResponseQueue = sessionStateResponseQueue;
        this.service = socketService;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }
NestedEditorActionProcessor(){
    
}
    private BlockingQueue<EditorActionResponse> actionResponseQueue = null;
    private BlockingQueue<SessionStateResponse> sessionStateResponseQueue = null;

    private BlockingQueue<EditorAction> nestedActionQueue = null;

    @Override
    public void run() {
        while (!isClosed) {
            EditorAction action = null;
            try {
                
                try {
                    action = nestedActionQueue.take();
                    System.out.println("stuff taken");
                } catch (InterruptedException ex) {
                    Logger.getLogger(ActionResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                switch (action.getAction()) {
                    
                    case DELETE:
                          
                      
                        System.out.println("DELETE REQUEST " + action.getTarget().getTarget_id() + " parent:" + action.getTarget().getParent_id());
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
                        break;
                        
                         case CREATE:
                   
                    //----------------------------------------------------------------------

                    if (action.getExtra().containsKey("create_method")) {
                        System.out.println(action.getExtra().get("create_method"));
                        DiagramObject obj=mapper.readValue(action.getJson(),DiagramObject.class);
                          
                            
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
                            
                              
                                   
                            }else if(obj instanceof PackageObject){
                                PackageObject casted=(PackageObject)obj;
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
                            }
                            else if(obj instanceof DiagramObject){
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
                     else {
                        System.out.println("can not create obj");
                    }

             

                break;
  case UPDATE://-------------------------------------------------------------------
                    
                

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

                  
//+service.getSessionStateById(action.getTarget().getTarget_id()).getLockerUser_id()+" has lock on this.\n object restored.");
                    
           
                break;
  case S_INJECT_CLASS_HEADER_TO_PACKAGE:
      this.service.injectClassHeaderToParentsPackageObject(action.getTarget().getTarget_id());
     // this.service.injectClassHeaderToParentsPackageObject((SimpleClass)((ServerSideEditorAction)action).getLoad());
      //.S_DELETE_CLASS_HEADER_FROM_PARENT_PACKAGE
      break;
  case S_UPDATE_CLASS_HEADER_TO_PARENT_PACKAGE:
      this.service.updateClassHeaderToParentsPackageObject((action.getTarget().getTarget_id()));
      break;
       case S_DELETE_CLASS_HEADER_FROM_PARENT_PACKAGE:
      this.service.deleteClassSoRemoveItFromParentPackageObject((PackageObject)((ServerSideEditorAction)action).getLoad());
      break;

                }
            } catch (JwtParseException ex) {
                Logger.getLogger(NestedEditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotFoundException ex) {
                try {//ha beakad valami, akkor a hamis elem törlését még el lehessen küldeni
                    sendAll(action, Q.ACTION);
                } catch (JwtParseException ex1) {
                    Logger.getLogger(NestedEditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (JsonProcessingException ex) {
                Logger.getLogger(NestedEditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }catch (Exception ex) {
                Logger.getLogger(NestedEditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
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

  protected  void sendRestoreMessage(EditorAction action, String message) throws JsonProcessingException, JwtParseException,NotFoundException {
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
