/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.threads.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.model.dto.top.DynamicSerialObject;
import com.szefi.uml_conference.model.dto.diagram.Diagram;
import com.szefi.uml_conference.model.dto.do_related.AttributeElement;
import com.szefi.uml_conference.model.dto.do_related.DiagramObject;
import com.szefi.uml_conference.model.dto.do_related.Element_c;
import com.szefi.uml_conference.model.dto.do_related.SimpleClass;
import com.szefi.uml_conference.model.dto.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.model.dto.socket.LOCK_TYPE;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.socket.tech.UserWebSocket;
import com.szefi.uml_conference.model.dto.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.socket.threads.SocketThreadManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author h9pbcl
 */
@Service("socketService")
@Configuration
public class SocketSessionService {

    @Autowired
    @Qualifier("actionSockets")
    private List<UserWebSocket> actionSockets;
    @Autowired
    @Qualifier("stateSockets")
    private List<UserWebSocket> stateSockets;

    public SocketSessionService() {
        init();
        //   actionSockets=new LinkedList<UserWebSocket>();
        //  stateSockets=new LinkedList<UserWebSocket>();
        //socketThreadManager=new SocketThreadManager(actionSockets, stateSockets);
        //  socketThreadManager.start(); 
    }

    public void deleteLocksRelatedToUser(String user_id){
     for( Entry<String,Pair<SessionState,DynamicSerialObject>> e : sessionItemMap.entrySet()){
        if(e.getValue().getFirst().getLockerUser_id().equals(user_id)){
        this.unLockObjectById(e.getKey(), user_id);
          
        }
    }
    }
    
    public List<UserWebSocket> getSockets(SOCKET s) {
        if (s == SOCKET.ACTION) {
            return actionSockets;
        }
        if (s == SOCKET.STATE) {
            return stateSockets;
        }

        return null;
    }
    

    ObjectMapper objectMapper;
    Diagram dg;
    Map<String, Pair<SessionState,DynamicSerialObject>> sessionItemMap=new HashMap<>();
    Map<String, Pair<SessionState,DynamicSerialContainer_I>> sessionContainerMap=new HashMap<>();

    public  Map<String, Pair<SessionState,DynamicSerialObject>> getSessionItemMap() {
        return sessionItemMap;
    }

    public SessionState getSessionStateById(String id) {
        if(sessionItemMap.get(id)!=null)
        return sessionItemMap.get(id).getFirst();
        return null;
    }
        public SessionState getContainerSessionStateById(String id) {
        if(sessionContainerMap.get(id)!=null)
        return sessionContainerMap.get(id).getFirst();
        return null;
    }

    public boolean lockObjectById(String target_id, String user_id, LOCK_TYPE[] locks) {
        SessionState s = sessionItemMap.get(target_id).getFirst();
        //even if someone locked it, and its me, i can still lock it again for myself
           if(s.getLocks().length==0||
                   (s.getLockerUser_id() == null ? user_id == null 
                   : s.getLockerUser_id().equals(user_id)))
           {
                System.out.println("locks are set for object"+target_id);
        s.setLockerUser_id(user_id);
        s.setLocks(locks);
          return true;
           }
      return false;

    }

    public boolean isLockedById(String id) {
        SessionState s = sessionItemMap.get(id).getFirst();
        if (s == null) 
            return false;
        return s.getLocks().length > 0;
    }
    public boolean isLockedByMe(String id,String user_id){
         SessionState s = sessionItemMap.get(id).getFirst();
          if (s == null) 
            return false;
          return s.getLockerUser_id().equals(user_id);
    }
    //returning new item's id
    public DynamicSerialObject createItemForContainer(String user_id,String cont_id,DynamicSerialObject obj){
        Pair<SessionState,DynamicSerialContainer_I> s= this.sessionContainerMap.get(cont_id);
        if(s!=null){
            DynamicSerialContainer_I cont=s.getSecond();
            String rand_id=UUID.randomUUID().toString();
            obj.setId(rand_id);
            cont.getContainer().add(obj);
            if(obj instanceof Element_c){
                
                Element_c casted=(Element_c)obj;
                casted.setEdit(false);
            }
         
            this.sessionItemMap.put(rand_id,Pair.of(new SessionState(),obj)); 
            this.lockObjectById(rand_id, user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
            SessionState state=new SessionState();
            state.setLockerUser_id(user_id);
            System.out.println("obj created and locked for"+user_id);
            return obj;
        }
      return null;
    }

    public void init() {
        try {

            dg = new Diagram();
            System.out.println("service started");

            Resource res = new ClassPathResource("d.json");
            byte[] buffer = new byte[res.getInputStream().available()];
            res.getInputStream().read(buffer);
            File targetFile = new File("src/main/resources/tmp_d.tmp");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            try {
                objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                dg = objectMapper.readValue(targetFile, Diagram.class);
                for (DiagramObject d : dg.getDgObjects()) {
                    sessionItemMap.put(d.getId(),Pair.of(new SessionState(),d));
                    if (d instanceof SimpleClass) {
                        SimpleClass c = (SimpleClass) d;
                        sessionItemMap.put(c.getTitleModel().getId(), Pair.of(new SessionState(),c.getTitleModel()));
                        for (SimpleClassElementGroup g : c.getGroups()) {
                            sessionContainerMap.put(g.getId(), Pair.of(new SessionState(), g));
                            for (Element_c e : g.getAttributes()) {
                                sessionItemMap.put(e.getId(), Pair.of(new SessionState(),e));
                            }
                        }
                    }

                }
            } catch (IOException ex) {

                Logger.getLogger(SocketSessionService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketSessionService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Diagram getDummyDiagram() {
        return dg;
    }

    public boolean unLockObjectById(String target_id, String user_id) {
       Pair<SessionState,DynamicSerialObject> ss = sessionItemMap.get(target_id);
                 if(ss==null) return false;
              SessionState s=ss.getFirst();
        if (user_id.equals(s.getLockerUser_id()==null?"":s.getLockerUser_id())) {
            s.setLockerUser_id("-");
            s.setLocks(new LOCK_TYPE[0]);
            return true;
        }
        return false;
    }

    public Pair<SessionState,DynamicSerialObject> updateObject(DynamicSerialObject obj) {
        if(obj instanceof AttributeElement){
          AttributeElement cast=(AttributeElement)obj;
          
         return this.sessionItemMap.replace(cast.getId(),Pair.of(sessionItemMap.get(cast.getId()).getFirst(),cast));
        }
        else return null;      
    }

    public DynamicSerialObject getRestoredModel(String target_id) {
        return this.sessionItemMap.get(target_id).getSecond();
    }

}
