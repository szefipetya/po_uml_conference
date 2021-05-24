/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket.tech;

import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author h9pbcl
 */
public class UserWebSocketWrapper {
    private WebSocketSession actionSocket;
    private WebSocketSession stateSocket;
    private Integer user_id;
    private String session_jwt;
    private String color="#fff";

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public WebSocketSession getActionSocket() {
        return actionSocket;
    }

    public void setActionSocket(WebSocketSession actionSocket) {
        this.actionSocket = actionSocket;
    }

    public WebSocketSession getStateSocket() {
        return stateSocket;
    }

    public void setStateSocket(WebSocketSession stateSocket) {
        this.stateSocket = stateSocket;
    }

    public String getSession_jwt() {
        return session_jwt;
    }

    public void setSession_jwt(String session_jwt) {
        this.session_jwt = session_jwt;
    }

    public UserWebSocketWrapper() {
      //  this.socket = socket;
        user_id=-1;
    }

   

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
