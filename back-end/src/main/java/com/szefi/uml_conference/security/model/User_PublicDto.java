/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model;

/**
 *
 * @author h9pbcl
 */
public class User_PublicDto {
      private Integer id;
    private String userName;
    private String email;
     private String name;

    public User_PublicDto() {
    }

    public Integer getId() {
        return id;
    }

    public User_PublicDto(UserEntity source) {
        this.name=source.getName();
        this.id=source.getId();
        this.userName=source.getUserName();
        this.email=source.getEmail();
        
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String Email) {
        this.email = Email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
  
}
