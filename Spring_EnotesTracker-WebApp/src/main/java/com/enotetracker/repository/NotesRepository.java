package com.enotetracker.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.enotetracker.model.Notes;
import com.enotetracker.model.User;

public interface NotesRepository extends JpaRepository<Notes, Integer>{

	public  Page<Notes> findByUser(User user,Pageable pageable);
}
