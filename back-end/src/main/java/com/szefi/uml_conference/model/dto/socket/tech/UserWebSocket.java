/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.socket.tech;

import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author h9pbcl
 */
public class UserWebSocket {
    private WebSocketSession socket;
    private String user_id;

    public UserWebSocket(WebSocketSession socket) {
        this.socket = socket;
        user_id="";
    }

    public WebSocketSession getSocket() {
        return socket;
    }

    public void setSocket(WebSocketSession socket) {
        this.socket = socket;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
