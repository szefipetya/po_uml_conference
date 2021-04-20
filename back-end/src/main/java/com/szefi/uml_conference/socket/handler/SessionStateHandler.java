/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.model.diagram.Diagram;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocket;
import com.szefi.uml_conference.socket.security.SocketSecurityService;
import com.szefi.uml_conference.socket.security.model.SocketAuthenticationRequest;
import com.szefi.uml_conference.socket.threads.SocketThreadManager;
import com.szefi.uml_conference.socket.threads.service.SOCKET;
import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserWebSocket toremove = null;
        for (UserWebSocket s : service.getSockets(SOCKET.STATE)) {
            if (s.getSocket() == null || s.getSocket().equals(session)) {

                EditorAction action = new EditorAction();
                action.setUser_id(s.getUser_id());
                action.setAction(ACTION_TYPE.S_USER_DISCONNECT);
                threadManager.postAction(action);
                toremove = s;

                break;
            }
        }
        if(toremove!=null){
            service.getSockets(SOCKET.STATE).remove(toremove);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //init step to identify the session
        if (initMap.get(session) == 0) {
            for (UserWebSocket u : service.getSockets(SOCKET.STATE)) {
                if (u.getSocket() == session) {
                     SocketAuthenticationRequest req=mapper.readValue(message.getPayload(), SocketAuthenticationRequest.class);
                if(socketSecutiryService.authenticateRequest(req)){
                    //get the session from the service, andi inject the user.
                }
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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserWebSocket s = new UserWebSocket(session);
        service.getSockets(SOCKET.STATE).add(s);
        initMap.put(s.getSocket(), 0);

        Map<Integer, Pair<SessionState, DynamicSerialObject>> a = service.getSessionItemMap();
        List<SessionStateResponse> responses = new ArrayList<>();
        for (Map.Entry<Integer, Pair<SessionState, DynamicSerialObject>> entry : a.entrySet()) {
            SessionStateResponse r = new SessionStateResponse(entry.getValue().getFirst(), "no action");
            r.setTarget_id(entry.getKey());
            //  System.out.println(entry.getValue().getFirst().getLocks().length);
            //  System.out.println(entry.getKey());
            responses.add(r);
        }
        session.sendMessage(new TextMessage(mapper.writeValueAsString(responses)));
    }

}
