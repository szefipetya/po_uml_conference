/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management;

import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.model.common.management.ICON;
import com.szefi.uml_conference.model.common.management.Permission;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */
@Entity
public class File_cEntity implements Serializable {
    @GeneratedValue
    @Id
        private String id;
    private String name;
    @ManyToOne
    private FolderEntity parentFolder ;
    @OneToOne
    private UserEntity owner;
    @OneToMany(mappedBy = "file")
    private List<PermissionEntity> permissions;
    private Date date;
    private ICON icon ;  

    protected File_cEntity(){}
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FolderEntity getParent_folder() {
        return parentFolder;
    }

    public void setParent_folder(FolderEntity parent_folder) {
        this.parentFolder = parent_folder;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

   
    public List<PermissionEntity> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionEntity> permissions) {
        this.permissions = permissions;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ICON getIcon() {
        return icon;
    }

    public void setIcon(ICON icon) {
        this.icon = icon;
    }
    
}
