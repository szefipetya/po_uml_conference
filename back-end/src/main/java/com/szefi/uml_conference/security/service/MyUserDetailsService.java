/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.service;

/**
 *
 * @author h9pbcl
 */

import com.szefi.uml_conference._exceptions.JwtExpiredException;
import com.szefi.uml_conference._exceptions.UnAuthorizedActionException;
import com.szefi.uml_conference._exceptions.management.UnstatisfiedNameException;
import com.szefi.uml_conference.management.model.dto.FolderDto;
import com.szefi.uml_conference.management.model.dto.response.FileResponse;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.FolderEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectFolderEntity;
import com.szefi.uml_conference.management.repository.File_cRepository;
import com.szefi.uml_conference.management.services.ManagementService;
import com.szefi.uml_conference.security.model.ROLE;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.model.MyUserDetails;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@Service
public class MyUserDetailsService implements UserDetailsService {

    
    @Autowired
    UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByUserName(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));
        return user.map(MyUserDetails::new).get();
    }
     
    public UserEntity registerUser(UserEntity newUser) throws UsernameNotFoundException {
        UserEntity user = userRepository.save(newUser);
        System.out.println("user "+newUser.getName()+" has been registered");
        return user;
    }
    
    //TEST
    @Autowired
    ManagementService mService;
    @Autowired
    File_cRepository fileRepo;
 
     @EventListener(ApplicationReadyEvent.class)
    public void init(){
          UserEntity user=new UserEntity();
           user.setName("Peter");
           user.setUserName("user");
           user.setPassword("pass");
         
           user.setRoles(new ArrayList<ROLE>() {{
                add(ROLE.ROLE_USER);
           }});
          
           user=registerUser(user);
             FolderEntity freshFolder=createFolderNative(user.getUserName(),user.getRootFolder().getId(), "test");
             
             UserEntity user2=new UserEntity();
           user2.setName("User2");
           user2.setUserName("user2");
           user2.setPassword("pass");
         
           user2.setRoles(new ArrayList<ROLE>() {{
                add(ROLE.ROLE_USER);
           }});
        
           user2=registerUser(user2);
           user2.getSharedFilesWithMe().add(freshFolder);
          freshFolder.getUsersIamSaredWith().add(user2);
         //  user2.getSharedFolder().getFiles().add(freshFolder);
           
           userRepository.save(user2);
    }
    
    FolderEntity createFolderNative(String username,Integer parent_id, String name){
       
      
        UserEntity user =userRepository.findByUserName(username).get();

        File_cEntity fent = fileRepo.findById(parent_id).get();
    
        FolderEntity folderToAdd = new FolderEntity();
        folderToAdd.setName(name);

        if (!fent.getOwner().getId().equals(user.getId())) {
          //  throw new UnAuthorizedActionException("Error: You are not te owner of the parent folder");
        }
        if (fent instanceof FolderEntity) {
            FolderEntity parentFolderEnt = (FolderEntity) fent;
            parentFolderEnt.addFile(folderToAdd);
           folderToAdd= this.fileRepo.save(folderToAdd);

            this.fileRepo.save(fent);
           return folderToAdd;
        }
        return null;

    }
}