/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.services;

import com.szefi.uml_conference._exceptions.JwtException;
import com.szefi.uml_conference._exceptions.JwtExpiredException;
import com.szefi.uml_conference._exceptions.UnAuthorizedActionException;
import com.szefi.uml_conference._exceptions.management.FileNotFoundException;
import com.szefi.uml_conference._exceptions.management.FileTypeConversionException;
import com.szefi.uml_conference._exceptions.management.IllegalDmlActionException;
import com.szefi.uml_conference._exceptions.management.UnstatisfiedNameException;
import com.szefi.uml_conference.management.repository.File_cRepository;
import com.szefi.uml_conference.model.dto.management.FolderDto;
import com.szefi.uml_conference.model.dto.management.project.ProjectDto;
import com.szefi.uml_conference.model.dto.management.project.ProjectFolderDto;
import com.szefi.uml_conference.model.dto.management.response.FileResponse;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFolderEntity;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 */
@Service
public class ProjectManagementService {
      @Autowired
    UserRepository userRepo;
    @Autowired
    File_cRepository fileRepo;
    @Autowired
    JwtUtilService jwtService;
    @Autowired
    MyUserDetailsService userService;
    
     public FileResponse createProject(String jwt, Integer parent_id, String name) throws 
            JwtException, UnAuthorizedActionException,UnstatisfiedNameException, IllegalDmlActionException {
        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        if(name.equals("")) throw new UnstatisfiedNameException(" name ["+name+"] is not vaild");
        UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();

        File_cEntity fent = fileRepo.findById(parent_id).get();
         ProjectEntity projectToAdd = new ProjectEntity(user);
         
        projectToAdd.setName(name);

        if (!fent.getOwner().getId().equals(user.getId())) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the parend folder");
        }
        if (fent instanceof FolderEntity) {
            FolderEntity parentFolderEnt = (FolderEntity) fent;
            parentFolderEnt.addFile(projectToAdd);
          //  projectToAdd.getRootFolder().setParentFolder(parentFolderEnt);
            this.fileRepo.save(projectToAdd);

            this.fileRepo.save(parentFolderEnt);
            if (user != null) {
                FileResponse resp = new FileResponse(parentFolderEnt,new FolderDto(parentFolderEnt));
              

                return resp;

            }
        }else{
            throw new IllegalDmlActionException("the parent's type is not a Folder");
        }

        return null;
    }
     /**
      * 
      * @param jwt
      * @param parent_id The id of the projectfolder where the new folder will be created
      * @param name
      * @return
      */
      public FileResponse createProjectFolder(String jwt, Integer parent_id, String name) throws 
            JwtException, UnAuthorizedActionException,UnstatisfiedNameException ,FileTypeConversionException{
        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        if(name.equals("")) throw new UnstatisfiedNameException(" name ["+name+"] is not vaild");
        UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();

        File_cEntity fent = fileRepo.findById(parent_id).get();
        ProjectFolderEntity folderToAdd = new ProjectFolderEntity();
        folderToAdd.setName(name);
      

        if (!fent.getOwner().getId().equals(user.getId())) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the parend folder");
        }
        if (fent instanceof ProjectFolderEntity) {
            ProjectFolderEntity parentFolderEnt = (ProjectFolderEntity) fent;
             // folderToAdd.setProject(parentFolderEnt.getProject());
            parentFolderEnt.addFile(folderToAdd);
            this.fileRepo.save(folderToAdd);
            
            this.fileRepo.save(parentFolderEnt);
          //  this.fileRepo.save(parentFolderEnt.getProject());
            if (user != null) {
                FileResponse resp = 
                        new FileResponse(parentFolderEnt,new ProjectFolderDto(parentFolderEnt));
              

                return resp;

            }
        }
        else throw new FileTypeConversionException("The action can't be perforemed on the object, defined by [id="+parent_id+"]");
      return null;
    }
     public FileResponse getProject(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException, UnAuthorizedActionException {

        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        //UserEntity user=userRepo.findByUserName(jwtService.extractUsername(jwt)).get();
        try {
            File_cEntity ent= fileRepo.findById(id).get();
            if(ent instanceof ProjectEntity){
                     if (ent.getOwner().getId().equals(userService.loadUserByUsername(jwtService.extractUsername(jwt)).getId())) {
                ProjectEntity casted=(ProjectEntity)ent;
                ProjectDto dto=new ProjectDto(casted);
                   FileResponse resp= new FileResponse(((ProjectEntity) ent).getRootFolder(),dto.getRootFolderDto());
      
                return resp;
            }else throw new UnAuthorizedActionException("The action can't be performed. Reason: Unauthorized ");
            }else throw new FileTypeConversionException("The action can't be perforemed on the object, defined by [id="+id+"]");

        } catch (java.util.NoSuchElementException ex) {
            throw new FileNotFoundException("requested project with id=" + id + " not found");
        } catch (ClassCastException ex) {
            throw new FileTypeConversionException("requested project is not a project id=" + id + " ");
        }
        
    }
      public FileResponse getProjectFolder(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException {

        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        //UserEntity user=userRepo.findByUserName(jwtService.extractUsername(jwt)).get();
        try {
            File_cEntity ent= fileRepo.findById(id).get();
         
            if (ent.getOwner().getId().equals(userService.loadUserByUsername(jwtService.extractUsername(jwt)).getId())) {
                   ProjectFolderDto dto = new ProjectFolderDto((ProjectFolderEntity)ent);
                FileResponse resp= new FileResponse(ent,dto);
              
                return resp;
            }
        } catch (java.util.NoSuchElementException ex) {
            throw new FileNotFoundException("requested folder with id=" + id + " not found");
        } catch (ClassCastException ex) {
            throw new FileTypeConversionException("requested folder is not a folder id=" + id + " ");
        }
        return null;
    }
    public FileResponse deleteProjectFolder(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException, IllegalDmlActionException, UnAuthorizedActionException {
              if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        } 
                  File_cEntity ent= fileRepo.findById(id).get();
                  
                  if (ent.getOwner().getId().equals(userService.loadUserByUsername(jwtService.extractUsername(jwt)).getId())) {
                      if(ent instanceof ProjectFolderEntity){
                      ProjectFolderEntity casted=(ProjectFolderEntity)ent;
                      if( casted.getParentProjectFolder()!=null){
                         ProjectFolderEntity parent=casted.getParentProjectFolder();
                            //delete folder
                            fileRepo.deleteById(id);
                            //return parent folderű
                            ProjectFolderDto dto = new ProjectFolderDto(parent);
                            FileResponse resp= new FileResponse(parent,dto);
                            return resp;
                        }else throw new IllegalDmlActionException("This File can not be deleted");       
                      }else throw new FileTypeConversionException("this file can not be deleted");
                        
                  }else throw new UnAuthorizedActionException("you don't have the privilege to execute this action");
                 // return null;
        }
}
