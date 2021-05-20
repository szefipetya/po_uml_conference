/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.controller;

import com.szefi.uml_conference._exceptions.JwtException;
import com.szefi.uml_conference._exceptions.JwtExpiredException;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference._exceptions.UnAuthorizedActionException;
import com.szefi.uml_conference._exceptions.management.FileNotFoundException;
import com.szefi.uml_conference._exceptions.management.FileTypeConversionException;
import com.szefi.uml_conference._exceptions.management.IllegalDmlActionException;
import com.szefi.uml_conference._exceptions.management.UnstatisfiedNameException;
import com.szefi.uml_conference.management.services.ManagementService;
import com.szefi.uml_conference.management.services.ProjectManagementService;
import com.szefi.uml_conference.management.model.dto.FolderDto;
import com.szefi.uml_conference.management.model.dto.request.FileShareRequest;
import com.szefi.uml_conference.management.model.dto.response.FileResponse;
import com.szefi.uml_conference.security.model.auth.AuthRequest;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
//@RequestMapping("/management")
public class ManagementController {

    @Autowired
    ManagementService service;
    @Autowired
    ProjectManagementService projectService;


    @GetMapping("/management/user_root_folder")
    public ResponseEntity<?> getUserRootFolder(
            @RequestHeader(value = "Authorization") String authHeader
    ) {
        try {
            return ResponseEntity.ok(service.getUserRootFolder(authHeader.substring(7)));
            //return service.getRootFolderByUserId()
        } catch (JwtException |UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } 
        // return ResponseEntity.status(HttpStatus.FORBIDDEN).body("inscufficient authorities");
    }

    @GetMapping("/management/folder/{id}")
    public ResponseEntity<?> getFolder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id
    ) {
        try {
            return ResponseEntity.ok(service.getFolder(authHeader.substring(7), Integer.valueOf(folder_id)));
            //return service.getRootFolderByUserId()
        } catch (JwtException |UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileTypeConversionException | FileNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
      @DeleteMapping("/management/folder/{id}")
    public ResponseEntity<?> deleteFolder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id
    ) {
        try {
            return ResponseEntity.ok(service.deleteFile(authHeader.substring(7), Integer.valueOf(folder_id)));
            //return service.getRootFolderByUserId()
        } catch (JwtException|UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileTypeConversionException | FileNotFoundException |IllegalDmlActionException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } 
    }

    @DeleteMapping("/management/FolderDto/{id}")//INTERNAL REDIRECT
    public ResponseEntity<?> deleteFolder2(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id, HttpServletResponse httpResponse
    ) throws IOException {

        httpResponse.sendRedirect("/management/folder/" + folder_id);
        return null;

    }
       @GetMapping("/management/FolderDto/{id}")//INTERNAL REDIRECT
    public ResponseEntity<?> getFolder2(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id, HttpServletResponse httpResponse
    ) throws IOException {

        httpResponse.sendRedirect("/management/folder/" + folder_id);
        return null;

    }
     @GetMapping("/management/projectFolderDto/{id}")//INTERNAL REDIRECT
    public ResponseEntity<?> getProjectFolder2(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id, HttpServletResponse httpResponse
    ) throws IOException {

        httpResponse.sendRedirect("/management/projectFolder/" + folder_id);
        return null;

    }
     
     

    @GetMapping("/management/create_folder/{id}")//create a folder with a parent_id given in param
    public ResponseEntity<?> createFolder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String parent_id,
            @RequestParam(value = "name") String name
    ) {
        try {
            return ResponseEntity.ok(service.createFolder(authHeader.substring(7), Integer.valueOf(parent_id),name));
            //return service.getRootFolderByUserId()
        } catch (JwtException | UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (UnstatisfiedNameException  ex) {
            try {
                FileResponse resp=service.getFolder(authHeader.substring(7),Integer.valueOf(parent_id));
                resp.setErrorMsg(ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
            } catch (JwtException | FileTypeConversionException | FileNotFoundException | UnAuthorizedActionException ex1) {
                Logger.getLogger(ManagementController.class.getName()).log(Level.SEVERE, null, ex1);
                          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

            }
        }
        catch ( FileTypeConversionException ex) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    
          @DeleteMapping("/management/project/{id}")
    public ResponseEntity<?> deleteProject(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable(value = "id") String folder_id
    ) {
        try {
            return ResponseEntity.ok(service.deleteFile(authHeader.substring(7), Integer.valueOf(folder_id)));
            //return service.getRootFolderByUserId()
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileTypeConversionException | FileNotFoundException |IllegalDmlActionException | UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } 
    }
     @RequestMapping(value = "/management/share", method = RequestMethod.POST)
    public ResponseEntity<?> share(@RequestBody FileShareRequest req) {
      
   
        try {
            return ResponseEntity.ok( service.shareFile(req));
        } catch (JwtParseException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (JwtExpiredException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (FileNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (UnAuthorizedActionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        catch(Exception ex){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

        }
        
       
    }
    //projectFolderDto
   
}
