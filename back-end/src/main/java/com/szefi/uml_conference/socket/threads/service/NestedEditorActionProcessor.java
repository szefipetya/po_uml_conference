/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.threads.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.Response.EditorActionResponse;
import com.szefi.uml_conference.editor.model.socket.Response.RESPONSE_SCOPE;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.Response.TARGET_TYPE;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.socket.threads.ActionResponseProcessor;
import com.szefi.uml_conference.socket.threads.CustomProcessor;
import com.szefi.uml_conference.socket.threads.EditorActionProcessor;
import com.szefi.uml_conference.socket.threads.EditorActionProcessor.Q;
import static com.szefi.uml_conference.socket.threads.service.SOCKET.STATE;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
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
            try {
                EditorAction action = null;
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
                }
            } catch (JwtParseException ex) {
                Logger.getLogger(NestedEditorActionProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     
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
                SessionStateResponse resp = new SessionStateResponse(
                        service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                        action.getId());
                  resp.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_id(action.getTarget().getTarget_id());
                sessionStateResponseQueue.add(resp);
                break;
            case ACTION:
                EditorActionResponse resp2 = new EditorActionResponse(action);
                  resp2.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp2.setTarget_id(action.getTarget().getTarget_id());
                actionResponseQueue.add(resp2);
                break;
        }
    }

   protected void sendBackPrivate(EditorAction action, Q queue, String msg) throws JwtParseException {
        switch (queue) {
            case STATE:
                SessionStateResponse resp = new SessionStateResponse(
                        service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                        action.getId());
              //  resp.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp.setTarget_user_id(action.getUser_id());
                resp.setTarget_id(action.getTarget().getTarget_id());
                resp.setScope(RESPONSE_SCOPE.PRIVATE);
                sessionStateResponseQueue.add(resp);
                break;
            case ACTION:
                EditorActionResponse resp2 = new EditorActionResponse(action);
                //resp2.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
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

    protected void sendCustomMessage(Integer target_id, EditorAction action, Q queue, TARGET_TYPE target_type, RESPONSE_SCOPE response_scope, String message) throws JwtParseException {

        switch (queue) {
            case ACTION:
                EditorActionResponse resp2 = new EditorActionResponse(action);
                resp2.setTarget_id(target_id);
                resp2.setTarget_user_id(action.getUser_id());
                resp2.setTarget_type(target_type);
                resp2.getAction().getTarget().setTarget_id(target_id);
                resp2.setScope(response_scope);
                  resp2.setTargetsUsers(service.getUserSocketsByToken(action.getSession_jwt()));
                resp2.setResponse_msg(message);
                actionResponseQueue.add(resp2);
                break;
            case STATE:
                SessionStateResponse resp = new SessionStateResponse(
                        service.tokenToSession(action.getSession_jwt()).getSessionStateById(target_id),
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

    protected void sendCustomMessage(EditorAction action, Q queue, TARGET_TYPE target_type, RESPONSE_SCOPE response_scope, String message) throws JwtParseException {

        switch (queue) {
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
                        service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
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

    public enum Q {
        STATE, ACTION
    }

  protected  void sendRestoreMessage(EditorAction action, String message) throws JsonProcessingException, JwtParseException {
        SessionStateResponse resp = new SessionStateResponse(
                service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                action.getId(), action.getTarget().getTarget_id(), action.getUser_id());
        resp.setTarget_id(action.getTarget().getTarget_id());
        resp.setTarget_user_id(action.getUser_id());

        sessionStateResponseQueue.add(resp);
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.

        action.setAction(ACTION_TYPE.RESTORE);
        action.setJson(mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getItemById(action.getTarget().getTarget_id())));
        EditorActionResponse resp2 = new EditorActionResponse(action, action.getUser_id());
        resp2.setTarget_user_id(action.getUser_id());
        resp2.setTarget_id(action.getTarget().getTarget_id());
        resp2.setScope(RESPONSE_SCOPE.PRIVATE);
        resp2.setResponse_msg(message);
        actionResponseQueue.add(resp2);

        System.out.println("object restoration is sent");
    }

   protected void sendDeleteRestoreMessage(EditorAction action, String message) throws JsonProcessingException, JwtParseException {
        SessionStateResponse resp = new SessionStateResponse(
                service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id()),
                action.getId(), action.getTarget().getTarget_id(), action.getUser_id());
        resp.setTarget_id(action.getTarget().getTarget_id());
        resp.setTarget_user_id(action.getUser_id());
        sessionStateResponseQueue.add(resp);
        //Vissza kell állítani az objektet, mert illetéktelenül próbálta meg kiválasztani.

        action.setAction(ACTION_TYPE.RESTORE);
        action.getExtra().put("sessionState", mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getSessionStateById(action.getTarget().getTarget_id())));
        action.setJson(mapper.writeValueAsString(service.tokenToSession(action.getSession_jwt()).getItemById(action.getTarget().getTarget_id())));
        EditorActionResponse resp2 = new EditorActionResponse(action, action.getUser_id());
        resp2.setTarget_user_id(action.getUser_id());
        resp2.setTarget_id(action.getTarget().getTarget_id());
        resp2.setScope(RESPONSE_SCOPE.PRIVATE);
        resp2.setResponse_msg(message);
        actionResponseQueue.add(resp2);

        System.out.println("object restoration is sent");
    }

    
}
