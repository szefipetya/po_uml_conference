/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.utils.server_side;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Event <E extends EventArgs> {
    private LinkedList<EventListener<E>> listeners = new LinkedList<>();
    private Map<Integer,EventListener<E>> indexedListenerMap=new HashMap<>();

    public void invoke(E args){
        for (EventListener<E> listener: listeners) {
            listener.action(args);
        }
    }

    public void addListener(EventListener<E> listener){
        listeners.add(listener);
    }
    public void addListener(EventListener<E> listener,Integer groupId){
        indexedListenerMap.put(groupId,listener);
        listeners.add(listener);
    }
    public void removeAllListeners(){
      indexedListenerMap.clear();
      listeners.clear();
    }
    public void removeListener(EventListener<E> listener){
        listeners.remove(listener);
    }
  
}

