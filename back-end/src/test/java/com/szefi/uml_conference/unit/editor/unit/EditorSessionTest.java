/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.unit.editor;

import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocketWrapper;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.repository.AttributeElementRepository;
import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.editor.service.EditorSession;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javassist.NotFoundException;
import javax.validation.constraints.AssertFalse;
import javax.ws.rs.core.Application;
import org.glassfish.hk2.classmodel.reflect.util.LinkedQueue;
import static org.graalvm.compiler.options.OptionType.User;
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
   
     @BeforeAll
       public static  void setUp() {
        

    }
       
       
 
       @BeforeEach
       public  void populateSession(){
         MockitoAnnotations.initMocks(this); // this is needed for init of mocks, if we use @Mock 
         session=new EditorSession(diagramRepo, objectRepo,nestedQueue, attrElementRepo);

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
    void deleteLocksRelatedToUser(){ 
       
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
   void createItemForContainer_AttributeElementCreation() throws NotFoundException,Exception{
      
      
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