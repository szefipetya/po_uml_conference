/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related.line;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import java.util.Map;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "BreakPoint")
public class BreakPoint extends DynamicSerialObject {
        private boolean edit;
        private Point point;
        private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
 public BreakPoint(){}
    public BreakPoint(boolean edit, Point point) {
        this.edit = edit;
        this.point = point;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public void update(DynamicSerialObject obj) {
        this.point=((BreakPoint)obj).getPoint();
    }

    @Override
    public void injectSelfToStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteSelfFromStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void injectIdWithPrefix(String newid) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
