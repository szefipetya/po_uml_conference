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
import com.szefi.uml_conference.security.model.auth.RegistrationRequest;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class MyUserDetailsService implements UserDetailsService {
@Autowired
private PasswordEncoder passwordEncoder;
  
    @Autowired
    UserRepository userRepository;

    public void save(UserEntity user){
        this.userRepository.save(user);
    }
    @Override
    public MyUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByUserName(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));
        return user.map(MyUserDetails::new).get();
    }
     
      
    public UserEntity loadUserEntityByUsername(String userName) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByUserName(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));
        return user.get();
    }
        public UserEntity loadUserEntityById(Integer id) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findById(id);
        user.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return user.get();
    }
     
    public UserEntity registerUser(UserEntity newUser) throws UsernameNotFoundException {
        newUser.setPassword(  passwordEncoder.encode(newUser.getPassword()));
        UserEntity user = userRepository.save(newUser);
        System.out.println("user "+newUser.getName()+" has been registered");
        return user;
    }
    
    //TEST
    @Autowired
    ManagementService mService;
    @Autowired
    File_cRepository fileRepo;
    
    @Autowired
private Environment environment;


    
     @EventListener(ApplicationReadyEvent.class)
    public void init(){
        boolean good=true;
for(String s:this.environment.getActiveProfiles()){
        if(s.equals("dev"))good=good&&true;
        else if(s.equals("prod"))good=good&&false;
    }
if(!good) return;
          UserEntity user=new UserEntity();
           user.setName("Peter");
           user.setUserName("user");
           user.setPassword("pass1");
         
           user.setRoles(new ArrayList<ROLE>() {{
                add(ROLE.ROLE_USER);
           }});
          
           user=registerUser(user);
             FolderEntity freshFolder=createFolderNative(user.getUserName(),user.getRootFolder().getId(), "test");
             
             UserEntity user2=new UserEntity();
           user2.setName("User2");
           user2.setUserName("user2");
           user2.setPassword("pass2");
         
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
        folderToAdd.setOwner(user);;
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

    boolean validateRegistration(RegistrationRequest req) {
        return !(this.userRepository.findByUserName(req.getUsername()).isPresent()||this.userRepository.findByEmail(req.getUsername()).isPresent());
    }
}