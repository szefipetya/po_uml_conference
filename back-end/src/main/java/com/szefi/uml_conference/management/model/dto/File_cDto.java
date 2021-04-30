/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.szefi.uml_conference.model.common.management.ICON;
import com.szefi.uml_conference.management.model.dto.interfaces.FileHeader_I;
import com.szefi.uml_conference.management.model.dto.project.ProjectDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFileDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFileHeaderDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFolderDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFolderHeaderDto;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.FolderEntity;
import com.szefi.uml_conference.management.model.entity.PermissionEntity;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.model.User_PublicDto;
import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */


public class File_cDto implements FileHeader_I {
    private Integer id;
    private String name;
    private Integer parentFolder_id ;
    private User_PublicDto owner;
   
private ICON icon ;  
    public File_cDto() {
    }
 public File_cDto(File_cEntity ent) {
     this.id=ent.getId();
     this.icon=ent.getIcon();
     this.name=ent.getName();
     if(ent.getParentFolder()!=null)
        this.parentFolder_id=ent.getParentFolder().getId();
     this.owner=ent.getOwner().makeDto();
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

    public Integer getParentFolder_id() {
        return parentFolder_id;
    }

    public void setParentFolder_id(Integer parentFolder_id) {
        this.parentFolder_id = parentFolder_id;
    }

  

    public User_PublicDto getOwner() {
        return owner;
    }

    public void setOwner(User_PublicDto owner) {
        this.owner = owner;
    }

    public ICON getIcon() {
        return icon;
    }

    public void setIcon(ICON icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object obj) {
        return ((File_cEntity)obj).getId().equals(this.getId());
    }
    
}
