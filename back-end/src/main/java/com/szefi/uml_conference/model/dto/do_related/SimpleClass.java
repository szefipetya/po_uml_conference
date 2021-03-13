/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import java.util.List;

/**
 *
 * @author h9pbcl
 */
public class SimpleClass extends DiagramObject{
   private String class_type;
   private List<SimpleClassElementGroup> groups;
  private AttributeElement titleModel;

    public String getClass_type() {
        return class_type;
    }

    public void setClass_type(String class_type) {
        this.class_type = class_type;
    }

    public List<SimpleClassElementGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<SimpleClassElementGroup> groups) {
        this.groups = groups;
    }

    public AttributeElement getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(AttributeElement titleModel) {
        this.titleModel = titleModel;
    }
}
