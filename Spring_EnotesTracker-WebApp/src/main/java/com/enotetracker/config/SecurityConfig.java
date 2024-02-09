package com.enotetracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private CustomUserDetailService detailService;
	
	@Autowired
	public CustomFailureHandler failureHandler;
		
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider getDaoAuthProvider() {
		
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(detailService);
		provider.setPasswordEncoder(getPasswordEncoder());
		
		return provider;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http.csrf().disable().authorizeRequests().antMatchers("/user/**").hasRole("USER")
		.antMatchers("/**").permitAll().and()
		.formLogin().loginPage("/signin")
		.loginProcessingUrl("/login")
		.failureHandler(failureHandler)
		.defaultSuccessUrl("/user/add_notes")
		.permitAll();
		
		http.authenticationProvider(getDaoAuthProvider());
		
		return http.build();
	}
}
