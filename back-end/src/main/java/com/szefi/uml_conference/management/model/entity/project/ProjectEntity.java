/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.management.model.entity.project;

import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.management.model.entity.File_cEntity;
import com.szefi.uml_conference.management.model.entity.FolderEntity;
import com.szefi.uml_conference.security.model.UserEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author h9pbcl
 */

@Entity
public class ProjectEntity extends File_cEntity{
    
    @OneToMany(mappedBy="project",cascade = CascadeType.ALL)
    private List<ProjectFileEntity> files;//root files

      @OneToMany(mappedBy="relatedProject",cascade = CascadeType.ALL)
    private List<ProjectFileEntity> relatedFiles;

    public List<ProjectFileEntity> getRelatedFiles() {
        return relatedFiles;
    }

    public void setRelatedFiles(List<ProjectFileEntity> relatedFiles) {
        this.relatedFiles = relatedFiles;
    }
 
    @OneToOne(mappedBy="project",cascade = CascadeType.REMOVE)
    private ProjectFolderEntity rootFolder;

    public ProjectFolderEntity getRootFolder() {
        return rootFolder;
    }

    public ProjectEntity() {
        super();
      
        files=new ArrayList<>();
           rootFolder=new ProjectFolderEntity();
           DiagramEntity dg=new DiagramEntity();
           dg.setRelatedFolder(rootFolder);
           rootFolder.setDiagram(dg);
        rootFolder.setDate(new Date());
        rootFolder.setIs_projectRoot(true);
        rootFolder.setName("~");
        rootFolder.setRelatedProject(this);
        rootFolder.setProject(this);
        this.files.add(rootFolder);
        
    }
    public ProjectEntity(UserEntity owner){
         super();
      
        files=new ArrayList<>();
        
        
        
           rootFolder=new ProjectFolderEntity();
       
           
           DiagramEntity dg=new DiagramEntity();
           dg.setRelatedFolder(rootFolder);
           dg.setOwner(owner);
           rootFolder.setDiagram(dg);
           
           this.setOwner(owner);  
           rootFolder.setOwner(owner);
        rootFolder.setDate(new Date());
        rootFolder.setIs_projectRoot(true);
        rootFolder.setName("~");
         rootFolder.setProject(this);
        rootFolder.setRelatedProject(this);
        
        this.files.add(rootFolder);
    }

    public void setRootFolder(ProjectFolderEntity rootFolder) {
        this.rootFolder = rootFolder;
    }
       public List<ProjectFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<ProjectFileEntity> files) {
        this.files = files;
    }
    
}
