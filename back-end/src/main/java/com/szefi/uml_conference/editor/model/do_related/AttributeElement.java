/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "AttributeElement")
@Entity
public class AttributeElement extends Element_c  {
   @ManyToOne
    SimpleClassElementGroup group;
@JsonIgnore
    public SimpleClassElementGroup getGroup() {
        return group;
    }

    public void setGroup(SimpleClassElementGroup group) {
        this.group = group;
    }
    
    private String visibility;
    private String attr_type;
   private String doc;
   
  
    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }
    private int index;

   
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public String getVisibility() {
        return visibility;
    }
    @Override
    public void update(DynamicSerialObject obj){
        super.update(obj);
         if(obj instanceof AttributeElement){
            AttributeElement casted=(AttributeElement)obj;
        this.attr_type=casted.attr_type;
        this.name=casted.name;
        this.visibility=casted.visibility;
       this.doc=casted.doc;
       this.index=casted.index;
         }else throw new UnsupportedClassVersionError("update class's type is incorrect");
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
