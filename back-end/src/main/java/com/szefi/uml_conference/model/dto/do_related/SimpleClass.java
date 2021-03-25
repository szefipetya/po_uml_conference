/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;

/**
 *
 * @author h9pbcl
 */

@JsonTypeName(value = "SimpleClass")
public class SimpleClass extends DiagramObject{
   private List<SimpleClassElementGroup> groups;
   private Element_c titleModel;
   private String  name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<SimpleClassElementGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<SimpleClassElementGroup> groups) {
        this.groups = groups;
    }

    public Element_c getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(Element_c titleModel) {
        this.titleModel = titleModel;
    }

   
}
