package com.enotetracker.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.enotetracker.model.Notes;
import com.enotetracker.model.User;

public interface NoteService {

	public Notes saveNotes(Notes notes);
	
	public Notes getNotesById(int id);
	
	public Page<Notes> getNotesByUser(User user, int pageno);
	
	public Notes updateNotes(Notes notes);
	
	public boolean deleteNotes(int id);
}
