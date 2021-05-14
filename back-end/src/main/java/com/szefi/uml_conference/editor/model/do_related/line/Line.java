/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related.line;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.model.converter.BreakPointListConverter;
import com.szefi.uml_conference.security.model.converter.RoleListConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "Line")
@Entity
public class Line extends DynamicSerialObject implements AutoSessionInjectable_I {
   
    @OneToOne(mappedBy = "line",cascade = CascadeType.ALL)
 private LineType lineType;
 private Integer object_start_id;
 private Integer object_end_id;
//@LazyCollection(LazyCollectionOption.FALSE)
 //@OneToMany(mappedBy = "line",cascade=CascadeType.ALL)
 @Column(length = 5000)
@Convert(converter = BreakPointListConverter.class)
 private List<BreakPoint> breaks;
 
 @ManyToOne
DiagramEntity diagram;

    @JsonIgnore
    public DiagramEntity getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramEntity diagram) {
        this.diagram = diagram;
    }

    public Line() {
        this.breaks=new ArrayList<>();
    }

    public List<BreakPoint> getBreaks() {
        return breaks;
    }

    public void setBreaks(List<BreakPoint> breaks) {
        this.breaks = breaks;
    }

  

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public Integer getObject_start_id() {
        return object_start_id;
    }

    public void setObject_start_id(Integer object_start_id) {
        this.object_start_id = object_start_id;
    }

    public Integer getObject_end_id() {
        return object_end_id;
    }

    public void setObject_end_id(Integer object_end_id) {
        this.object_end_id = object_end_id;
    }

    @Override
    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
      //  this.injectIdWithPrefix(UUID.randomUUID().toString());
            sessionItemMap.put(this.getId(), Pair.of(new SessionState(),this));
    }

    @Override
    public void deleteSelfFromStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
            sessionItemMap.remove(this.getId());
    }

  

    @Override
    public void update(DynamicSerialObject obj) {
        if(obj instanceof Line){
            Line l=(Line)obj;
            this.breaks=l.getBreaks();
           // this.breaks.stream().forEach((BreakPoint bp)->bp.setLine(this));
           Integer tid=this.lineType.getId();
            this.lineType=l.getLineType();
            this.lineType.setLine(this);
            this.lineType.setId(tid);
            this.object_start_id=l.getObject_start_id();
            this.object_end_id=l.getObject_end_id();
        }
        
    }
}
