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
import com.szefi.uml_conference._exceptions.JwtParseException;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
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
import com.szefi.uml_conference.editor.model.do_related.line.Line;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocketWrapper;
import com.szefi.uml_conference.editor.model.top.AutoSessionInjectable_I;
import com.szefi.uml_conference.editor.model.top.DynamicSerialContainer_I;
import com.szefi.uml_conference.editor.repository.AttributeElementRepository;

import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.security.model.MyUserDetails;
import com.szefi.uml_conference.security.model.UserEntity;
import com.szefi.uml_conference.security.repository.UserRepository;
import com.szefi.uml_conference.security.service.JwtUtilService;
import com.szefi.uml_conference.editor.service.socket.security.model.SocketAuthenticationRequest;
import com.szefi.uml_conference.editor.service.socket.threads.SocketThreadManager;
import com.szefi.uml_conference.editor.service.socket.threads.service.SOCKET;
import com.szefi.uml_conference.management.model.ICON;
import com.szefi.uml_conference.security.service.MyUserDetailsService;

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
import javassist.NotFoundException;

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
    
  public DiagramEntity  getDiagramById(Integer id) throws java.util.NoSuchElementException{
      return  diagramRepo.findById(id).get();
    }
  public EditorSession getSessionByDiagramId(Integer d_id){
     Optional<EditorSession> opt= sessions.stream().filter(r->r.getDg().getId().equals(d_id)).findFirst();
     if(opt.isPresent()) return opt.get();
     else return null;
             
  }
    
    List<EditorSession> sessions=new LinkedList<>();
    
    public SocketSessionService() {
   
    }

    ObjectMapper objectMapper;
    DiagramEntity dg;
 
    @Autowired
    UserRepository userRepo;//temp
    
    @Bean
    @Qualifier("colorList")
    public List<String> getColors(){
        return colors;
    }
    List<String> colors;
 @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
          //  EditorSession session=new EditorSession();
          //  sessions.add(session);
         
            System.out.println("service started");
           // this.dg=new DiagramEntity();
               objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Resource res = new ClassPathResource("json/colors.json");
            byte[] buffer = new byte[res.getInputStream().available()];
            res.getInputStream().read(buffer);
            File targetFile = new File("src/main/resources/json/colors.tmp");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
              
           colors=   Arrays.asList(objectMapper.readValue(buffer, String[].class));
               for(String c:colors)
                   System.out.println(c+",");
           /* try {
                objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                       this.dg=objectMapper.readValue(targetFile, DiagramEntity.class);

         
                for (DiagramObject d :this.dg.getDgObjects()) { 
                    //  sessionItemMap.put(d.getId(),Pair.of(new SessionState(),d));
                    d.setDiagram(this.dg);
                 //   d.getDimensionModel().setDgObject(d);
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
               
            
               
            } catch (IOException ex) {

                Logger.getLogger(SocketSessionService.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (IOException ex) {
            Logger.getLogger(SocketSessionService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

@Autowired JwtUtilService jwtService;


    public void onSocketDisconnect(UserWebSocketWrapper userSocket){
        
    }
   public EditorSession getSessionById(Long id){
        return this.sessions.stream().filter(r->r.getId().equals(id)).findFirst().get();

    }
   public EditorSession tokenToSession(String token) throws JwtParseException{
         return  this.getSessionById(Long.valueOf((String)this.jwtService.extractAllClaims(token).get("session_id")));

    }
 
   @Autowired
   AttributeElementRepository attrElementRepo;
   
   @Autowired 
   MyUserDetailsService userService;
   
   @Bean
   @Scope("prototype")    
   EditorSession generateSession(){
       return new EditorSession(this.diagramRepo,this.objectRepo,this.nestedActionQueue,attrElementRepo,colors);
   }

 public List<UserWebSocketWrapper> getUserSocketsByToken(String token) throws JwtParseException{
       return this.tokenToSession(token).getUserSockets();
   }
   
    public UserEntity getUserEntityById(Integer id){
        return this.userService.loadUserEntityById(id);
    }
    public String autoProcessRequest( MyUserDetails details,UserWebSocketWrapper userSocket,SocketAuthenticationRequest req) {
        //step 1 ://find a diagram that matches the req.getDiagram_id()
                  //if not found, create a new session and load the diagram from the database.
       //step 2: Inject the user to the session with  a session Token as an identificator. This enables one users to connect to multiple sessions.
     // Optional<DiagramEntity> opt= sessionsToDiagrams().stream().filter(d->d.getId().equals(req.getDiagram_id())).findFirst();
     //identify user with session id
     Optional<EditorSession> opt= findSessionByDiagramId(req.getDiagram_id());
        EditorSession s=null;
      if(opt.isPresent()){//if Session is present with diagram id
           s=opt.get();
       
          Map<String,Object> claims=new HashMap<>();
          claims.put("session_id",s.getId().toString());
          userSocket.setSession_jwt(jwtService.generateToken(claims, details));//generated a token, containing the session's id.
                    userSocket.setUser_id(details.getId());

            s.addUserSocket(userSocket);
      }else{
          //create new session and load diagram with given id.
           s=this.generateSession();//new EditorSession(this.diagramRepo,this.objectRepo);
           
          s.setDg(diagramRepo.findById(req.getDiagram_id()).get());  
          
          Map<String,Object> claims=new HashMap<>();
          claims.put("session_id",s.getId().toString());     
          userSocket.setSession_jwt(jwtService.generateToken(claims, details));//generated a token, containing the session's id.
          userSocket.setUser_id(details.getId());
          s.addUserSocket(userSocket);
      
          sessions.add(s);
      }
      return userSocket.getSession_jwt();
    }
    
    public Pair<UserWebSocketWrapper,EditorSession> findSessionForNativeSocketAndReturnUserSocket(SOCKET type,WebSocketSession socket){
        for(EditorSession e:sessions){
           UserWebSocketWrapper u=e.getUserSocketByNativeSocket(type,socket);
           if(u!=null)
           {
            return Pair.of(u,e);
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
    public void deleteSessionIfEmpty(EditorSession s){
        if(s.getUserSockets().isEmpty()){
            if(this.sessions.remove(s))
            D.log("Session "+s.getId()+" have been deleted due to userCount=0", this.getClass(),DLEVEL.INFO);
        }
    }
    public void injectClassHeaderToParentsPackageObject(SimpleClass clas){
        //SimpleClass clas=(SimpleClass)objectRepo.findById(class_id).get();
      if(clas.getDiagram().getRelatedFolder().getParentProjectFolder()!=null){
       Optional<PackageObject> savedPackageOpt=clas.getDiagram().getRelatedFolder().getParentProjectFolder().getDiagram().getDgObjects().stream().map(obj->{
        if(obj instanceof PackageObject){
        PackageObject pack=(PackageObject)obj;
        pack=(PackageObject)objectRepo.findById(pack.getId()).get();
        if(pack.getTitleModel().getName().equals( clas.getDiagram().getRelatedFolder().getName()))
        {
              PackageElement classPackageModel=new PackageElement();
                          classPackageModel.setName(clas.getTitleModel().getName());
                          classPackageModel.setIcon(ICON.PROJECT_CLASS);
                         // classPackageModel.setParent(pack);
                          classPackageModel.setReferencedObjectId(clas.getId());
                          pack.getElements().add(classPackageModel);
                        return objectRepo.save(pack);
        }
            }
        return null;
        }).filter(o->o!=null).findFirst();
      if(savedPackageOpt.isPresent())
          sendObjectToSessionIfExists(savedPackageOpt.get(),  clas.getDiagram().getRelatedFolder().getParentProjectFolder().getDiagram().getId());
      }
    }
    
    
    
      public void updateClassHeaderToParentsPackageObject(Integer class_id){
        SimpleClass clas=(SimpleClass)objectRepo.findById(class_id).get();
      if(clas.getDiagram().getRelatedFolder().getParentProjectFolder()!=null){
       Optional<PackageObject> savedPackageOpt=clas.getDiagram().getRelatedFolder().getParentProjectFolder().getDiagram().getDgObjects().stream().map(obj->{
        if(obj instanceof PackageObject){
        PackageObject pack=(PackageObject)obj;
        if(pack.getTitleModel().getName().equals( clas.getDiagram().getRelatedFolder().getName()))
        {
              Optional<PackageElement> elemopt= pack.getElements().stream().filter(e->e.getReferencedObjectId().equals(class_id)).findFirst();
              if(elemopt.isPresent()){
              PackageElement elem=elemopt.get();
              elem.setName(clas.getTitleModel().getName());
              }        
                        return objectRepo.save(pack);
        }
            }
        return null;
        }).filter(l->l!=null).findFirst();
       if(savedPackageOpt.isPresent())
          sendObjectToSessionIfExists(savedPackageOpt.get(),  clas.getDiagram().getRelatedFolder().getParentProjectFolder().getDiagram().getId());
      }
    }
      
       public void deleteClassSoRemoveItFromParentPackageObject(PackageObject savedPackageOpt){
           
    //  PackageObject savedPackageOpt=(PackageObject)objectRepo.findById(pack_id).get();
      /*if(clas.getDiagram().getRelatedFolder().getParentProjectFolder()!=null){
       Optional<PackageObject> savedPackageOpt=clas.getDiagram().getRelatedFolder().getParentProjectFolder().getDiagram().getDgObjects().stream().map(obj->{
        if(obj instanceof PackageObject){
        PackageObject pack=(PackageObject)obj;
        if(pack.getTitleModel().getName().equals( clas.getDiagram().getRelatedFolder().getName()))
        {
              Optional<PackageElement> elemopt= pack.getElements().stream().filter(e->e.getReferencedObjectId().equals(class_id)).findFirst();
              if(elemopt.isPresent()){
              PackageElement elem=elemopt.get();
             // elem.setName(clas.getTitleModel().getName());
              pack.getElements().remove(elem);
              }        
                        return objectRepo.save(pack);
        }
            }
        return null;
        }).findFirst();*/
            try{
           savedPackageOpt= this.objectRepo.save(savedPackageOpt);
            }catch(Exception e){
                D.log(e.getMessage());
            }
           //diagramRepo.save(savedPackageOpt.getDiagram());
          sendObjectToSessionIfExists(savedPackageOpt,  savedPackageOpt.getDiagram().getId());
      
     //  this.getSessionByDiagramId(clas.getDiagram().getId()).deleteDgObjectNative(clas);
    
       //
       
    }
      
      
    private void sendObjectToSessionIfExists(DiagramObject pack,Integer diagram_id){
        Optional<EditorSession> sessionOpt=findSessionByDiagramId(diagram_id);
        if(sessionOpt.isPresent()){
                try {
                    EditorSession session=sessionOpt.get();
                    session.updateObjectAndSend_Internal(pack);
                } catch (NotFoundException | JsonProcessingException ex) {
                    Logger.getLogger(SocketSessionService.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
     }
    
   
    private Optional<EditorSession> findSessionByDiagramId(Integer id){
        return this.sessions.stream().filter(s->s.getDg().getId().equals(id)).findFirst();
    }

}
