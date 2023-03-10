package com.szefi.uml_conference;

import com.szefi.uml_conference.editor.controller.ApiController;
import com.szefi.uml_conference.debug.ApplicatoinStartupRunner;

import java.util.Arrays;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAutoConfiguration
@EntityScan({"com.szefi.uml_conference"})
@ComponentScan({"com.szefi.uml_conference"})
@EnableJpaRepositories({"com.szefi.uml_conference"})
@EnableTransactionManagement
@EnableWebMvc
@PropertySource({ "classpath:application.properties", "classpath:application-${spring.profiles.active}.properties"})

/*@EnableAutoConfiguration
@ComponentScan(basePackages={""})
@EnableJpaRepositories(basePackages="") 

@EntityScan(basePackages="")*/
public class UmlConferenceApplication {
    @Autowired
     static ApplicatoinStartupRunner runner;
	public static void main(String[] args) {
		ApplicationContext ct= SpringApplication.run(UmlConferenceApplication.class, args);  
              /*  String[] n=ct.getBeanDefinitionNames();
                Arrays.sort(n);
                for (String s:n){
                    System.out.println(s);
                }*/
               
                
	}
        
     
}
