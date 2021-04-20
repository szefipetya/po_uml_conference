/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */
@Entity
@JsonTypeName("TitleElement")
public class TitleElement extends Element_c{ 
    @OneToOne
    SimpleClass parent;
    @JsonIgnore
    public SimpleClass getParent() {
        return parent;
    }

    public void setParent(SimpleClass parent) {
        this.parent = parent;
    }
    
}
