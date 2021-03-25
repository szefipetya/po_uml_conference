/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.top;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.szefi.uml_conference.model.dto.do_related.AttributeElement;
import com.szefi.uml_conference.model.dto.do_related.Element_c;
import com.szefi.uml_conference.model.dto.do_related.NoteBox;
import com.szefi.uml_conference.model.dto.do_related.SimpleClass;

import com.szefi.uml_conference.model.dto.do_related.SimpleClass;
import com.szefi.uml_conference.model.dto.do_related.SimpleClassElementGroup;

/**
 *
 * @author h9pbcl
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.PROPERTY,
        property="_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=SimpleClass.class, name="SimpleClass"),
        @JsonSubTypes.Type(value=NoteBox.class, name="NoteBox"),
        @JsonSubTypes.Type(value=Element_c.class, name="Element_c"),
        @JsonSubTypes.Type(value=AttributeElement.class, name="AttributeElement"),
        @JsonSubTypes.Type(value=SimpleClassElementGroup.class, name="SimpleClassElementGroup"),
        
      
})
public class DynamicSerialObject {
    private String _type;
    private String id;

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
