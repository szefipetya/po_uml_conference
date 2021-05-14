/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.management.model.ICON;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */

@JsonTypeName("PackageElement")
public class PackageElement{ 

    public PackageElement() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
   /* @ManyToOne
    PackageObject parent;*/
    ICON icon;
    Integer referencedObjectId;
protected String name;
 protected boolean edit;
    public Integer getReferencedObjectId() {
        return referencedObjectId;
    }

    public void setReferencedObjectId(Integer referencedObjectId) {
        this.referencedObjectId = referencedObjectId;
    }

    public ICON getIcon() {
        return icon;
    }

    public void setIcon(ICON icon) {
        this.icon = icon;
    }
   /* @JsonIgnore
    public PackageObject getParent() {
        return parent;
    }

    public void setParent(PackageObject parent) {
        this.parent = parent;
    }*/
    
}
