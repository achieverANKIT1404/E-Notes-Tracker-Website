package com.enotetracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.enotetracker.model.User;
import com.enotetracker.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
        User user = userRepo.findByEmail(username);
        
	if(user!=null) {
			
			return new CustomUser(user);
		}
	
	  throw new UsernameNotFoundException("user not available!");

	}

}
