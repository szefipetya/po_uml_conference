/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.editor.model.do_related.Rect;
import com.szefi.uml_conference.editor.model.do_related.line.BreakPoint;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RectConverter implements AttributeConverter<Rect, String> {
    private static final String SPLIT_CHAR = ";";
    ObjectMapper mapper=new ObjectMapper();

    public RectConverter() {
        super();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    @Override
    public String convertToDatabaseColumn(Rect r) {
          
  
         
      try {
              return  mapper.writeValueAsString(r);
           } catch (JsonProcessingException ex) {
               Logger.getLogger(BreakPointListConverter.class.getName()).log(Level.SEVERE, null, ex);
           }
      
     return null;
    }

    @Override
    public Rect convertToEntityAttribute(String string) {
     
       
        try {
            return mapper.readValue(string,Rect.class);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(BreakPointListConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
           
      return null;
      
    }
}