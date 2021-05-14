/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto.project;

import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.management.model.entity.project.ProjectFileEntity;
import com.szefi.uml_conference.management.model.ICON;
import com.szefi.uml_conference.security.model.User_PublicDto;

/**
 *
 * @author h9pbcl
 */
public class ProjectSimpleClassHeaderDto extends ProjectFileHeaderDto {

    public ProjectSimpleClassHeaderDto(SimpleClass ent) {
       super();
       this.setName(ent.getTitleModel().getName());
       this.setOwner(new User_PublicDto(ent.getDiagram().getOwner()));
       this.setId(ent.getId());//the class's id
         this.setIcon(ICON.PROJECT_CLASS);
    }

    public ProjectSimpleClassHeaderDto() {
    }
    
}
