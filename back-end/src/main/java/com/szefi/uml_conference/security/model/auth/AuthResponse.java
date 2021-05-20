/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model.auth;

import com.szefi.uml_conference.security.model.User_PublicDto;

/**
 *
 * @author h9pbcl
 */
public class AuthResponse {
    private String jwt_token;
    private boolean success;
    private User_PublicDto user;

    public AuthResponse() {
    }

    public User_PublicDto getUser() {
        return user;
    }

    public void setUser(User_PublicDto user) {
        this.user = user;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public AuthResponse(String jwt) {
        this.jwt_token = jwt;
    }

    public String getJwt_token() {
        return jwt_token;
    }
}
