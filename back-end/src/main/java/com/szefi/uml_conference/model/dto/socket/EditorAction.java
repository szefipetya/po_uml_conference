/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.socket;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author h9pbcl
 */
public class EditorAction implements Serializable,Comparable<EditorAction>{
    private String user_id;
private String id;
private String json;
private ACTION_TYPE action;
private ActionTarget target;

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

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

}
