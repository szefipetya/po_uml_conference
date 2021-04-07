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
//@JsonTypeName(value = "Point")
public class Point  {
    float x;
    float y;
   public Point(){}
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

   
}
