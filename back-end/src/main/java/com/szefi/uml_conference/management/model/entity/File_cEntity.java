/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.entity;

import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.model.common.management.ICON;
import com.szefi.uml_conference.model.common.management.Permission;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author h9pbcl
 */
@Entity
@Table(name="files")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class File_cEntity implements Serializable {
  //   @GenericGenerator(name = "client_id", strategy = "com.szefi.uml_conference.model.generator.IdGenerator")
  //  @GeneratedValue(generator = "client_id")  
    @GeneratedValue
    @Id
        private Integer id;
    private String name;
    @OneToOne
    private FolderEntity parentFolder ;
    @ManyToOne
    private UserEntity owner;
    @ManyToMany(mappedBy = "files")
    private List<PermissionEntity> permissions;
    private Date date;
    private ICON icon ;  

    @ManyToMany(mappedBy="sharedFilesWithMe")
        @LazyCollection(LazyCollectionOption.FALSE)
        Set<UserEntity> usersIamSaredWith=new HashSet<>();

    public Set<UserEntity> getUsersIamSaredWith() {
        return usersIamSaredWith;
    }

    public void setUsersIamSaredWith(Set<UserEntity> usersIamSaredWith) {
        this.usersIamSaredWith = usersIamSaredWith;
    }
    public File_cEntity(){
        this.date=new Date();
        this.icon=ICON.FILE;
        this.name="New file";
      
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FolderEntity getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FolderEntity parentFolder) {
        this.parentFolder = parentFolder;
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
