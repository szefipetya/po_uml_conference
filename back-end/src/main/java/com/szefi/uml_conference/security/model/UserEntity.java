/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.security.converter.RoleListConverter;
import com.szefi.uml_conference.security.model.ROLE;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
    
    @OneToMany(mappedBy = "owner")
    private List<DiagramEntity> diagrams;
    
    @ManyToMany
    @JoinTable(
  name = "diagram_share_table", 
  joinColumns = @JoinColumn(name = "user_id"), 
  inverseJoinColumns = @JoinColumn(name = "diagram_id"))
        @LazyCollection(LazyCollectionOption.FALSE)
    private Set<DiagramEntity> sharedDiagramsWithMe;
    @JsonIgnore
    public Set<DiagramEntity> getSharedDiagramsWithMe() {
        return sharedDiagramsWithMe;
    }

    public void setSharedDiagramsWithMe(Set<DiagramEntity> sharedDiagramsWithMe) {
        this.sharedDiagramsWithMe = sharedDiagramsWithMe;
    }
    
    
    public List<DiagramEntity> getDiagrams() {
        return diagrams;
    }

    public void setDiagrams(List<DiagramEntity> diagrams) {
        this.diagrams = diagrams;
    }
    
    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL)
    private List<File_cEntity> files;

    public List<File_cEntity> getFiles() {
        return files;
    }

    public void setFiles(List<File_cEntity> files) {
        this.files = files;
    }
 
    @Convert(converter = RoleListConverter.class)
    private List<ROLE> roles;

    public UserEntity() {
        diagrams=new ArrayList<>();
        files=new ArrayList<>();
       FolderEntity rootFolder=new FolderEntity();
        rootFolder.setOwner(this);
        rootFolder.setDate(new Date());
        rootFolder.setIs_root(true);
        rootFolder.setName("~");
        this.files.add(rootFolder);
    }
    public  User_PublicDto makeDto(){
      return new User_PublicDto(this);
    }
    
    public List<ROLE> getRoles() {
        return roles;
    }

    public void setRoles(List<ROLE> roles) {
        this.roles = roles;
    }

    public FolderEntity getRootFolder() {
        return (FolderEntity)this.files.stream().filter(f->{
        if(f instanceof FolderEntity){
            return ((FolderEntity)f).isIs_root();   
        }
        return false;
        }).collect(Collectors.toList()).get(0);
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
