/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.controller;

import com.szefi.uml_conference._exceptions.JwtException;
import com.szefi.uml_conference._exceptions.UnAuthorizedActionException;
import com.szefi.uml_conference._exceptions.management.FileNotFoundException;
import com.szefi.uml_conference._exceptions.management.FileTypeConversionException;
import com.szefi.uml_conference._exceptions.management.IllegalDmlActionException;
import com.szefi.uml_conference._exceptions.management.UnstatisfiedNameException;
import com.szefi.uml_conference.management.model.dto.response.FileResponse;
import com.szefi.uml_conference.management.services.ManagementService;
import com.szefi.uml_conference.management.services.ProjectManagementService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author h9pbcl
 */
@RestController
public class ProjectManagementController {
@Autowired ManagementService mService;
    @Autowired
    ProjectManagementService service;

    @RequestMapping(value = "/project_management/create_project/{id}", method = RequestMethod.GET)//create a project with a parent_id given in param
    public ResponseEntity<?> createProject(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String parent_id,
            @RequestParam(value = "name") String name
    ) {
        try {
            return ResponseEntity.ok(service.createProject(authHeader.substring(7), Integer.valueOf(parent_id), name));
            //return service.getRootFolderByUserId()
        } catch (JwtException | UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalDmlActionException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (UnstatisfiedNameException ex) {
            FileResponse resp;
            try {
                resp = mService.getFolder(authHeader.substring(7), Integer.valueOf(parent_id));
                resp.setErrorMsg(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
            } catch (JwtException | FileTypeConversionException | FileNotFoundException ex1) {
                Logger.getLogger(ProjectManagementController.class.getName()).log(Level.SEVERE, null, ex1);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

            } catch (UnAuthorizedActionException ex1) {
              //  Logger.getLogger(ProjectManagementController.class.getName()).log(Level.SEVERE, null, ex1);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }

        }
    }

    @RequestMapping(value = "/project_management/create_folder/{id}", method = RequestMethod.GET)//create a projectfolder with a parent_id given in param
    public ResponseEntity<?> createProjectFolder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String parent_id,
            @RequestParam(value = "name") String name
    ) {
        try {
            return ResponseEntity.ok(service.createProjectFolder(authHeader.substring(7), Integer.valueOf(parent_id), name));
            //return service.getRootFolderByUserId()
        } catch (JwtException | UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileTypeConversionException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (UnstatisfiedNameException ex) {
            FileResponse resp;
            try {
                resp = service.getProjectFolder(authHeader.substring(7), Integer.valueOf(parent_id));
                resp.setErrorMsg(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
            } catch (JwtException | FileTypeConversionException | FileNotFoundException ex1) {
                Logger.getLogger(ProjectManagementController.class.getName()).log(Level.SEVERE, null, ex1);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

            }

        }

    }
    
    //PROJECT
 @RequestMapping(value="/management/project/{id}" ,method = RequestMethod.GET)//create a projectfolder with a parent_id given in param
    public ResponseEntity<?> getProject(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String parent_id
    ) {
        try {
            return ResponseEntity.ok(service.getProject(authHeader.substring(7), Integer.valueOf(parent_id)));
            //return service.getRootFolderByUserId()
        } catch (JwtException | UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileNotFoundException | FileTypeConversionException ex) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } 
         
    }



  @GetMapping("/management/projectFolder/{id}")
    public ResponseEntity<?> getProjectFolder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id
    ) {
        try {
            return ResponseEntity.ok(service.getProjectFolder(authHeader.substring(7), Integer.valueOf(folder_id)));
            //return service.getRootFolderByUserId()
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileTypeConversionException | FileNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }    
    
        @DeleteMapping("/management/projectFolder/{id}")
    public ResponseEntity<?> deleteProjectFolder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id
    ) {
        try {
            return ResponseEntity.ok(service.deleteProjectFolder(authHeader.substring(7), Integer.valueOf(folder_id)));
            //return service.getRootFolderByUserId()
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileTypeConversionException | FileNotFoundException |IllegalDmlActionException | UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } 
    }
    

}
