/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.model.dto.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import java.util.Map;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */

public class DiagramObject extends DynamicSerialObject implements AutoSessionInjectable_I {
Rect dimensionModel;

    public Rect getDimensionModel() {
        return dimensionModel;
    }

    public void setDimensionModel(Rect dimensionModel) {
        this.dimensionModel = dimensionModel;
    }
 private DiagramObject_Scaled scaledModel;
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
   

    public DiagramObject_Scaled getScaledModel() {
        return scaledModel;
    }

    public void setScaledModel(DiagramObject_Scaled scaledModel) {
        this.scaledModel = scaledModel;
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
    public void injectSelfToStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
          sessionItemMap.put(getId(),Pair.of(new SessionState(),this));
    }

    @Override
    public void injectIdWithPrefix(String newid) {
        this.setId(newid);
    }

    @Override
    public void deleteSelfFromStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        sessionItemMap.remove(getId());
    }

  
}
