/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.services;

import com.szefi.uml_conference._exceptions.JwtException;
import com.szefi.uml_conference._exceptions.JwtExpiredException;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference._exceptions.UnAuthorizedActionException;
import com.szefi.uml_conference._exceptions.management.FileNotFoundException;
import com.szefi.uml_conference._exceptions.management.FileTypeConversionException;
import com.szefi.uml_conference._exceptions.management.IllegalDmlActionException;
import com.szefi.uml_conference._exceptions.management.UnstatisfiedNameException;
import com.szefi.uml_conference.management.repository.File_cRepository;
import com.szefi.uml_conference.management.model.dto.FileHeaderDto;
import com.szefi.uml_conference.management.model.dto.File_cDto;
import com.szefi.uml_conference.management.model.dto.FolderDto;
import com.szefi.uml_conference.management.model.dto.FolderHeaderDto;
import com.szefi.uml_conference.management.model.dto.request.FileShareRequest;
import com.szefi.uml_conference.management.model.dto.response.FileResponse;
import com.szefi.uml_conference.management.model.dto.response.PathFile;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.FolderEntity;
import com.szefi.uml_conference.management.model.entity.SPECIAL_FOLDER;
import com.szefi.uml_conference.management.model.entity.project.ProjectEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectFolderEntity;
import com.szefi.uml_conference.management.model.ICON;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.PathParam;
import static jdk.nashorn.internal.runtime.Debug.id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author h9pbcl
 */
@Service

public class ManagementService {

 /*  @Autowired
    UserRepository userRepo;*/
    @Autowired
    File_cRepository fileRepo;
    @Autowired
    JwtUtilService jwtService;
    @Autowired
    MyUserDetailsService userService;
    @Autowired
    ProjectManagementService projectService;
    

    public FolderDto limitFolderLevel(FolderEntity ent) {
        return new FolderDto(ent);
    }

    public FileResponse getUserRootFolder(String jwt) throws JwtException, UnAuthorizedActionException {
        UserEntity user = userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));
        if (user != null) {
          
            try {
                return getFolder(jwt,user.getRootFolder().getId());
                //  resp.setFile(new FolderDto(user.getRootFolder()));
            } catch (FileTypeConversionException ex) {
                Logger.getLogger(ManagementService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ManagementService.class.getName()).log(Level.SEVERE, null, ex);
            }         
        }
        return null;
    }
        @Transactional
        public FileResponse deleteFile(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException, IllegalDmlActionException, UnAuthorizedActionException {
              if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        } 
              try{
                  File_cEntity ent= fileRepo.findById(id).get();
                  if (ent.getOwner().getId().equals(userService.loadUserByUsername(jwtService.extractUsername(jwt)).getId())) {
                      boolean canBeDeleted=true;
                      if(ent instanceof FolderEntity){
                          FolderEntity fold=(FolderEntity)ent;
                         canBeDeleted= fold.getSpecial()!=SPECIAL_FOLDER.SHARED&& fold.getSpecial()!=SPECIAL_FOLDER.USER_ROOT;
                      }
                        if(ent.getParentFolder()!=null&&canBeDeleted){
                            Integer parent_id=ent.getParentFolder().getId();
                            //delete folder
                             deleteShareRuleRecursively(ent);
                            fileRepo.delete(ent);
                      
                            //return parent folderű
                            return getFolder(jwt,parent_id);
                        }else throw new IllegalDmlActionException("This File can not be deleted");
   
                }
                   } catch (java.util.NoSuchElementException ex) {
            throw new FileNotFoundException("requested folder with id=" + id + " not found");
        } catch (ClassCastException ex) {
            throw new FileTypeConversionException("requested folder is not a folder id=" + id + " ");
        }
                  return null;
        }
        @Transactional
      private void  deleteShareRuleRecursively(File_cEntity ent){
          fileRepo.deleteShareRulesByFileId(ent.getId());
            if(ent instanceof FolderEntity){
                ((FolderEntity)ent).getFiles().stream().forEach(f->deleteShareRuleRecursively(f));
            }
            if(ent instanceof ProjectEntity){
                 ((ProjectEntity)ent).getRelatedFiles().stream().forEach(f->  fileRepo.deleteShareRulesByFileId(f.getId()));
                     fileRepo.deleteById(ent.getId());
            }
        
            
        }
      @Transactional
    public FileResponse getFolder(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException, UnAuthorizedActionException {

        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        try {
            File_cEntity ent= fileRepo.findById(id).get();
         
       UserEntity actionPerformerUser=userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));
        if(     ent.getOwner().getId().equals(actionPerformerUser.getId())
                       ||ent.getUsersIamSaredWith().stream().anyMatch(u->u.getId().equals(actionPerformerUser.getId()))){
                   FolderDto dto = new FolderDto((FolderEntity)ent);
                FileResponse resp= new FileResponse(ent,dto,actionPerformerUser);
              
                return resp;
            }
        } catch (java.util.NoSuchElementException ex) {
            throw new FileNotFoundException("requested folder with id=" + id + " not found");
        } catch (ClassCastException ex) {
            throw new FileTypeConversionException("requested folder is not a folder id=" + id + " ");
        }
         throw new UnAuthorizedActionException("you don't have access to this file") ;
    }
@Transactional
    public FileResponse createFolder(String jwt, Integer parent_id, String name) throws 
            JwtException, UnAuthorizedActionException,UnstatisfiedNameException, FileTypeConversionException {
        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        if(name.equals("")||name.equals("~")) throw new UnstatisfiedNameException(" name [\""+name+"\"] is not vaild");
        UserEntity user = userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));

        File_cEntity fent = fileRepo.findById(parent_id).get();
        if(fent instanceof ProjectFolderEntity){
            return projectService.createProjectFolder(jwt, parent_id, name);
        }//normal folder
        FolderEntity folderToAdd = new FolderEntity();
        folderToAdd.setName(name);
    
        if (!fent.getOwner().getId().equals(user.getId())) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the parent folder");
        }
        if (fent instanceof FolderEntity) {
            FolderEntity parentFolderEnt = (FolderEntity) fent;
            if(parentFolderEnt.getFiles().stream().filter(f->f.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).size()>0)
                throw new UnstatisfiedNameException(" name ["+name+"] is used by another file in the folder.");
            //inherit share rules
                folderToAdd.getUsersIamSaredWith().addAll(parentFolderEnt.getUsersIamSaredWith());
                      parentFolderEnt.addFile(folderToAdd);    
                      this.fileRepo.save(folderToAdd);
            this.fileRepo.save(fent);
            //update on the user side as well
                      parentFolderEnt.getUsersIamSaredWith().stream().forEach((UserEntity u)->{u.getSharedFilesWithMe().add(folderToAdd);    
                userService.save(u);
                });
      
      
            if (user != null) {
                FileResponse resp = new FileResponse();
                resp.setFile(new FolderDto(parentFolderEnt));

                return resp;

            }
        }

        return null;
    }
    @Transactional
   public FileShareRequest shareFile(FileShareRequest req) throws JwtParseException, JwtExpiredException, FileNotFoundException, UnAuthorizedActionException{
          if (jwtService.isTokenExpired(req.getAuth_jwt())) {
            throw new JwtExpiredException("Error: token expired");
        }
          File_cEntity ent;
             UserEntity targetUser ;
        try {
             ent= fileRepo.findById(req.getFile_id()).get();
              } catch (java.util.NoSuchElementException ex) {
            throw new FileNotFoundException("requested folder with id=" + req.getFile_id()+ " not found");
        }   try{
         targetUser = userService.loadUserEntityByUsername(req.getTarget_userName());
             if(ent instanceof ProjectEntity && ent.getParentFolder().isIs_root()) throw new FileNotFoundException("Project share directly from root is not supported");
             if(ent instanceof ProjectFolderEntity ) throw new FileNotFoundException("This file tyle is not shareable");
        }catch (java.util.NoSuchElementException ex) {
            throw new FileNotFoundException("user with name \"" +req.getTarget_userName()+ "\" not found");
        } 
        MyUserDetails userDets=userService.loadUserByUsername(jwtService.extractUsername(req.getAuth_jwt()));
        if(     ent.getOwner().getId().equals(userDets.getId())
                       ){    
            //add share rules 
           targetUser.getSharedFilesWithMe().add(ent);
          ent.getUsersIamSaredWith().add(targetUser);
          //apply share rules recursively to child files
               updateShareRecursively(ent, targetUser);         
          //fileRepo.save(ent);
          userService.save(targetUser);
                return req;
            }
       
         throw new UnAuthorizedActionException("you are not the owner of this file") ;     
    }
   private void updateShareRecursively(File_cEntity file,UserEntity targetUser){
         updateShareOnFile(file, targetUser);
        if(file instanceof FolderEntity){
         
         ((FolderEntity)file).getFiles().stream().forEach(f->
            updateShareRecursively(f, targetUser)
         ); 
          }        
   }
   private  void updateShareOnFile(File_cEntity file,UserEntity targetUser){
           targetUser.getSharedFilesWithMe().add(file);
          file.getUsersIamSaredWith().add(targetUser);
            fileRepo.save(file);
   }
   
  
  
}
