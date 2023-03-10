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
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
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
import com.szefi.uml_conference.management.model.ICON;
import com.szefi.uml_conference.management.model.entity.SPECIAL_FOLDER;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.security.service.MyUserDetailsService;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 */
@Service
public class ProjectManagementService {

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
            JwtException, UnAuthorizedActionException, UnstatisfiedNameException, IllegalDmlActionException {
        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        if (name.equals("") || name.equals("~")) {
            throw new UnstatisfiedNameException(" name [" + name + "] is not vaild");
        }
        UserEntity user = userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));
        File_cEntity fent = fileRepo.findById(parent_id).get();
        ProjectEntity projectToAdd = new ProjectEntity(user);
        projectToAdd.setName(name);
        if (!fent.getOwner().getId().equals(user.getId())) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the parent folder");
        }
        if (fent instanceof FolderEntity) {
            FolderEntity parentFolderEnt = (FolderEntity) fent;
            if (parentFolderEnt.getFiles().stream().filter(f -> f.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).size() > 0) {
                throw new UnstatisfiedNameException(" name [" + name + "] is used by another file in the folder.");
            }
             if(parentFolderEnt.getSpecial().equals(SPECIAL_FOLDER.SHARED))
                                throw new UnstatisfiedNameException("You can not create files here");
            parentFolderEnt.addFile(projectToAdd);
            //inherit share rules
            projectToAdd.getUsersIamSaredWith().addAll(parentFolderEnt.getUsersIamSaredWith());
            this.fileRepo.save(projectToAdd);
            //update on the user side as well
            parentFolderEnt.getUsersIamSaredWith().stream().forEach((UserEntity u) -> {
                u.getSharedFilesWithMe().add(projectToAdd);
                userService.save(u);
            });

            parentFolderEnt = this.fileRepo.save(parentFolderEnt);
            if (user != null) {
                FileResponse resp = new FileResponse(parentFolderEnt, new FolderDto(parentFolderEnt), user);
                return resp;
            }
        } else {
            throw new IllegalDmlActionException("the parent's type is not a Folder");
        }

        return null;
    }

    /**
     *
     * @param jwt
     * @param parent_id The id of the projectfolder where the new folder will be
     * created
     * @param name
     * @return
     */
    public FileResponse createProjectFolder(String jwt, Integer parent_id, String name) throws
            JwtException, UnAuthorizedActionException, UnstatisfiedNameException, FileTypeConversionException {
        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        if (name.equals("") || name.equals("~")) {
            throw new UnstatisfiedNameException(" name [" + name + "] is not vaild");
        }

        UserEntity user = userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));

        File_cEntity parentFolder = fileRepo.findById(parent_id).get();
        ProjectFolderEntity pfolder = (ProjectFolderEntity) parentFolder;
        if (!pfolder.getOwner().getId().equals(user.getId()) && !pfolder.getRelatedProject().getUsersIamSaredWith().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new UnAuthorizedActionException("Error: You are not te owner of the project or not invited");
        }

        ProjectFolderEntity folderToAdd = new ProjectFolderEntity();
        folderToAdd.setName(name);

        if (parentFolder instanceof ProjectFolderEntity) {
            ProjectFolderEntity parentFolderEnt = (ProjectFolderEntity) parentFolder;
            if (parentFolderEnt.getFiles().stream().filter(f -> f.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).size() > 0) {
                throw new UnstatisfiedNameException(" name [" + name + "] is used by another file in the project folder.");
            }
            parentFolderEnt.addFile(folderToAdd);
            //add a diagram Entity
            //make a diagram for the projectFolder
            DiagramEntity dg = new DiagramEntity();
            dg.setOwner(parentFolderEnt.getOwner());
            dg.setRelatedFolder(folderToAdd);
            folderToAdd.setDiagram(dg);

            this.fileRepo.save(folderToAdd);
            parentFolderEnt = this.fileRepo.save(parentFolderEnt);
            injectPackageObjectToParentDiagram(parentFolderEnt, folderToAdd);
            if (user != null) {
                FileResponse resp
                        = new FileResponse(parentFolderEnt, new ProjectFolderDto(parentFolderEnt), user);
                return resp;
            }
        } else {
            throw new FileTypeConversionException("The action can't be perforemed on the object, defined by [id=" + parent_id + "]");
        }
        return null;
    }
    @Autowired
    DynamicSerialObjectRepository objectRepo;

    void injectPackageObjectToParentDiagram(ProjectFolderEntity parentFolder, ProjectFolderEntity newFolder) {
        PackageObject pobject = new PackageObject();
        pobject.setDiagram(parentFolder.getDiagram());
        pobject.setpFolder(newFolder);
        newFolder.setRelatedPackageObject(pobject);
        pobject.setDimensionModel(DiagramObject.StandardDimensionModel());
        TitleElement title = new TitleElement();
        title.setName(newFolder.getName());
        title.setParent(pobject);
        pobject.setTitleModel(title);
        objectRepo.save(pobject);
        parentFolder.getDiagram().getDgObjects().add(pobject);
        this.diagramRepo.save(parentFolder.getDiagram());

        try {
            EditorSession session = socketService.getSessionByDiagramId(parentFolder.getDiagram().getId());
            if (session != null) {
                session.injectToStateMapAndSendToAll(pobject);
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProjectManagementService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (parentFolder.getParentProjectFolder() != null) {
            parentFolder.getParentProjectFolder().getDiagram().getDgObjects().stream().forEach((DiagramObject dg) -> {
                if (dg instanceof PackageObject) {
                    PackageObject packageObj = (PackageObject) dg;
                    if (packageObj.getTitleModel().getName().equals(parentFolder.getName())) {
                        PackageElement folderPackageModel = new PackageElement();
                        folderPackageModel.setName(newFolder.getName());
                        folderPackageModel.setIcon(ICON.PROJECT_FOLDER);
                        folderPackageModel.setReferencedObjectId(newFolder.getId());
                        packageObj.getElements().add(folderPackageModel);
                        objectRepo.save(packageObj);

                        EditorSession session = socketService.getSessionByDiagramId(parentFolder.getParentProjectFolder().getDiagram().getId());
                        if (session != null)try {
                            session.updateObjectAndSend_Internal(packageObj);
                        } catch (NotFoundException | JsonProcessingException ex) {
                            Logger.getLogger(ProjectManagementService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            );
        }
    }

    void deleteinjectedPackageObject(ProjectFolderEntity parentFolder, ProjectFolderEntity folderToDelete) {
    if (parentFolder.getParentProjectFolder() != null) {
            parentFolder.getParentProjectFolder().getDiagram().getDgObjects().stream().forEach((DiagramObject dg) -> {
                if (dg instanceof PackageObject) {
                    PackageObject packageObj = (PackageObject) dg;
                    if (packageObj.getTitleModel().getName().equals(parentFolder.getName())) {

                        /*  ArrayList<PackageElement> todelElems=new ArrayList<>();
                    todelElems.addAll(packageObj.getElements().stream().filter((PackageElement e)->{
                              if(e.getName().equals(folderToDelete.getName())) return false;
                              return true;
                          }).collect(Collectors.toList()));*/
                        packageObj.getElements().removeIf((PackageElement e) -> {
                            return e.getName().equals(folderToDelete.getName());

                        });
                        objectRepo.save(packageObj);

                        EditorSession ppsession = socketService.getSessionByDiagramId(parentFolder.getParentProjectFolder().getDiagram().getId());
                        if (ppsession != null)try {
                            ppsession.updateObjectAndSend_Internal(packageObj);
                        } catch (NotFoundException | JsonProcessingException ex) {
                            Logger.getLogger(ProjectManagementService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            );
        }
        try {
            ArrayList<PackageObject> pobjToDeleteList = new ArrayList<>();
            EditorSession session = socketService.getSessionByDiagramId(parentFolder.getDiagram().getId());
            if (session != null) {
                session.getSessionItemMap().forEach(((Integer k, Pair<SessionState, DynamicSerialObject> v) -> {
                    if (v.getSecond() instanceof PackageObject) {
                        if (((PackageObject) v.getSecond()).getTitleModel().getName().equals(folderToDelete.getName())) {
                            pobjToDeleteList.add((PackageObject) v.getSecond());
                        }
                    }
                }));
                session.deleteFromStateMapAndSendToAll(pobjToDeleteList.get(0));

            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProjectManagementService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ProjectManagementService.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }

    public FileResponse getProject(String jwt, Integer id) throws JwtException, FileTypeConversionException, FileNotFoundException, UnAuthorizedActionException {

        if (jwtService.isTokenExpired(jwt)) {
            throw new JwtExpiredException("Error: token expired");
        }
        UserEntity user = userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));
        try {
            File_cEntity ent = fileRepo.findById(id).get();
            if (ent instanceof ProjectEntity) {
                if (ent.getOwner().getId().equals(user.getId())
                        || ent.getUsersIamSaredWith().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
                    ProjectEntity casted = (ProjectEntity) ent;
                    ProjectDto dto = new ProjectDto(casted);
                    FileResponse resp = new FileResponse(((ProjectEntity) ent).getRootFolder(), dto.getRootFolderDto(), user);

                    return resp;
                } else {
                    throw new UnAuthorizedActionException("The action can't be performed. Reason: Unauthorized ");
                }
            } else {
                throw new FileTypeConversionException("The action can't be perforemed on the object, defined by [id=" + id + "]");
            }

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
        UserEntity user = userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));

        try {
            File_cEntity ent = fileRepo.findById(id).get();

            if (((ProjectFileEntity) ent).getRelatedProject().getOwner().getId().equals(user.getId())
                    || ((ProjectFileEntity) ent).getRelatedProject().getUsersIamSaredWith().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
                ProjectFolderDto dto = new ProjectFolderDto((ProjectFolderEntity) ent);
                FileResponse resp = new FileResponse(ent, dto, user);

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
        File_cEntity ent = fileRepo.findById(id).get();
        UserEntity user = userService.loadUserEntityByUsername(jwtService.extractUsername(jwt));

        if (ent instanceof ProjectFolderEntity) {
            ProjectFolderEntity casted = (ProjectFolderEntity) ent;

            if (!casted.getOwner().getId().equals(user.getId()) && !casted.getRelatedProject().getUsersIamSaredWith().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
                throw new UnAuthorizedActionException("Error: You are not te owner of the project or not invited");
            }
            if (casted.getParentProjectFolder() != null) {

                ProjectFolderEntity parent = casted.getParentProjectFolder();
                //delete folder
                fileRepo.deleteById(id);
                //delete the packageobject and manage its grandparent
                deleteinjectedPackageObject(parent, casted);
                //return parent folder??
                ProjectFolderDto dto = new ProjectFolderDto(parent);
                FileResponse resp = new FileResponse(parent, dto, user);
                return resp;
            } else {
                throw new IllegalDmlActionException("This File can not be deleted");
            }
        } else {
            throw new FileTypeConversionException("Requested File Type is incorrect");
        }

        // return null;
    }

    void deleteProject(ProjectEntity project) {
        
       
          //  diagramRepo.delete(project.getRootFolder().getDiagram());
      //  project.getRootFolder().setDiagram(diagramRepo.save(project.getRootFolder().getDiagram()));
    }

}
