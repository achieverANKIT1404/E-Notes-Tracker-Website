package com.enotetracker.service;

import java.util.Date;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.enotetracker.model.User;
import com.enotetracker.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncode;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public User saveUser(User user, String url) {

		user.setPassword(passwordEncode.encode(user.getPassword()));

		user.setRole("ROLE_USER");
		
        user.setEnabled(false);
				
		user.setVerificationCode(UUID.randomUUID().toString());

		user.setAccountNonLocked(true);
		
		user.setFailedAttempt(0);
		
		user.setLockTime(null);
		
		User us = userRepo.save(user);
		
		if(us!=null) {
			
			sendVerificationMail(user, url);
		}
		
		return us;
	}

	@Override
	public boolean checkEmail(String email) {
		
		return userRepo.existsByEmail(email);
	}
	
public void sendVerificationMail(User user, String url) {
		
		String from = "trackernotemasterpro@gmail.com";
		
		String to = user.getEmail();
		
		String subject = "Account Verification";
		
		String content = "Dear [[name]], <br>"
				+ "Please click the link below to verify your registration: <br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
				+ "Thank You!, <br>"
				+ "ENotes Tracker";
		
		try {
			
			MimeMessage message = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(message);
			
			helper.setFrom(from, "ENotes Tracker");
			
			helper.setTo(to);
			
			helper.setSubject(subject);
			
			content = content.replace("[[name]]", user.getName());
			
			String siteUrl = url+"/verify?code=" +user.getVerificationCode();
			
			content = content.replace("[[URL]]", siteUrl);
			
			helper.setText(content, true);
			
			mailSender.send(message);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
}
	
		@Override
		public boolean verifyAccount(String code) {
			
			User user = userRepo.findByVerificationCode(code);
			
			if(user!=null) {
						
						user.setEnabled(true);
						
						user.setVerificationCode(null);
						
						userRepo.save(user);
						
						return true;
					}
			
			return false;
		}
		
	@Override
	public void increaseFailedAttempt(User user) {
		
		int attempt = user.getFailedAttempt()+1;
		
		userRepo.updateFailedAttempt(attempt, user.getEmail());
	}
	
	private static final long lock_duration_time=30000; //5sec = 30000 MilliSec
	
	public static final long attempt_Time=3;

	@Override
	public void resetAttempt(String email) {
		
		userRepo.updateFailedAttempt(0, email);
	}

	@Override
	public void lock(User user) {

		user.setAccountNonLocked(false);
		
		user.setLockTime(new Date());
		
		userRepo.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(User user) {
		
		long lockTimeMills = user.getLockTime().getTime();
		
		long currentTimeMills = System.currentTimeMillis();
		
		if(lockTimeMills+lock_duration_time < currentTimeMills) {
			
			user.setAccountNonLocked(true);
			
			user.setLockTime(null);
			
			user.setFailedAttempt(0);
			
			userRepo.save(user);
			
			return true;
		}
		
		return false;		
	}

	@Override
	public User getUserById(int id) {
		
		return userRepo.findById(id).get();
	}


	@Override
	public User saveUser(User user) {
		
		return userRepo.save(user);
	}
	

}
 