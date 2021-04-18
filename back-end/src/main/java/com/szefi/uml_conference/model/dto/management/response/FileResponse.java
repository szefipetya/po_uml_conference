/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.management.response;

import com.szefi.uml_conference.model.dto.management.FileHeaderDto;
import com.szefi.uml_conference.model.dto.management.File_cDto;
import com.szefi.uml_conference.model.dto.management.FolderHeaderDto;
import com.szefi.uml_conference.model.dto.management.project.ProjectDto;
import com.szefi.uml_conference.model.dto.management.project.ProjectFileHeaderDto;
import com.szefi.uml_conference.model.dto.management.project.ProjectFolderHeaderDto;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFileEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFolderEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author h9pbcl
 */
public class FileResponse {

    public List<PathFile> getPathFiles() {
        return pathFiles;
    }

    public void setPathFiles(List<PathFile> pathFiles) {
        this.pathFiles = pathFiles;
    }

    public File_cDto getFile() {
        return file;
    }

    public FileResponse(File_cEntity file,File_cDto dto) {
        this.file = dto;
      this.pathFiles=new ArrayList<>();
       File_cEntity current=file;
                 int i=0;
                 while(current!=null){
                    if(current instanceof FolderEntity){
                        FolderEntity casted=(FolderEntity)current;
                        getPathFiles().add(new PathFile(i,new FolderHeaderDto(casted)));
                    }
                     else if(current instanceof ProjectFolderEntity){
                       ProjectFolderEntity casted=(ProjectFolderEntity)current;

                            getPathFiles().add(new PathFile(i,new ProjectFolderHeaderDto(casted)));
                      }
                      else if(current instanceof ProjectFileEntity){
                            ProjectFileEntity casted=(ProjectFileEntity)current;
                            getPathFiles().add(new PathFile(i,new ProjectFileHeaderDto(casted)));
                      }
                       else if(current instanceof ProjectEntity){
                            ProjectEntity casted=(ProjectEntity)current;
                            getPathFiles().add(new PathFile(i,new ProjectDto(casted)));
                      }
                    else{
                        getPathFiles().add(new PathFile(i,new FileHeaderDto(current)));
                    }
                    if(current instanceof ProjectFileEntity){
                           ProjectFileEntity casted=(ProjectFileEntity)current;
                           current=casted.getParentProjectFolder();
                           if(current==null){
                               current=casted.getRelatedProject();
                           }
                    }else{
                        current=current.getParentFolder();
                    }
                      i++;
                      
                 }
        
    }

    public FileResponse() {
        this.pathFiles=new ArrayList<>();
    }
    

    public void setFile(File_cDto file) {
        this.file = file;
    }
    List<PathFile> pathFiles;
    File_cDto file;
}
