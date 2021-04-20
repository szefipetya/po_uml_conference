/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket.Response;

import com.szefi.uml_conference.editor.model.socket.EditorAction;

/**
 *
 * @author h9pbcl
 */
public class EditorActionResponse extends CustomResponse {
    private EditorAction action;
    public EditorActionResponse(EditorAction action, Integer target_user_id) {
        this.action = action;
        this.target_user_id = target_user_id;
        this.scope=RESPONSE_SCOPE.PRIVATE;
        this.target_type=TARGET_TYPE.ITEM;
    }
  public EditorActionResponse(EditorAction action) {
        this.action = action;
        this.scope=RESPONSE_SCOPE.PUBLIC;
        this.target_type=TARGET_TYPE.ITEM;
    }
    public EditorAction getAction() {
        return action;
    }
    public void setAction(EditorAction action) {
        this.action = action;
    }
    
}
