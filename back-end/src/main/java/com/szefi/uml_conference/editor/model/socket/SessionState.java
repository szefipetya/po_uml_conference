/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author h9pbcl
 */
public class SessionState {
    private LOCK_TYPE[] locks;
    private Integer lockerUser_id;
    private  Map<String,String> extra;
    private boolean draft;

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }
    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }
    public SessionState() {
       locks=new LOCK_TYPE[0];
       lockerUser_id=-1;
    }

    public LOCK_TYPE[] getLocks() {
        return locks;
    }

    public void setLocks(LOCK_TYPE[] locks) {
        this.locks = locks;
    }

    public Integer getLockerUser_id() {
        return lockerUser_id;
    }

    public void setLockerUser_id(Integer lockerUser_id) {
        this.lockerUser_id = lockerUser_id;
    }
}
