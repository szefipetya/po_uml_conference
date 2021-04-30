/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto.request;

/**
 *
 * @author h9pbcl
 */
public class FileShareRequest {
        Integer file_id;
        String auth_jwt;
        String target_userName;

    public Integer getFile_id() {
        return file_id;
    }

    public void setFile_id(Integer file_id) {
        this.file_id = file_id;
    }

    public String getAuth_jwt() {
        return auth_jwt;
    }

    public void setAuth_jwt(String auth_jwt) {
        this.auth_jwt = auth_jwt;
    }

    public String getTarget_userName() {
        return target_userName;
    }

    public void setTarget_userName(String target_userName) {
        this.target_userName = target_userName;
    }
}
