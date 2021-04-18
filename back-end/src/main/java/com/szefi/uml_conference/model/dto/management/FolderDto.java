/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.management;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.management.project.ProjectDto;
import com.szefi.uml_conference.model.dto.management.project.ProjectFolderDto;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "FolderDto")
public class FolderDto extends File_cDto{
       List<FileHeaderDto> files=new ArrayList<>();

    public List<FileHeaderDto> getFiles() {
        return files;
    }

    public FolderDto(FolderEntity ent) {
        super(ent);
        this.is_root=ent.isIs_root();
        this.setId(ent.getId());
        setIcon(ent.getIcon());
       for(File_cEntity fent:ent.getFiles()){
           if(fent instanceof FolderEntity){
               FolderEntity folder=(FolderEntity)fent;
               FolderHeaderDto folderDto=new FolderHeaderDto(folder);
              this.files.add(folderDto);
           }else if(fent instanceof ProjectEntity){//Ha projektet érzékelünk, akkor betesszük root folderként a dto-ba
               ProjectEntity pfile=(ProjectEntity)fent;
               ProjectDto pfiledto=new ProjectDto(pfile);
              this.files.add(pfiledto);
           }
           else if(fent instanceof File_cEntity){
               File_cEntity file=(File_cEntity)fent;
               FileHeaderDto filedto=new FileHeaderDto(file);
              this.files.add(filedto);
           }
            
       }
    }

    public void setFiles(List<FileHeaderDto> files) {
        this.files = files;
    }

    public boolean isIs_root() {
        return is_root;
    }

    public void setIs_root(boolean is_root) {
        this.is_root = is_root;
    }
   boolean is_root;

}
