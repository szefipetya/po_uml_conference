/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author h9pbcl
 */
public class EditorAction implements Serializable,Comparable<EditorAction>{
    private Integer user_id;
private String id;
private String json;
private ACTION_TYPE action;
private ActionTarget target;
private String session_jwt;

    public String getSession_jwt() {
        return session_jwt;
    }

    public void setSession_jwt(String session_jwt) {
        this.session_jwt = session_jwt;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }
Map<String,String> extra;

    public EditorAction() {
        this.extra=new HashMap<>();
        this.target=new ActionTarget();
    }
       public EditorAction(ACTION_TYPE type) {
           this.setAction(type);
        this.extra=new HashMap<>();
        this.target=new ActionTarget();
    }

  

   
    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
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

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

}
