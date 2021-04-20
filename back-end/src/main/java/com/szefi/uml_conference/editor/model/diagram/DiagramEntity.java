/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.diagram;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author h9pbcl
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class DiagramEntity implements Serializable{
    @Id
    @GeneratedValue
    private Integer id;
/*@ManyToOne
private UserEntity owner;*/
@OneToMany(mappedBy = "diagram",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.REFRESH})
private List<DiagramObject> dgObjects;
@OneToMany(mappedBy = "diagram",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.REFRESH})
private List<Line> lines;
    /*@JsonIgnore
   public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }*/

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

  
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
}
