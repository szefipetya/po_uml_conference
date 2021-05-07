/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.szefi.uml_conference._exceptions.JwtException;
import com.szefi.uml_conference._exceptions.JwtExpiredException;
import com.szefi.uml_conference._exceptions.UnAuthorizedActionException;
import com.szefi.uml_conference._exceptions.management.FileNotFoundException;
import com.szefi.uml_conference._exceptions.management.FileTypeConversionException;
import com.szefi.uml_conference._exceptions.management.IllegalDmlActionException;
import com.szefi.uml_conference._exceptions.management.UnstatisfiedNameException;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.do_related.DiagramObject;
import com.szefi.uml_conference.editor.model.do_related.PackageElement;
import com.szefi.uml_conference.editor.model.do_related.PackageObject;
import com.szefi.uml_conference.editor.model.do_related.TitleElement;
import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.editor.service.EditorSession;
import com.szefi.uml_conference.editor.service.SocketSessionService;
import com.szefi.uml_conference.management.repository.File_cRepository;
import com.szefi.uml_conference.management.model.dto.FolderDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectDto;
import com.szefi.uml_conference.management.model.dto.project.ProjectFolderDto;
import com.szefi.uml_conference.management.model.dto.response.FileResponse;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.FolderEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectFileEntity;
import com.szefi.uml_conference.management.model.entity.project.ProjectFolderEntity;
import com.szefi.uml_conference.model.common.management.ICON;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javassist.NotFoundException;
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
    @Autowired
    DiagramRepository diagramRepo;
    
    @Autowired
    SocketSessionService socketService;
     public FileResponse createProject(String jwt, Integer parent_id, String name) throws 
            JwtException, UnAuthorizedActionException,UnstatisfiedNameException, IllegalDmlActionException {
        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        if(name.equals("")) throw new UnstatisfiedNameException(" name ["+name+"] is not vaild");
                if(name.equals("~")) throw new UnstatisfiedNameException(" name ["+name+"] is not vaild");

        UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();

        File_cEntity fent = fileRepo.findById(parent_id).get();
         ProjectEntity projectToAdd = new ProjectEntity(user);
         
        projectToAdd.setName(name);
       // this.diagramRepo.save(projectToAdd.getRootFolder().getDiagram());
        if (!fent.getOwner().getId().equals(user.getId())  ) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the parend folder");
        }
        if (fent instanceof FolderEntity) {
            FolderEntity parentFolderEnt = (FolderEntity) fent;
            if(parentFolderEnt.getFiles().stream().filter(f->f.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).size()>0)
                 throw new UnstatisfiedNameException(" name ["+name+"] is used by another file in the folder.");
            parentFolderEnt.addFile(projectToAdd);
            
            //inherit share rules
                projectToAdd.getUsersIamSaredWith().addAll(parentFolderEnt.getUsersIamSaredWith());
              /*  projectToAdd.getRootFolder().getUsersIamSaredWith().addAll(parentFolderEnt.getUsersIamSaredWith());
                projectToAdd.getRootFolder().getDiagram().getUsersIamSaredWith().addAll(parentFolderEnt.getUsersIamSaredWith());*/
                         
                      this.fileRepo.save(projectToAdd);
          
            //update on the user side as well
                      parentFolderEnt.getUsersIamSaredWith().stream().forEach((UserEntity u)->{
                          u.getSharedFilesWithMe().add(projectToAdd); 
                          /*   u.getSharedFilesWithMe().add(projectToAdd.getRootFolder()); 
                             u.getSharedDiagramsWithMe().add(projectToAdd.getRootFolder().getDiagram()); */
                userRepo.save(u);
                });
            
            parentFolderEnt=this.fileRepo.save(parentFolderEnt);
            if (user != null) {
                FileResponse resp = new FileResponse(parentFolderEnt,new FolderDto(parentFolderEnt),user);
              

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
              if(name.equals("~")) throw new UnstatisfiedNameException(" name ["+name+"] is not vaild");

        UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();

        File_cEntity fent = fileRepo.findById(parent_id).get();
        ProjectFolderEntity folderToAdd = new ProjectFolderEntity();
        folderToAdd.setName(name);
      ProjectFolderEntity pfolder=(ProjectFolderEntity)fent;

        if (!pfolder.getOwner().getId().equals(user.getId())&&!pfolder.getRelatedProject().getUsersIamSaredWith().stream().anyMatch(u->u.getId().equals(user.getId()))) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the project or not invited");
        }
        if (fent instanceof ProjectFolderEntity) {
            ProjectFolderEntity parentFolderEnt = (ProjectFolderEntity) fent;
            if(parentFolderEnt.getFiles().stream().filter(f->f.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).size()>0)
                 throw new UnstatisfiedNameException(" name ["+name+"] is used by another file in the project folder.");
             // folderToAdd.setProject(parentFolderEnt.getProject());
            parentFolderEnt.addFile(folderToAdd);
            //add a diagram Entity
           //make a diagram for the projectFolder
            DiagramEntity dg=new DiagramEntity();
            dg.setOwner(parentFolderEnt.getOwner());
            dg.setRelatedFolder(folderToAdd);
            folderToAdd.setDiagram(dg);
            
          //inherit share rules
              /* folderToAdd.getUsersIamSaredWith().addAll(parentFolderEnt.getUsersIamSaredWith());
                folderToAdd.getDiagram().getUsersIamSaredWith().addAll(parentFolderEnt.getUsersIamSaredWith());*/
                    
                      this.fileRepo.save(folderToAdd);
           parentFolderEnt= this.fileRepo.save(parentFolderEnt);
            //update on the user side as well
                     /* parentFolderEnt.getUsersIamSaredWith().stream().forEach((UserEntity u)->{
                          u.getSharedFilesWithMe().add(folderToAdd);    
                          u.getSharedDiagramsWithMe().add(folderToAdd.getDiagram());
                userRepo.save(u);
                });*/
           
            injectPackageObjectToParentDiagram(parentFolderEnt, folderToAdd);
          
          //  this.fileRepo.save(parentFolderEnt.getProject());
            if (user != null) {
                FileResponse resp = 
                        new FileResponse(parentFolderEnt,new ProjectFolderDto(parentFolderEnt),user);
              

                return resp;

            }
        }
        else throw new FileTypeConversionException("The action can't be perforemed on the object, defined by [id="+parent_id+"]");
      return null;
    }
      @Autowired
      DynamicSerialObjectRepository objectRepo;
      void injectPackageObjectToParentDiagram(ProjectFolderEntity parentFolder,ProjectFolderEntity newFolder){
          PackageObject pobject=new PackageObject();
          pobject.setDiagram(parentFolder.getDiagram());
          pobject.setDimensionModel(DiagramObject.StandardDimensionModel());
          TitleElement title=new TitleElement();
          title.setName(newFolder.getName());
          title.setParent(pobject);
          pobject.setTitleModel(title);
          objectRepo.save(pobject);
          parentFolder.getDiagram().getDgObjects().add(pobject);
          
          this.diagramRepo.save(parentFolder.getDiagram());
          
          try {
              EditorSession session=socketService.getSessionByDiagramId(parentFolder.getDiagram().getId());
                      if(session!=null)session.injectToStateMapAndSendToAll(pobject);
          } catch (JsonProcessingException ex) {
              Logger.getLogger(ProjectManagementService.class.getName()).log(Level.SEVERE, null, ex);
          }
          if(parentFolder.getParentProjectFolder()!=null ){
              parentFolder.getParentProjectFolder().getDiagram().getDgObjects().stream().forEach((DiagramObject dg)->{
                  if(dg instanceof PackageObject){
                      PackageObject packageObj=(PackageObject)dg;
                      if(packageObj.getTitleModel().getName().equals(parentFolder.getName())){
                          PackageElement folderPackageModel=new PackageElement();
                          folderPackageModel.setName(newFolder.getName());
                          folderPackageModel.setIcon(ICON.PROJECT_FOLDER);
                        //  folderPackageModel.setParent(packageObj);
                          folderPackageModel.setReferencedObjectId(newFolder.getId());
                          packageObj.getElements().add(folderPackageModel);
                        objectRepo.save(packageObj);
                       
              EditorSession session=socketService.getSessionByDiagramId( parentFolder.getParentProjectFolder().getDiagram().getId());
                      if(session!=null)try {
                          session.updateObjectAndSend_Internal(packageObj);
                          } catch (NotFoundException | JsonProcessingException ex) {
                              Logger.getLogger(ProjectManagementService.class.getName()).log(Level.SEVERE, null, ex);
                          }
       
                      }
                     
                  }
              });
              
                      
          }
          
      }
     public FileResponse getProject(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException, UnAuthorizedActionException {

        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
     UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();
     try {
            File_cEntity ent= fileRepo.findById(id).get();
            if(ent instanceof ProjectEntity){
                     if (ent.getOwner().getId().equals(user.getId())
                       ||ent.getUsersIamSaredWith().stream().anyMatch(u->u.getId().equals(user.getId()))) {
                ProjectEntity casted=(ProjectEntity)ent;
                ProjectDto dto=new ProjectDto(casted);
                   FileResponse resp= new FileResponse(((ProjectEntity) ent).getRootFolder(),dto.getRootFolderDto(),user);
      
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
     UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();
     
     try {
            File_cEntity ent= fileRepo.findById(id).get();
            
            if ( ((ProjectFileEntity)ent).getRelatedProject().getOwner().getId().equals(user.getId())
                       ||((ProjectFileEntity)ent).getRelatedProject().getUsersIamSaredWith().stream().anyMatch(u->u.getId().equals(user.getId()))) {
                   ProjectFolderDto dto = new ProjectFolderDto((ProjectFolderEntity)ent);
                FileResponse resp= new FileResponse(ent,dto,user);
              
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
                       UserEntity user = userRepo.findByUserName(jwtService.extractUsername(jwt)).get();
                  if (ent.getOwner().getId().equals(userService.loadUserByUsername(jwtService.extractUsername(jwt)).getId())) {
                      if(ent instanceof ProjectFolderEntity){
                      ProjectFolderEntity casted=(ProjectFolderEntity)ent;
                      if( casted.getParentProjectFolder()!=null){
                         ProjectFolderEntity parent=casted.getParentProjectFolder();
                            //delete folder
                            fileRepo.deleteById(id);
                            //return parent folderű
                            ProjectFolderDto dto = new ProjectFolderDto(parent);
                            FileResponse resp= new FileResponse(parent,dto,user);
                            return resp;
                        }else throw new IllegalDmlActionException("This File can not be deleted");       
                      }else throw new FileTypeConversionException("Requested File Type is incorrect");
                        
                  }else throw new UnAuthorizedActionException("you don't have the authority to execute this action");
                 // return null;
        }

   /* void updateShareRecursivelyOnProjectFolder(ProjectFolderEntity pFolder, UserEntity targetUser) {
        this.updateShareOnFile(pFolder, targetUser);
      
         
       pFolder.getFiles().stream().forEach(f->{
               if(f instanceof ProjectFolderEntity)
            updateShareRecursivelyOnProjectFolder((ProjectFolderEntity)f, targetUser);
                    }); 
          
       
    }*/
                  private  void updateShareOnFile(File_cEntity file,UserEntity targetUser){
           targetUser.getSharedFilesWithMe().add(file);
          file.getUsersIamSaredWith().add(targetUser);
            fileRepo.save(file);
   }

    
    public void  deleteShareRuleRecursively(ProjectFileEntity ent){
          fileRepo.deleteShareRulesByFileId(ent.getId());
            if(ent instanceof ProjectFolderEntity){
                ((ProjectFolderEntity)ent).getFiles().stream().forEach(f->deleteShareRuleRecursively(f));
            }
             fileRepo.deleteById(ent.getId());
          
        }
}
