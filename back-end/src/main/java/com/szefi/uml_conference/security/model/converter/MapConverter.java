/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.utils.generator.MapToJsonGenerator;
import com.szefi.uml_conference.security.model.ROLE;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import java.lang.Class;

/**
 *
 * @author h9pbcl
 */
public class MapConverter implements AttributeConverter<Map<String,String>, String> {
   
    ObjectMapper mapper=new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(Map<String,String> map) {
        try {
            return mapper.writeValueAsString(map);
            // return roleList != null ?  String.join(SPLIT_CHAR,roleList.stream().map(r->r.toString()).collect(Collectors.toList())) : "";
        } catch (JsonProcessingException ex) {
            Logger.getLogger(MapConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Map<String,String> convertToEntityAttribute(String string) {
        try {
            return mapper.readValue(string,Map.class);
            //return string != null ? Arrays.asList(string.split(SPLIT_CHAR)).stream().map(s->ROLE.valueOf(s)).collect(Collectors.toList()) : emptyList();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(MapConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
          return null;
    }
    
    
    
  
            
}
