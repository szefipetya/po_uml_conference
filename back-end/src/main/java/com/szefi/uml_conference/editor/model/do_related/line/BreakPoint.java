/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related.line;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
public class BreakPoint {
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

  

  
        
}
