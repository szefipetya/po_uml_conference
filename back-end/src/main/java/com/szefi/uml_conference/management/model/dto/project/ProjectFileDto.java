/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto.project;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.management.model.dto.File_cDto;
import com.szefi.uml_conference.management.model.dto.interfaces.FileHeader_I;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectFileEntity;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "projectFileDto")
public class ProjectFileDto extends File_cDto{

    public ProjectFileDto() {
    }
    Integer project_id;

    public Integer getProject_id() {
        return project_id;
    }
    public ProjectFileDto(ProjectFileEntity ent) {
        super(ent);
        if(ent.getParentProjectFolder()!=null)
            this.setParentFolder_id(ent.getParentProjectFolder().getId());//this id is special, its a ProjectFolder!!
        this.project_id=ent.getRelatedProject().getId();
    }
 
}
