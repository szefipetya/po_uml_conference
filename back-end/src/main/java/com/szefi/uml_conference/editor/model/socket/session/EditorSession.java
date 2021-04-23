/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.model.socket.session;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.editor.model.diagram.Diagram;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.DiagramObject;
import com.szefi.uml_conference.editor.model.do_related.Element_c;
import com.szefi.uml_conference.editor.model.do_related.NoteBox;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.do_related.line.Line;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocket;
import com.szefi.uml_conference.editor.model.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.socket.threads.service.SOCKET;
import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author h9pbcl
 */
@Component
public class EditorSession {
    Long id;
      @Autowired
    @Qualifier("nestedActionQueue") 
    BlockingQueue<EditorAction> nestedActionQueue;
     /*MAIN SESSION DECLARATION*/
       DiagramEntity dg;
    List<UserWebSocket> userSockets=new ArrayList<>();
    Map<Integer, Pair<SessionState,DynamicSerialObject>> sessionItemMap=new HashMap<>();
    Map<Integer, Pair<SessionState,DynamicSerialContainer_I>> sessionContainerMap=new HashMap<>();
   
    
     ObjectMapper objectMapper;
     @Autowired
DiagramRepository diagramRepo;
     
      @Autowired
 DynamicSerialObjectRepository objectRepo;
    public EditorSession(DiagramRepository diagramRepo, DynamicSerialObjectRepository objectRepo,   BlockingQueue<EditorAction>  nestedActionQueue) {
           this.id=UUID.randomUUID().getLeastSignificantBits();
        this.objectRepo=objectRepo;
        this.diagramRepo=diagramRepo;
    }
     
     
   public UserWebSocket getUserSocketByNativeSocket(SOCKET type,WebSocketSession socket){
       for(UserWebSocket u:userSockets){
           if(type==SOCKET.ACTION)
             if(u.getActionSocket().equals(socket))return u;
           if(type==SOCKET.STATE)
             if(u.getStateSocket().equals(socket))return u;
       }
       return null;
   }
     /**
      @return returns true if the user was present inside
      */
     public boolean userDisconnect(String session_jwt){
       for(UserWebSocket sock:this.userSockets){
           if(sock.getSession_jwt().equals(session_jwt)){
               this.deleteLocksRelatedToUser(sock.getUser_id());
               return true;
           }
       }
       return false;
     }
     
     /*MAIN SESSION DECLARATION*/
    /*MAIN SESSION LOGIC*/
     public List<Integer> deleteLocksRelatedToUser(Integer user_id){
        List<Integer> ret=new ArrayList<>();
     for( Map.Entry<Integer,Pair<SessionState,DynamicSerialObject>> e : sessionItemMap.entrySet()){
        if(e.getValue().getFirst().getLockerUser_id()!=null&&
               e.getValue().getFirst().getLockerUser_id().equals(user_id)){
        if(this.unLockObjectById(e.getKey(), user_id)){
            ret.add(e.getKey());
        }
        }
    }
        return ret;
    }
    
 

 
    public  Map<Integer, Pair<SessionState,DynamicSerialObject>> getSessionItemMap() {
        return sessionItemMap;
    }
    public List<UserWebSocket> getSocketListContainingUserWithId(Integer id){
        return this.userSockets.stream().filter(u->u.getUser_id().equals(id)).collect(Collectors.toList());
    }
    public SessionState getSessionStateById(Integer id) {
        if(sessionItemMap.get(id)!=null)
            return sessionItemMap.get(id).getFirst();
        return null;
    }
    public SessionState getContainerSessionStateById(Integer id) {
        if(sessionContainerMap.get(id)!=null)
            return sessionContainerMap.get(id).getFirst();
        return null;
    }

    public boolean lockObjectById(Integer target_id, Integer user_id, LOCK_TYPE[] locks) {
        SessionState s = this.getSessionStateById(target_id);
        if(s!=null){
        //even if someone locked it, and its me, i can still lock it again for myself
           if(s.getLocks().length==0||
                   (Integer.valueOf(-1).equals(s.getLockerUser_id()) ? user_id == null 
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

  
    
    public boolean isLockedById(Integer id) {
        SessionState s = this.getSessionStateById(id);
        if (s == null) 
            return false;
        return s.getLocks().length > 0;
    }
    public boolean isItemLockedByMe(Integer id,Integer user_id){
         SessionState s =this.getSessionStateById(id);
          if (s == null) 
            return false;
          return s.getLockerUser_id().equals(user_id);
    }
    //returning new item's id
  @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DynamicSerialObject createItemForContainer(Integer user_id,Integer cont_id,DynamicSerialObject obj){
        Pair<SessionState,DynamicSerialContainer_I> s= this.sessionContainerMap.get(cont_id);
         
        if(s!=null){
            DynamicSerialContainer_I cont=s.getSecond();
         
           // obj.setId(rand_id);
          //obj.setId(null);
                
          //obj gets an id.
         //   cont.container().add(obj);
           
            if(obj instanceof AttributeElement){              
                AttributeElement casted=(AttributeElement)obj; 
                casted.setEdit(false);
               
                   casted.setGroup((SimpleClassElementGroup)cont); 
                   casted=objectRepo.save(casted);//id injected  
                   cont.container().add(casted);
                 SimpleClassElementGroup gr=objectRepo.save((SimpleClassElementGroup)cont);
                 
               //  obj=gr.get
               // objectRepo.save((SimpleClassElementGroup)cont);//update function will save it
                //objectRepo.save((SimpleClassElementGroup)cont);
               obj=casted;
            }
         
            this.sessionItemMap.put(obj.getId(),Pair.of(new SessionState(),obj)); 
            this.lockObjectById(obj.getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
            SessionState state=getSessionStateById(obj.getId());
            state.setDraft(true);
            state.setLockerUser_id(user_id);
            System.out.println("obj created and locked for"+user_id);
              getSessionStateById(obj.getId()).setExtra(new HashMap<>());
              getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+user_id);
              
             //   this.dg= this.diagramRepo.save(this.dg); 
            return obj;
            //the object has no parent, therefore the container is the diagram itself
        }else if(cont_id.equals(SocketSessionService.ROOT_ID)){
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
              //   this.dg=this.diagramRepo.save(this.dg);
                return obj;
            }
            return null;
        }else if(cont_id.equals(SocketSessionService.L_ROOT_ID)){
            if(obj instanceof AutoSessionInjectable_I){
                AutoSessionInjectable_I casted=(AutoSessionInjectable_I)obj;
                casted.injectSelfToStateMap(this.sessionItemMap,this.sessionContainerMap);
               
           
            
            System.out.println("obj created and locked for"+user_id);
              getSessionStateById(obj.getId()).setExtra(new HashMap<>());
              getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+user_id);
              if(obj instanceof Line){
                 this.dg.getLines().add((Line)obj);
                   this.dg= this.diagramRepo.save(this.dg);
              return (Line)obj;
              }
            }      
        }
     //  this.dg= this.diagramRepo.save(this.dg);
      return null;
    }


    @Transactional
    public Pair<SessionState,DynamicSerialObject> updateObjectAndUnlock(DynamicSerialObject obj,Integer user_id) throws NullPointerException {
       
       
          SessionState s=this.getSessionStateById(obj.getId());
          if(s!=null){
                s.setDraft(false);
               if(this.unLockObjectById(obj.getId(), user_id)){
                  
                   this.getItemById(obj.getId()).update(obj); 
                   
                  /* if(obj instanceof SimpleClass)
                  this.objectRepo.save((SimpleClass)this.getItemById(obj.getId()));
                    else if(obj instanceof NoteBox)
                  this.objectRepo.save((NoteBox)this.getItemById(obj.getId()));*/
                     if(obj instanceof AttributeElement){
                         ((AttributeElement) obj).setGroup(((AttributeElement)this.getItemById(obj.getId())).getGroup());
                     }this.objectRepo.save(this.getItemById(obj.getId()));
                     
                   return this.sessionItemMap.get(obj.getId());
                }
          }
             // this.dg= this.diagramRepo.save(this.dg); 
         return null;      
    }
    public static final Integer L_ROOT_ID=-2;
    public static final Integer ROOT_ID=-3;
  
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteItemFromContainerById(Integer user_id,Integer target_id, Integer parent_id) {
        if(this.isItemLockedByMe(target_id, user_id)){
            DynamicSerialContainer_I cont= getContainerById(parent_id);
            if(cont!=null){
               DynamicSerialObject obj=getItemById(target_id);
               if(obj!=null){
                  if(obj instanceof AttributeElement){
                      //((AttributeElement)obj).setGroup(null);
                      objectRepo.deleteById(target_id);
                   this.objectRepo.delete((AttributeElement)obj);
                   sessionItemMap.remove(target_id);
                   boolean l=cont.container().remove(obj);
                   try{
                   if(cont instanceof SimpleClassElementGroup){
                        this.objectRepo.save((SimpleClassElementGroup)cont);
                   }
                   }catch(Exception e){
                       e.printStackTrace();
                   }
                  //this.dg=  this.diagramRepo.save(this.dg);
                   return l;
               }
               }
            }else{
                if(parent_id.equals(SocketSessionService.ROOT_ID)){//global object
                  
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
                                action.getTarget().setParent_id(SocketSessionService.L_ROOT_ID);
                                action.getTarget().setTarget_id(l.getId());
                                action.getTarget().setType("Line");
                                action.setUser_id(user_id);
                                nestedActionQueue.add(action);// TODO: PASS THE USERS TO SEND SOMEHOW
                        }
                        filteredList.clear();
                    }
                    
                 
                         item.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
                    DiagramObject ob=new DiagramObject();
                    ob.setId(target_id);
                   boolean l=this.dg.getDgObjects().remove(ob);//the equals is overrided with id comparison;
                   
                   //  this.dg= this.diagramRepo.save(this.dg);
                   return  l;
                }else  if(parent_id.equals(SocketSessionService.L_ROOT_ID)){//global line
                  
                        DynamicSerialObject item=this.getItemById(target_id);
                    if(item instanceof Line){
                        System.out.println("this is a Line delete");
                        Line casted=(Line)item;
                        
                      casted.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);           
                      boolean l= this.dg.getLines().remove(casted);//the equals is overrided with id comparison;
                       
                    //     this.dg=this.diagramRepo.save(this.dg);
                    return l;
                    }
                }
            }
        }
        
            return false;
    }

    public DynamicSerialObject getItemById(Integer target_id){
          Pair<SessionState,DynamicSerialObject> ss = sessionItemMap.get(target_id);
                 if(ss==null) return null;
             return ss.getSecond();
    }
 
     public DynamicSerialContainer_I getContainerById(Integer cont_id){
          Pair<SessionState,DynamicSerialContainer_I> ss = sessionContainerMap.get(cont_id);
                 if(ss==null) return null;
             return ss.getSecond();
    }
    
    
    public boolean unLockObjectById(Integer target_id, Integer user_id) {
              SessionState s=getSessionStateById(target_id);
        if(s!=null)
       if (user_id.equals(s.getLockerUser_id()==null?"":s.getLockerUser_id())) {
            s.setLockerUser_id(-1);
            s.setLocks(new LOCK_TYPE[0]);
           if(s.getExtra()!=null) s.getExtra().clear();
            return true;
        }
        return false;
    }

    private void setItemById(Integer id,DynamicSerialObject obj){
         Pair<SessionState,DynamicSerialObject> ss = sessionItemMap.get(id);
         if(ss!=null){
            sessionItemMap.replace(id, Pair.of(ss.getFirst(),obj));
         }
    }
    private Integer getItemParentId(Integer target_id){
        for(Map.Entry<Integer, Pair<SessionState, DynamicSerialContainer_I>> e:this.sessionContainerMap.entrySet()){
            for(Object item:e.getValue().getSecond().container()){
                if(item instanceof DynamicSerialObject){
                   DynamicSerialObject casted=(DynamicSerialObject)item;
                   if(casted.getId().equals(target_id)){
                       return e.getKey();
                   }
                }
            }
        }
        return null;
    }
    
   
    
    //returning the list of deleted object's ids
    public List<Integer> deleteDraftsByUser(Integer user_id) {
        
        List<Integer> ret=new ArrayList<>();
          List<Map.Entry<Integer,Pair<SessionState,DynamicSerialObject>>> list=this.getItemsByUser(user_id);
           for(Map.Entry<Integer,Pair<SessionState,DynamicSerialObject>> e:list){
         if(e.getValue().getFirst().isDraft()){
              if( this.deleteItemFromContainerById(user_id,e.getKey(),getItemParentId(e.getKey())));
              ret.add(e.getKey());
                
            }
        }
           //if all the elements were removed, return true else false
           return ret;
         
    }
    public List<Integer> unlockObjectsByUserId(Integer user_id){
    List<Integer> ret=new ArrayList<>();
        List<Map.Entry<Integer, Pair<SessionState, DynamicSerialObject>>> items= this.getItemsByUser(user_id);
        for( Map.Entry<Integer, Pair<SessionState, DynamicSerialObject>> item:items){
         if(this.unLockObjectById(item.getKey(), user_id))
             ret.add(item.getKey());
        }
        return ret;
    }
    private List<Map.Entry<Integer,Pair<SessionState,DynamicSerialObject>>> getItemsByUser(Integer user_id){
        List<Map.Entry<Integer,Pair<SessionState,DynamicSerialObject>>> list=new ArrayList<>();
        for(Map.Entry<Integer,Pair<SessionState,DynamicSerialObject>> e:this.getSessionItemMap().entrySet()){
          if(e.getValue().getFirst().getLockerUser_id()!=null&&e.getValue().getFirst().getLockerUser_id().equals(user_id)){
              list.add(e);
          }
        }
        return list;
    }

    public Object updateObjectAndHoldLock(DynamicSerialObject obj, Integer user_id) {
          SessionState s=this.getSessionStateById(obj.getId());
          if(s!=null){
                s.setDraft(false);
               if(this.isItemLockedByMe(obj.getId(), user_id)){
                   System.out.println("object updated");
                   this.getItemById(obj.getId()).update(obj); 
             
                   return this.sessionItemMap.get(obj.getId());
                }
          }
         return null;      
    }


     
     /**
      * Loads the diagram's components to the session Maps
    */
    public void init() {
                objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
               // this.dg=(objectMapper.readValue(targetFile, DiagramEntity.class));
                for (DiagramObject d : this.dg.getDgObjects()) { 
                    //  sessionItemMap.put(d.getId(),Pair.of(new SessionState(),d));
                  //  d.setDiagram(getDg());
                    d.getDimensionModel().setDgObject(d);
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
                              //g.setParentClass(c);
                             for(AttributeElement e:g.getAttributes()){
                                // e.setGroup(g);
                                sessionItemMap.put(e.getId(), Pair.of(new SessionState(),e));

                             }
                         }
                    }
                }
    }
     /*_MAIN SESSION LOGIC*/
     

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

  

    public void setSessionItemMap(Map<Integer, Pair<SessionState, DynamicSerialObject>> sessionItemMap) {
        this.sessionItemMap = sessionItemMap;
    }

    public Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> getSessionContainerMap() {
        return sessionContainerMap;
    }

    public void setSessionContainerMap(Map<Integer, Pair<SessionState, DynamicSerialContainer_I>> sessionContainerMap) {
        this.sessionContainerMap = sessionContainerMap;
    }
   
   public UserWebSocket getUserByJwt(String session_jwt){
       return this.userSockets.stream().filter(s->s.getSession_jwt().equals(session_jwt)).findFirst().get();
   }

    public List<UserWebSocket> getUserSockets() {
        return userSockets;
    }

    public void setUserSockets(List<UserWebSocket> userSockets) {
        this.userSockets = userSockets;
    }

    private EditorSession() {
        this.id=UUID.randomUUID().getLeastSignificantBits();
    }

    public DiagramEntity getDg() {
        return dg;
    }

    public void setDg(DiagramEntity dg) {
        this.dg = dg;
        this.init();
    }
}
