/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.editor.model.do_related.PackageElement;
import com.szefi.uml_conference.editor.model.do_related.line.BreakPoint;
import com.szefi.uml_conference.security.model.ROLE;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author h9pbcl
 */


@Converter
public class BreakPointListConverter implements AttributeConverter<List<BreakPoint>, String> {
    private static final String SPLIT_CHAR = ";";
    ObjectMapper mapper=new ObjectMapper();

    public BreakPointListConverter() {
        super();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    @Override
    public String convertToDatabaseColumn(List<BreakPoint> roleList) {
          
  
         
      try {
              return  mapper.writeValueAsString(roleList);
           } catch (JsonProcessingException ex) {
               Logger.getLogger(BreakPointListConverter.class.getName()).log(Level.SEVERE, null, ex);
           }
      
     return "[]";
    }

    @Override
    public List<BreakPoint> convertToEntityAttribute(String string) {
      List<BreakPoint> ret=new ArrayList<>();
       
        try {
             Arrays.asList(mapper.readValue(string, BreakPoint[].class)).forEach(i->ret.add(i));
             return ret;
       
    
        
        } catch (JsonProcessingException ex) {
            Logger.getLogger(BreakPointListConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
           
        return emptyList();
      
    }
}
