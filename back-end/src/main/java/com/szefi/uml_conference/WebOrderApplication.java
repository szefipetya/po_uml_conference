package com.szefi.uml_conference;

import com.szefi.uml_conference.controller.ApiController;
import com.szefi.uml_conference.debug.ApplicatoinStartupRunner;
import com.clarmont.orderentities.model.entity.CartItemEntity;
import com.szefi.uml_conference.service.OrderService;
import java.util.Arrays;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication
@EnableAutoConfiguration
@EntityScan({"com.clarmont.orderentities.model.entity"})
@ComponentScan({"com.szefi.uml_conference"})
public class WebOrderApplication {
    @Autowired
     static ApplicatoinStartupRunner runner;
	public static void main(String[] args) {
		ApplicationContext ct= SpringApplication.run(WebOrderApplication.class, args);  
                String[] n=ct.getBeanDefinitionNames();
                Arrays.sort(n);
                /*for (String s:n){
                    System.out.println(s);
                }*/
               
                
	}
        
     
}
