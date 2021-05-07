/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.common.management.ICON;
import com.szefi.uml_conference.management.model.dto.FileHeaderDto;
import com.szefi.uml_conference.management.model.dto.FolderDto;
import com.szefi.uml_conference.management.model.dto.FolderHeaderDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFileDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFileHeaderDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFolderDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFolderHeaderDto;
import com.szefi.uml_conference.security.model.User_PublicDto;

/**
 *
 * @author h9pbcl
 */


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.PROPERTY,
        property="_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=FileHeader_I.class, name="file"),
        @JsonSubTypes.Type(value=FolderDto.class, name="FolderDto"),
        @JsonSubTypes.Type(value=FolderHeaderDto.class, name="folder"),
        @JsonSubTypes.Type(value=ProjectDto.class, name="project"),
        @JsonSubTypes.Type(value=ProjectFolderDto.class, name="projectFolderDto"),
        @JsonSubTypes.Type(value=ProjectFileDto.class, name="projectFileDto"),
        @JsonSubTypes.Type(value=ProjectFileHeaderDto.class, name="projectFile"),
        @JsonSubTypes.Type(value=ProjectFolderHeaderDto.class, name="projectFolder"),
})
@JsonTypeName(value = "file")
public interface FileHeader_I {
     public Integer getId() ;

    public void setId(Integer id) ;

    public String getName() ;

    public void setName(String name) ;

    public Integer getParentFolder_id() ;

    public void setParentFolder_id(Integer parentFolder_id);
  

    public User_PublicDto getOwner();

    public void setOwner(User_PublicDto owner) ;
    public ICON getIcon() ;

    public void setIcon(ICON icon);
}
