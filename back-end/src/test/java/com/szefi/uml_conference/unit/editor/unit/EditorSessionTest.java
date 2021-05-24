/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.unit.editor.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.DiagramObject;
import com.szefi.uml_conference.editor.model.do_related.PackageElement;
import com.szefi.uml_conference.editor.model.do_related.PackageObject;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.do_related.TitleElement;
import com.szefi.uml_conference.editor.model.do_related.line.Line;
import com.szefi.uml_conference.editor.model.socket.ACTION_TYPE;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocketWrapper;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.repository.AttributeElementRepository;
import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.editor.service.EditorSession;
import com.szefi.uml_conference.management.model.entity.project.ProjectFolderEntity;
import static java.awt.PageAttributes.MediaType.A;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javassist.NotFoundException;
import javax.validation.constraints.AssertFalse;
import javax.ws.rs.core.Application;
import org.glassfish.hk2.classmodel.reflect.util.LinkedQueue;
import static org.graalvm.compiler.options.OptionType.User;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author h9pbcl
 */
//@ContextConfiguration(classes=Application.class)
//@RunWith(MockitoJUnitRunner.class)

//@DataJpaTest
public class EditorSessionTest {
     @Mock
    private static DiagramRepository diagramRepo;
      @Mock
     private static DynamicSerialObjectRepository objectRepo;
       @Mock
     private static AttributeElementRepository attrElementRepo;
     private static BlockingQueue<EditorAction> nestedQueue=new LinkedBlockingQueue<>();
     
 static  EditorSession session;
     
     UserWebSocketWrapper s2;
     UserWebSocketWrapper s;
   public static ObjectMapper mapper;
     @BeforeAll
       public static  void setUp() {
           mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }
       
       
 
       @BeforeEach
       public  void populateSession(){
         MockitoAnnotations.initMocks(this); // this is needed for init of mocks, if we use @Mock 
            List<String> cols=new ArrayList<>();
           cols.add("#fff");
           cols.add("#aaa");
           cols.add("#bbb");
           cols.add("#ccc");
           cols.add("#ddd");
         session=new EditorSession(diagramRepo, objectRepo,nestedQueue, attrElementRepo,cols);

             s=new UserWebSocketWrapper();
            s.setUser_id(1);
        s.setSession_jwt("testjwt");
           s2=new UserWebSocketWrapper();
        s2.setSession_jwt("testjwt2");
        s2.setUser_id(2);
           session.getUserSockets().add(s);
           session.getUserSockets().add(s2);
           
           //add some objects
            DynamicSerialObject anyObject=new DynamicSerialObject();
        anyObject.setId(1);
       
        //init 
        session.getSessionItemMap().put(1,  Pair.of(new SessionState(),anyObject));
       }
       
       @AfterEach
    public void cleanUp(){
        session.getUserSockets().clear();
        session.getSessionItemMap().clear();
    }

    @Test
    void userDisconnect(){ 
        //test
        
        session.userDisconnect("testjwt");
        Assertions.assertEquals(null,session.getUserByJwt("testjwt"));
        Assertions.assertEquals(s2,session.getUserByJwt("testjwt2"));
                session.userDisconnect("testjwt2");
        Assertions.assertEquals(0,session.getUserSockets().size());

        
    }
       private LOCK_TYPE[] generateCommonLock(){
        return  new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE};
    }
    
    @Test
    void deleteLocksRelatedToUser() throws NotFoundException{ 
       
        //test
        //if the object is free
        Assertions.assertEquals(true,session.lockObjectById(1, 1,generateCommonLock()));
        //make sure, that the object with id 1 is locked
                Assertions.assertEquals(1,session.getSessionStateById(1).getLockerUser_id().intValue());
                Assertions.assertEquals(2,session.getSessionStateById(1).getLocks().length);

        //if the object is locked, but the user is the same
        Assertions.assertEquals(true,session.lockObjectById(1, 1,generateCommonLock()));
           //make sure, that the object with id 1 is locked
                Assertions.assertEquals(1,session.getSessionStateById(1).getLockerUser_id().intValue());
                Assertions.assertEquals(2,session.getSessionStateById(1).getLocks().length);
                
           //if the object is locked, but the user is different
        Assertions.assertEquals(false,session.lockObjectById(1, 2,generateCommonLock()));
           //make sure, that the object with id 1 is locked
                Assertions.assertEquals(1,session.getSessionStateById(1).getLockerUser_id().intValue());
                Assertions.assertEquals(2,session.getSessionStateById(1).getLocks().length);
                
          //if the object is locked, unlock by another user
        Assertions.assertEquals(false,session.unLockObjectById(1, 2));
           //make sure, that the object with id 1 is still locked, untouched
                Assertions.assertEquals(1,session.getSessionStateById(1).getLockerUser_id().intValue());
                Assertions.assertEquals(2,session.getSessionStateById(1).getLocks().length);
        
           //if the object is locked, unlock by the locker user
        Assertions.assertEquals(true,session.unLockObjectById(1, 1));
           //make sure, that the object with id 1 is unlocked
                Assertions.assertEquals(-1,session.getSessionStateById(1).getLockerUser_id().intValue());
                Assertions.assertEquals(0,session.getSessionStateById(1).getLocks().length);

        
    }
    @Test
    void isItemLockedByMe() throws NotFoundException{
         Exception exception = assertThrows(NotFoundException.class, () -> {
      session.isItemLockedByMe(3, 1);
    });
    assertTrue(exception.getMessage().equals("item with id"+3+"does not exist in the session"));
    
    session.lockObjectById(1, 1, generateCommonLock());
    assertFalse(session.isItemLockedByMe(1, 2));
    assertTrue(session.isItemLockedByMe(1, 1));
    }
      @Test
   void createItemForContainer_universal(){
       
            //container does not exists
        Exception exception = assertThrows(NotFoundException.class, () -> {
        session.createItemForContainer(1,2,new DynamicSerialObject());
        });
        assertTrue(exception.getMessage().equals("container with id "+2+" not found!"));
        
   } 
    @Test
   void createItemForContainer_AttributeElementCreation_Test() throws NotFoundException,Exception{
      
      
        //INIT 
       
           SimpleClassElementGroup group=new SimpleClassElementGroup();
           group.setId(10);
          
                  doReturn(Optional.of(group)).when(objectRepo).findById(10);
                   

       //  group= objectRepo.save(group);
          //inject group to session
        group.injectSelfToStateMap(session.getSessionItemMap(), session.getSessionContainerMap());
      
          AttributeElement elem=new AttributeElement();
        elem.setAttr_type("int");
        elem.setName("valami");
        elem.setId(-20);//something temporary generated on client side
        elem.setVisibility("+");
        
        AttributeElement elemSavedMock=new AttributeElement();
        elemSavedMock.setId(3);
        elemSavedMock.setAttr_type("int");
        elemSavedMock.setName("valami");
        elemSavedMock.setVisibility("+");
        elemSavedMock.setGroup(group);
    
     when(objectRepo.save(any(DynamicSerialObject.class))).thenReturn(elemSavedMock);

        //doReturn(elemSavedMock).when(objectRepo).save();
   
        //TEST
           AttributeElement elemResult=(AttributeElement)session.createItemForContainer(1, group.getId(), elem);

         //check if elem is present in the session
       Assertions.assertEquals(group.getAttributes().get(0),elemResult );
       Assertions.assertEquals(group,elemResult.getGroup() );
       
        //check if the element is inserted into the databese,+ linked to the (container) group
              
    }
   @Test
   public void deleteObjectWhenLinesAreConnected() throws NotFoundException{
       DiagramEntity dg=helper_initDiagram();
       DiagramObject o1=new DiagramObject();
       o1.setId(1);
       DiagramObject o2=new DiagramObject();
       o2.setId(2);
         Line l=new Line();
           l.setId(3);
           l.setObject_start_id(1);
           l.setObject_end_id(2);
           l.setDiagram(session.getDg());
           session.getDg().getLines().add(l);
           session.getDg().getDgObjects().add(o1);
           session.getDg().getDgObjects().add(o2);
           session.init();       
           session.lockObjectById(o1.getId(), 2, this.generateCommonLock());
           
           Assertions.assertEquals(o1,session.getItemById(1));
           Assertions.assertEquals(o2,session.getItemById(2));
           Assertions.assertEquals(l,session.getItemById(3));
                          Assertions.assertEquals(3,session.getSessionItemMap().size());

           session.deleteItemFromContainerById(2, o1.getId(), -1);//delete o1
           
                     Assertions.assertEquals(2,nestedQueue.size());//a packageobjectet újrefeldolgozásra a szülő sessionnak átküldésre kerül.
                   //a vonal pedig az aktuális sessionba, törlésre.
                
                   Assertions.assertEquals(ACTION_TYPE.S_DELETE_CLASS_HEADER_FROM_PARENT_PACKAGE,nestedQueue.poll().getAction());
                   
                     Assertions.assertEquals(ACTION_TYPE.DELETE,nestedQueue.poll().getAction());//itt a nestedActionProcesszor szál fogja feldolgozni ezt az üzenetet. a vonaltörlést már teszteltük.

               Assertions.assertEquals(2,session.getSessionItemMap().size());//az object kitörlődött, de a vonal törlését nestedActionQueue fogja meghívni, amit már leteszteltünk.
               

    
   }
   
   private DiagramEntity helper_initDiagram(){
          session.getSessionContainerMap().clear();
       session.getSessionItemMap().clear();
       //INIT
          DiagramEntity dg=new DiagramEntity();
           dg.setDgObects(new ArrayList<>());
           dg.setLines(new ArrayList<>());
          when(diagramRepo.save(any(DiagramEntity.class))).thenReturn(dg);
           session.setDg(dg);
           return dg;
   }
   @Test
   public void deleteSimpleClass_Test() throws JsonProcessingException, NotFoundException{
       session.getSessionItemMap().clear();
       session.getSessionContainerMap().clear();
        SimpleClass clas = mapper.readValue(this.SimpleClassTestJSON, SimpleClass.class);
        ProjectFolderEntity parentpFolder=new ProjectFolderEntity();
        
        DiagramEntity parentdg=new DiagramEntity();
        parentdg.setId(3);
        parentdg.setDgObects(new ArrayList<>());
        parentdg.setLines(new ArrayList<>());
    
        parentpFolder.setDiagram(parentdg);
        PackageObject packageObj=new PackageObject();
        TitleElement parentpTitle=new TitleElement();
        parentpTitle.setName("folder1");
        packageObj.setTitleModel(parentpTitle);
        packageObj.setElements(new ArrayList<>());
        PackageElement pelem=new PackageElement();
        pelem.setReferencedObjectId(clas.getId());//set reference to SimpleClass
        packageObj.getElements().add(pelem);
        parentdg.getDgObjects().add(packageObj);
           
           ProjectFolderEntity currFolder=new ProjectFolderEntity();
           currFolder.setName("folder1");
           
           currFolder.setParentProjectFolder(parentpFolder);
        DiagramEntity dg=new DiagramEntity();
           dg.setDgObects(new ArrayList<>());
           dg.setLines(new ArrayList<>());
               dg.setRelatedFolder(currFolder);
                clas.setDiagram(dg);
           dg.getDgObjects().add(clas);
           session.setDg(dg);
           session.init();
           session.lockObjectById(clas.getId(), 2, this.generateCommonLock());
            when(diagramRepo.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.of(parentdg));
            when(objectRepo.saveAndFlush(ArgumentMatchers.any(PackageObject.class))).thenReturn(packageObj);

           //when(diagramRepo.save(ArgumentMatchers.anyInt())).thenReturn(Optional.of(dg));
           //TESTING
       Assertions.assertEquals(1,packageObj.getElements().size());
       Assertions.assertEquals(1,dg.getDgObjects().size());
              Assertions.assertEquals(1,((PackageObject)parentdg.getDgObjects().get(0)).getElements().size());

        Assertions.assertEquals(2,session.getSessionItemMap().size());//Titlemodel, SimpleClass, 
       Assertions.assertEquals(3,session.getSessionContainerMap().size()); //SimpleClass, SimpleClassElementGroup x2
       session.deleteItemFromContainerById(2,clas.getId(),-1);
       
       Assertions.assertEquals(0,packageObj.getElements().size());
       Assertions.assertEquals(0,dg.getDgObjects().size());
       Assertions.assertEquals(0,((PackageObject)parentdg.getDgObjects().get(0)).getElements().size());
       Assertions.assertEquals(0,session.getSessionItemMap().size());
       Assertions.assertEquals(0,session.getSessionContainerMap().size());
       // SimpleClass classResult = (SimpleClass) session.createItemForContainer(2, -1, clas);//-1 means, its a global object.
            

   }
   
   @Test //this ona also tests init
   public void deleteItemFromContainerById_deleteLine_Test() throws NotFoundException{
      helper_initDiagram();
           Line l=new Line();
           l.setId(3);
          
           l.setDiagram(session.getDg());
           session.getDg().getLines().add(l);
                      session.init();
           session.lockObjectById(l.getId(), 2, this.generateCommonLock());
                Assertions.assertEquals(1,session.getDg().getLines().size());
           Assertions.assertEquals(1,session.getSessionItemMap().size());
           session.deleteItemFromContainerById(2,3,-2);
           Assertions.assertEquals(0,session.getDg().getLines().size());
           Assertions.assertEquals(0,session.getSessionItemMap().size());

           
   }
   
       String SimpleClassTestJSON="{\"doc\":\"\",\"_type\":\"SimpleClass\",\"id\":10,\"dimensionModel\":{\"width\":150,\"height\":200,\"x\":51.011383056640625,\"y\":148.0142059326172},\"extra\":{\"old_id\":2140236275,\"draft\":true},\"viewModel\":null,\"scaledModel\":{\"posx_scaled\":51.011383056640625,\"posy_scaled\":148.0142059326172,\"width_scaled\":150,\"height_scaled\":200,\"min_height_scaled\":75},\"z\":1,\"edit\":false,\"name\":\"Class\",\"groups\":[{\"id\":-681069448,\"group_name\":\"attributes\",\"group_syntax\":1,\"attributes\":[],\"_type\":\"SimpleClassElementGroup\",\"viewModel\":null,\"edit\":false,\"extra\":{\"old_id\":-681069448}},{\"id\":888906808,\"group_name\":\"functions\",\"group_syntax\":0,\"attributes\":[],\"_type\":\"SimpleClassElementGroup\",\"viewModel\":null,\"edit\":false,\"extra\":{\"old_id\":888906808}}],\"titleModel\":{\"extra\":{\"old_id\":1310368347,\"draft\":true},\"_type\":\"TitleElement\",\"edit\":true,\"id\":1310368347,\"name\":\"New Class\",\"viewModel\":null}}";//"{\"doc\":\"\",\"_type\":\"SimpleClass\",\"id\":11,\"dimensionModel\":{\"x\":51.011383,\"y\":148.0142,\"width\":162,\"height\":213},\"extra\":{\"old_id\":\"2140236275\",\"draft\":\"true\"},\"z\":1,\"edit\":false,\"name\":\"Class\"}";

}
  /*@BeforeAll
    public static void beforeClass() {
        System.out.println("@BeforeClass");
    }

    @BeforeEach
    public void before() {
        System.out.println("@Before");
    }

    @Test
    public void test() {
        System.out.println("@Test");
    }

    @AfterEach
    public void after() {
        System.out.println("@After");
    }

    @AfterAll
    public static void afterClass() {
        System.out.println("@AfterClass");
    }*/