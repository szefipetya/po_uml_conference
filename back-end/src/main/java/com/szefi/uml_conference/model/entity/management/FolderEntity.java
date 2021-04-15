/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author h9pbcl
 */
@Entity
public class FolderEntity extends File_cEntity {
    
    @OneToMany(mappedBy = "parentFolder")
   List<File_cEntity> files;
   boolean is_root;

    public List<File_cEntity> getFiles() {
        return files;
    }

    public void setFiles(List<File_cEntity> files) {
        this.files = files;
    }

    public boolean isIs_root() {
        return is_root;
    }

    public void setIs_root(boolean is_root) {
        this.is_root = is_root;
    }
}
