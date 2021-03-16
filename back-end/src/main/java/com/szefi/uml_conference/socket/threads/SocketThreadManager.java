/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.threads;

import com.szefi.uml_conference.model.dto.socket.EditorAction;
import com.szefi.uml_conference.socket.handler.EditorActionProcessor;
import com.szefi.uml_conference.socket.handler.ResponseProcessor;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author h9pbcl
 */

public class SocketThreadManager {
    private  List<WebSocketSession> sessions;
    // @Autowired
    private final  BlockingQueue<EditorAction> responseQueue;
   //  @Autowired
    private final   BlockingQueue<EditorAction> actionQueue;
    
    public SocketThreadManager( List<WebSocketSession> sessions){
        System.out.print("hello im started");
        actionQueue=new LinkedBlockingQueue<>(200);
        responseQueue=new LinkedBlockingQueue<>(200);
        this.sessions=sessions;
    }
    
    Thread actionProcessorThread;
    Thread responseProcessorThread;
    public void post(EditorAction a){
        try {
            actionQueue.put(a);
            System.out.println("putted on list");
        } catch (InterruptedException ex) {
            Logger.getLogger(SocketThreadManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void start(){
        actionProcessorThread=new Thread(new EditorActionProcessor(actionQueue,responseQueue));
        actionProcessorThread.start();
        responseProcessorThread=new Thread(new ResponseProcessor(sessions,responseQueue));
        responseProcessorThread.start();  
    }
}
