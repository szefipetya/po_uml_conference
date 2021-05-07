/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szefi.uml_conference.security.config;


import com.szefi.uml_conference.controller.config.CorsFilter;
import com.szefi.uml_conference.security.filters.JwtAuthRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	 @Autowired
    UserDetailsService userDetailsService;
	@Autowired
	private JwtAuthRequestFilter jwtAuthRequestFilter;
        
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/user").hasAnyRole("ADMIN", "USER","ROLE_USER")
                .antMatchers("/log_me_out").hasAnyRole("ADMIN", "USER","ROLE_USER")
                .antMatchers("/test").permitAll()
                .antMatchers("/get/**").hasAnyRole("ADMIN", "USER","ROLE_USER")
                .antMatchers("/").permitAll()
                .antMatchers("/state").permitAll()
                .antMatchers("/action").permitAll()
                .antMatchers("/register").permitAll()
          
                
                .and().csrf().disable()
				.authorizeRequests().antMatchers("/login").permitAll().
						anyRequest().authenticated().and().
						exceptionHandling().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);
	http.addFilterBefore(jwtAuthRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }
    
    
  @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/db/**");
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
        
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}