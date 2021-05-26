/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.tests.management.integration.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.WebOrderApplication;
import com.szefi.uml_conference.management.controller.ManagementController;
import com.szefi.uml_conference.management.model.ICON;
import com.szefi.uml_conference.management.model.dto.FileHeaderDto;
import com.szefi.uml_conference.management.model.dto.FolderDto;
import com.szefi.uml_conference.management.model.dto.FolderHeaderDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
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

 /* @TestPropertySource(locations="classpath:test.properties")*/
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Transactional( Transactional.TxType.REQUIRES_NEW)
public class ManamegentControllerTest {
    
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
    
@BeforeEach
public void beforeEach(){
   UserEntity _u1= userService.loadUserEntityByUsername("test1");
   ArrayList<File_cEntity> filesToDel=new ArrayList<>();
  _u1.getFiles().stream().map(f->{
   if(f instanceof FolderEntity){
     if(!((FolderEntity)f).getSpecial().equals(SPECIAL_FOLDER.USER_ROOT)&&!((FolderEntity)f).getSpecial().equals(SPECIAL_FOLDER.SHARED)) filesToDel.add(f);
 }
   return null;
  });
  _u1.getFiles().removeAll(filesToDel);
     userService.save(_u1);
     
     
        UserEntity _u2= userService.loadUserEntityByUsername("test2");
   ArrayList<File_cEntity> filesToDel2=new ArrayList<>();
  _u2.getFiles().stream().map(f->{
   if(f instanceof FolderEntity){
     if(!((FolderEntity)f).getSpecial().equals(SPECIAL_FOLDER.USER_ROOT)&&!((FolderEntity)f).getSpecial().equals(SPECIAL_FOLDER.SHARED)) filesToDel2.add(f);
 }
   return null;
  });
  _u2.getFiles().removeAll(filesToDel2);
     userService.save(_u2);
}

RequestBuilder requestWithAuth(HttpMethod method,String uri,String content,String token){
   return MockMvcRequestBuilders.request(method , uri).content(content).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+token);
}
RequestBuilder request(HttpMethod method,String uri,String content){
   return MockMvcRequestBuilders.request(method , uri).content(content).contentType(MediaType.APPLICATION_JSON);
}


    
    @Test
public void getUserRootFolder_Test()
  throws IOException, Exception {
        UserEntity u1=userService.loadUserEntityByUsername("test1");

    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/user_root_folder", "",jwt); 
    MvcResult res=mvc.perform(req).andReturn();   
    
  Assertions.assertEquals(200,res.getResponse().getStatus());
//  String a=res.getResponse().getContentAsString();
  
//  Assertions.assertEquals("{\"pathFiles\":[{\"index\":0,\"file\":{\"_type\":\"folder\",\"id\":9,\"name\":\"~\",\"parentFolder_id\":null,\"owner\":{\"id\":8,\"userName\":\"test1\",\"email\":\"test@test.com\",\"name\":\"Test12\"},\"icon\":\"FOLDER\",\"is_root\":true}}],\"file\":{\"_type\":\"FolderDto\",\"id\":9,\"name\":\"~\",\"parentFolder_id\":null,\"owner\":{\"id\":8,\"userName\":\"test1\",\"email\":\"test@test.com\",\"name\":\"Test12\"},\"icon\":\"FOLDER\",\"files\":[{\"_type\":\"folder\",\"id\":10,\"name\":\"SharedWithMe\",\"parentFolder_id\":9,\"owner\":{\"id\":8,\"userName\":\"test1\",\"email\":\"test@test.com\",\"name\":\"Test12\"},\"icon\":\"FOLDER\",\"is_root\":false}],\"is_root\":true},\"errorMsg\":\"\"}", a);
//  Assertions.assertEquals("{\"pathFiles\":[{\"index\":0,\"file\":{\"_type\":\"folder\",\"id\":2,\"name\":\"~\",\"parentFolder_id\":null,\"owner\":{\"id\":1,\"userName\":\"test1\",\"email\":\"test@test.com\",\"name\":\"Test12\"},\"icon\":\"FOLDER\",\"is_root\":true}}],\"file\":{\"_type\":\"FolderDto\",\"id\":2,\"name\":\"~\",\"parentFolder_id\":null,\"owner\":{\"id\":1,\"userName\":\"test1\",\"email\":\"test@test.com\",\"name\":\"Test12\"},\"icon\":\"FOLDER\",\"files\":[{\"_type\":\"folder\",\"id\":3,\"name\":\"SharedWithMe\",\"parentFolder_id\":2,\"owner\":{\"id\":1,\"userName\":\"test1\",\"email\":\"test@test.com\",\"name\":\"Test12\"},\"icon\":\"FOLDER\",\"is_root\":false}],\"is_root\":true},\"errorMsg\":\"\"}", a);
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FolderDto dto_test1=((FolderDto)fresp.getFile());
 Assertions.assertEquals(ICON.FOLDER,dto_test1.getIcon());
 Assertions.assertEquals("~",dto_test1.getName());
 Assertions.assertEquals(u1.getRootFolder().getId(),dto_test1.getId());
  return;
  
}
  @Test
public void getUserRootFolder_Test_jwtError()
  throws IOException, Exception {   
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/user_root_folder", "",jwt+"asd");
    MvcResult res=mvc.perform(req).andReturn();  
    
  Assertions.assertEquals(403,res.getResponse().getStatus());
  String a=res.getResponse().getErrorMessage();
  Assertions.assertEquals("Access Denied",a);
  return;
  
}
  @Test
public void getFolder_Test_ok()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/folder/"+u1.getRootFolder().getId(), "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
    
  Assertions.assertEquals(200,res.getResponse().getStatus());
 String a=res.getResponse().getContentAsString();
  FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FolderDto dto_test1=((FolderDto)fresp.getFile());
 Assertions.assertEquals(ICON.FOLDER,dto_test1.getIcon());
 Assertions.assertEquals("~",dto_test1.getName());
 Assertions.assertEquals(u1.getRootFolder().getId(),dto_test1.getId());
  return;
}

  @Test
public void getFolder_Test_jwt_error()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/folder/"+u1.getRootFolder().getId(), "",jwt+"asd");
    MvcResult res=mvc.perform(req).andReturn();  
    
  Assertions.assertEquals(403,res.getResponse().getStatus());
  return;
}
  @Test
public void getFolder_Test_unAuthorized()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/folder/"+u1.getRootFolder().getId(), "",jwt2);
    MvcResult res=mvc.perform(req).andReturn();  
    
  Assertions.assertEquals(403,res.getResponse().getStatus());
 String a=res.getResponse().getContentAsString();
  return;
}

  @Test
  
public void deleteFolder_Test_RootDelete_400()
  throws IOException, Exception {   
        UserEntity u1=userService.loadUserEntityByUsername("test1");

    RequestBuilder req = requestWithAuth(HttpMethod.DELETE, "/management/folder/"+u1.getRootFolder().getId(), "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
    
  Assertions.assertEquals(400,res.getResponse().getStatus());
 String a=res.getResponse().getContentAsString();
  return;
}


    @Rollback(false) // This is key to avoid rollback.
        @Commit
FileResponse createFolder(String username,String filename) throws Exception{
      UserEntity u1=userService.loadUserEntityByUsername(username);
      RequestBuilder req0 = requestWithAuth(HttpMethod.GET, "/management/create_folder/"+u1.getRootFolder().getId()+"?name="+filename, "",jwt);
    MvcResult res0=mvc.perform(req0).andReturn();  
      Assertions.assertEquals(200,res0.getResponse().getStatus());
 String a0=res0.getResponse().getContentAsString();
 return mapper.readValue(a0, FileResponse.class);
       
}

@Autowired
UserRepository userRepo;

@Commit
 
  @Test
public void deleteFolder_Test_ok()
  throws IOException, Exception {   
      UserEntity u1=userService.loadUserEntityByUsername("test1");
FileResponse fileResp=createFolder("test1","test12");
    FileHeaderDto dto_test=((FolderDto)fileResp.getFile()).getFiles().stream().filter(f-> f.getName().equals("test12")).findFirst().get();
    
    RequestBuilder req = requestWithAuth(HttpMethod.DELETE, "/management/folder/"+dto_test.getId(), "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
    
  
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("test12")).findFirst().get();
 Assertions.assertEquals(ICON.FOLDER,dto_test1.getIcon());
 Assertions.assertEquals("test12",dto_test1.getName());
 Assertions.assertEquals(u1.getRootFolder().getId(),dto_test1.getParentFolder_id());
  Assertions.assertEquals(200,res.getResponse().getStatus());

//check that folder does not exists
    RequestBuilder req2 = requestWithAuth(HttpMethod.GET, "/management/folder/"+dto_test.getId(), "",jwt);
    MvcResult res2=mvc.perform(req2).andReturn();  
     String a2=res2.getResponse().getContentAsString();
     
    
  Assertions.assertEquals(400,res2.getResponse().getStatus());
  Assertions.assertEquals("requested folder with id="+dto_test1.getId()+" not found",res2.getResponse().getContentAsString());
  

  return;
}
   @Test
public void deleteFolder_Test_unAuthorized()
  throws IOException, Exception {   
        UserEntity u1=userService.loadUserEntityByUsername("test1");

 File_cEntity f1= u1.getFiles().stream().filter(f->{
 if(f instanceof FolderEntity){
     return !((FolderEntity)f).getSpecial().equals(SPECIAL_FOLDER.USER_ROOT)&&((FolderEntity)f).getSpecial().equals(SPECIAL_FOLDER.SHARED);
 }
 return false;
 }).findFirst().get();
    
    RequestBuilder req = requestWithAuth(HttpMethod.DELETE, "/management/folder/"+f1.getId(), "",jwt2);
    MvcResult res=mvc.perform(req).andReturn();  
    
  Assertions.assertEquals(403,res.getResponse().getStatus());
 String a=res.getResponse().getContentAsString();
  return;
}
  @Test
public void createFolder_Test_ok()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");

    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/create_folder/"+u1.getRootFolder().getId()+"?name=test12", "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
    
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("test12")).findFirst().get();
 Assertions.assertEquals(ICON.FOLDER,dto_test1.getIcon());
 Assertions.assertEquals("test12",dto_test1.getName());
 Assertions.assertEquals(u1.getRootFolder().getId(),fresp.getFile().getId());
  Assertions.assertEquals(200,res.getResponse().getStatus());

  return;
}

  @Test
public void createFolder_Test_UnAuthorized_403()
  throws IOException, Exception {   
     FileResponse fileResp=createFolder("test1","test12");
    FileHeaderDto dto_test=((FolderDto)fileResp.getFile()).getFiles().stream().filter(f->{
 if(f instanceof FolderHeaderDto){
     return ((FolderHeaderDto)f).getName().equals("test12");
 }
 return false;
 }).findFirst().get();
    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/folder/"+dto_test.getId(), "",jwt2);
    MvcResult res=mvc.perform(req).andReturn();  
    
  Assertions.assertEquals(403,res.getResponse().getStatus());
 String a=res.getResponse().getContentAsString();
  Assertions.assertEquals("you don't have access to this file"
          ,a);
  return;
}

   @Test
public void shareFolder_Test_ok()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");

    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/create_folder/"+u1.getRootFolder().getId()+"?name=test12", "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
       
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("test12")).findFirst().get();
       FileShareRequest fshareReq=new FileShareRequest();
       fshareReq.setAuth_jwt(jwt);
       fshareReq.setFile_id(dto_test1.getId());
       fshareReq.setTarget_userName("test2");
    
        RequestBuilder req2 = requestWithAuth(HttpMethod.POST, "/management/share/",mapper.writeValueAsString(fshareReq),jwt);
    MvcResult res2=mvc.perform(req2).andReturn();  
    String a=res2.getResponse().getContentAsString();
   
  Assertions.assertEquals(200,res2.getResponse().getStatus());

  return;
}

   @Test
public void shareFolder_Test_user_not_found()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");

    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/create_folder/"+u1.getRootFolder().getId()+"?name=test12", "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
       
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("test12")).findFirst().get();
       FileShareRequest fshareReq=new FileShareRequest();
       fshareReq.setAuth_jwt(jwt);
       fshareReq.setFile_id(dto_test1.getId());
       fshareReq.setTarget_userName("test2_asdasd123");
    
        RequestBuilder req2 = requestWithAuth(HttpMethod.POST, "/management/share/",mapper.writeValueAsString(fshareReq),jwt);
    MvcResult res2=mvc.perform(req2).andReturn();  
    String a=res2.getResponse().getContentAsString();
   
  Assertions.assertEquals(400,res2.getResponse().getStatus());
  Assertions.assertEquals("User not found: test2_asdasd123",a);
  
  return;
}

  @Test
public void shareFolder_Test_unauthorized_403()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");

    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/create_folder/"+u1.getRootFolder().getId()+"?name=test12", "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
       
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("test12")).findFirst().get();
       FileShareRequest fshareReq=new FileShareRequest();
       fshareReq.setAuth_jwt(jwt2);
       fshareReq.setFile_id(dto_test1.getId());
       fshareReq.setTarget_userName("test2");
    
        RequestBuilder req2 = requestWithAuth(HttpMethod.POST, "/management/share/",mapper.writeValueAsString(fshareReq),jwt2);
    MvcResult res2=mvc.perform(req2).andReturn();  
    String a=res2.getResponse().getContentAsString();
   
  Assertions.assertEquals(403,res2.getResponse().getStatus());
  Assertions.assertEquals("you are not the owner of this file",a);
  
  return;
}
  @Test
public void shareFolder_Test_share_that_sharedWithMe_403()
  throws IOException, Exception {   
    UserEntity u1=userService.loadUserEntityByUsername("test1");

    
    RequestBuilder req = requestWithAuth(HttpMethod.GET, "/management/create_folder/"+u1.getRootFolder().getId()+"?name=test12", "",jwt);
    MvcResult res=mvc.perform(req).andReturn();  
       
 FileResponse fresp=mapper.readValue(res.getResponse().getContentAsString(),FileResponse.class);
   FileHeaderDto dto_test1=((FolderDto)fresp.getFile()).getFiles().stream().filter(f->f.getName().equals("test12")).findFirst().get();
       FileShareRequest fshareReq=new FileShareRequest();
       fshareReq.setAuth_jwt(jwt);
       fshareReq.setFile_id(dto_test1.getId());
       fshareReq.setTarget_userName("test2");
    
        RequestBuilder req2 = requestWithAuth(HttpMethod.POST, "/management/share/",mapper.writeValueAsString(fshareReq),jwt);
    MvcResult res2=mvc.perform(req2).andReturn();  
    //share complete
    //register a third user
        RequestBuilder reqt=request(HttpMethod.POST,"/register","{\"username\": \"test3\", \"email\": \"test@test.com\", \"password\": \"pass1\", \"fullName\": \"Test123\"}");   
                mvc.perform(reqt).andReturn();
       FileShareRequest fshareReq3=new FileShareRequest();
       fshareReq3.setAuth_jwt(jwt2);
       fshareReq3.setFile_id(dto_test1.getId());
       fshareReq3.setTarget_userName("test3");
     RequestBuilder req3= requestWithAuth(HttpMethod.POST, "/management/share/",mapper.writeValueAsString(fshareReq3),jwt2);
    MvcResult res3=mvc.perform(req3).andReturn();  
    
    String a=res3.getResponse().getContentAsString();
    
    
   
  Assertions.assertEquals(403,res3.getResponse().getStatus());
  Assertions.assertEquals("you are not the owner of this file",a);
  
  return;
}
 
  
    @Autowired
    ManagementService mService;
    @Autowired
    File_cRepository fileRepo;
  
    FolderEntity  helper_createFolderNative(String username,Integer parent_id, String name){
       
      
        UserEntity user =userService.loadUserEntityByUsername(username);

        File_cEntity fent = fileRepo.findById(parent_id).get();
    
        FolderEntity folderToAdd = new FolderEntity();
        folderToAdd.setName(name);
        folderToAdd.setOwner(user);
        user.getFiles().add(folderToAdd);

        
        if (!fent.getOwner().getId().equals(user.getId())) {
          //  throw new UnAuthorizedActionException("Error: You are not te owner of the parent folder");
        }
        if (fent instanceof FolderEntity) {
            FolderEntity parentFolderEnt = (FolderEntity) fent;
            parentFolderEnt.addFile(folderToAdd);
           folderToAdd= this.fileRepo.save(folderToAdd);

            this.fileRepo.save(fent);
            this.userService.save(user);
           return folderToAdd;
        }
        return null;

    }

}
