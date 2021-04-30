/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.editor.socket.threads;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author h9pbcl
 */
public abstract class CustomProcessor implements Runnable{
    protected boolean isClosed=false;
  public  CustomProcessor(){
      
  }
  
  
    public void stop(){
        isClosed=true;
    }

 
    @Override
    public abstract void run();
}
