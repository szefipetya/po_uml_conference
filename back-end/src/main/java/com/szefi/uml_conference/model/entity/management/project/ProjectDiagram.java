/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.entity.management.project;

import javax.persistence.Entity;

/**
 *
 * @author h9pbcl
 */
@Entity
public class ProjectDiagram extends ProjectFileEntity{

    public ProjectDiagram(String diagramJson) {
        super();
        this.diagramJson = diagramJson;
    }

    public ProjectDiagram() {
        super();
    }
    
    String diagramJson;
}
