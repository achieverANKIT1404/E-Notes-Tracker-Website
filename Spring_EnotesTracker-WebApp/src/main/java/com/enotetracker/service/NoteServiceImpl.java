package com.enotetracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.enotetracker.model.Notes;
import com.enotetracker.model.User;
import com.enotetracker.repository.NotesRepository;

@Service
public class NoteServiceImpl implements NoteService{

	@Autowired
	private NotesRepository notesRepo;
	
	@Override
	public Notes saveNotes(Notes notes) {
		
		return notesRepo.save(notes);
	}

	@Override
	public Notes getNotesById(int id) {
		
		return notesRepo.findById(id).get();
	}

	@Override
	public Page<Notes> getNotesByUser(User user, int pageno) {
		
		Pageable pageble = PageRequest.of(pageno, 2);
		
		return notesRepo.findByUser(user,pageble);
	}

	@Override
	public Notes updateNotes(Notes notes) {
		
		return notesRepo.save(notes);
	}

	@Override
	public boolean deleteNotes(int id) {
		
		Notes notes = notesRepo.findById(id).get();
		
		if(notes!=null) {
			
			notesRepo.delete(notes);
			
			return true;
		}
		
		return false;
	}

}
