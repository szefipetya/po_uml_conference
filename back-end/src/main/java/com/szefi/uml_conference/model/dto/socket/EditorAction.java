/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.socket;

import java.io.Serializable;

/**
 *
 * @author h9pbcl
 */
public class EditorAction implements Serializable,Comparable<EditorAction>{
    private String user_id;
private String id;
private String json;
private ACTION_TYPE action;

  
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public ACTION_TYPE getAction() {
        return action;
    }

    public void setAction(ACTION_TYPE action) {
        this.action = action;
    }

    @Override
    public int compareTo(EditorAction o) {
        if(o!=null)
         return o.getId().compareTo(this.id);
        else return 1;
    }

}
