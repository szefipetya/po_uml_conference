/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.entity.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.do_related.PackageObject;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */
@Entity
public class ProjectFolderEntity extends ProjectFileEntity {
    
 @OneToMany(mappedBy = "parentProjectFolder" ,cascade = CascadeType.REMOVE)
   List<ProjectFileEntity> files;
   boolean is_projectRoot;
   
   @OneToOne(mappedBy="relatedFolder",cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
   DiagramEntity diagram;
   
   @JsonIgnore
    @OneToOne(mappedBy = "pFolder",cascade = {CascadeType.REMOVE})
    private PackageObject relatedPackageObject; 

    public PackageObject getRelatedPackageObject() {
        return relatedPackageObject;
    }

    public void setRelatedPackageObject(PackageObject relatedPackageObject) {
        this.relatedPackageObject = relatedPackageObject;
    }

    public DiagramEntity getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramEntity diagram) {
        this.diagram = diagram;
    }
   
    public List<ProjectFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<ProjectFileEntity> files) {
        this.files = files;
    }

    public ProjectFolderEntity() {
        
            super();
            files=new ArrayList<>();
    }

    public boolean isIs_projectRoot() {
        return is_projectRoot;
    }

    public void setIs_projectRoot(boolean is_projectRoot) {
        this.is_projectRoot = is_projectRoot;
    }

     public boolean addFile(ProjectFileEntity ent){
        ent.setParentProjectFolder(this);
        ent.setOwner(this.getOwner());
        ent.setRelatedProject(this.relatedProject);
    //    ent.setPermissions(this.getPermissions());
        return this.getFiles().add(ent);
    }


}
