/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.unit.management.integration.controller;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.WebOrderApplication;
import com.szefi.uml_conference.management.controller.ManagementController;
import com.szefi.uml_conference.management.model.ICON;
import com.szefi.uml_conference.management.model.dto.FileHeaderDto;
import com.szefi.uml_conference.management.model.dto.FolderDto;
import com.szefi.uml_conference.management.model.dto.FolderHeaderDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectDto;
import com.szefi.uml_conference.management.model.dto.request.FileShareRequest;
import com.szefi.uml_conference.management.model.dto.response.FileResponse;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.FolderEntity;
import com.szefi.uml_conference.management.model.entity.SPECIAL_FOLDER;
import com.szefi.uml_conference.management.repository.File_cRepository;
import com.szefi.uml_conference.management.services.ManagementService;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.security.model.ROLE;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.model.auth.AuthResponse;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import org.hibernate.Hibernate;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
/**
 *
 * @author h9pbcl
 */

  @RunWith(SpringRunner.class)
   @ComponentScan(basePackages = {"com.szefi"})
   @SpringBootTest
   @AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Transactional( Transactional.TxType.REQUIRES_NEW)
public class ProjectManagementControllerTest {
    
  /*  @Autowired
    JwtUtilService jwtUtils;*/
     @Autowired
     MyUserDetailsService userService;
      ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
     String jwt;
     String jwt2;
 
    @BeforeAll
    public  void beforeClass() throws Exception {
          mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
          RequestBuilder req=request(HttpMethod.POST,"/register","{\"username\": \"test1\", \"email\": \"test@test.com\", \"password\": \"pass1\", \"fullName\": \"Test12\"}");   
                mvc.perform(req).andReturn();
                
                   RequestBuilder req2=request(HttpMethod.POST,"/register","{\"username\": \"test2\", \"email\": \"test@test.com\", \"password\": \"pass1\", \"fullName\": \"Test12\"}");
                mvc.perform(req2).andReturn();
                
     RequestBuilder req3=request(HttpMethod.POST,"/login","{\"username\":\"test1\",\"password\":\"pass1\"}");   
    MvcResult res3=mvc.perform(req3).andReturn();  
        AuthResponse resp3=mapper.readValue(res3.getResponse().getContentAsString(),AuthResponse.class);
        jwt=resp3.getJwt_token();
        
             RequestBuilder req4=request(HttpMethod.POST,"/login","{\"username\":\"test2\",\"password\":\"pass1\"}");   
    MvcResult res4=mvc.perform(req4).andReturn();  
        AuthResponse resp4=mapper.readValue(res4.getResponse().getContentAsString(),AuthResponse.class);
        jwt2=resp4.getJwt_token();

    }
    
       @Test
public void createProject_Test_ok()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");

    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/project_management/create_project/"+u1.getRootFolder().getId()+"?name=testp12", "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
       
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("testp12")).findFirst().get();
   Assertions.assertTrue(dto_test1 instanceof ProjectDto);
    Assertions.assertEquals("testp12",dto_test1.getName());
  Assertions.assertEquals(200,res.getResponse().getStatus());
  return;
}
     @Test
public void createProject_Test_unauthorized_403()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");

    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/project_management/create_project/"+u1.getRootFolder().getId()+"?name=testp12", "",jwt2);
    MvcResult res=mvc.perform(req).andReturn();  
       
 /*FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("testp12")).findFirst().get();
   Assertions.assertTrue(dto_test1 instanceof ProjectDto);*/
  //  Assertions.assertEquals("testp12",dto_test1.getName());
//  String a=res.getResponse().getContentAsString();
  Assertions.assertEquals(403,res.getResponse().getStatus());
    Assertions.assertEquals("Error: You are not te owner of the parent folder",res.getResponse().getContentAsString());

  return;
}
    @Test
public void createProject_Test_withnameExisting_400()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");
    RequestBuilder req1 = requestWithAuth(HttpMethod.GET, "/management/create_folder/"+u1.getRootFolder().getId()+"?name=testp12", "",jwt);
    MvcResult res1=mvc.perform(req1).andReturn();  
    
    RequestBuilder req2 = requestWithAuth(HttpMethod.GET, "/project_management/create_project/"+u1.getRootFolder().getId()+"?name=testp12", "",jwt);
    MvcResult res2=mvc.perform(req2).andReturn();  
  String a=res2.getResponse().getContentAsString();
  Assertions.assertEquals(400,res2.getResponse().getStatus());
  return;
}
    

    RequestBuilder requestWithAuth(HttpMethod method,String uri,String content,String token){
   return MockMvcRequestBuilders.request(method , uri).content(content).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+token);
}
RequestBuilder request(HttpMethod method,String uri,String content){
   return MockMvcRequestBuilders.request(method , uri).content(content).contentType(MediaType.APPLICATION_JSON);
}
  }