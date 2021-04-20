/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.diagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.szefi.uml_conference.editor.model.do_related.DiagramObject;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.line.Line;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.model.User_PublicDto;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author h9pbcl
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Diagram implements Serializable{
private User_PublicDto owner;

private List<DiagramObject> dgObjects;

private List<Line> lines;

    public User_PublicDto getOwner() {
        return owner;
    }

    public void setOwner(User_PublicDto owner) {
        this.owner = owner;
    }

    public List<DiagramObject> getDgObjects() {
        return dgObjects;
    }

    public void setDgObects(List<DiagramObject> dgObjects) {
        this.dgObjects = dgObjects;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

  
    
}
