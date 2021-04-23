/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.top;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.Element_c;
import com.szefi.uml_conference.editor.model.do_related.NoteBox;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.do_related.TitleElement;
import com.szefi.uml_conference.editor.model.do_related.line.BreakPoint;
import com.szefi.uml_conference.editor.model.do_related.line.Line;
import com.szefi.uml_conference.editor.model.do_related.line.Point;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.security.converter.MapConverter;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKeyColumn;
import org.springframework.data.util.Pair;

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
        @JsonSubTypes.Type(value=Element_c.class, name="Element_c"),
        @JsonSubTypes.Type(value=Line.class, name="Line"),
        @JsonSubTypes.Type(value=AttributeElement.class, name="AttributeElement"),
        @JsonSubTypes.Type(value=TitleElement.class, name="TitleElement"),
        @JsonSubTypes.Type(value=BreakPoint.class, name="BreakPoint"),
      /*  @JsonSubTypes.Type(value=Point.class, name="Point"),*/
        @JsonSubTypes.Type(value=SimpleClassElementGroup.class, name="SimpleClassElementGroup"), 
})
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class DynamicSerialObject implements AutoSessionInjectable_I {
    @Id
    @GeneratedValue
    private Integer id;
    // @Convert(converter = MapConverter.class)
      @ElementCollection
    @MapKeyColumn(name="extra_map")
    private Map<String,String>extra;

    public Map<String, String> getExtra() {
        if(extra==null){
            this.extra=new HashMap<>();
        }
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }
  

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void update(DynamicSerialObject obj){};
      @Override
    public boolean equals(Object obj) {
        if(obj instanceof DynamicSerialObject){
            return ((DynamicSerialObject)obj).getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteSelfFromStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}
