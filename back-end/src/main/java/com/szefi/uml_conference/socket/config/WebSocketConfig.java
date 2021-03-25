package com.szefi.uml_conference.socket.config;

import com.szefi.uml_conference.socket.handler.SessionStateHandler;
import com.szefi.uml_conference.socket.handler.EditorActionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

   // private final static String endpoint="/test";
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WebSocketHandlerRegistration setHandshakeHandlerForEditorActionHandler = registry.addHandler(getActionHandler(),"/action").setAllowedOrigins("*");
        WebSocketHandlerRegistration setHandshakeHandlerForAsyncCheckHandler = registry.addHandler( getHandler(),"/state").setAllowedOrigins("*");
    
    }
    @Bean
    public EditorActionHandler getActionHandler(){
        return new EditorActionHandler();
    }
      @Bean
    public SessionStateHandler getHandler(){
        return new SessionStateHandler();
    }
    @Bean
    @Scope("prototype")
public ThreadPoolTaskExecutor taskExecutor() {
    return new ThreadPoolTaskExecutor();
    
    
    
}
   
}
/*@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

}
*/