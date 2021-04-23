/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
@Entity
@JsonTypeName(value = "SimpleClassElementGroup")
public class SimpleClassElementGroup extends DynamicSerialObject 
        implements DynamicSerialContainer_I<AttributeElement>{
    @ManyToOne
    SimpleClass parent;
   
    
    private String group_name;
     @Enumerated(EnumType.ORDINAL)
    private GROUP_SYNTAX group_syntax;
     @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "group",cascade =CascadeType.PERSIST)
     private List<AttributeElement> attributes=new ArrayList<>();
    
    
 @JsonIgnore
    public SimpleClass getParentClass() {
        return parent;
    }

    public void setParentClass(SimpleClass parent) {
        this.parent = parent;
    }
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
    public List<AttributeElement> container() {
        return this.getAttributes();
    }

    @Override
    public void update(DynamicSerialObject obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



   @Override
    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
          //  this.injectIdWithPrefix("c"+UUID.randomUUID());
            sessionContainerMap.put(getId(), Pair.of(new SessionState(),this));

    }

  

    @Override
    public void deleteSelfFromStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        sessionContainerMap.remove(getId());
    }
}
