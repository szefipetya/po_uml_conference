/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.socket;

import java.util.List;

/**
 *
 * @author h9pbcl
 */
public class SessionState {
    private List<LOCK_TYPE> locks;
    private String lockerUser_id;

    public List<LOCK_TYPE> getLocks() {
        return locks;
    }

    public void setLocks(List<LOCK_TYPE> locks) {
        this.locks = locks;
    }

    public String getLockerUser_id() {
        return lockerUser_id;
    }

    public void setLockerUser_id(String lockerUser_id) {
        this.lockerUser_id = lockerUser_id;
    }
}
