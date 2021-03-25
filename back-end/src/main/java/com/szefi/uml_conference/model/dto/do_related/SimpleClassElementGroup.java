/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import java.util.List;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "SimpleClassElementGroup")
public class SimpleClassElementGroup extends DynamicSerialObject 
        implements DynamicSerialContainer_I<AttributeElement>{
 
    private String group_name;
private GROUP_SYNTAX group_syntax;
 private List<AttributeElement> attributes;

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public GROUP_SYNTAX getGroup_syntax() {
        return group_syntax;
    }

    public void setGroup_syntax(GROUP_SYNTAX group_syntax) {
        this.group_syntax = group_syntax;
    }

    public List<AttributeElement> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeElement> attributes) {
        this.attributes = attributes;
    }

    @Override
    public List<AttributeElement> getContainer() {
        return this.getAttributes();
    }
}
