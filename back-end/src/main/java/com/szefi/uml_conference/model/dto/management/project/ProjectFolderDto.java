/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.management.project;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.management.FileHeaderDto;
import com.szefi.uml_conference.model.dto.management.FolderHeaderDto;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFileEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFolderEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "projectFolderDto")
public class ProjectFolderDto extends ProjectFileDto{

    
       List<ProjectFileHeaderDto> files=new ArrayList<>();

    public List<ProjectFileHeaderDto> getFiles() {
        return files;
    }

    public ProjectFolderDto(ProjectFolderEntity ent) {
        super(ent);
        this.is_projectRoot=ent.isIs_projectRoot();
        this.setId(ent.getId());
        setIcon(ent.getIcon());
       for(File_cEntity fent:ent.getFiles()){
           if(fent instanceof ProjectFolderEntity){
               ProjectFolderEntity pfolder=(ProjectFolderEntity)fent;
               ProjectFolderHeaderDto pfolderDto=new ProjectFolderHeaderDto(pfolder);
              this.files.add(pfolderDto);
           }
           else if(fent instanceof ProjectFileEntity){
               ProjectFileEntity pfile=(ProjectFileEntity)fent;
               ProjectFileHeaderDto pfiledto=new ProjectFileHeaderDto(pfile);
              this.files.add(pfiledto);
           }
       }
    }

    public void setFiles(List<ProjectFileHeaderDto> files) {
        this.files = files;
    }

  
  boolean is_projectRoot;

    public boolean isIs_projectRoot() {
        return is_projectRoot;
    }

    public void setIs_projectRoot(boolean is_projectRoot) {
        this.is_projectRoot = is_projectRoot;
    }
    public ProjectFolderDto() {
    }

  
    
}
