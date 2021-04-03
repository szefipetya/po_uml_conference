/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.do_related;

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
@JsonTypeName(value = "NoteBox")
public class NoteBox extends DiagramObject {
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
    public void injectSelfToStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        super.injectSelfToStateMap(sessionItemMap, sessionContainerMap); 
    }
     @Override
    public void injectIdWithPrefix(String newid) {
        this.setId("n"+newid);
    }
}
