package com.szefi.uml_conference;

import com.szefi.uml_conference.controller.ApiController;
import com.szefi.uml_conference.debug.ApplicatoinStartupRunner;
import com.clarmont.orderentities.model.entity.CartItemEntity;
import com.szefi.uml_conference.service.OrderService;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan({"com.clarmont.orderentities.model.entity"})
//@ComponentScan({"com.szefi.uml_conference.socket"})

public class WebOrderApplication {
    @Autowired
     static ApplicatoinStartupRunner runner;
	public static void main(String[] args) {
		SpringApplication.run(WebOrderApplication.class, args);  
               
                
	}
        
     
}
