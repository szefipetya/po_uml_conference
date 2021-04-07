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
import com.szefi.uml_conference.model.dto.do_related.NoteBox;
import com.szefi.uml_conference.model.dto.do_related.SimpleClass;
import com.szefi.uml_conference.model.dto.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.model.dto.do_related.line.Line;
import com.szefi.uml_conference.model.dto.socket.ACTION_TYPE;
import com.szefi.uml_conference.model.dto.socket.EditorAction;
import com.szefi.uml_conference.model.dto.socket.LOCK_TYPE;
import com.szefi.uml_conference.model.dto.socket.SessionState;
import com.szefi.uml_conference.model.dto.socket.tech.UserWebSocket;
import com.szefi.uml_conference.model.dto.top.AutoSessionInjectable_I;
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
import java.util.concurrent.BlockingQueue;
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
    @Qualifier("nestedActionQueue") 
    BlockingQueue<EditorAction> nestedActionQueue;
    
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

    public List<String> deleteLocksRelatedToUser(String user_id){
        List<String> ret=new ArrayList<>();
     for( Entry<String,Pair<SessionState,DynamicSerialObject>> e : sessionItemMap.entrySet()){
        if(e.getValue().getFirst().getLockerUser_id()!=null&&
               e.getValue().getFirst().getLockerUser_id().equals(user_id)){
        if(this.unLockObjectById(e.getKey(), user_id)){
            ret.add(e.getKey());
        }
        }
    }
        return ret;
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
        SessionState s = this.getSessionStateById(target_id);
        if(s!=null){
        //even if someone locked it, and its me, i can still lock it again for myself
           if(s.getLocks().length==0||
                   ("-".equals(s.getLockerUser_id()) ? user_id == null 
                   : s.getLockerUser_id().equals(user_id)))
           {
                System.out.println("locks are set for object"+target_id);
        s.setLockerUser_id(user_id);
        s.setLocks(locks);
          return true;
           }
        }
      return false;

    }

  
    
    public boolean isLockedById(String id) {
        SessionState s = this.getSessionStateById(id);
        if (s == null) 
            return false;
        return s.getLocks().length > 0;
    }
    public boolean isItemLockedByMe(String id,String user_id){
         SessionState s =this.getSessionStateById(id);
          if (s == null) 
            return false;
          return s.getLockerUser_id().equals(user_id);
    }
    //returning new item's id
  
    public DynamicSerialObject createItemForContainer(String user_id,String cont_id,DynamicSerialObject obj){
        Pair<SessionState,DynamicSerialContainer_I> s= this.sessionContainerMap.get(cont_id);
         String rand_id=UUID.randomUUID().toString();
        if(s!=null){
            DynamicSerialContainer_I cont=s.getSecond();
         
            obj.setId(rand_id);
            cont.container().add(obj);
            if(obj instanceof Element_c){              
                Element_c casted=(Element_c)obj;
                casted.setEdit(false);
            }
         
            this.sessionItemMap.put(rand_id,Pair.of(new SessionState(),obj)); 
            this.lockObjectById(rand_id, user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
            SessionState state=getSessionStateById(obj.getId());
            state.setDraft(true);
            state.setLockerUser_id(user_id);
            System.out.println("obj created and locked for"+user_id);
              getSessionStateById(obj.getId()).setExtra(new HashMap<>());
              getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+user_id);
              
                   
            return obj;
            //the object has no parent, therefore the container is the diagram itself
        }else if(cont_id.equals("root")){
            //most akkor a Diagramra bízzam a beinjektálást vagy csinááljam meg itt?
           
            if(obj instanceof AutoSessionInjectable_I){
                AutoSessionInjectable_I casted=(AutoSessionInjectable_I)obj;
              //  casted.injectIdWithPrefix(rand_id);
                casted.injectSelfToStateMap(this.sessionItemMap,this.sessionContainerMap);
               
                
            this.lockObjectById(obj.getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
            SessionState state=getSessionStateById(obj.getId());
            state.setDraft(true);
           
            
            System.out.println("obj created and locked for"+user_id);
              getSessionStateById(obj.getId()).setExtra(new HashMap<>());
              getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+user_id);
              //extra step for titleModel locking
              if(obj instanceof DiagramObject){
                DiagramObject dgo=(DiagramObject)obj;
                    if(dgo instanceof SimpleClass){
                        SimpleClass cls=(SimpleClass)dgo;
                      this.lockObjectById(cls.getTitleModel().getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});

                    }


                this.dg.getDgObjects().add(dgo);
              }       
                return obj;
            }
            return null;
        }else if(cont_id.equals("l_root")){
            if(obj instanceof AutoSessionInjectable_I){
                AutoSessionInjectable_I casted=(AutoSessionInjectable_I)obj;
                casted.injectSelfToStateMap(this.sessionItemMap,this.sessionContainerMap);
                 /*   this.lockObjectById(obj.getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
            SessionState state=getSessionStateById(obj.getId());
            state.setDraft(true);*/
           
            
            System.out.println("obj created and locked for"+user_id);
              getSessionStateById(obj.getId()).setExtra(new HashMap<>());
              getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+user_id);
              if(obj instanceof Line){
                 this.dg.getLines().add((Line)obj);
              return (Line)obj;
              }
            }      
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
                    //  sessionItemMap.put(d.getId(),Pair.of(new SessionState(),d));
                    if(d instanceof NoteBox){
                        NoteBox n=(NoteBox)d;
         sessionItemMap.put(n.getId(), Pair.of(new SessionState(),n));
                       // n.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
                    }
                    if (d instanceof SimpleClass) {
                        SimpleClass c = (SimpleClass) d;
                        
                         sessionItemMap.put(c.getId(), Pair.of(new SessionState(),c));
                         sessionContainerMap.put(c.getId(), Pair.of(new SessionState(),c));
                         sessionItemMap.put(c.getTitleModel().getId(), Pair.of(new SessionState(),c.getTitleModel()));
                         for(SimpleClassElementGroup g:c.getGroups()){
                             sessionContainerMap.put(g.getId(), Pair.of(new SessionState(),g));

                             for(AttributeElement e:g.getAttributes()){
                                sessionItemMap.put(e.getId(), Pair.of(new SessionState(),e));

                             }
                         }
                        //c.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
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

    public DynamicSerialObject getItemById(String target_id){
          Pair<SessionState,DynamicSerialObject> ss = sessionItemMap.get(target_id);
                 if(ss==null) return null;
             return ss.getSecond();
    }
 
     public DynamicSerialContainer_I getContainerById(String cont_id){
          Pair<SessionState,DynamicSerialContainer_I> ss = sessionContainerMap.get(cont_id);
                 if(ss==null) return null;
             return ss.getSecond();
    }
    
    
    public boolean unLockObjectById(String target_id, String user_id) {
              SessionState s=getSessionStateById(target_id);
        if(s!=null)
       if (user_id.equals(s.getLockerUser_id()==null?"":s.getLockerUser_id())) {
            s.setLockerUser_id("-");
            s.setLocks(new LOCK_TYPE[0]);
           if(s.getExtra()!=null) s.getExtra().clear();
            return true;
        }
        return false;
    }

    public Pair<SessionState,DynamicSerialObject> updateObjectAndUnlock(DynamicSerialObject obj,String user_id) throws NullPointerException {
       
       
          SessionState s=this.getSessionStateById(obj.getId());
          if(s!=null){
                s.setDraft(false);
               if(this.unLockObjectById(obj.getId(), user_id)){
                   this.getItemById(obj.getId()).update(obj); 
                   return this.sessionItemMap.get(obj.getId());
                }
          }
         return null;      
    }

  

    public boolean deleteItemFromContainerById(String user_id,String target_id, String parent_id) {
        if(this.isItemLockedByMe(target_id, user_id)){
            DynamicSerialContainer_I cont= getContainerById(parent_id);
            if(cont!=null){
               DynamicSerialObject obj=getItemById(target_id);
               if(obj!=null){
                   sessionItemMap.remove(target_id);
                   return cont.container().remove(obj);
               }
            }else{
                if(parent_id.equals("root")){//global object
                  
                        DynamicSerialObject item=this.getItemById(target_id);
                    if(item instanceof SimpleClass){
                        System.out.println("this is a SimpleClass delete");
                        SimpleClass casted=(SimpleClass)item;
                        for(SimpleClassElementGroup g: casted.getGroups()){
                         g.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
                               for(AttributeElement e:g.getAttributes()){
                                 e.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
                               }
                        }
                        this.sessionItemMap.remove(casted.getTitleModel().getId());
                        
                        //find any related lines. Delete them first.
                        List<Line> filteredList=new ArrayList<>();
                        for(Line l:this.dg.getLines()){
                            if((l.getObject_start_id()==null?"":l.getObject_start_id()).equals(casted.getId())
                                    ||(l.getObject_end_id()==null?"":l.getObject_end_id()).equals(casted.getId())){
                                //send a delete event to the actionprocessor
                                filteredList.add(l);
                               
                            }
                        }
                        for(Line l:filteredList){
                               this.lockObjectById(l.getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
                                EditorAction action=new EditorAction(ACTION_TYPE.DELETE);
                                action.getTarget().setParent_id("l_root");
                                action.getTarget().setTarget_id(l.getId());
                                action.getTarget().setType("Line");
                                action.setUser_id(user_id);
                                nestedActionQueue.add(action);
                        }
                        filteredList.clear();
                    }
                    
                 
                         item.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
                    DiagramObject ob=new DiagramObject();
                    ob.setId(target_id);
                   
                    return  this.dg.getDgObjects().remove(ob);//the equals is overrided with id comparison;
                }else  if(parent_id.equals("l_root")){//global line
                  
                        DynamicSerialObject item=this.getItemById(target_id);
                    if(item instanceof Line){
                        System.out.println("this is a Line delete");
                        Line casted=(Line)item;
                        
                      casted.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);             
                    return  this.dg.getLines().remove(casted);//the equals is overrided with id comparison;
                    }
                }
            }
        }
        
            return false;
    }

    private String getItemParentId(String target_id){
        for(Entry<String, Pair<SessionState, DynamicSerialContainer_I>> e:this.sessionContainerMap.entrySet()){
            for(Object item:e.getValue().getSecond().container()){
                if(item instanceof DynamicSerialObject){
                   DynamicSerialObject casted=(DynamicSerialObject)item;
                   if(casted.getId().equals(target_id)){
                       return e.getKey();
                   }
                }
            }
        }
        return "" ;
    }
    
    //returning the list of deleted object's ids
    public List<String> deleteDraftsByUser(String user_id) {
        
        List<String> ret=new ArrayList<>();
          List<Map.Entry<String,Pair<SessionState,DynamicSerialObject>>> list=this.getItemsByUser(user_id);
           for(Map.Entry<String,Pair<SessionState,DynamicSerialObject>> e:list){
         if(e.getValue().getFirst().isDraft()){
              if( this.deleteItemFromContainerById(user_id,e.getKey(),getItemParentId(e.getKey())));
              ret.add(e.getKey());
                
            }
        }
           //if all the elements were removed, return true else false
           return ret;
         
    }
    public List<String> unlockObjectsByUserId(String user_id){
    List<String> ret=new ArrayList<>();
        List<Entry<String, Pair<SessionState, DynamicSerialObject>>> items= this.getItemsByUser(user_id);
        for( Entry<String, Pair<SessionState, DynamicSerialObject>> item:items){
         if(this.unLockObjectById(item.getKey(), user_id))
             ret.add(item.getKey());
        }
        return ret;
    }
    private List<Map.Entry<String,Pair<SessionState,DynamicSerialObject>>> getItemsByUser(String user_id){
        List<Map.Entry<String,Pair<SessionState,DynamicSerialObject>>> list=new ArrayList<>();
        for(Map.Entry<String,Pair<SessionState,DynamicSerialObject>> e:this.getSessionItemMap().entrySet()){
          if(e.getValue().getFirst().getLockerUser_id()!=null&&e.getValue().getFirst().getLockerUser_id().equals(user_id)){
              list.add(e);
          }
        }
        return list;
    }

    public Object updateObjectAndHoldLock(DynamicSerialObject obj, String user_id) {
          SessionState s=this.getSessionStateById(obj.getId());
          if(s!=null){
                s.setDraft(false);
               if(this.isItemLockedByMe(obj.getId(), user_id)){
                   System.out.println("object updated");
                   this.getItemById(obj.getId()).update(obj); 
                /*   if(obj.getExtra().containsKey("draft")){
                       obj.getExtra().replace("draft", "false");
                   }*/
                   return this.sessionItemMap.get(obj.getId());
                }
          }
         return null;      
    }

}
