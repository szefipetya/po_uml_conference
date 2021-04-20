/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Cascade;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
@Entity
public class DiagramObject extends DynamicSerialObject implements AutoSessionInjectable_I {
   
    @OneToOne(cascade=CascadeType.ALL,mappedBy = "dgObject")
    Rect dimensionModel;
    @ManyToOne
    DiagramEntity diagram;

    @JsonIgnore
    public DiagramEntity getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramEntity diagram) {
        this.diagram = diagram;
    }

    public Rect getDimensionModel() {
        return dimensionModel;
    }

    public void setDimensionModel(Rect dimensionModel) {
        this.dimensionModel = dimensionModel;
    }
    

private float  min_height;
private int  z;
 private boolean edit;
 private String doc;

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }
   

 
    public float getMin_height() {
        return min_height;
    }

    public void setMin_height(float min_height) {
        this.min_height = min_height;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    @Override
    public void update(DynamicSerialObject obj) {
        System.out.println("update diagram on backend");
        if(obj instanceof DiagramObject){
           this.dimensionModel= ((DiagramObject) obj).getDimensionModel();
        }
      
    }
    
  
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DiagramObject){
            return ((DiagramObject)obj).getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
          sessionItemMap.put(getId(),Pair.of(new SessionState(),this));
    }

  

    @Override
    public void deleteSelfFromStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        sessionItemMap.remove(getId());
    }

  
}
