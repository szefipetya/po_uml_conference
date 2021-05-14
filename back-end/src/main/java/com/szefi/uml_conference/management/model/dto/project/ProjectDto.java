/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto.project;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.management.model.ICON;
import com.szefi.uml_conference.management.model.dto.FileHeaderDto;
import com.szefi.uml_conference.management.model.dto.File_cDto;
import com.szefi.uml_conference.management.model.dto.interfaces.FileHeader_I;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectEntity;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "project")
public class ProjectDto extends FileHeaderDto {

    public ProjectDto() {
    }

    public ProjectDto(ProjectEntity ent) {
        super(ent);
        this.setIcon(ICON.PROJECT);
        this.rootFolderDto=new ProjectFolderDto(ent.getRootFolder());
    }
   
    
    ProjectFolderDto rootFolderDto;

    public ProjectFolderDto getRootFolderDto() {
        return rootFolderDto;
    }

    public void setRootFolderDto(ProjectFolderDto rootFolderDto) {
        this.rootFolderDto = rootFolderDto;
    }
}
