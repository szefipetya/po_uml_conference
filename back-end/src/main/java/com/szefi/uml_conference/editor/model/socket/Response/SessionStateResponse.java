/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket.Response;

import com.szefi.uml_conference.editor.model.socket.SessionState;

/**
 *
 * @author h9pbcl
 */
public class SessionStateResponse extends CustomResponse {
    private SessionState sessionState;
    private String action_id;
    

    
    public SessionStateResponse(SessionState sessionState, String action_id, Integer target_id, Integer target_user_id) {
        this.sessionState = sessionState;
        this.action_id = action_id;
        this.target_id = target_id;
        this.target_user_id = target_user_id;
        this.scope=RESPONSE_SCOPE.PRIVATE;
                this.target_type=TARGET_TYPE.ITEM;

        
    }

   


    public SessionStateResponse(SessionState sessionState, String action_id) {
        this.sessionState = sessionState;
        this.action_id = action_id;
        scope=RESPONSE_SCOPE.PUBLIC;
        this.target_type=TARGET_TYPE.ITEM;
    }

    public SessionStateResponse(SessionState sessionState, String action_id, RESPONSE_SCOPE scope) {
        this.sessionState = sessionState;
        this.action_id = action_id;
        this.scope = scope;
        this.target_type=TARGET_TYPE.ITEM;
    }

    public String getAction_id() {
        return action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public SessionState getSessionState() {
        return sessionState;
    }

    public void setSessionState(SessionState sessionState) {
        this.sessionState = sessionState;
    }
    
}
