/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.service.socket.threads;

import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import com.szefi.uml_conference.editor.model.socket.tech.UserWebSocketWrapper;
import com.szefi.uml_conference.editor.service.socket.threads.service.NestedEditorActionProcessor;
import com.szefi.uml_conference.editor.service.socket.threads.service.QueueManager;
import com.szefi.uml_conference.editor.service.socket.threads.service.SOCKET;
import com.szefi.uml_conference.editor.service.SocketSessionService;
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
 
     @Autowired
     @Qualifier("actionQueue")
    private  BlockingQueue<EditorAction> actionQueue;
    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private TaskExecutor taskExecutorEditorActionProcessor;  
    
    @Autowired
    private TaskExecutor taskExecutorSessionStateResponseProcessor;  
    @Autowired
    private TaskExecutor taskExecutorActionResponseProcessor;  
    @Autowired
    private TaskExecutor taskExecutorNestedEditorActionProcessor;  
       
    @Autowired
    public SocketThreadManager( SocketSessionService socketService){
        this.socketService=socketService;
    }
    
  
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
        taskExecutorEditorActionProcessor.execute(ctx.getBean("EditorActionProcessor", EditorActionProcessor.class));
        taskExecutorSessionStateResponseProcessor.execute(ctx.getBean(SessionStateResponseProcessor.class));
        taskExecutorActionResponseProcessor.execute(ctx.getBean(ActionResponseProcessor.class));
        taskExecutorNestedEditorActionProcessor.execute(ctx.getBean(NestedEditorActionProcessor.class));
    }
}
