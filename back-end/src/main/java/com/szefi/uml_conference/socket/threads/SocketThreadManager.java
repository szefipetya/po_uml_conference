/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.threads;

import com.szefi.uml_conference.model.dto.socket.EditorAction;
import com.szefi.uml_conference.model.dto.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.model.dto.socket.tech.UserWebSocket;
import com.szefi.uml_conference.socket.threads.service.QueueManager;
import com.szefi.uml_conference.socket.threads.service.SOCKET;
import com.szefi.uml_conference.socket.threads.service.SocketSessionService;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author h9pbcl
 */

@Component
@Configuration
@Import(QueueManager.class)
public class SocketThreadManager {
   
    SocketSessionService socketService;
 /*@Autowired
    public void setSocketService(SocketSessionService socketService) {
        this.socketService = socketService;
        actionSockets=this.socketService.getSockets(SOCKET.ACTION);
        stateSockets=this.socketService.getSockets(SOCKET.STATE);
    }*/
    
   // private  List<UserWebSocket> actionSockets;
  //  private  List<UserWebSocket> stateSockets;
    // @Autowired
  //  private final  BlockingQueue<EditorAction> responseQueue;
      @Autowired
     @Qualifier("sessionStateResponseQueue")
    private  BlockingQueue<SessionStateResponse> sessionStateResponseQueue;
     @Autowired
     @Qualifier("actionQueue")
    private  BlockingQueue<EditorAction> actionQueue;
    
       @Bean("actionSockets")
                @Scope("singleton")
      public  List<UserWebSocket> getActionSockets(){
             return new  LinkedList<>();
         }
           
          @Bean("stateSockets")
                  @Scope("singleton")
        public List<UserWebSocket> getStateSockets(){
             return new  LinkedList<>();
         }
     
     
    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private TaskExecutor taskExecutor;  
    
     @Autowired
    private TaskExecutor taskExecutor2;  
       @Autowired
    private TaskExecutor taskExecutor3;  
    
    
    
    
  
        
    
    
    @Autowired
    public SocketThreadManager( SocketSessionService socketService){
        this.socketService=socketService;
       // System.out.print("hello im started2222"+socketService==null);
        //actionSockets=socketService.getSockets(SOCKET.ACTION);
       // stateSockets=socketService.getSockets(SOCKET.STATE);
                
                
        //actionQueue=new LinkedBlockingQueue<>(200);
       // responseQueue=new LinkedBlockingQueue<>(200);
      //  sessionStateResponseQueue=new LinkedBlockingQueue<>(200);
   
       // start1();
    }
    
    Thread actionProcessorThread;
    Thread actionResponseProcessorThread;
    Thread sessionStateResponseProcessorThread;
    public void postAction(EditorAction a){
        try {
            actionQueue.put(a);
            System.out.println("putted on list");
        } catch (InterruptedException ex) {
            Logger.getLogger(SocketThreadManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @EventListener(ApplicationReadyEvent.class)
    private void executeProcessors(){
        taskExecutor.execute(ctx.getBean(EditorActionProcessor.class));
    //    taskExecutor.execute(ctx.getBean(ActionResponseProcessor.class));
        taskExecutor2.execute(ctx.getBean(SessionStateResponseProcessor.class));
        taskExecutor3.execute(ctx.getBean(ActionResponseProcessor.class));
        
        
      /*  actionProcessorThread=new Thread(new EditorActionProcessor(socketService,actionQueue,responseQueue,sessionStateResponseQueue));
        actionProcessorThread.start();
        sessionStateResponseProcessorThread=new Thread(new SessionStateResponseProcessor(stateSockets,sessionStateResponseQueue));
        sessionStateResponseProcessorThread.start(); 
        
        actionResponseProcessorThread=new Thread(new ActionResponseProcessor(actionSockets,responseQueue));
        actionResponseProcessorThread.start(); */ 
    }
}
