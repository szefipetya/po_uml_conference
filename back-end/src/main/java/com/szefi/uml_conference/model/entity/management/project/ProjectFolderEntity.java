/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management.project;

import com.szefi.uml_conference.model.entity.management.File_cEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author h9pbcl
 */
@Entity
public class ProjectFolderEntity extends ProjectFileEntity {
    
 @OneToMany(mappedBy = "parentProjectFolder" ,cascade = CascadeType.REMOVE)
   List<ProjectFileEntity> files;
   boolean is_projectRoot;
   
    public List<ProjectFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<ProjectFileEntity> files) {
        this.files = files;
    }

    public ProjectFolderEntity() {
        
            super();
            files=new ArrayList<>();
    }

    public boolean isIs_projectRoot() {
        return is_projectRoot;
    }

    public void setIs_projectRoot(boolean is_projectRoot) {
        this.is_projectRoot = is_projectRoot;
    }

     public boolean addFile(ProjectFileEntity ent){
        ent.setParentProjectFolder(this);
        ent.setOwner(this.getOwner());
        ent.setRelatedProject(this.relatedProject);
    //    ent.setPermissions(this.getPermissions());
        return this.getFiles().add(ent);
    }


}
