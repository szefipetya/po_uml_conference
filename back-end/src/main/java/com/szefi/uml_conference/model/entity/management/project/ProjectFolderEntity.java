/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management.project;

import com.szefi.uml_conference.model.entity.management.File_cEntity;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

/**
 *
 * @author h9pbcl
 */
//@Entity
public class ProjectFolderEntity extends ProjectFileEntity {

    public List<ProjectFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<ProjectFileEntity> files) {
        this.files = files;
    }

    public boolean isIs_root() {
        return is_root;
    }

    public void setIs_root(boolean is_root) {
        this.is_root = is_root;
    }
   // @OneToMany(mappedBy = )
       List<ProjectFileEntity> files;
   boolean is_root;
}
