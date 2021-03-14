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
  private AttributeElement titleModel;


private String _type;
private String  name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
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
