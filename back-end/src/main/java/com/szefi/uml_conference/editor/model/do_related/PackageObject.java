/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.model.converter.PackageElementsListConverter;
import com.szefi.uml_conference.editor.model.converter.RectConverter;
import com.szefi.uml_conference.management.model.entity.project.ProjectFolderEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
@JsonTypeName(value = "PackageObject")
@Entity
public class PackageObject extends DiagramObject {

    public PackageObject() {
        
    }
    
    @OneToOne(mappedBy = "parent",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.REFRESH})
    private TitleElement titleModel; 
    
       @OneToOne
       @JsonIgnore
       private ProjectFolderEntity pFolder; 

    public ProjectFolderEntity getpFolder() {
        return pFolder;
    }

    public void setpFolder(ProjectFolderEntity pFolder) {
        this.pFolder = pFolder;
    }

    public TitleElement getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(TitleElement titleModel) {
        this.titleModel = titleModel;
    }
     //   @LazyCollection(LazyCollectionOption.FALSE)

  // @OneToMany(cascade = CascadeType.ALL,mappedBy = "parent")
    @Column(length = 5000)
     @Convert(converter = PackageElementsListConverter.class)
    private List<PackageElement> elements=new ArrayList<>();    

    public List<PackageElement> getElements() {
        return elements;
    }

    public void setElements(List<PackageElement> element) {
        this.elements = element;
    }
    
  
     @Override
    public void update(DynamicSerialObject obj) {
        super.update(obj);
        
       if(obj instanceof PackageObject){
         
         PackageObject casted=(PackageObject)obj; 
         this.elements.clear();
         this.setElements(casted.getElements());
      /*     this.elements.stream().forEach(e->{
        //   e.setParent(this);
           });*/
          //  this.titleModel=((PackageObject)obj).getTitleModel();
           // this.titleModel.setParent(this);
        }
    }

    @Override
    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        super.injectSelfToStateMap(sessionItemMap, sessionContainerMap); 
        this.titleModel.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
    }

    @Override
    public void deleteSelfFromStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        super.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap); //To change body of generated methods, choose Tools | Templates.
                this.titleModel.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);

    }
    
   
}
