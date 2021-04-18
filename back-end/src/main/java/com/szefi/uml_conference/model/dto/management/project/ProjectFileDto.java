/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.management.project;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.management.File_cDto;
import com.szefi.uml_conference.model.dto.management.interfaces.FileHeader_I;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFileEntity;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "projectFileDto")
public class ProjectFileDto extends File_cDto implements FileHeader_I {

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
