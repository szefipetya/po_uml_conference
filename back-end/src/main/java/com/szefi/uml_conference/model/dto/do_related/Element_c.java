/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import java.util.Map;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "Element_c")
public class Element_c extends DynamicSerialObject{

protected String name;
 protected boolean edit;


 //viewModel: AttributeElement

 
  
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

    @Override
    public void update(DynamicSerialObject obj) {
        if(obj instanceof Element_c){
            Element_c casted=(Element_c)obj;
            this.name=casted.name; 
        }
    }



  
}
