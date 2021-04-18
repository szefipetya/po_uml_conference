/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.management.project;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.common.management.ICON;
import com.szefi.uml_conference.model.entity.management.project.ProjectFileEntity;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "projectFile")
public class ProjectFileHeaderDto extends ProjectFileDto {

    public ProjectFileHeaderDto() {
    }

    public ProjectFileHeaderDto(ProjectFileEntity ent) {
        super(ent);
        this.setIcon(ICON.PROJECT_FILE);
    }
    
}
