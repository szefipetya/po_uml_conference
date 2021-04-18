/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management.project;

import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class ProjectFileEntity extends File_cEntity {

   @ManyToOne
    private ProjectFolderEntity parentProjectFolder;

   @ManyToOne
    protected ProjectEntity project;

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    
    @ManyToOne
    protected ProjectEntity relatedProject;

    public ProjectEntity getRelatedProject() {
        return relatedProject;
    }

    public void setRelatedProject(ProjectEntity relatedProject) {
        this.relatedProject = relatedProject;
    }

   
    public ProjectFileEntity() {
            super();
    }

    

    public ProjectFolderEntity getParentProjectFolder() {
        return parentProjectFolder;
    }

    public void setParentProjectFolder(ProjectFolderEntity parentProjectFolder) {
        this.parentProjectFolder = parentProjectFolder;
    }
  
 
  
}
