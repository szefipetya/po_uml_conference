/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.tests.editor.integration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szefi.uml_conference.UmlConferenceApplication;
import com.szefi.uml_conference.editor.model.diagram.DiagramEntity;
import com.szefi.uml_conference.editor.model.do_related.AttributeElement;
import com.szefi.uml_conference.editor.model.do_related.SimpleClass;
import com.szefi.uml_conference.editor.model.do_related.SimpleClassElementGroup;
import com.szefi.uml_conference.editor.model.do_related.TitleElement;
import com.szefi.uml_conference.editor.model.do_related.line.Line;
import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.LOCK_TYPE;
import com.szefi.uml_conference.editor.model.socket.SessionState;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocketWrapper;
import com.szefi.uml_conference.editor.model.top.DynamicSerialObject;
import com.szefi.uml_conference.editor.repository.AttributeElementRepository;
import com.szefi.uml_conference.editor.repository.DiagramRepository;
import com.szefi.uml_conference.editor.repository.DynamicSerialObjectRepository;
import com.szefi.uml_conference.editor.service.EditorSession;
import com.szefi.uml_conference.editor.service.SocketSessionService;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.internal.runners.JUnit4ClassRunner;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author h9pbcl
 */
//@ContextConfiguration(classes=Application.class)
//@RunWith(MockitoJUnitRunner.class)

//@DataJpaTest
//@RunWith(SpringRunner.class)
@SpringBootTest(classes={UmlConferenceApplication.class})
@RunWith(JUnit4ClassRunner.class)
public class EditorSessionTest {
    @Autowired
    SocketSessionService socketService;
    
     @Autowired
    private  DiagramRepository diagramRepo;
      @Autowired
     private  DynamicSerialObjectRepository objectRepo;
       @Autowired
     private  AttributeElementRepository attrElementRepo;
     private  BlockingQueue<EditorAction> nestedQueue=new LinkedBlockingQueue<>();
      static ObjectMapper mapper;
 static  EditorSession session;
     
     UserWebSocketWrapper s2;
     UserWebSocketWrapper s;
   
     @BeforeAll
       public static  void setUp() {
       mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }
       
         private LOCK_TYPE[] generateCommonLock(){
        return  new LOCK_TYPE[]{LOCK_TYPE.NO_EDIT, LOCK_TYPE.NO_MOVE};
    }
 
       @BeforeEach
       public  void populateSession(){
           DiagramEntity dg=new DiagramEntity();
           dg.setDgObects(new ArrayList<>());
           dg.setLines(new ArrayList<>());
           
           diagramRepo.save(dg);
           List<String> cols=new ArrayList<>();
           cols.add("#fff");
           cols.add("#aaa");
           cols.add("#bbb");
           cols.add("#ccc");
           cols.add("#ddd");
         session=new EditorSession(diagramRepo, objectRepo,nestedQueue, attrElementRepo,cols);
         session.setDg(dg);
         session.init();
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
   void createItemForContainer__AttributeElementCreation() throws NotFoundException,Exception{
        //INIT 
           SimpleClassElementGroup group=new SimpleClassElementGroup();
         group= objectRepo.save(group);
          //inject group to session
        group.injectSelfToStateMap(session.getSessionItemMap(), session.getSessionContainerMap());
      
          AttributeElement elem=new AttributeElement();
        elem.setAttr_type("int");
        elem.setName("valami");
        elem.setId(-20);//something temporary generated on client side
        elem.setVisibility("+");
        //TEST
           AttributeElement elemResult=(AttributeElement)session.createItemForContainer(1, group.getId(), elem);
         //check if elem is present in the session

       Assertions.assertEquals(session.getContainerById(group.getId()),elemResult.getGroup() );
       
        //check if the element is inserted into the databese,+ linked to the (container) group
       Assertions.assertTrue(((AttributeElement)objectRepo.findById(elemResult.getId()).get()).getGroup().getId().equals(group.getId()));
              
    }
   
   
     @Test
   void createItemForContainer__SimpleClassCreation() throws NotFoundException,Exception{
      SimpleClass clas=mapper.readValue(this.SimpleClassTestJSON, SimpleClass.class);
      //TEST
           SimpleClass classResult=(SimpleClass)session.createItemForContainer(1, -1, clas);
         assertTrue(classResult.getGroups().get(0).getParentClass().equals(classResult));
         assertTrue(classResult.getGroups().get(1).getParentClass().equals(classResult));
         //check if elem is present in the session
      // Assertions.assertEquals(group.getAttributes().get(0),elemResult );
     //  Assertions.assertEquals(session.getContainerById(-1).,elemResult.getGroup() );
       
        //check if the element is inserted into the databese,+ linked to the (container) group
       Assertions.assertTrue(((SimpleClass)objectRepo.findById(clas.getId()).get()).getDiagram().getId().equals(session.getDg().getId()));
       Assertions.assertEquals(51,Math.round(((SimpleClass)objectRepo.findById(clas.getId()).get()).getDiagram().getDgObjects().get(0).getDimensionModel().getX()));
              
    }
   
      @Test
   void createItemForContainer__LineCreation() throws NotFoundException,Exception{
       String json_from_client="{\"_type\":\"Line\",\"id\":null,\"extra\":{},\"lineType\":{\"id\":null,\"startHead\":\"NONE\",\"endHead\":\"ARROW\",\"body\":\"SOLID\",\"type\":\"DIRECTED_ASSOC\"},\"object_start_id\":11,\"object_end_id\":12,\"breaks\":[]}";
               
         Line l=(Line)mapper.readValue(json_from_client,DynamicSerialObject.class);
         Line lresult=(Line)session.createItemForContainer(1, -2, l);
         assertTrue(lresult.getDiagram().getLines().get(0).getId().equals(lresult.getId()));
         assertTrue(session.getDg().getLines().get(0).getId().equals(lresult.getId()));
         assertTrue(session.getDg().getLines().get(0).getLineType().getStartHead().equals(com.szefi.uml_conference.editor.model.do_related.line.LINE_HEAD.NONE));
         assertTrue(session.getDg().getLines().get(0).getLineType().getEndHead().equals(com.szefi.uml_conference.editor.model.do_related.line.LINE_HEAD.ARROW));
         Assertions.assertEquals(true,((Line)objectRepo.findById(lresult.getId()).get()).getDiagram().getLines().get(0).equals(lresult));      
   }
   
   
   @Test
    void updateObjectAndUnlock_TitleElement_update() throws JsonProcessingException, NotFoundException {
        //INIT
        SimpleClass clas = mapper.readValue(this.SimpleClassTestJSON, SimpleClass.class);
        SimpleClass classResult = (SimpleClass) session.createItemForContainer(2, -1, clas);//-1 means, its a global object.

        //lock the title
        session.lockObjectById(classResult.getTitleModel().getId(), 10, this.generateCommonLock());
        //send modified title from client
        String titleJSONFromClient = "{\n"
                + "        \"extra\": { \"old_id\": -17, \"draft\": true },\n"
                + "        \"_type\": \"TitleElement\",\n"
                + "        \"edit\": true,\n"
                + "        \"id\": " + classResult.getTitleModel().getId() + ",\n"
                + "        \"name\": \"NewTitle\",\n"
                + "        \"viewModel\": null\n"
                + "      }";
        TitleElement elem = mapper.readValue(titleJSONFromClient, TitleElement.class);
        //INIT end
        Pair<SessionState, DynamicSerialObject> resultPair = session.updateObjectAndUnlock(elem, 10);
        //TESTS
        Assertions.assertEquals(resultPair.getFirst().getLockerUser_id() , -1);//-1 means, the object is free.
        Assertions.assertEquals(((TitleElement )resultPair.getSecond()).getName() ,"NewTitle");//-1 means, the object is free.
    }
    
    String SimpleClassTestJSON="{\"doc\":\"\",\"_type\":\"SimpleClass\",\"id\":2140236275,\"dimensionModel\":{\"width\":150,\"height\":200,\"x\":51.011383056640625,\"y\":148.0142059326172},\"extra\":{\"old_id\":2140236275,\"draft\":true},\"viewModel\":null,\"scaledModel\":{\"posx_scaled\":51.011383056640625,\"posy_scaled\":148.0142059326172,\"width_scaled\":150,\"height_scaled\":200,\"min_height_scaled\":75},\"z\":1,\"edit\":false,\"name\":\"Class\",\"groups\":[{\"id\":-681069448,\"group_name\":\"attributes\",\"group_syntax\":1,\"attributes\":[],\"_type\":\"SimpleClassElementGroup\",\"viewModel\":null,\"edit\":false,\"extra\":{\"old_id\":-681069448}},{\"id\":888906808,\"group_name\":\"functions\",\"group_syntax\":0,\"attributes\":[],\"_type\":\"SimpleClassElementGroup\",\"viewModel\":null,\"edit\":false,\"extra\":{\"old_id\":888906808}}],\"titleModel\":{\"extra\":{\"old_id\":1310368347,\"draft\":true},\"_type\":\"TitleElement\",\"edit\":true,\"id\":1310368347,\"name\":\"New Class\",\"viewModel\":null}}";//"{\"doc\":\"\",\"_type\":\"SimpleClass\",\"id\":11,\"dimensionModel\":{\"x\":51.011383,\"y\":148.0142,\"width\":162,\"height\":213},\"extra\":{\"old_id\":\"2140236275\",\"draft\":\"true\"},\"z\":1,\"edit\":false,\"name\":\"Class\"}";
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