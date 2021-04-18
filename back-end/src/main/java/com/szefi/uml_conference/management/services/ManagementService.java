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
import com.szefi.uml_conference.model.dto.management.FileHeaderDto;
import com.szefi.uml_conference.model.dto.management.File_cDto;
import com.szefi.uml_conference.model.dto.management.FolderDto;
import com.szefi.uml_conference.model.dto.management.FolderHeaderDto;
import com.szefi.uml_conference.model.dto.management.response.FileResponse;
import com.szefi.uml_conference.model.dto.management.response.PathFile;
import com.szefi.uml_conference.model.entity.management.File_cEntity;
import com.szefi.uml_conference.model.entity.management.FolderEntity;
import com.szefi.uml_conference.model.entity.management.project.ProjectFolderEntity;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author h9pbcl
 */
@Service

public class ManagementService {

    @Autowired
    UserRepository userRepo;
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

    public FileResponse getUserRootFolder(String jwt) throws JwtException {
        UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();
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
   
        public FileResponse deleteFolder(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException, IllegalDmlActionException {
              if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        } 
                  File_cEntity ent= fileRepo.findById(id).get();
                  
                  if (ent.getOwner().getId().equals(userService.loadUserByUsername(jwtService.extractUsername(jwt)).getId())) {
                      
                        if(ent.getParentFolder()!=null){
                            Integer parent_id=ent.getParentFolder().getId();
                            //delete folder
                            fileRepo.deleteById(id);
                            //return parent folderű
                            return getFolder(jwt,parent_id);
                        }else throw new IllegalDmlActionException("This File can not be deleted");
                      
                       
                       
                  }
                  return null;
        }
    public FileResponse getFolder(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException {

        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        //UserEntity user=userRepo.findByUserName(jwtService.extractUsername(jwt)).get();
        try {
            File_cEntity ent= fileRepo.findById(id).get();
         
            if (ent.getOwner().getId().equals(userService.loadUserByUsername(jwtService.extractUsername(jwt)).getId())) {
                   FolderDto dto = new FolderDto((FolderEntity)ent);
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

    public FileResponse createFolder(String jwt, Integer parent_id, String name) throws 
            JwtException, UnAuthorizedActionException,UnstatisfiedNameException, FileTypeConversionException {
        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        if(name.equals("")) throw new UnstatisfiedNameException(" name ["+name+"] is not vaild");
        UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();

        File_cEntity fent = fileRepo.findById(parent_id).get();
        if(fent instanceof ProjectFolderEntity){
            return projectService.createProjectFolder(jwt, parent_id, name);
        }
        FolderEntity folderToAdd = new FolderEntity();
        folderToAdd.setName(name);

        if (!fent.getOwner().getId().equals(user.getId())) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the parent folder");
        }
        if (fent instanceof FolderEntity) {
            FolderEntity parentFolderEnt = (FolderEntity) fent;
            parentFolderEnt.addFile(folderToAdd);
            this.fileRepo.save(folderToAdd);

            this.fileRepo.save(fent);
            if (user != null) {
                FileResponse resp = new FileResponse();
                resp.setFile(new FolderDto(parentFolderEnt));

                return resp;

            }
        }

        return null;
    }
   
  
  
}
