/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.socket.security.model;

/**
 *
 * @author h9pbcl
 */
public class SocketAuthenticationRequest {
    String auth_jwt;
    Integer diagram_id;

    public String getAuth_jwt() {
        return auth_jwt;
    }

    public void setAuth_jwt(String auth_jwt) {
        this.auth_jwt = auth_jwt;
    }

    public Integer getDiagram_id() {
        return diagram_id;
    }

    public void setDiagram_id(Integer diagram_id) {
        this.diagram_id = diagram_id;
    }
}
