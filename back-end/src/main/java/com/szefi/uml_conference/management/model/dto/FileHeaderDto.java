/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.management.model.dto.interfaces.FileHeader_I;
import com.szefi.uml_conference.management.model.entity.File_cEntity;

/**
 *
 * @author h9pbcl
 */

public class FileHeaderDto extends File_cDto implements FileHeader_I {
    public FileHeaderDto(){
        
    }
     public FileHeaderDto(File_cEntity ent){
         super(ent);
    }
}
