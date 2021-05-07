/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.D;
import com.szefi.uml_conference.DLEVEL;
import com.szefi.uml_conference.editor.model.diagram.Diagram;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.DiagramObject;
import com.szefi.uml_conference.editor.model.do_related.Element_c;
import com.szefi.uml_conference.editor.model.do_related.NoteBox;
import com.szefi.uml_conference.editor.model.do_related.PackageElement;
import com.szefi.uml_conference.editor.model.do_related.PackageObject;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.do_related.TitleElement;
import com.szefi.uml_conference.editor.model.do_related.line.BreakPoint;
import com.szefi.uml_conference.editor.model.do_related.line.Line;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.ServerSideEditorAction;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocket;
import com.szefi.uml_conference.editor.model.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.repository.AttributeElementRepository;

import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.editor.socket.threads.service.SOCKET;
import com.szefi.uml_conference.editor.service.SocketSessionService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javassist.NotFoundException;

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
     AttributeElementRepository attrElementRepo;
      @Autowired
 DynamicSerialObjectRepository objectRepo;
     
    public EditorSession(DiagramRepository diagramRepo, DynamicSerialObjectRepository objectRepo,   BlockingQueue<EditorAction>  nestedActionQueue
    ,AttributeElementRepository attrElementRepo
    
    ) {
           this.id=UUID.randomUUID().getLeastSignificantBits();
        this.objectRepo=objectRepo;
        this.diagramRepo=diagramRepo;
        this.attrElementRepo=attrElementRepo;
        this.nestedActionQueue=nestedActionQueue;
      
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
         UserWebSocket toDelete=null;
       for(UserWebSocket sock:this.userSockets){
           if(sock.getSession_jwt().equals(session_jwt)){
            
              toDelete=sock;
               break;
           }
       }
       if(toDelete!=null)
        this.userSockets.remove(toDelete);
       return true;
     }
     
     /*MAIN SESSION DECLARATION*/
    /*MAIN SESSION LOGIC*/
    /**
    * Deletes the locks that have been locked by the user, 
    @return the unlocked items id's in a list
     */
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
     for(UserWebSocket w:this.getUserById(user_id)){
          this.userSockets.remove(w);
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
    public boolean isItemLockedByMe(Integer id,Integer user_id) throws NotFoundException{
         SessionState s =this.getSessionStateById(id);
          if (s == null) 
           throw new NotFoundException("item with id"+id+"does not exist in the session");
          return s.getLockerUser_id().equals(user_id);
    }
    
private void  injectAndLockAfterCreate(Integer user_id,DynamicSerialObject obj){
      obj.injectSelfToStateMap(this.sessionItemMap,this.sessionContainerMap);
                 getSessionStateById(obj.getId()).setExtra(new HashMap<>());
              getSessionStateById(obj.getId()).getExtra().put("placeholder", "c:"+user_id);
                   SessionState state=getSessionStateById(obj.getId());
            state.setDraft(true);
            this.lockObjectById(obj.getId(), user_id, this.generateCommonLock());
    }


    //returning new item's id
  @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DynamicSerialObject createItemForContainer(Integer user_id,Integer cont_id,DynamicSerialObject obj) throws NotFoundException{
        Pair<SessionState,DynamicSerialContainer_I> s= this.sessionContainerMap.get(cont_id);
         
        if(s!=null){
            DynamicSerialContainer_I cont=s.getSecond();
           try{
            if(obj instanceof AttributeElement){              
                AttributeElement casted=(AttributeElement)obj; 
                casted.setEdit(false);
                   casted.setGroup((SimpleClassElementGroup)cont); 
                   casted=objectRepo.save(casted);//id injected  
                   cont.container().add(casted);
               obj=casted;
            }}
           catch(org.springframework.orm.jpa.JpaObjectRetrievalFailureException ex)
           {
               System.err.println(ex);
           }
          
              injectAndLockAfterCreate(user_id, obj);
             //   this.dg= this.diagramRepo.save(this.dg); 
            return obj;
            //the object has no parent, therefore the container is the diagram itself
        }else if(cont_id.equals(SocketSessionService.ROOT_ID)){
            //most akkor a Diagramra bízzam a beinjektálást vagy csinááljam meg itt?
           
            if(obj instanceof AutoSessionInjectable_I){
               // AutoSessionInjectable_I casted=(AutoSessionInjectable_I)obj;
              //  casted.injectIdWithPrefix(rand_id);
                  if(obj instanceof DiagramObject){    
                DiagramObject dgo=(DiagramObject)obj;
             
                    if(dgo instanceof SimpleClass){
                        SimpleClass cls=(SimpleClass)dgo;
                         cls.prepareConnectionsForSave(dg);
                         cls=this.objectRepo.saveAndFlush(cls);
                         
                     dgo=cls;
                        this.lockObjectById(cls.getTitleModel().getId(), user_id, this.generateCommonLock());
                    try {
                        doSomethingWithClassHeaderToParentPackageObject(cls,ACTION_TYPE.S_INJECT_CLASS_HEADER_TO_PACKAGE);
                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(EditorSession.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }  else{  
                       dgo.setId(null);
                     dgo.setDiagram(dg);
                     this.dg.getDgObjects().add(dgo);
                     dgo=  this.objectRepo.save(dgo);
                    }
                     this.injectAndLockAfterCreate(user_id, obj);        
                obj=dgo;
              }    
                    
            System.out.println("obj created and locked for"+user_id);
 
                return obj;
            }
            return null;
        }else if(cont_id.equals(SocketSessionService.L_ROOT_ID)){
            if(obj instanceof AutoSessionInjectable_I){
            System.out.println("obj created and locked for"+user_id);         
              if(obj instanceof Line){ 
                  Line l=(Line)obj;  
                  l.setId(null);
                  l.getLineType().setLine(l); 
                  (l).setDiagram(dg);
                     l= this.objectRepo.save(l);
             
               l.injectSelfToStateMap(this.sessionItemMap,this.sessionContainerMap);   
               getSessionStateById(l.getId()).setExtra(new HashMap<>());
              getSessionStateById(l.getId()).getExtra().put("placeholder", "c:"+user_id);
                 this.dg.getLines().add(l);
              
                 //  this.dg= this.diagramRepo.save(this.dg);
              return l;
              }
            }      
        }
     //  this.dg= this.diagramRepo.save(this.dg);
     throw new NotFoundException("container with id "+cont_id+" not found!");
     // return null;
    }

    private LOCK_TYPE[] generateCommonLock(){
         return new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE};
    }
    /**
     Transaction need to be flushed before using this to work properly
     * 
     */
    private void doSomethingWithClassHeaderToParentPackageObject(DiagramObject obj,ACTION_TYPE type) throws JsonProcessingException{
          ServerSideEditorAction action=new ServerSideEditorAction(type);
                                action.getTarget().setParent_id(SocketSessionService.ROOT_ID);
                                action.getTarget().setTarget_id(obj.getId());
                                //action.setJson(this.objectMapper.writeValueAsString(clas.getTitleModel()));
                               //action.getTarget().setType();
                               action.setLoad(obj);
                                action.setUser_id(-1);
                                
                                nestedActionQueue.add(action);// TODO: PASS THE USERS TO SEND SOMEHOW                         
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Pair<SessionState,DynamicSerialObject> updateObjectAndUnlock(DynamicSerialObject obj,Integer user_id) throws NullPointerException,NotFoundException {
       
          SessionState s=this.getSessionStateById(obj.getId());
          if(s!=null){
                s.setDraft(false);
               if(this.unLockObjectById(obj.getId(), user_id)){
                  
                
                   
                  /* if(obj instanceof SimpleClass)
                  this.objectRepo.save((SimpleClass)this.getItemById(obj.getId()));
                    else if(obj instanceof NoteBox)
                  this.objectRepo.save((NoteBox)this.getItemById(obj.getId()));*/
                  if(obj instanceof TitleElement){
                            this.getItemById(obj.getId()).update(obj); 
                            //TitleElement real=(TitleElement)this.getItemById(obj.getId()); 
                             TitleElement real=(TitleElement)this.objectRepo.saveAndFlush(this.getItemById(obj.getId()));
                            if(real.getParent() instanceof SimpleClass){
                            SimpleClass parClass=(SimpleClass)real.getParent();
                                try {
                                    doSomethingWithClassHeaderToParentPackageObject(parClass,ACTION_TYPE.S_UPDATE_CLASS_HEADER_TO_PARENT_PACKAGE);
                                } catch (JsonProcessingException ex) {
                                    Logger.getLogger(EditorSession.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                           
                     }
                     if(obj instanceof AttributeElement){
                            this.getItemById(obj.getId()).update(obj); 
                         ((AttributeElement) obj).setGroup(((AttributeElement)this.getItemById(obj.getId())).getGroup());
                           this.objectRepo.save(this.getItemById(obj.getId()));
                     }
                     else if(obj instanceof Line){
                        this.getItemById(obj.getId()).update(obj);
                        
                          
                                  objectRepo.save(this.getItemById(obj.getId()));
                   //     this.dg= this.diagramRepo.save(this.dg);  
                           
                 
                       }
                     else{
                            this.getItemById(obj.getId()).update(obj); 
                         this.objectRepo.save(this.getItemById(obj.getId()));
                     }
                     
                   return this.sessionItemMap.get(obj.getId());
                }
          }
             // 
         return null;      
    }

  
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteItemFromContainerById(Integer user_id,Integer target_id, Integer parent_id) throws NotFoundException {
        try{
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
          
                  //clear the ones with empty name
              
                   return l;
               }
               }
            }else{
                if(parent_id.equals(SocketSessionService.ROOT_ID)){//global object
                  
                        DynamicSerialObject item=this.getItemById(target_id);
                        
                    if(item instanceof SimpleClass){
                       SimpleClass casted=(SimpleClass)item;
                        
      if(casted.getDiagram().getRelatedFolder().getParentProjectFolder()!=null){
                                DiagramEntity diag=this.diagramRepo.findById(casted.getDiagram().getRelatedFolder().getParentProjectFolder().getDiagram().getId()).get();

       Optional<PackageObject> savedPackageOpt=diag.getDgObjects().stream().map(o->{
        if(o instanceof PackageObject){
        PackageObject pack=(PackageObject)o;
        if(pack.getTitleModel().getName().equals( casted.getDiagram().getRelatedFolder().getName()))
        {
              Optional<PackageElement> elemopt= pack.getElements().stream().filter(e->e.getReferencedObjectId().equals(casted.getId())).findFirst();
              System.out.println("pause");
              if(elemopt.isPresent()){
              PackageElement elem=elemopt.get();
              pack.getElements().remove(elem);
              }        
                        return objectRepo.saveAndFlush(pack);
        }
            }
        return null;
        }).findFirst();
      
                            try {
                                doSomethingWithClassHeaderToParentPackageObject(savedPackageOpt.get(),ACTION_TYPE.S_DELETE_CLASS_HEADER_FROM_PARENT_PACKAGE);
                            } catch (JsonProcessingException ex) {
                                Logger.getLogger(EditorSession.class.getName()).log(Level.SEVERE, null, ex);
                            }
      }
           System.out.println("this is a SimpleClass delete");
                    
                        for(SimpleClassElementGroup g: casted.getGroups()){
                         g.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
                               for(AttributeElement e:g.getAttributes()){
                                 e.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
                                   this.objectRepo.delete(e);
                                  
                               }
                                this.objectRepo.delete(g);
                                
                        }
                        this.sessionItemMap.remove(casted.getTitleModel().getId());  
                         this.objectRepo.delete(casted.getTitleModel());
                          casted.getGroups().clear();
                          //the normal object
                    }
                     
                      //  this.dg=diagramRepo.save(this.dg);
                        //find any related lines. Delete them first.       
                        this.dg.getDgObjects().remove((DiagramObject)item);

                        List<Line> filteredList=new ArrayList<>();
                        for(Line l:this.dg.getLines()){
                            if((l.getObject_start_id()==null?"":l.getObject_start_id()).equals(item.getId())
                                    ||(l.getObject_end_id()==null?"":l.getObject_end_id()).equals(item.getId())){
                                //send a delete event to the actionprocessor
                                filteredList.add(l);
                               
                            }
                        }
                        for(Line l:filteredList){
                               this.lockObjectById(l.getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
                                EditorAction action=new EditorAction(ACTION_TYPE.DELETE);
                                action.getTarget().setParent_id(SocketSessionService.L_ROOT_ID);
                                action.setSession_jwt(this.getUserSockets().stream().filter(u->u.getUser_id().equals(user_id)).findFirst().get().getSession_jwt());
                                action.getTarget().setTarget_id(l.getId());
                                action.getTarget().setType("Line");
                                action.setUser_id(user_id);
                                nestedActionQueue.add(action);// TODO: PASS THE USERS TO SEND SOMEHOW
                        }
                        filteredList.clear();
                        
                       //  if(!(item instanceof SimpleClass)){
                          this.dg.getDgObjects().remove((DiagramObject)item);
                     this.objectRepo.delete(item);
                     
                       //  }
                       
                    
                    
                 
                         item.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
                  
              try{
                  
                        dg=  this.diagramRepo.save(this.dg);}
              catch(Exception e){
              D.log(e.getMessage(), e.getClass(), DLEVEL.ERR);
              }
                  //   this.dg= this.diagramRepo.save(this.dg);
                   return  true;
                }else  if(parent_id.equals(SocketSessionService.L_ROOT_ID)){//global line
                  
                        DynamicSerialObject item=this.getItemById(target_id);
                    if(item instanceof Line){
                        System.out.println("this is a Line delete");
                        Line l1=(Line)item;
                        
                        
                        
                        
                        
                      l1.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);           
                      boolean l= this.dg.getLines().remove(l1);//the equals is overrided with id comparison;
                      
                          //((AttributeElement)obj).setGroup(null);
                     // objectRepo.deleteById(target_id);
                   this.objectRepo.delete(l1);
                   sessionItemMap.remove(target_id);
                
                  this.dg=  this.diagramRepo.save(this.dg);
                  //clear the ones with empty name
              
             
                      
                      
                      // this.dg=this.diagramRepo.save(dg);
                    //     this.dg=this.diagramRepo.save(this.dg);
                    return l;
                    }
                }
            }
        }
        }
        catch(NotFoundException ex){
            //probably the element didn't got its server side id before it got deleted.
            System.err.println(ex.getMessage());
            
               List<AttributeElement> todel= attrElementRepo.findAllByName("");
               //todel. 
               for(AttributeElement l:todel){
                            //  this.lockObjectById(l.getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
                                EditorAction action=new EditorAction(ACTION_TYPE.DELETE);
                                action.setSession_jwt(this.getUserSockets().stream().filter(u->u.getUser_id().equals(user_id)).findFirst().get().getSession_jwt());
                                action.getTarget().setParent_id(l.getGroup().getId());
                                action.getTarget().setTarget_id(l.getId());
                                action.getTarget().setType("AttributeElement");
                                action.setUser_id(user_id);
                                nestedActionQueue.add(action);// TODO: PASS THE USERS TO SEND SOMEHOW
                        }
            todel.stream().forEach((Consumer<? super AttributeElement>) e->{
                    
                    this.attrElementRepo.deleteById(e.getId());
                      e.getGroup().container().remove(e);
                      this.sessionItemMap.remove(e.getId());
                      this.objectRepo.save(e.getGroup());
                 });
                              
                        throw ex;
        }
            return false;
    }
    
    public void deleteDgObjectNative(DiagramObject ob){
        ob.deleteSelfFromStateMap(sessionItemMap, sessionContainerMap);
         this.dg.getDgObjects().remove(ob);
                      
           //          this.objectRepo.delete(ob);
                   dg=  this.diagramRepo.save(this.dg);
    }

    public DynamicSerialObject getItemById(Integer target_id) throws NotFoundException{
          Pair<SessionState,DynamicSerialObject> ss = sessionItemMap.get(target_id);
                 if(ss==null) throw new NotFoundException("item not found with id="+target_id);
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
    public List<Integer> deleteDraftsByUser(Integer user_id) throws NotFoundException {
        
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

    public Object updateObjectAndHoldLock(DynamicSerialObject obj, Integer user_id) throws NotFoundException {
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
      * Loads the diagram's components to the session Maps and sends them in the current session
     * @param obj
    */
    public void injectToStateMapAndSendToAll(DiagramObject obj) throws JsonProcessingException{
        obj.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
        
               EditorAction action=new EditorAction(ACTION_TYPE.CREATE);
                                action.setSession_jwt(this.getUserSockets().stream().filter(u->u.getUser_id().equals(obj.getDiagram().getOwner().getId())).findFirst().get().getSession_jwt());
                                action.getTarget().setParent_id(SocketSessionService.ROOT_ID);
                                action.getTarget().setTarget_id(obj.getId());
                                action.setJson(this.objectMapper.writeValueAsString(obj));
                                action.getTarget().setType("PackageObject");
                                action.setUser_id(-1);
                                action.getExtra().put("create_method", "nested");
                                nestedActionQueue.add(action);// TODO: PASS THE USERS TO SEND SOMEHOW
        
    }
    public void init() {
                objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                for (DiagramObject d : this.dg.getDgObjects()) { 
                                 d.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
                }
    }
     /*_MAIN SESSION LOGIC*/
     
    public void updateObjectAndSend_Internal(DiagramObject o) throws NotFoundException, JsonProcessingException{
        this.getItemById(o.getId()).update(o);
        //o.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
               EditorAction action=new EditorAction(ACTION_TYPE.UPDATE);
                               Optional<UserWebSocket> opt=this.getUserSockets().stream().findFirst();
                                    if(opt.isPresent()){
                                  action.setSession_jwt(opt.get().getSession_jwt());
                                action.getTarget().setParent_id(SocketSessionService.ROOT_ID);
                                action.getTarget().setTarget_id(o.getId());
                                action.setJson(this.objectMapper.writeValueAsString(o));
                               //action.getTarget().setType();
                                action.setUser_id(-1);
                                
                                nestedActionQueue.add(action);// TODO: PASS THE USERS TO SEND SOMEHOW
                                    }    
                                       
        
    }
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

       Optional<UserWebSocket> opt= this.userSockets.stream().filter(s->s.getSession_jwt().equals(session_jwt)).findFirst();
               if(opt.isPresent()) return opt.get();
               return null;
   }
    public List<UserWebSocket> getUserById (Integer id){
       return this.userSockets.stream().filter(s->s.getUser_id().equals(id)).collect(Collectors.toList());
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
