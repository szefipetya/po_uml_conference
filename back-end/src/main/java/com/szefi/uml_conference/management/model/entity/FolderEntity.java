/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.entity;

import com.szefi.uml_conference.model.common.management.ICON;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author h9pbcl
 */
@Entity
public class FolderEntity extends File_cEntity {
        @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "parentFolder" ,cascade = CascadeType.REMOVE)
   List<File_cEntity> files=new ArrayList<>();
   boolean is_root;

   SPECIAL_FOLDER special=SPECIAL_FOLDER.NONE;

    public SPECIAL_FOLDER getSpecial() {
        return special;
    }

    public void setSpecial(SPECIAL_FOLDER special) {
        this.special = special;
    }
   
    public FolderEntity() {
        super();
        this.setName("New Folder");
        this.setIcon(ICON.FOLDER);
        this.is_root=false;
        
    }
    public boolean addFile(File_cEntity ent){
        ent.setParentFolder(this);
        ent.setOwner(this.getOwner());
    //    ent.setPermissions(this.getPermissions());
        return this.getFiles().add(ent);
    }

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
