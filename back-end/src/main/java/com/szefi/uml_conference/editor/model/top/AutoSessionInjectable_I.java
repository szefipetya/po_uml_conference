/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.top;

import com.szefi.uml_conference.editor.model.socket.SessionState;
import java.util.Map;
import org.springframework.data.util.Pair;

/**
 *
 * @author h9pbcl
 */
public interface AutoSessionInjectable_I {

    public void injectSelfToStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap);
    public void deleteSelfFromStateMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap, Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap);
  //  public void injectIdWithPrefix(Integer newid);
    
}
