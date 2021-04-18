/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.management.project;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.common.management.ICON;
import com.szefi.uml_conference.model.dto.management.FolderHeaderDto;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFileEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFolderEntity;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "projectFolder")
public class ProjectFolderHeaderDto extends ProjectFileHeaderDto{
      boolean is_projectRoot;

    public boolean isIs_projectRoot() {
        return is_projectRoot;
    }

    public void setIs_projectRoot(boolean is_projectRoot) {
        this.is_projectRoot = is_projectRoot;
    }

    public ProjectFolderHeaderDto() {
    }

    public ProjectFolderHeaderDto(ProjectFolderEntity ent) {
        super(ent);
        this.setIcon(ICON.PROJECT_FOLDER);
    }

  
    
}
