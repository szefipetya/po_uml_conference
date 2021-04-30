/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.management.model.dto.project.ProjectDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFolderDto;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.FolderEntity;
import com.szefi.uml_conference.management.model.entity.SPECIAL_FOLDER;
import com.szefi.uml_conference.management.model.entity.project.ProjectEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectFolderEntity;
import com.szefi.uml_conference.security.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if(ent.getSpecial()==SPECIAL_FOLDER.SHARED){
            //ha a shared foldelt kéri le a user, akkor virtuálisan bele kell pakolni a userral megosztott fájlokat.
            addAllFromSource(makeTreeFromSharedFiles(ent.getOwner().getSharedFilesWithMe().stream().collect(Collectors.toList())));
        }else{addAllFromSource(ent.getFiles());}
    
    }
    private List<File_cEntity> makeTreeFromSharedFiles(List<File_cEntity> p_files){
        List<File_cEntity> resList=new ArrayList();
        resList.addAll(p_files);
        for(File_cEntity f:p_files){ 
            if(f instanceof ProjectEntity||f.getName().equals("~")||f instanceof ProjectFolderEntity){
                  resList.remove(f);  
                }
            if(f.getParentFolder()!=null){ 
               // File_cEntity parEntity=f.getParentFolder();
                File_cEntity fEntity=f;
               
                while(fEntity!=null){  
                     if(fEntity.getParentFolder()!=null&&
                             resList.contains(fEntity.getParentFolder())){
                    resList.remove(f);    
                    break;
                }
                     fEntity=fEntity.getParentFolder();
                }
               
            }
           
        }
        return resList;
    }
    private void addAllFromSource(List<File_cEntity> source){
           for(File_cEntity fent:source){
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
