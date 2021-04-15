/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model.auth;

/**
 *
 * @author h9pbcl
 */
public class AuthRequest {
    
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //need default constructor for JSON parsing to work
    public AuthRequest()
    {

    }

    public AuthRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}
