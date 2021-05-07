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
public class RegistrationRequest {
    String username="";
    String email="";
    String FullName="";
  String password="";
    public String getUsername() {
        return username.trim();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email.trim();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return FullName.trim();
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
  
}
