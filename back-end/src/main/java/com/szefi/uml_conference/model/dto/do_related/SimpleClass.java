/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */

@JsonTypeName(value = "SimpleClass")
public class SimpleClass extends DiagramObject implements DynamicSerialContainer_I<SimpleClassElementGroup>{
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
    
    
  @Override
    public void injectSelfToStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        this.injectIdWithPrefix(UUID.randomUUID().toString());
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
    public void injectIdWithPrefix(String newid) {
        this.setId("c"+newid);
    }
    

    @Override
    public List<SimpleClassElementGroup> container() {
        return this.groups;
    }
        @Override
    public void deleteSelfFromStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        sessionContainerMap.remove(getId());
        sessionItemMap.remove(getId());
    }


}
