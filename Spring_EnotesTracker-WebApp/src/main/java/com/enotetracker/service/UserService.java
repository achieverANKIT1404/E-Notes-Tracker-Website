package com.enotetracker.service;

import com.enotetracker.model.User;

public interface UserService {

	public User saveUser(User user, String url);
	
	public User saveUser(User user);
	
	public User getUserById(int id);
	
	public boolean checkEmail(String email);
	
	public boolean verifyAccount(String code);

	public void increaseFailedAttempt(User user);
	
	public void resetAttempt(String email);
	
	public void lock(User user);
	
	public boolean unlockAccountTimeExpired(User user);
}
