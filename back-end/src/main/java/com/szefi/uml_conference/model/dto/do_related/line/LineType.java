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
class LineType {
  private LINE_HEAD  startHead;
 private LINE_HEAD endHead;
 private LINE_BODY body;
 private LINE_TYPE type;

    public LINE_HEAD getStartHead() {
        return startHead;
    }

    public void setStartHead(LINE_HEAD startHead) {
        this.startHead = startHead;
    }

    public LINE_HEAD getEndHead() {
        return endHead;
    }

    public void setEndHead(LINE_HEAD endHead) {
        this.endHead = endHead;
    }

    public LINE_BODY getBody() {
        return body;
    }

    public void setBody(LINE_BODY body) {
        this.body = body;
    }

    public LINE_TYPE getType() {
        return type;
    }

    public void setType(LINE_TYPE type) {
        this.type = type;
    }
}
