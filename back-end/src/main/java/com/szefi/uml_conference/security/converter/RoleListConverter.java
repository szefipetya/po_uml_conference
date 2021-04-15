/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.converter;

import com.szefi.uml_conference.security.model.ROLE;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author h9pbcl
 */

@Converter
public class RoleListConverter implements AttributeConverter<List<ROLE>, String> {
    private static final String SPLIT_CHAR = ";";
    
    @Override
    public String convertToDatabaseColumn(List<ROLE> roleList) {
        return roleList != null ?  String.join(SPLIT_CHAR,roleList.stream().map(r->r.toString()).collect(Collectors.toList())) : "";
    }

    @Override
    public List<ROLE> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(SPLIT_CHAR)).stream().map(s->ROLE.valueOf(s)).collect(Collectors.toList()) : emptyList();
    }
}