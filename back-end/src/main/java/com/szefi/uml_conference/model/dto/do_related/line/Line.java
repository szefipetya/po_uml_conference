/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related.line;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.model.dto.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "Line")
public class Line extends DynamicSerialObject implements AutoSessionInjectable_I {
    private String id;
 private LineType lineType;
 private String object_start_id;
 private String object_end_id;
 private List<BreakPoint> breaks;

    public List<BreakPoint> getBreaks() {
        return breaks;
    }

    public void setBreaks(List<BreakPoint> breaks) {
        this.breaks = breaks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public String getObject_start_id() {
        return object_start_id;
    }

    public void setObject_start_id(String object_start_id) {
        this.object_start_id = object_start_id;
    }

    public String getObject_end_id() {
        return object_end_id;
    }

    public void setObject_end_id(String object_end_id) {
        this.object_end_id = object_end_id;
    }

    @Override
    public void injectSelfToStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        this.injectIdWithPrefix(UUID.randomUUID().toString());
            sessionItemMap.put(this.id, Pair.of(new SessionState(),this));
    }

    @Override
    public void deleteSelfFromStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
            sessionItemMap.remove(this.id);
    }

    @Override
    public void injectIdWithPrefix(String newid) {
        this.id="l_"+newid;
        }

    @Override
    public void update(DynamicSerialObject obj) {
        if(obj instanceof Line){
            Line l=(Line)obj;
            this.breaks=l.getBreaks();
            this.lineType=l.getLineType();
            this.object_start_id=l.getObject_start_id();
            this.object_end_id=l.getObject_end_id();
        }
        
    }
}
