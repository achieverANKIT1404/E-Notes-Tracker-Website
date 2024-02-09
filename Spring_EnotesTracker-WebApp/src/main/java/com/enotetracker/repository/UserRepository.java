package com.enotetracker.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.enotetracker.model.User;

@Transactional
public interface UserRepository extends JpaRepository<User, Integer>{

	public boolean existsByEmail(String email);

	public User findByEmail(String email);
	
	public User findByEmailAndMobile(String email, String mobile);
	
    public User findByVerificationCode(String code);
    
	@Query("update User u set u.failedAttempt=?1 where email=?2")
	@Modifying
	public void  updateFailedAttempt(int attempt, String email);

}
