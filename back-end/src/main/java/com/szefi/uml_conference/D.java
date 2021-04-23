/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference;

import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author h9pbcl
 */
public class D {
 

  // static Logger logger = 
    private static void writeLog(Class<?> clazz,DLEVEL level, String msg){
    
        switch(level){
            case DEBUG:    LoggerFactory.getLogger(clazz).debug(msg); break;
            case INFO:     LoggerFactory.getLogger(clazz).info(msg);break;
            case WARN:     LoggerFactory.getLogger(clazz).warn(msg);break;
            case ERR:     LoggerFactory.getLogger(clazz).error(msg);break;
            default:  LoggerFactory.getLogger(clazz).debug(msg); break;
        }
    }
 
    public static void log(String msg,Class<?> clazz,DLEVEL level) {
        writeLog(clazz,level,msg);
    }
      public static void log(String msg,Class<?> clazz) {
        writeLog(clazz,DLEVEL.DEBUG,msg);
    }
        public static void log(String msg) {
        writeLog(D.class,DLEVEL.DEBUG,msg);
    }

}
