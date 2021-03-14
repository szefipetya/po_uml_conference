/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author h9pbcl
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.PROPERTY,
        property="_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=SimpleClass.class, name="SimpleClass"),
        @JsonSubTypes.Type(value=NoteBox.class, name="NoteBox"),
      
})
public class DiagramObject {
     private String id;
 private float posx;
 private float posy;
  private float width;
  private float height;
 private DiagramObject_Scaled scaledModel;
private float  min_height;
private int  z;
 private boolean edit;
private String _type;

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getPosx() {
        return posx;
    }

    public void setPosx(float posx) {
        this.posx = posx;
    }

    public float getPosy() {
        return posy;
    }

    public void setPosy(float posy) {
        this.posy = posy;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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

  
}
