/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related.line;

/**
 *
 * @author h9pbcl
 */
public class Line {
    private String id;
 private LineType lineType;
 private String object_start_id;
 private String object_end_id;

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
}
