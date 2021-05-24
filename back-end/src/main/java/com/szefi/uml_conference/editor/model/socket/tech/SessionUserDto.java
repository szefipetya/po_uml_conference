/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket.tech;

import com.szefi.uml_conference.security.model.User_PublicDto;

/**
 *
 * @author h9pbcl
 */
public class SessionUserDto {
    User_PublicDto user;
    String color="transparent";

    public User_PublicDto getUser() {
        return user;
    }

    public void setUser(User_PublicDto user) {
        this.user = user;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
