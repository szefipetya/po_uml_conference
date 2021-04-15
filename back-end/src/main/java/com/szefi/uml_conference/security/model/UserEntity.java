/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model;

import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.security.converter.RoleListConverter;
import com.szefi.uml_conference.security.model.ROLE;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */
        @Entity
public class UserEntity {
    @GeneratedValue
    @Id
    private Integer id;
    private String userName;
    private String Email;
    private String password;
    private String name;
    
    @OneToOne(mappedBy = "owner")//,cascade=CascadeType.PERSIST)
    private FolderEntity rootFolder;
   // @ManyToMany//(cascade=CascadeType.PERSIST)
    @Convert(converter = RoleListConverter.class)
    private List<ROLE> roles;

    public List<ROLE> getRoles() {
        return roles;
    }

    public void setRoles(List<ROLE> roles) {
        this.roles = roles;
    }

    public FolderEntity getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(FolderEntity rootFolder) {
        this.rootFolder = rootFolder;
    }

    public Integer getId() {
        return id;
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
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
