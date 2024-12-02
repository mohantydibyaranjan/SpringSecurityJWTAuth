package com.nt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nt.filter.AppFilter;
import com.nt.service.CustomerService;

@Configuration
public class AppConfig {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AppFilter filter;
	
	@Bean
	public PasswordEncoder pwdEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public AuthenticationProvider authprovider() {
		DaoAuthenticationProvider  provider=new DaoAuthenticationProvider();
		provider.setUserDetailsService(customerService);
		provider.setPasswordEncoder(pwdEncoder());
		return provider;
	}
	@Bean
	public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
		
	}
	@Bean
	public SecurityFilterChain securityConfig(HttpSecurity http) throws Exception {
	    return http
	            .csrf()
	                .disable()
	            .authorizeHttpRequests()
	                .requestMatchers("/api/register", "/api/login")
	                    .permitAll()
	                .requestMatchers("/api/**")
	                    .authenticated()
	            .and()
	            .sessionManagement()
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	            .and()
	            .authenticationProvider(authprovider()) // Ensure this method is defined
	            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class) // Ensure `filter` is valid
	            .build();
	}

	}


