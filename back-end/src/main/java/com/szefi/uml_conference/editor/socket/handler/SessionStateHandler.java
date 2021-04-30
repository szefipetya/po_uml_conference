/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.socket.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.model.diagram.Diagram;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.service.EditorSession;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocket;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.editor.socket.security.SocketSecurityService;
import com.szefi.uml_conference.editor.socket.security.model.SocketAuthenticationRequest;
import com.szefi.uml_conference.editor.socket.threads.SocketThreadManager;
import com.szefi.uml_conference.editor.socket.threads.service.SOCKET;
import com.szefi.uml_conference.editor.service.SocketSessionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author h9pbcl
 */
public class SessionStateHandler extends TextWebSocketHandler {

    @Autowired
    SocketSessionService service;
    @Autowired
    SocketThreadManager threadManager;
    Map<WebSocketSession, Integer> initMap = new HashMap<>();
    ObjectMapper mapper;
      @Autowired SocketSecurityService socketSecutiryService;

    public SessionStateHandler() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    Pair<UserWebSocket,EditorSession> latestsessionWhereUserExited;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Pair<UserWebSocket,EditorSession> sockAndSession =this.service.findSessionForNativeSocketAndReturnUserSocket(SOCKET.STATE,session);
        this.latestsessionWhereUserExited=sockAndSession;
       

                EditorAction action = new EditorAction();
                action.getExtra().put("session_id",sockAndSession.getSecond().getId().toString());
                action.setUser_id(sockAndSession.getFirst().getUser_id());
                action.setAction(ACTION_TYPE.S_USER_DISCONNECT);
                threadManager.postAction(action);
        
(new Thread() {
  public void run() {
      try {
          Thread.sleep(5000);
         service.deleteSessionIfEmpty(latestsessionWhereUserExited.getSecond());
      } catch (InterruptedException ex) {
          Logger.getLogger(SessionStateHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
 }).start();

             
      
    }
@Autowired
SocketSecurityService socketSecurityService;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //init step to identify the session
        
        if (initMap.get(session) == 0) {
            for (UserWebSocket u :this.tempSockets) {
                if (u.getStateSocket()== session) {
                    String session_jwt=message.getPayload();
                    // SocketAuthenticationRequest req=mapper.readValue(message.getPayload(), SocketAuthenticationRequest.class);
                          // EditorSession eSession= this.service.autoProcessRequest(socketSecutiryService.authenticateRequest(req),u,req);
                     EditorSession   eSession=  this.service.updateuserWithStateAndGetSession(session_jwt,u.getStateSocket());
                        Map<Integer, Pair<SessionState, DynamicSerialObject>> a = eSession.getSessionItemMap();
        List<SessionStateResponse> responses = new ArrayList<>();
        for (Map.Entry<Integer, Pair<SessionState, DynamicSerialObject>> entry : a.entrySet()) {
            SessionStateResponse r = new SessionStateResponse(entry.getValue().getFirst(), "no action");
            r.setTarget_id(entry.getKey());
            //  System.out.println(entry.getValue().getFirst().getLocks().length);
            //  System.out.println(entry.getKey());
            responses.add(r);
        
        }
        session.sendMessage(new TextMessage(mapper.writeValueAsString(responses)));
                  //  u.setUser_id(message.getPayload());
                    System.out.println(initMap.get(session));
                    System.out.println("session msg" + message.getPayload());
                }
            }

            initMap.replace(session, 1);
        } else {
            //  threadManager.postAction(mapper.readValue(message.getPayload(), EditorAction.class));
            System.out.println("session msg" + message.getPayload());
        }
    }
    List<UserWebSocket> tempSockets=new ArrayList<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserWebSocket s = new UserWebSocket();
        s.setStateSocket(session);
   this.tempSockets.add(s);
        initMap.put(s.getStateSocket(), 0);

     
    }

}
