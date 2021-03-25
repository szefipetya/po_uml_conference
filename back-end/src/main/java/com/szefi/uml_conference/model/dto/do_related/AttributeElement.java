/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "AttributeElement")
public class AttributeElement extends Element_c  {
    private String visibility;
    private String attr_type;

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getAttr_type() {
        return attr_type;
    }

    public void setAttr_type(String attr_type) {
        this.attr_type = attr_type;
    }
}
