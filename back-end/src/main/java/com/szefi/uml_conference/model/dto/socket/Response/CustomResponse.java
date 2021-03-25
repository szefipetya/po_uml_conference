/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.socket.Response;

/**
 *
 * @author h9pbcl
 */
public class CustomResponse {
    protected    RESPONSE_SCOPE scope;
    protected  String target_user_id;
    protected String response_msg;
    protected String target_id;

    public TARGET_TYPE getTarget_type() {
        return target_type;
    }

    public void setTarget_type(TARGET_TYPE target_type) {
        this.target_type = target_type;
    }
    protected TARGET_TYPE target_type;

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public RESPONSE_SCOPE getScope() {
        return scope;
    }

    public void setScope(RESPONSE_SCOPE scope) {
        this.scope = scope;
    }

    public String getTarget_user_id() {
        return target_user_id;
    }

    public void setTarget_user_id(String target_user_id) {
        this.target_user_id = target_user_id;
    }

    public String getResponse_msg() {
        return response_msg;
    }

    public void setResponse_msg(String response_msg) {
        this.response_msg = response_msg;
    }
}
