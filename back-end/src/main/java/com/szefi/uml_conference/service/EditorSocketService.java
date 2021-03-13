/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.model.dto.diagram.Diagram;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

/**
 *
 * @author h9pbcl
 */
@Service
public class EditorSocketService {
 
    ObjectMapper objectMapper;
    Diagram dg;
   public EditorSocketService(){
        try {
           
            dg=new Diagram();
            objectMapper=new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Resource res = new ClassPathResource("d.json");
            byte[] buffer = new byte[res.getInputStream().available()];
            res.getInputStream().read(buffer);
            File targetFile = new File("src/main/resources/tmp_d.tmp");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            try {
                
                dg=  objectMapper.readValue(targetFile, Diagram.class);
            } catch (IOException ex) {
              
                Logger.getLogger(EditorSocketService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(EditorSocketService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
  public  Diagram getDummyDiagram(){
      return dg;
    }
}
