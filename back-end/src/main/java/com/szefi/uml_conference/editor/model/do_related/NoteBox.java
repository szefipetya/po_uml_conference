/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.do_related;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
@JsonTypeName(value = "NoteBox")
@Entity
public class NoteBox extends DiagramObject {
        @Column(length = 2000)
    private String content;    

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
     @Override
    public void update(DynamicSerialObject obj) {
        super.update(obj);
        if(obj instanceof NoteBox){
            this.content=((NoteBox)obj).getContent();
        }
    }

    @Override
    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        super.injectSelfToStateMap(sessionItemMap, sessionContainerMap); 
    }
   
}
