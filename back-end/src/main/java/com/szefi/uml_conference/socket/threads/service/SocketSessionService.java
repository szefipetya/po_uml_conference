/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.threads.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
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
import com.szefi.uml_conference.editor.model.socket.session.EditorSession;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocket;
import com.szefi.uml_conference.editor.model.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.socket.security.model.SocketAuthenticationRequest;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

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
            DynamicSerialObjectRepository objectRepo;
    
    @Autowired 
            DiagramRepository diagramRepo;
     public static final Integer L_ROOT_ID=-2;
    public static final Integer ROOT_ID=-1;
    
  public DiagramEntity  getDiagramById(Integer id){
      return  diagramRepo.findById(id).get();
    }
    
    List<EditorSession> sessions=new LinkedList<>();
    
    public SocketSessionService() {
      
        //   actionSockets=new LinkedList<UserWebSocket>();
        //  stateSockets=new LinkedList<UserWebSocket>();
        //socketThreadManager=new SocketThreadManager(actionSockets, stateSockets);
        //  socketThreadManager.start(); 
    }
/**
 * @return The list of ids, that were locked by the user
 */
   /* public List<Integer> deleteLocksRelatedToUser(Integer user_id){
        List<Integer> ret=new ArrayList<>();
     for( Entry<Integer,Pair<SessionState,DynamicSerialObject>> e : sessionItemMap.entrySet()){
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
    
*/
    ObjectMapper objectMapper;
    DiagramEntity dg;
  /*  Map<Integer, Pair<SessionState,DynamicSerialObject>> sessionItemMap=new HashMap<>();
    Map<Integer, Pair<SessionState,DynamicSerialContainer_I>> sessionContainerMap=new HashMap<>();*/

  /*  public  Map<Integer, Pair<SessionState,DynamicSerialObject>> getSessionItemMap() {
        return sessionItemMap;
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
  
    public DynamicSerialObject createItemForContainer(Integer user_id,Integer cont_id,DynamicSerialObject obj){
        Pair<SessionState,DynamicSerialContainer_I> s= this.sessionContainerMap.get(cont_id);
         
        if(s!=null){
            DynamicSerialContainer_I cont=s.getSecond();
         
           // obj.setId(rand_id);
            cont.container().add(obj);
            objectRepo.save(obj);
            if(obj instanceof Element_c){              
                Element_c casted=(Element_c)obj;
                casted.setEdit(false);
            }
         
            this.sessionItemMap.put(obj.getId(),Pair.of(new SessionState(),obj)); 
            this.lockObjectById(obj.getId(), user_id, new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE});
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
    }*/
    @Autowired
    UserRepository userRepo;//temp
 @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
          //  EditorSession session=new EditorSession();
          //  sessions.add(session);
         
            System.out.println("service started");
           // this.dg=new DiagramEntity();
         
            Resource res = new ClassPathResource("d.json");
            byte[] buffer = new byte[res.getInputStream().available()];
            res.getInputStream().read(buffer);
            File targetFile = new File("src/main/resources/tmp_d.tmp");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            try {
                objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                       this.dg=objectMapper.readValue(targetFile, DiagramEntity.class);

         
                for (DiagramObject d :this.dg.getDgObjects()) { 
                    //  sessionItemMap.put(d.getId(),Pair.of(new SessionState(),d));
                    d.setDiagram(this.dg);
                    d.getDimensionModel().setDgObject(d);
                    if(d instanceof NoteBox){
                        NoteBox n=(NoteBox)d;
                       // n.injectSelfToStateMap(sessionItemMap, sessionContainerMap);
                    }
                    if (d instanceof SimpleClass) {
                        SimpleClass c = (SimpleClass) d;     
                        c.getTitleModel().setParent(c);
                         for(SimpleClassElementGroup g:c.getGroups()){
                              g.setParentClass(c);
                             for(AttributeElement e:g.getAttributes()){
                                 e.setGroup(g);
                             }
                         }
                    }
                }
               
            
                   // this.diagramRepo.save(session.getDg());   
            UserEntity user=userRepo.findByUserName("user").get();
          
          //  user.getDiagrams().add(this.dg);
           this.dg.setOwner(user);  
            this.diagramRepo.save(this.dg); 
            UserEntity user2=userRepo.findByUserName("user2").get();
            user2.getSharedDiagramsWithMe().add(this.dg);
            userRepo.save(user2);
             // session.setDg(this.dg);
            } catch (IOException ex) {

                Logger.getLogger(SocketSessionService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketSessionService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
/*
    public DiagramEntity getDummyDiagram() {
        return this.sessions.get(0).getDg();
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

    public Pair<SessionState,DynamicSerialObject> updateObjectAndUnlock(DynamicSerialObject obj,Integer user_id) throws NullPointerException {
       
       
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
   
  

    public boolean deleteItemFromContainerById(Integer user_id,Integer target_id, Integer parent_id) {
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
                                action.getTarget().setParent_id(SocketSessionService.L_ROOT_ID);
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

    private Integer getItemParentId(Integer target_id){
        for(Entry<Integer, Pair<SessionState, DynamicSerialContainer_I>> e:this.sessionContainerMap.entrySet()){
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
        List<Entry<Integer, Pair<SessionState, DynamicSerialObject>>> items= this.getItemsByUser(user_id);
        for( Entry<Integer, Pair<SessionState, DynamicSerialObject>> item:items){
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
*//*
    public Object updateObjectAndHoldLock(DynamicSerialObject obj, Integer user_id) {
          SessionState s=this.getSessionStateById(obj.getId());
          if(s!=null){
                s.setDraft(false);
               if(this.isItemLockedByMe(obj.getId(), user_id)){
                   System.out.println("object updated");
                   this.getItemById(obj.getId()).update(obj); 
                 if(obj.getExtra().containsKey("draft")){
                       obj.getExtra().replace("draft", "false");
                   }
                   return this.sessionItemMap.get(obj.getId());
                }
          }
         return null;      
    }*/ 
@Autowired JwtUtilService jwtService;


    public void onSocketDisconnect(UserWebSocket userSocket){
        
    }
   public EditorSession getSessionById(Long id){
        return this.sessions.stream().filter(r->r.getId().equals(id)).findFirst().get();

    }
   public EditorSession tokenToSession(String token) throws JwtParseException{
         return  this.getSessionById(Long.valueOf((String)this.jwtService.extractAllClaims(token).get("session_id")));

    }
   @Bean
   @Scope("prototype")    
   EditorSession generateSession(){
       return new EditorSession(this.diagramRepo,this.objectRepo,this.nestedActionQueue);
   }

 public List<UserWebSocket> getUserSocketsByToken(String token) throws JwtParseException{
       return this.tokenToSession(token).getUserSockets();
   }
   
    public EditorSession autoProcessRequest( MyUserDetails details,UserWebSocket userSocket,SocketAuthenticationRequest req) {
        //step 1 ://find a diagram that matches the req.getDiagram_id()
                  //if not found, create a new session and load the diagram from the database.
       //step 2: Inject the user to the session with  a session Token as an identificator. This enables one users to connect to multiple sessions.
     // Optional<DiagramEntity> opt= sessionsToDiagrams().stream().filter(d->d.getId().equals(req.getDiagram_id())).findFirst();
     //identify user with session id
     Optional<EditorSession> opt= findSessionByDiagramId(req.getDiagram_id());
        EditorSession s=null;
      if(opt.isPresent()){//if Session is present with diagram id
           s=opt.get();
           s.getUserSockets().add(userSocket);
          Map<String,Object> claims=new HashMap<>();
          claims.put("session_id",s.getId().toString());
          userSocket.setSession_jwt(jwtService.generateToken(claims, details));//generated a token, containing the session's id.
                    userSocket.setUser_id(details.getId());

            s.getUserSockets().add(userSocket);
      }else{
          //create new session and load diagram with given id.
           s=this.generateSession();//new EditorSession(this.diagramRepo,this.objectRepo);
           
          s.setDg(diagramRepo.findById(req.getDiagram_id()).get());  
          
          Map<String,Object> claims=new HashMap<>();
          claims.put("session_id",s.getId().toString());     
          userSocket.setSession_jwt(jwtService.generateToken(claims, details));//generated a token, containing the session's id.
          userSocket.setUser_id(details.getId());
          s.getUserSockets().add(userSocket);
      
          sessions.add(s);
      }
      return s;
    }
    public Pair<Long,List<Integer>> findSessionForNativeSocketAndDeleteFromSession(SOCKET type,WebSocketSession socket){
        for(EditorSession e:sessions){
           UserWebSocket u=e.getUserSocketByNativeSocket(type,socket);
           if(u!=null)
           {
               return Pair.of(e.getId(), e.deleteLocksRelatedToUser(u.getUser_id()));
            }
        }
        return null;
    }
    private List<DiagramEntity> sessionsToDiagrams(){
        return this.sessions.stream().map(s->s.getDg()).collect(Collectors.toList());
    }
    public EditorSession updateuserWithStateAndGetSession(String session_jwt,WebSocketSession socket) throws JwtParseException{
        Long session_id=  Long.valueOf((String)this.jwtService.extractAllClaims(session_jwt).get("session_id"));
        return this.sessions.stream().filter(s->{
            if(s.getId().equals(session_id)){
                s.getUserByJwt(session_jwt).setStateSocket(socket);
            }
            return s.getId().equals(session_id);
       }).findFirst().get();
    }
    
    private  Optional<EditorSession>  findSessionWithuser_session_jwt_inside(String session_jwt){
      /*for(EditorSession s:sessions){
          if(s.userDisconnect(session_jwt))
      }
        
        ((String)this.jwtService.extractAllClaims(session_jwt).get("session_id"))*/
      return null;
    }
    
    private Optional<EditorSession> findSessionByDiagramId(Integer id){
        return this.sessions.stream().filter(s->s.getDg().getId().equals(id)).findFirst();
    }

}
