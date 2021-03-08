/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.controller.config;

import com.szefi.uml_conference.controller.ApiController;
import com.szefi.uml_conference.debug.ApplicatoinStartupRunner;
import java.util.logging.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 *
 * @author peti
 */
/*@Component*/
public class JerseyConfig extends ResourceConfig 
{
    public JerseyConfig() 
    {
        LOG.info("JerseyConfig - configuring REST classes");
      register(ApiController.class);     
       register(ApplicatoinStartupRunner.class);  
       register(CorsFilter.class);  
    
    }
    private static final Logger LOG = Logger.getLogger(JerseyConfig.class.getName());
}