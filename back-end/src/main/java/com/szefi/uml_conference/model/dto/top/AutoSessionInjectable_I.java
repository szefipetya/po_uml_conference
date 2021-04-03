/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.model.dto.top;

import com.szefi.uml_conference.model.dto.socket.SessionState;
import java.util.Map;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
public interface AutoSessionInjectable_I {

    public void injectSelfToStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap);
    public void deleteSelfFromStateMap(Map<String, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<String, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap);
    public void injectIdWithPrefix(String newid);
    
}
