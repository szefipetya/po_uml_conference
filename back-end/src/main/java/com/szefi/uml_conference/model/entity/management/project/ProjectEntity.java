/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management.project;

import com.szefi.uml_conference.model.entity.management.File_cEntity;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */

//@Entity
public class ProjectEntity /*extends File_cEntity*/{
    
    
    // @OneToOne(mappedBy="project")
    private ProjectFolderEntity rootFolder;

    public ProjectFolderEntity getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(ProjectFolderEntity rootFolder) {
        this.rootFolder = rootFolder;
    }
}
