/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */

@JsonTypeName(value = "SimpleClass")
@Entity
public class SimpleClass extends DiagramObject implements DynamicSerialContainer_I<SimpleClassElementGroup>{
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="parent",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.REFRESH})
   private List<SimpleClassElementGroup> groups=new ArrayList<>();
   @OneToOne(mappedBy = "parent",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.REFRESH})
   private TitleElement titleModel;
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

    public TitleElement getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(TitleElement titleModel) {
        this.titleModel = titleModel;
    }
    
    
  @Override
    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
     //   this.injectIdWithPrefix(UUID.randomUUID().toString());
        SessionState s=new SessionState();
           sessionContainerMap.put(getId(),Pair.of(s,this));
           sessionItemMap.put(getId(),Pair.of(s,this));
        getTitleModel().injectSelfToStateMap(sessionItemMap, sessionContainerMap);
                        for (SimpleClassElementGroup g : getGroups()) {
                            g.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
                            for (Element_c e : g.getAttributes()) {
                                e.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
                            }
                        }
    }
    
    

    @Override
    public List<SimpleClassElementGroup> container() {
        return this.groups;
    }
        @Override
    public void deleteSelfFromStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        sessionContainerMap.remove(getId());
        sessionItemMap.remove(getId());
    }


}
