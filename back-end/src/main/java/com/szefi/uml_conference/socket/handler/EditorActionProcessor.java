/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.socket.handler;

import com.szefi.uml_conference.model.dto.socket.ACTION_TYPE;
import com.szefi.uml_conference.model.dto.socket.EditorAction;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;

/**
 *
 * @author h9pbcl

 */
public class EditorActionProcessor extends CustomProcessor {
   
    
    public EditorActionProcessor(BlockingQueue<EditorAction> queue, BlockingQueue<EditorAction> responseQueue){
      this.actionQueue=queue;
       this.responseQueue=responseQueue;
    }
  
    private  BlockingQueue<EditorAction> responseQueue=null;
  
    private  BlockingQueue<EditorAction> actionQueue=null;
  
    @Override
    public void run() {
        while(!isClosed){
            
                 System.out.println("waiting this:");  
                EditorAction action=null;
               try {
                action  = actionQueue.take();
                 System.out.println("stuff taken"); 
            } catch (InterruptedException ex) {
                Logger.getLogger(ResponseProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
                 if(action==null) continue;
                System.out.println("processing this:");         
                System.out.println(action.getJson());         
                responseQueue.add(action);
               
            
        }
    }
    
}
