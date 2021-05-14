/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.service.socket.threads.service;

import com.szefi.uml_conference.editor.model.socket.EditorAction;
import com.szefi.uml_conference.editor.model.socket.Response.EditorActionResponse;
import com.szefi.uml_conference.editor.model.socket.Response.SessionStateResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author h9pbcl
 */
@Configuration
public class QueueManager {
      
    @Bean("sessionStateResponseQueue")
    public BlockingQueue<SessionStateResponse> getSessionStateResponseQueue(){
        return new  LinkedBlockingQueue<>();
    }
    @Bean("actionQueue")
    public BlockingQueue<EditorAction> getActionQueue(){
        return new  LinkedBlockingQueue<>();
    }
    @Bean("nestedActionQueue")
    public BlockingQueue<EditorAction> getNestedActionQueue(){
        return new  LinkedBlockingQueue<>();
    }
     @Bean("actionResponseQueue")
    public BlockingQueue<EditorActionResponse> getActionResponseQueue(){
        return new  LinkedBlockingQueue<>();
    }
    
    
}
