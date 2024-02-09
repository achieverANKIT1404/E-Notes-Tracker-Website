package com.enotetracker.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.enotetracker.model.User;
import com.enotetracker.repository.UserRepository;
import com.enotetracker.service.UserService;
import com.enotetracker.service.UserServiceImpl;

@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler{

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String email = request.getParameter("username");
		
		User user = userRepo.findByEmail(email);
		
		if(user!=null) {
			
			if(user.isEnabled()) {
				
				if(user.isAccountNonLocked()) {
					
					if(user.getFailedAttempt()<UserServiceImpl.attempt_Time-1) {
						
						userService.increaseFailedAttempt(user);
					}
					else {
						
						userService.lock(user);
						
						exception = new LockedException("Failed login attempts, Please login after 5 sec");
					}
				}
				else if(!user.isAccountNonLocked()) {
					
					if(userService.unlockAccountTimeExpired(user)) {
						
						exception = new LockedException("Account is unlocked, Please try to login");
					}
					else {
						
						exception = new LockedException("Account is locked, Please try after somethime!");
					}
				}
				else {
					
					exception = new LockedException("Account is inactive verify account!");
				}
			}
		}
		
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

	
}
