/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.management.model.dto.interfaces.FileHeader_I;
import com.szefi.uml_conference.management.model.entity.FolderEntity;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "folder")
public class FolderHeaderDto extends FileHeaderDto  {
    boolean is_root;

    public FolderHeaderDto() {
    }
    
    public FolderHeaderDto(FolderEntity ent) {
       super(ent);
       
       this.is_root=ent.isIs_root();
    }
    
    public boolean isIs_root() {
        return is_root;
    }

    public void setIs_root(boolean is_root) {
        this.is_root = is_root;
    }
}
