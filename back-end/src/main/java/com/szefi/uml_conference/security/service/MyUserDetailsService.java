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
 
     @EventListener(ApplicationReadyEvent.class)
    public void init(){
          UserEntity user=new UserEntity();
           user.setName("Peter");
           user.setUserName("user");
           user.setPassword("pass");
         
           user.setRoles(new ArrayList<ROLE>() {{
                add(ROLE.ROLE_USER);
           }});
           registerUser(user);
    }
}